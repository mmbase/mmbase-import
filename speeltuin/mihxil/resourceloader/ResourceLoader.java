/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

// general
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.net.*;

// used for resolving in servlet-environment
import javax.servlet.ServletContext;


// used for resolving in MMBase database
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.Resources;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

// XML stuff
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.transform.*;
import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

// used for Unicode Escaping when editing property files
import org.mmbase.util.transformers.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * MMBase resource loader, for loading config-files and those kind of things. It knows about MMBase config file locations.
 *
 * I read <a href="http://www.javaworld.com/javaqa/2003-08/02-qa-0822-urls.html">http://www.javaworld.com/javaqa/2003-08/02-qa-0822-urls.html</a>.
 * 
 * Programmers should do something like this if they need a configuration file:
<pre>
InputStream configStream = ResourceLoader.getConfigurationRoot().getResourceAsStream("modules/myconfiguration.xml");
</pre>
or
<pre>
InputSource config = ResourceLoader.getConfiguationRoot().getInputSource("modules/myconfiguration.xml");
</pre>
of if you need a list of all resources:
<pre>
ResourceLoader builderLoader = new ResourceLoader("builders");
List list = builderLoader.getResourcePaths(ResourceLoader.XML_PATTERN, true)
</pre>

When you want to place a configuration file then you have several options, wich are in order of preference:
<ol>
  <li>Place it as on object in 'resources' builder (if such a builder is present)</li>
  <li>Place it in the directory identified by the 'mmbase.config' setting (A system property or web.xml setting).</li>
  <li>Place it in the directory WEB-INF/config. If this is a real directory (you are not in a war), then the resource will also be returned by {@link #getFiles}.</li>
  <li>
  Place it in the class-loader path of your app-server, below the 'org.mmbase.config' package.
  For tomcat this boils down to the following list (Taken from <a href="http://jakarta.apache.org/tomcat/tomcat-5.0-doc/class-loader-howto.html">tomcat 5 class-loader howto</a>)
   <ol>
    <li>Bootstrap classes of your JVM</li>
    <li>System class loader classses</li>
    <li>/WEB-INF/classes of your web application. If this is a real directory (you are not in a war), then the resource will also be returned by {@link #getFiles}.</li>
    <li>/WEB-INF/lib/*.jar of your web application</li>
    <li>$CATALINA_HOME/common/classes</li>
    <li>$CATALINA_HOME/common/endorsed/*.jar</li>
    <li>$CATALINA_HOME/common/lib/*.jar</li>
    <li>$CATALINA_BASE/shared/classes</li>
    <li>$CATALINA_BASE/shared/lib/*.jar</li>
  </ol>
  </li>
</ol>
 *
 * You can programmaticly place or change resources by the use of {@link #createResourceAsStream}.
 * Which will probably only work for one of the first three options.
 *
<pre>
  Impact: 
     URIResolver uses files, must depend on this too. --> FormatterTag, Editwizards!
     MMBase.java, MMBaseContext.java, security, logging
     IncludeTag#cite
     MMAdmin.java 
</pre>
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ResourceLoader.java,v 1.12 2004-10-18 19:12:34 michiel Exp $
 */
public class ResourceLoader extends ClassLoader {

    private static final Logger log = Logging.getLoggerInstance(ResourceLoader.class);
 
    /**
     * Protocol prefix used by URL objects in this class.
     */
    protected static final String PROTOCOL         = "mm";
    
    /**
     * Used for files, and servlet resources.
     */
    protected static final String RESOURCE_ROOT    = "/WEB-INF/config";

    /**
     * Used when getting resources with normal class-loader.
     */
    protected static final String CLASSLOADER_ROOT = "/org/mmbase/config";

    /**
     * Protocol prefix used by URL objects in this class.
     */
    public static final URL NODE_URL_CONTEXT = createNodeURL();

    private static URL createNodeURL() {
        // sigh I don't see another compiling way to fill NODE_URL_CONTEXT
        try {
            return new URL("http", "localhost", "/node/");
        } catch (MalformedURLException mfue) {
            assert false : mfue;
            return null;
        }
    }

    
    /**
     * Used when using getResourcePaths for normal class-loaders.
     */
    protected static final String INDEX            = "INDEX";

    private static  ResourceLoader configRoot = null;
    private static  ResourceLoader webRoot = null;
    private static ServletContext servletContext = null;


    static MMObjectBuilder resourceBuilder = null;



    private final MMURLStreamHandler mmStreamHandler = new MMURLStreamHandler();

    /**
     * Creates a new URL object, which is used to load resources. First a normal java.net.URL is
     * instantiated, if that fails, we check for the 'mmbase' protocol. If so, a URL is instantiated
     * with a URLStreamHandler which can handle that. 
     * 
     * If that too fails, it should actually already be a MalformedURLException, but we try
     * supposing it is some existing file and return a file: URL. If no such file, only then a
     * MalformedURLException is thrown.
     */
    protected  URL newURL(String url) throws MalformedURLException {
        // Try already installed protocols first:
        try {
            return new URL (url);
        } catch (MalformedURLException ignore) {
            // Ignore: try our own handler next.
        }
        
        final int firstColon = url.indexOf (':');
        if (firstColon <= 0) {
            if (new File(url).exists()) return new URL("file:" + url); // try it as a simply file
            throw new MalformedURLException ("No protocol specified: " + url);
        } else {
            
            final String protocol = url.substring (0, firstColon);
            if (protocol.equals(PROTOCOL)) {
                return new URL (null/* no context */, url, mmStreamHandler);
            } else {
                if (new File(url).exists()) return new URL("file:" + url);
                throw new MalformedURLException ("Unknown protocol: " + protocol);
            }
        }
    }


    // these could perhaps be made non-static to make more generic ResourceLoaders possible

    private List /* <File> */   fileRoots;   
    private List /* <String> */ resourceRoots; 
    private List /* <String> */ classLoaderRoots;
    private Set /*  <Integer> */ typeValues;


    static {
        // make sure it works a bit before servlet-startup.
        init(null);
    }



    /**
     * Initializes the Resourceloader using a servlet-context (makes e.g. resolving relatively to WEB-INF/config possible).
     * @param sc The ServletContext used for determining the mmbase configuration directory. Or <code>null</code>.
     */
    public static void init(ServletContext sc) {
        servletContext = sc;
        configRoot = null;
        webRoot    = null;
    }

    /**
     * Sets the MMBase builder which must be used for resource.
     * The builder must have an URL and a HANDLE field.
     * This method can be called only once.
     * @param b An MMObjectBuilder (this may be <code>null</code> if no such builder available)
     * @throws RuntimeException if builder was set already.
     */
    public static void setResourceBuilder(MMObjectBuilder b) {
        if (ResourceWatcher.resourceWatchers == null) {
            throw new RuntimeException("A resource builder was set already: " + resourceBuilder);
        }
        resourceBuilder = b;
        // must be informed to existing ResourceWatchers.
        ResourceWatcher.setResourceBuilder(); // this will also set ResourceWatcher.resourceWatchers to null.
    }

    
    /** 
     * Utility method to return the name part of a resource-name (removed directory and 'extension'). 
     * Used e.g. when loading builders in MMBase.
     */
    public static String getName(String path) {       
        int i = path.lastIndexOf('/');
        path = path.substring(i + 1);

        i = path.lastIndexOf('.');
        if (i > 0) {
            path = path.substring(0, i);
        }
        return path;        
    }

    /**
     * Utility method to returns the 'directory' part of a resource-name.
     * Used e.g. when loading builders in MMBase.
     */
    public static String getDirectory(String path) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            path = path.substring(0, i);
        } else {
            path = "";
        }
        return path;
    }

    /**
     * Returns a set of File object for a given resource (relative to the file roots).
     * @param path A path relative to the fileRoots
     * @return A List.
     */
    protected  List getRootFiles(final String path) {
        return new AbstractList() {
                public int size()            { return fileRoots.size(); }
                public Object  get(int i)    { return new File((File) fileRoots.get(i), path); }
            };
    }

    /**
     * Returns collection of Strings for resourceRoots (relative to root)
     * path must not start with /.
     */

    protected  List getRootResources(final String path) {
        return new AbstractList() {
                public int size()            { return resourceRoots.size(); }
                public Object  get(int i)    { return resourceRoots.get(i) + "/" + path; }
            };
    }



    /**
     * The one ResourceLoader which loads from the mmbase config root is static, and can be obtained with this method
     */
    public static ResourceLoader getConfigurationRoot() {
        if (configRoot == null) {
            configRoot = new ResourceLoader();
            
            // mmbase.config settings
            String configPath = null;
            if (servletContext != null) {
                configPath = servletContext.getInitParameter("mmbase.config");
            }
            if (configPath == null) {
                configPath = System.getProperty("mmbase.config");
            }
            if (configPath != null) {
                if (servletContext != null) {
                    // take into account configpath can start at webrootdir
                    if (configPath.startsWith("$WEBROOT")) {
                        configPath = servletContext.getRealPath(configPath.substring(8));
                    }
                }
                configRoot.fileRoots.add(new File(configPath));
            }
            
            if (servletContext != null) {
                String s = servletContext.getRealPath(RESOURCE_ROOT);
                if (s != null) {
                    configRoot.fileRoots.add(new File(s));
                }
                s = servletContext.getRealPath("/WEB-INF/classes" + CLASSLOADER_ROOT); // prefer opening as a files.
                if (s != null) {
                    configRoot.fileRoots.add(new File(s));
                }
                configRoot.resourceRoots.add(RESOURCE_ROOT);
            }
            
            /**
               if (fileRoots.size() == 0) {
               File [] roots = File.listRoots();
               fileRoots.addAll(Arrays.asList(roots));
               }
            */
            
            configRoot.classLoaderRoots.add(CLASSLOADER_ROOT);            

            configRoot.typeValues.add(Resources.TYPE_CONFIG);
        }
        return configRoot;
    }


    /**
     * The one ResourceLoader which loads from the mmbase web root is static, and can be obtained with this method
     */
    public static ResourceLoader getWebRoot() {
        if (webRoot == null) {
            webRoot = new ResourceLoader();
            if (servletContext != null) {
                String s = servletContext.getRealPath("/");
                if (s != null) {
                    webRoot.fileRoots.add(new File(s));
                }
                webRoot.resourceRoots.add("/");
            }
            webRoot.typeValues.add(Resources.TYPE_WEB);
        }

        return webRoot;
    }


    /**
     * The URL relative to which this class-loader resolves. Cannot be <code>null</code>.
     */
    private URL context;


    /**
     * This constructor instantiates the root resource-loader. There is only one such ResourceLoader
     * (acquirable with {@link #getConfigurationRoot}) so this constructor is private.
     */
    private ResourceLoader() {
        super();
        fileRoots        = new ArrayList();
        resourceRoots    = new ArrayList();
        classLoaderRoots = new ArrayList();
        typeValues       = new HashSet();
        try {
            context = newURL(PROTOCOL + ":/");
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }

    /**
     * Instantiates a new ResourceLoader relative to the root ResourceLoader. See {@link #getConfigurationRoot()}
     */
    public ResourceLoader(final String context)  {
        this(getConfigurationRoot(), context);
    }


    /** 
     * Instantiates a ResourceLoader for a 'sub directory' of given ResourceLoader
     */
    public ResourceLoader(final ResourceLoader cl, final String context)  {
        super(ResourceLoader.class.getClassLoader());
        this.context = cl.findResource(context + "/");
        this.fileRoots        = cl.fileRoots;
        this.resourceRoots    = cl.resourceRoots;
        this.classLoaderRoots = cl.classLoaderRoots;
        this.typeValues       = cl.typeValues;
    }

    /**
     * If name starts with '/' the root resourceloader is used.
     * If name starts with <protocol>: a new {@link java.net.URL} will be created.
     * Otherwise the name is resolved relatively. (For the root ResourceLoader that it the same as starting with /)
     * 
     * {@inheritDoc}
     */
    public URL findResource(final String name) {
        try {
            if (name.startsWith("/")) {
                return newURL(PROTOCOL + ":" + name);
            } else {
                return new URL(context, name);
            }
        } catch (MalformedURLException mfue) {
            log.info(mfue + Logging.stackTrace(mfue));
            return null;
        }
    }


    /**
     * Can be used as an argument for {@link #getResourcePaths(Pattern, boolean)}. MMBase works mainly
     * with xml configuration files, so this comes in handy.
     */
    public static final Pattern XML_PATTERN = Pattern.compile(".*\\.xml$");

    /**
     * Returns the 'context' for the ResourceLoader (an URL).
     */
    public URL getContext() {
        return context;
    }

    /**
     * Returns a set of 'sub resources' (read: 'files in the same directory'), which can succesfully be be loaded by the ResourceLoader.
     *
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     * @return A Set of Strings which can be successfully loaded with the resourceloader.
     */
    public Set getResourcePaths(final Pattern pattern, final boolean recursive) {
        return getResourcePaths(pattern, recursive, false);
    }

    /**
     * Returns a set of context strings which can be used to instantiated new ResourceLoaders (resource loaders for directories)
     * (see {@link #ResourceLoader(ResourceLoader, String)}).
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     */
    public Set getResourceContexts(final Pattern pattern, final boolean recursive) {
        return getResourcePaths(pattern, recursive, true);
    }

    /**
     * Used by {@link #getResourcePaths(Pattern, boolean)} and {@link #getResourceContext(Pattern, boolean)}
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     * @param directories getResourceContext supplies <code>true</code> getResourcePaths supplies <code>false</code>
     */
    protected Set getResourcePaths(final Pattern pattern, final boolean recursive, boolean directories) {
        FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    File f = new File(dir, name);
                    return pattern == null || (f.isDirectory() && recursive) || pattern.matcher(f.toString()).matches();
                }
            };
        Set results = new LinkedHashSet(); // a set with fixed iteration order
        getNodeResourcePaths(pattern, recursive, results, directories);
        getFileResourcePaths(filter, recursive ? "" : null, results, directories);
        getServletContextResourcePaths(pattern, recursive ? "" : null, results, directories);
        getClassLoaderResourcePaths(pattern, results, directories);
        return results;
    }


    
    /**
     * The set of {@link #getResourcePaths(Pattern, boolean)} is merged with all entries of the
     * resource with the given index, which is a simply list of resources. 
     *
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @return A Set of Strings which can be successfully loaded with the resourceloader.
     */
    protected Set getClassLoaderResourcePaths(final Pattern pattern, final Set results, final boolean directories) {
        InputStream inputStream = getResourceAsStream(INDEX);
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (line.startsWith("#")) continue; // support for comments
                    line = line.trim();
                    if (line.equals("")) continue;     // support for empty lines
                    if (directories) {
                        line = getDirectory(line);
                    }
                    if (pattern == null || pattern.matcher(line).matches()) {
                        results.add(line);
                    }
                }
            } catch (IOException ioe) {
            }
        } else {

        }
        return results;
    }

    /**
     * Used by {@link #getResourcePaths(Pattern, boolean)}. This is the function which does the
     * recursion for files.
     */
    protected Set getFileResourcePaths(final FilenameFilter filter,  final String recursive, final Set results, final boolean directories) {
        Iterator i = getFiles(recursive == null ? "" : recursive).iterator();
        while (i.hasNext()) {
            File f = (File) i.next();
            if (f.isDirectory()) { // should always be true
                File [] files = f.listFiles(filter);
                if (files == null) continue;
                for (int j = 0; j < files.length; j++) {
                    if (files[j].getName().equals("")) continue;
                    if (recursive != null && files[j].isDirectory()) {
                        getFileResourcePaths(filter, recursive + files[j].getName() + "/", results, directories);
                    } 
                    if (files[j].canRead() && (directories == files[j].isDirectory())) { 
                        results.add((recursive == null ? "" : recursive) + files[j].getName());
                    }

                }
            }
        }

        return results;
    }

    /**
     * Used by {@link #getResourcePaths(Pattern, boolean)}. This performs the database part of the job.
     */
    protected Set getNodeResourcePaths(final Pattern pattern, final boolean recursive, final Set results, final boolean directories) {
        if (resourceBuilder != null && typeValues.size() > 0) {
            try {
                NodeSearchQuery query = new NodeSearchQuery(resourceBuilder);
                BasicFieldValueInConstraint typeConstraint  =
                    new BasicFieldValueInConstraint(query.getField(resourceBuilder.getField(Resources.TYPE_FIELD)));
                Iterator j = typeValues.iterator();
                while (j.hasNext()) {
                    typeConstraint.addValue(j.next());
                }
                BasicFieldValueConstraint nameConstraint = 
                    new BasicFieldValueConstraint(query.getField(resourceBuilder.getField(Resources.RESOURCENAME_FIELD)), context.getPath().substring(1) + "%");
                nameConstraint.setOperator(FieldCompareConstraint.LIKE);

                BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

                constraint.addChild(typeConstraint).addChild(nameConstraint);


                query.setConstraint(constraint);
                Iterator i = resourceBuilder.getNodes(query).iterator();
                while (i.hasNext()) {
                    MMObjectNode node = (MMObjectNode) i.next();
                    String url = node.getStringValue(Resources.RESOURCENAME_FIELD);
                    String subUrl = url.substring(context.getPath().length() - 1);
                    int pos = subUrl.indexOf('/');

                    if (directories) {
                        if (pos < 0) continue; // not a directory
                        do {
                            String u = subUrl.substring(0, pos);
                            if (pattern != null && ! pattern.matcher(u).matches()) {
                                continue;
                            }
                            results.add(u);
                            pos = subUrl.indexOf('/', pos + 1);
                        } while (pos > 0 && recursive);
                    } else {
                        if (pos > 0 && ! recursive) continue;
                        if (pattern != null && ! pattern.matcher(subUrl).matches()) {
                            continue;
                        }
                        results.add(subUrl);
                    }

                }
            } catch (SearchQueryException sqe) {
                log.warn(sqe);
            }
        }
        return results;
    }


    private static boolean warned23 = false;
    /**
     * Recursing for {@link javax.servlet.ServletContext#getResourcePaths}
     */
    protected Set getServletContextResourcePaths(Pattern pattern,  String recursive, Set results, boolean directories) {
        if (servletContext != null) {
            try {
                Iterator i = getClassLoaderResources(recursive == null ? "" : recursive).iterator();
                while (i.hasNext()) {
                    String resourcePath = ((String) i.next());
                    String currentRoot  = resourcePath.substring(0, resourcePath.length() - (recursive == null ? 0 : recursive.length()));
                    Collection c = servletContext.getResourcePaths(resourcePath);
                    if (c == null) continue;
                    Iterator j = c.iterator();
                    while (j.hasNext()) {
                        String newResourcePath = ((String) j.next()).substring(currentRoot.length());
                        boolean isDir = newResourcePath.endsWith("/");
                        if (isDir) {
                            // subdirs
                            if (recursive != null) {                            
                                getServletContextResourcePaths(pattern, newResourcePath.substring(0, newResourcePath.length() - 1), results, directories);
                            } 
                            if (newResourcePath.equals("/")) continue;
                        }
                        if ((pattern == null || pattern.matcher(newResourcePath).matches()) && (directories == isDir)) {
                            if (isDir) newResourcePath = newResourcePath.substring(0, newResourcePath.length() - 1) ;
                            results.add(newResourcePath);
                        }
                    }
                }
            } catch (NoSuchMethodError nsme) {
                if (! warned23) {
                    log.warn("Servet 2.3 feature not supported! " +  nsme.getMessage());
                    warned23 = true;
                }
                // servletContext.getResourcePaths is only a servlet 2.3 feature.
                
                // old app-server (orion 1.5.4: java.lang.NoSuchMethodError: javax.servlet.ServletContext.getResourcePaths(Ljava/lang/String;)Ljava/util/Set;)
                // simply ignore, running on war will not work in such app-servers
            } catch (Throwable t) { 
                log.error(Logging.stackTrace(t));
                // ignore
            }
        }
        return results;
    }

    /**
     * If you want to change a resource, or create one. Then this method can be used. Specify the
     * desired resource-name and you get an OutputStream back, to which you must write.
     * 
     * This is a shortcut to <code>findResource(name).openConnection().getOutputStream()</code>
     *
     * If the given resource already existed, it will be overwritten, or shadowed, if it was not
     * writeable.
     *
     * @throws IOException If the Resource for some reason could not be created.
     */
    public OutputStream createResourceAsStream(String name) throws IOException {
        if (name.equals("")) {
            throw new IOException("You cannot create a resource with an empty name");
        }
        return findResource(name).openConnection().getOutputStream();
    }

    /**
     * Returns the givens resource as a InputSource (XML streams). ResourceLoader is often used for
     * XML.
     * The System ID is set, otherwise you could as wel do new InputSource(r.getResourceAsStream());
     * @param name The name of the resource to be loaded
     * @return The InputSource if succesfull, <code>null</code> otherwise.
     */
    public InputSource getInputSource(String name)  throws IOException {
        try {
            URL url = findResource(name);
            InputStream stream = url.openStream();
            if (stream == null) return null;                                                
            InputSource is = new InputSource(stream);
            //is.setCharacterStream(new InputStreamReader(stream));
            is.setSystemId(url.toExternalForm());
            return is;
        } catch (MalformedURLException mfue) {
            log.info(mfue);
            return null;
        }
    }



    /**
     * Returns the givens resource as a Document (parsed XML). This can come in handly, because most
     * configuration in in XML.
     *
     * @param name The name of the resource to be loaded
     * @return The Document if succesfull, <code>null</code> if there is not such resource.
     */
    public Document getDocument(String name) throws org.xml.sax.SAXException, IOException  {
        InputSource source = getInputSource(name);
        if (source == null) return null;
        XMLEntityResolver resolver = new XMLEntityResolver(true, null);
        DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(true, null/* no error handler */, resolver);
        if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
        if (log.isDebugEnabled()) log.debug("Reading " + source.getSystemId());
        return  dbuilder.parse(source);
    }

    /**
     * Give a StreamResult for resource with given name. This can be used to write XML to a resource.
     * @see #createResourceAsStream(String)
     */
    protected StreamResult getStreamResult(String name)  throws IOException {
        OutputStream stream = createResourceAsStream(name);
        StreamResult streamResult = new StreamResult(stream);
        return streamResult;
    }

    /**
     * Creates a resource with given name for given Source.
     *
     * @see #createResourceAsStream(String)
     */
    public void storeSource(String name, Source source) throws IOException {
        try {
            StreamResult streamResult = getStreamResult(name);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            // Indenting not very nice int all xslt-engines, but well, its better then depending
            // on a real xslt or lots of code.
            serializer.transform(source, streamResult);
        } catch (final TransformerException te) {
            throw new IOException(te.getMessage()) {
                    public Throwable getCause() {
                        return te;
                    }
                };
        }
    }

    /**
     * Creates a resource for a given Document.
     * @param name Name of the resource.
     * @param doc  The xml document which must be stored.
     * @see #createResourceAsStream(String)
     */
    public void  storeDocument(String name, Document doc) throws IOException {
        storeSource(name, new DOMSource(doc));
    }

    /**
     * Returns a reader for a given resource. This performs the tricky task of finding the encoding.
     * Resource are actually InputStreams (byte arrays), but often they are quite text-oriented
     * (like e.g. XML's), so this method may be useful.
     * @see #getResourceAsStream(String)
     */
    public Reader getReader(String name) throws IOException {
        try {
            InputStream is = getResourceAsStream(name);
            if (is == null) return null;
            if (name.endsWith(".properties")) {
                // todo \ u escapes must be escaped to decent Character's.
                return new TransformingReader(new InputStreamReader(is, "UTF-8"), new InverseCharTransformer(new UnicodeEscaper()));
            }
            byte b[] = new byte[100];
            if (is.markSupported()) {
                is.mark(101);
            }
            try {
                is.read(b, 0, 100);
                if (is.markSupported()) {
                    is.reset();
                } else {
                    is = getResourceAsStream(name);
                }
            } catch (IOException ioe) {
                is = getResourceAsStream(name);
            }

            
            String encoding = GenericResponseWrapper.getXMLEncoding(b);
            if (encoding != null) {
                return new InputStreamReader(is, encoding);
            }
            
            // all other things, default to UTF-8
            return new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            // could not happen
            return null;
        }
    }

    /**
     * Returns a reader for a given resource. This performs the tricky task of finding the encoding.
     * @see #getReader(String)
     * @see #createResourceAsStream(String)
     */
    public Writer getWriter(String name) throws IOException {
        OutputStream os = createResourceAsStream(name);
        try {
            if (os == null) return null;
            if (name.endsWith(".properties")) {
                // todo: perform \ u escaping.
                return new TransformingWriter(new OutputStreamWriter(os, "UTF-8"), new UnicodeEscaper());
            }
        } catch (UnsupportedEncodingException uee) {
            log.error("uee " + uee);
        }
        return new EncodingDetectingOutputStreamWriter(os);        
    }

    /**
     * Returns a 'Resolver' for a certain resource.
     */

    Resolver getResolver(String name) {
        return new Resolver(findResource(name));
    }

    /**
     * Returns an abstract URL for a resource with given name. findResource(name).toString() would give an 'external' form.
     */
    public String toInternalForm(String name) {
       return toInternalForm(findResource(name));
     
    }

    public static String toInternalForm(URL u) {
        return u.getProtocol() + ":" + u.getPath();
    }

    /**
     * @return A List of all files associated with the resource.
     *         Used by {@link ResourceWatcher}. And by some deprecated code that wants to produce File objects.
     */
    public List getFiles(String name) {
        URL url = findResource(name);
        if (url == null) return new ArrayList();
        return getRootFiles(url.getPath());
    }

    

    protected List getClassLoaderResources(String name) {
        URL url = findResource(name);
        if (url == null) return new ArrayList();
        return getRootResources(url.getPath().substring(1));
    }



    public String toString() {
        return "" + context.getPath()  + " fileroots:" + fileRoots + " resourceroots: " + resourceRoots + " classloaderroots: " + classLoaderRoots;
    }

    public boolean equals(Object o) {
        if (o instanceof ResourceLoader) {
            ResourceLoader rl = (ResourceLoader) o;
            return rl.context.sameFile(context);            
        } else {
            return false;
        }
    }


    /**
     * Resolves these abstract mm:-urls to actual things, like Files, MMObjectNodes and 'external' URL's.
     */
    protected  class Resolver {
        private URL url;

        // try to cache the results a bit.
        private MMObjectNode node = null;
        private File files[] = {null, null, null, null};
        private URL servletContextResource = null;
        private URL classLoaderResource    = null;
        

        private Resolver(URL u) {
            this.url = u;
        }

        /**
         * Gets the Node associated with this URL if there is one.
         * @return MMObjectNode or <code>null</code>
         */
        protected MMObjectNode getResourceNode() {
            if (node != null) return node;
            if (ResourceLoader.resourceBuilder != null) {
                try {
                    NodeSearchQuery query = new NodeSearchQuery(resourceBuilder);
                    StepField urlField = query.getField(resourceBuilder.getField(Resources.RESOURCENAME_FIELD));

                    BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(urlField, url.getPath().substring(1));
                    BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(urlField, url.getPath());
                    BasicCompositeConstraint  constraint  = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_OR);
                    constraint.addChild(constraint1);
                    constraint.addChild(constraint2);
                    query.setConstraint(constraint);
                    Iterator i = resourceBuilder.getNodes(query).iterator();
                    if (i.hasNext()) {
                        node = (MMObjectNode) i.next();
                        return node;
                    }
                } catch (org.mmbase.storage.search.SearchQueryException sqe) {
                }
            }
            return null;
        }

        /**
         * Gets the first File associated with this URL.
         * @param exists    The file must exist (you want to open it for read)
         * @param writeable The file must be writeable (you want to open it for write) The file will be created if not it didn't exists.
         * @return File or <code>null</code> if no file obeying the parameters exists.
         */
        protected File getResourceFile(boolean exists, boolean writeable) {
            int index = (exists ? 0 : 1) + (writeable ? 0 : 2);
            if (files[index] != null) return files[index];
            Iterator i = ResourceLoader.this.getRootFiles(url.getPath()).iterator();
            while (i.hasNext()) {
                File file = (File) i.next();
                if (exists && file.exists()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found file " + file);
                    }
                    if (! writeable || file.canWrite()) {
                        files[index] = file;
                        return file;
                    }
                }
                if (writeable && ! file.exists()) {
                    if (file.getParentFile().canWrite()) {
                        files[index] = file;
                        return file;
                    }
                }
            }
            return null;
        }

        /**
         * Gets a URL from ServletContext (if there is one).
         * @return URL or <code>null</code> if there is no ServletContext, or it does not have this resource.
         */
        protected URL getServletContextResource() {
            if (servletContextResource != null) return servletContextResource;
            if (ResourceLoader.servletContext != null) {
                Iterator resources = ResourceLoader.this.resourceRoots.iterator();
                while (resources.hasNext()) {
                    String root = (String) resources.next();
                    try {
                        URL u  = servletContext.getResource(root + url.getPath());                    
                        if (u != null) {
                            servletContextResource = u;
                            return u;
                        }
                    } catch (MalformedURLException mfue) {
                        assert false : mfue;
                    }
                }
            }
            return null;
            
        }

        /**
         * Gets a URL from ClassLoaders.
         * @return URL or <code>null</code> if there is no such resource according to ClassLoader.
         */
        protected URL getClassLoaderResource() {
            if (classLoaderResource != null) return classLoaderResource;
            Iterator resources  = ResourceLoader.this.classLoaderRoots.iterator();
            while (resources.hasNext()) {
                String root = (String) resources.next();
                URL u = ResourceLoader.class.getResource(root + url.getPath());
                if (u != null) {
                    classLoaderResource = u;
                    return u;
                }
            } 
            return null;
        }

        /**
         * Returns an URL which is associated with a certain resource-node.
         */
        URL getNodeURL(MMObjectNode node) {
            try {
                return new URL(NODE_URL_CONTEXT, "" + node.getNumber());
            } catch (MalformedURLException mfue) {
                assert false : mfue;
                return null;
            }
        }


        /**
         * Determine wether File f is shadowed.
         * @param name Check for resource with this name
         * @param file The file to check for this resource.
         * @return The URL for the shadowing resource, or <code>null</code> if not shadowed.
         * @throws IllegalArgumentException if <code>file</code> is not a file associated with the resource with given name.
         */
        URL shadowed(File f) {
            MMObjectNode node = getResourceNode();
            if (node != null) {
                return getNodeURL(node);
            }
            Iterator i = ResourceLoader.this.getRootFiles(url.getPath()).iterator();
            while (i.hasNext()) {
                File file = (File) i.next();
                if (file.equals(f)) {
                    return null; // ok, not shadowed.
                } else {
                    if (file.exists()) {
                        try {
                            return file.toURL(); // f is shadowed!
                        } catch (MalformedURLException mfue) {
                            assert false : mfue;
                        }
                    }
                }
            }
            // did not find f as a file for this resource
            throw new IllegalArgumentException("File " + f + " is not a file for resource "  + url.getProtocol() + ":" + url.getPath());
        }

        /**
         * Logs warning if 'newer' resources are shadowed by older ones.
         */
        void checkShadowedNewerResources() {
            long lastModified = -1;
            URL  usedUrl = null;
            MMObjectNode node = getResourceNode();
            if (node != null) {
                usedUrl = getNodeURL(node);
                Date lm = node.getDateValue("lastmodified");
                if (lm != null) {                    
                    lastModified = lm.getTime();
                }
            }
            Iterator i = ResourceLoader.this.getRootFiles(url.getPath()).iterator();
            while (i.hasNext()) {
                File file = (File) i.next();
                if (file.exists()) {
                    long lm = file.lastModified();
                    if (lastModified > 0 && lm > lastModified) {
                        log.warn("File " + file + " is newer then " + usedUrl + " but shadowed by it");
                    }
                    if (usedUrl == null) {
                        try {
                            usedUrl = file.toURL();
                            lastModified = lm;
                        } catch (MalformedURLException mfue) {
                            assert false : mfue;
                        }
                    }
                }
            }            
        }

        /**
         * implementation for MMUrlStreamHandler.
         */
        String toExternalForm() {
            {
                MMObjectNode node = getResourceNode();
                if (node != null) {
                    return getNodeURL(node).toExternalForm();
                }
            }
            {
                File file = getResourceFile(true, false);
                if (file != null) {
                    try {
                        return file.toURL().toExternalForm();
                    } catch (MalformedURLException mfue) {
                        return "file:///" + file;
                    }
                }
            } 
            { 
                URL u = getServletContextResource();
                if (u != null) {
                    return u.toExternalForm();
                }
            }
            {
                URL u = getClassLoaderResource();
                if (u != null) {
                    return u.toExternalForm();
                }
            }
            try {
                return new URL("http", "localhost", "/NOTFOUND" + url.getPath()).toExternalForm();
            } catch (MalformedURLException mfue) {
                assert false : mfue;
                return mfue.toString();
            }
            
        }
    }

    /***
     * The MMURLStreamHandler is a StreamHandler for the protocol PROTOCOL. 
     */
    
    protected class MMURLStreamHandler extends URLStreamHandler {

        private URLConnection connection = null;
        MMURLStreamHandler() {
            super();
        }
        protected URLConnection openConnection(URL u) throws IOException {
            return new MMURLConnection(u);
        }
        /**
         * mm: cannot be an external form, so the 'external' form of that will be
         * http://www.mmbase.org/mmbase/config
         *
         * ExternalForms are mainly used in entity-resolving and URL.toString()
         * {@inheritDoc}
         */
        protected String toExternalForm(URL u) {
            Resolver res = new Resolver(u);
            return res.toExternalForm();
        }
    }

    /**
     * Implements the logic for our MM protocol.
     */
    protected class MMURLConnection extends URLConnection {           

        private boolean determinedDoOutput = false;
        private boolean determinedDoInput  = false;
        private Resolver resolver;

        MMURLConnection(URL u) {
            super(u);
            //log.debug("Connection to " + url + Logging.stackTrace(new Throwable()));
            if (! url.getProtocol().equals(PROTOCOL)) {
                throw new RuntimeException("Only supporting URL's with protocol " + PROTOCOL);
            }
            resolver = new Resolver(url);
        }

        /**
         * {@inheritDoc}
         */
        public void connect() throws IOException {
            // don't know..
            connected = true;
        }

        /**
         * Returns <code>true</true> if you can successfully use getInputStream();
         */
        public boolean getDoInput() {
            if (! determinedDoInput) {
                determinedDoInput = true;
                if (resolver.getResourceFile(true, false) != null) {
                    setDoInput(true);
                    return true;
                } 
                if (ResourceLoader.resourceBuilder != null) {
                    if(resolver.getResourceNode() != null) {
                        setDoInput(true);
                        return true;
                    }
                }


                URL u = resolver.getServletContextResource();                    
                if (u != null) {
                    setDoInput(true);
                    return true;
                }
                u = resolver.getClassLoaderResource();
                if (u != null) {
                    setDoInput(true);
                    return true;
                }

                //defaulting to false.
                setDoInput(false);
            }
            return super.getDoInput();
                            
        }
        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream() throws IOException  {
            {
                MMObjectNode node = resolver.getResourceNode();
                if (node != null) {
                    return new ByteArrayInputStream(node.getByteValue(Resources.HANDLE_FIELD));
                }
            }
            {
                File file = resolver.getResourceFile(true, false);
                if (file != null) {
                    return new FileInputStream(file);
                }
            }
            try {
                URL u = resolver.getServletContextResource();
                if (u != null) {
                    return u.openStream();
                }
            } catch (UnknownServiceException use) {
                assert false : use;
            }
            try {
                URL u = resolver.getClassLoaderResource();
                if (u != null) {
                    return u.openStream();
                }
            } catch (UnknownServiceException use) {
                assert false : use;
            }
            return null;
            
        }


        /** 
         * Makes an OutputStream for a Node to fill the handle field.
         */
        protected OutputStream getOutputStream(final MMObjectNode node) {
            return new OutputStream() {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    public void close() throws IOException {
                        byte[] b = bytes.toByteArray();
                        node.setValue(Resources.HANDLE_FIELD, b);
                        String type = guessContentTypeFromStream(new ByteArrayInputStream(b));
                        if (type == null) {
                            guessContentTypeFromName(url.getFile());
                        }
                        if (ResourceLoader.this.typeValues.size() > 0) {
                            node.setValue(Resources.TYPE_FIELD, ResourceLoader.this.typeValues.iterator().next());
                        }
                        node.setValue("mimetype", type);
                        node.commit();
                    }                    
                    public void write(int b) {
                        bytes.write(b);
                    }
                };
        }

        /**
         * Returns <code>true</true> if you can successfully use getOutputStream();
         */
        public boolean getDoOutput() {
            if (! determinedDoOutput) {
                determinedDoOutput = true;
                if (resolver.getResourceFile(false, true) != null) {
                    setDoOutput(true);
                    return true;
                } 
                if (ResourceLoader.resourceBuilder != null) {
                    setDoOutput(true);
                    return true;
                }
                try {
                    URL u = resolver.getServletContextResource();
                    if (u != null && u.openConnection().getDoOutput()) {
                        setDoOutput(true);
                        return true;
                    }
                    u = resolver.getClassLoaderResource();
                    if (u != null && u.openConnection().getDoOutput()) {
                        setDoOutput(true);
                        return true;
                    }
                } catch (Exception e) {
                }
                //defaulting to false.
            }
            return super.getDoOutput();
                            
        }
        /**
         * {@inheritDoc}
         */
        public OutputStream getOutputStream() throws IOException  {
            { // if already a node, change that.
                MMObjectNode node = resolver.getResourceNode();
                if (node != null) { // already a node, change this node
                    return getOutputStream(node);
                }
            }
            { // if already a file, change that
                File file = resolver.getResourceFile(true, true);
                if (file != null) { // already a file, rewrite this file
                    
                    return new FileOutputStream(file);
                }
            } 
            try { // little hope that this would work, but lets try it any way
                URL u = resolver.getServletContextResource();
                if (u != null) {
                    return u.openConnection().getOutputStream();
                }
            } catch (UnknownServiceException use) {
                // will very probably happen, ignore
            }

            try { // little hope that this would work, but lets try it any way
                URL u = resolver.getClassLoaderResource();
                if (u != null) {
                    return u.openConnection().getOutputStream();
                } 
            } catch (UnknownServiceException use) {
                // will very probably happen, ignore
            } 

            // Still not found! We will have to create the resource. (it either does not exist, or
            // is not writeable like this)

            // first, conservatively, try to create a file:
            { 
                File file = resolver.getResourceFile(false, true);
                if (file != null) { // that would succeed!
                    if (! file.exists()) {
                        file.createNewFile();
                    }
                    return new FileOutputStream(file);
                }
            }
            // Could not create file, lets store it in the database then
            if (ResourceLoader.resourceBuilder != null) {
                MMObjectNode node = ResourceLoader.resourceBuilder.getNewNode(Resources.DEFAULT_CONTEXT);
                node.setValue(Resources.RESOURCENAME_FIELD, url.getPath().substring(1)); // minus the starting /
                node.insert(Resources.DEFAULT_CONTEXT);
                return getOutputStream(node);
            }

            // Can find no place to store this resource. Giving up.
            throw new IOException("Cannot create an OutputStream for " + url + "( " + resolver.getResourceFile(false, false) + " cannot be written, no resource-node could be created)");
        }

    }

    /**
     * For testing purposes only
     */
    public static void main(String[] argv) {
        ResourceLoader resourceLoader = getConfigurationRoot();
        try {
            if (argv.length == 0) {
                System.err.println("useage: java [-Dmmbase.config=<config dir>] " + ResourceLoader.class.getName() + " [<sub directory>] <resource-name>");
                return;
            }
            String arg = argv[0];
            if (argv.length > 1) {
                resourceLoader = new ResourceLoader(argv[0]);
                arg = argv[1];
            } 
            InputStream resource = resourceLoader.getResourceAsStream(arg);
            if (resource == null) {
                System.out.println("No such resource " + arg + " for " + resourceLoader.findResource(arg) + ". Creating now.");                
                PrintWriter writer = new PrintWriter(resourceLoader.createResourceAsStream(arg));
                writer.println("TEST");
                writer.close();
                return;
            }
            System.out.println("-------------------- resolved " + arg + " with " + resourceLoader + ": ");

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            
            while(true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println(line);
            }
        } catch (Exception mfeu) {
            System.err.println(mfeu.getMessage() + Logging.stackTrace(mfeu));
        }
    }

    
}

/**
 * Like {@link java.io.OutputStreamWriter} but it tries to autodetect the encoding of the
 * OutputStream. This works at least if the OutputStream is XML, which is a very common thing to be for Resources.
 *
 * For this to work at least the first part (e.g. the first 100 bytes) need to be buffered. 
 * 
 * If determining the encoding did not succeed it is supposed to be 'UTF-8', which is (should be) an
 * acceptable encoding, and also the default encoding for XML streams.
 */
class EncodingDetectingOutputStreamWriter extends Writer {
    
    private OutputStream outputStream;

    // Either wrapped or buffer is null, and the other one is currenlty in use.
    private Writer wrapped = null;
    private StringBuffer buffer = new StringBuffer(100);

    EncodingDetectingOutputStreamWriter(OutputStream os) {
        outputStream = os;
    }

    /**
     * Stop buffering, determine encoding, and start behaving as a normal OutputStreamWriter (by
     * wrapping one). Unless, this happened already.
     */
    private void wrap() throws IOException {
        if (wrapped == null) {
            String encoding = GenericResponseWrapper.getXMLEncoding(buffer.toString());
            if (encoding == null) {
                encoding = "UTF-8";
            }
            try {
                wrapped = new OutputStreamWriter(outputStream, encoding);
            } catch (UnsupportedEncodingException uee) {
            }
            wrapped.write(buffer.toString());
            buffer = null;
        }
    }
    public void close() throws IOException {
        wrap();
        wrapped.close();                    
    }
    public void flush() throws IOException {
        wrap();
        wrapped.flush();
    }
    
    public void write(char[] cbuf) throws IOException {
        if (wrapped != null) {
            wrapped.write(cbuf);
        } else {
            write(cbuf, 0, cbuf.length);
        } 
    }
    
    public void write(int c) throws IOException {
        if (wrapped != null) { wrapped.write(c); } else { super.write(c); }
    }
    
    public void write(String str) throws IOException {
        if (wrapped != null) { wrapped.write(str); } else { super.write(str); }
    }
    
    public void write(String str, int off, int len) throws IOException {
        if (wrapped != null) { wrapped.write(str, off, len); } else { super.write(str, off, len); }
        
    }
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (wrapped != null) {
            wrapped.write(cbuf, off, len);
        } else {
            for (int i = off; i < len + off; i++) {
                buffer.append(cbuf[i]);
                if (buffer.length() == 100) {
                    wrap();
                    i++;
                    if (i < len) {
                        wrapped.write(cbuf, i, len - (i - off));
                    }
                    break;
                }
            }
        }
    }
}
