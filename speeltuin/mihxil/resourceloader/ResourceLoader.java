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
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

// XML stuff
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.transform.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * MMBase resource loader, for loading config-files and those kind of things. It knows about MMBase config file locations.
 *
 * I read <a href="http://www.javaworld.com/javaqa/2003-08/02-qa-0822-urls.html">http://www.javaworld.com/javaqa/2003-08/02-qa-0822-urls.html</a>.
 * 
 * Programmers should do something like this if they need a configuration file:
<pre>
InputStream configStream = ResourceLoader.getRoot().getResourceAsStream("modules/myconfiguration.xml");
</pre>
or
<pre>
InputSource config = ResourceLoader.getRoot().getInputSource("modules/myconfiguration.xml");
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
 * Impact: 
 *    URIResolver uses files, must depend on this too. --> FormatterTag, Editwizards!
 *    MMBase.java, MMBaseContext.java, security, logging
 *    IncludeTag#cite
 *    MMAdmin.java 
 *    Not yet implemented: use of getOutputStream, which may be possible
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ResourceLoader.java,v 1.2 2004-09-30 07:56:18 michiel Exp $
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
     * Used when using getResourcePaths for normal class-loaders.
     */
    protected static final String INDEX            = "INDEX";

    private static final MMURLStreamHandler mmStreamHandler = new MMURLStreamHandler();

    private static final ResourceLoader root = new ResourceLoader();

    /**
     * Creates a new URL object, which is used to load resources. First a normal java.net.URL is
     * instantiated, if that fails, we check for the 'mmbase' protocol. If so, a URL is instantiated
     * with a URLStreamHandler which can handle that. 
     * 
     * If that too fails, it should actually already be a MalformedURLException, but we try
     * supposing it is some existing file and return a file: URL. If no such file, only then a
     * MalformedURLException is thrown.
     */
    protected static URL newURL(String url) throws MalformedURLException {
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

    private static ServletContext servletContext = null;

    private static MMObjectBuilder resourceBuilder = null;

    // these should perhaps be configurable:
    private static final String    URL_FIELD        = "url";
    private static final String    HANDLE_FIELD     = "handle";
    private static final String    DEFAULT_CONTEXT  = "admin";

    // these could perhaps be made non-static to make more generic ResourceLoaders possible

    private static List /* <File> */   fileRoots         = new ArrayList();
    private static List /* <String> */ resourceRoots     = new ArrayList();
    private static List /* <String> */ classLoaderRoots  = new ArrayList();


    static {
        // make sure it works a bit before servlet-startup.
        init(null);
    }



    /**
     * Initializes the Resourceloader using a servlet-context (makes e.g. resolving relatively to WEB-INF/config possible).
     * @param servletContext The ServletContext used for determining the mmbase configuration directory. Or <code>null</code>.
     */
    public static void init(ServletContext sc) {
        servletContext = sc;
        fileRoots.clear();
        resourceRoots.clear();
        classLoaderRoots.clear();

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
            fileRoots.add(new File(configPath));
        }

        if (servletContext != null) {
            String s = servletContext.getRealPath(RESOURCE_ROOT);
            if (s != null) {
                fileRoots.add(new File(s));
            }
            s = servletContext.getRealPath("/WEB-INF/classes" + CLASSLOADER_ROOT); // prefer opening as a files.
            if (s != null) {
                fileRoots.add(new File(s));
            }
            resourceRoots.add(RESOURCE_ROOT);
        }

        if (fileRoots.size() == 0) {
            File [] roots = File.listRoots();
            fileRoots.addAll(Arrays.asList(roots));
        }

        classLoaderRoots.add(CLASSLOADER_ROOT);

    }

    /**
     * Sets the MMBase builder which must be used for resource.
     * The builder must have an URL and a HANDLE field.
     * This method can be called only once.
     * @param b An MMObjectBuilder.
     * @throws RuntimeException if builder was set already.
     */
    public static void setResourceBuilder(MMObjectBuilder b) {
        if (resourceBuilder != null) {
            throw new RuntimeException("An resource builder was set already: " + resourceBuilder);
        }
        resourceBuilder = b;
    }

    
    /** 
     * Utility method to return the name part of a resouce-name (removed directory and 'extension'). 
     * Used e.g. when loading builders in MMBase.
     */
    public static String getName(String path) {       
        int i = path.lastIndexOf("/");
        path = path.substring(i + 1);

        i = path.lastIndexOf(".");
        if (i > 0) {
            path = path.substring(0, i);
        }
        return path;        
    }

    /*
     * Utility method to returns the 'directory' part of a resouce-name.
     * Used e.g. when loading builders in MMBase.
     */
    public static String getDirectory(String path) {
        int i = path.lastIndexOf("/");
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
    protected static List getRootFiles(final String path) {
        return new AbstractList() {
                public int size()            { return fileRoots.size(); }
                public Object  get(int i)    { return new File((File) fileRoots.get(i), path); }
            };
    }

    /**
     * Returns collection of Strings for resourceRoots (relative to root)
     * path must not start with /.
     */

    protected static List getRootResources(final String path) {
        return new AbstractList() {
                public int size()            { return resourceRoots.size(); }
                public Object  get(int i)    { return resourceRoots.get(i) + "/" + path; }
            };
    }



    /**
     * The one ResourceLoader which loads from the mmbase config root is static, and can be obtained with this method
     */
    public static ResourceLoader getRoot() {
        return root;
        
    }

    /**
     * The URL relative to which this class-loader resolves. Cannot be <code>null</code>.
     */
    private URL context;


    /**
     * This constructor instantiates the root resource-loader. There is only one such ResourceLoader
     * (acquirable with {@link #getRoot}) so this constructor is private.
     */
    private ResourceLoader() {
        super();
        try {
            context = newURL(PROTOCOL + ":/");
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }

    /**
     * Instantiates a new ResourceLoader relative to the root ResourceLoader.
     */
    public ResourceLoader(final String context)  {
        this(getRoot(), context);
    }


    /** 
     * Instantiates a ResourceLoader for a 'sub directory' of given ResourceLoader
     */
    public ResourceLoader(final ResourceLoader cl, final String context)  {
        super();
        this.context = cl.findResource(context + "/");
    }

    
    public ResourceLoader(final URL context)  {
        super();
        this.context = context;
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
    public static final Pattern XML_PATTERN = Pattern.compile(".*\\.xml");

    /**
     * Returns a set of 'sub resources' (read: 'files in the same directory'), which can succesfully be be loaded by the ResourceLoader.
     *
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     * @return A Set of Strings which can be successfully loaded with the resourceloader.
     */
    public Set getResourcePaths(final Pattern pattern, final boolean recursive) {
        FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    File f = new File(dir, name);
                    return pattern == null || (f.isDirectory() && recursive) || pattern.matcher(f.toString()).matches();
                }
            };
        Set results = new LinkedHashSet(); // a set with fixed iteration order
        getNodeResourcePaths(pattern, recursive, results);
        getFileResourcePaths(filter, recursive ? "" : null, results);
        getServletContextResourcePaths(pattern, recursive ? "" : null, results);
        getClassLoaderResourcePaths(pattern, results);
        return results;
    }

    
    /**
     * The set of {@link #getResourcePaths(Pattern, boolean)} is merged with all entries of the
     * resource with the given index, which is a simply list of resources. 
     *
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     * @param index     An index of resources, if this index cannot be loaded, it will be ignored.
     * @return A Set of Strings which can be successfully loaded with the resourceloader.
     */
    protected Set getClassLoaderResourcePaths(final Pattern pattern, Set results) {
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
     * recursion.
     */
    protected Set getFileResourcePaths(FilenameFilter filter,  String recursive, Set results) {
        Iterator i = getFiles(recursive == null ? "" : recursive).iterator();
        while (i.hasNext()) {
            File f = (File) i.next();
            if (f.isDirectory()) { // should always be true
                File [] files = f.listFiles(filter);
                for (int j = 0; j < files.length; j++) {
                    if (recursive != null && files[j].isDirectory()) {
                        getFileResourcePaths(filter, recursive + files[j].getName() + "/", results);
                    } else {
                        results.add(recursive + files[j].getName());
                    }
                }
            }
        }

        return results;
    }

    protected Set getNodeResourcePaths(final Pattern pattern, boolean recursive, Set results) {
        if (resourceBuilder != null) {
            try {
                NodeSearchQuery query = new NodeSearchQuery(resourceBuilder);
                BasicFieldValueConstraint constraint = 
                    new BasicFieldValueConstraint(query.getField(resourceBuilder.getField(URL_FIELD)), context.getPath().substring(1) + "%");
                constraint.setOperator(FieldCompareConstraint.LIKE);
                query.setConstraint(constraint);
                Iterator i = resourceBuilder.getNodes(query).iterator();
                while (i.hasNext()) {
                    MMObjectNode node = (MMObjectNode) i.next();
                    String url = node.getStringValue(URL_FIELD);
                    String subUrl = url.substring(context.getPath().length());
                    if (! recursive && subUrl.indexOf("/") >0) {
                        continue;
                    }
                    if (pattern != null && ! pattern.matcher(subUrl).matches()) {
                        continue;
                    }
                    results.add(url);
                }
            } catch (SearchQueryException sqe) {
                log.warn(sqe);
            }
        }
        return results;
    }

    /**
     * Recursing for {@link javax.servlet.ServletContext#getResourcePaths}
     */
    protected Set getServletContextResourcePaths(Pattern pattern,  String recursive, Set results) {
        if (servletContext != null) {
            try {
                Iterator i = getClassLoaderResources(recursive == null ? "" : recursive).iterator();
                while (i.hasNext()) {
                    String resourcePath = ((String) i.next());
                    String currentRoot  = resourcePath.substring(0, resourcePath.length() - (recursive == null ? 0 : recursive.length()));
                    Iterator j = servletContext.getResourcePaths(resourcePath).iterator();
                    while (j.hasNext()) {
                        String newResourcePath = ((String) j.next()).substring(currentRoot.length());
                        if (newResourcePath.endsWith("/")) {   
                            // subdirs
                            if (recursive != null) {                            
                                getServletContextResourcePaths(pattern, newResourcePath.substring(0, newResourcePath.length() - 1), results);
                            } else {
                                // ignore
                            }
                        } else {
                            if (pattern.matcher(newResourcePath).matches()) {
                                results.add(newResourcePath);
                            }
                        }
                    }
                }
            } catch (Throwable t) { //hopefully this catches errors from app-server which dont' or badly support servlet api 2.3's getResourcePaths
                log.error(t);
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
     * @throws IOException If the Resource for some reason could not be created.
     */
    public OutputStream createResourceAsStream(String name) throws IOException {
        return findResource(name).openConnection().getOutputStream();
    }

    /**
     * Returns the givens resource as a InputSource (XML streams). ResourceLoader is often used for
     * XML.
     * The System ID is set, otherwise you could as wel do new InputSource(r.getResourceAsStream());
     * @param name The name of the resource to be loaded
     * @return The InputSource if succesfull, <code>null</code> otherwise.
     */
    public InputSource getInputSource(String name)  {
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
        } catch (IOException ieo) {
            log.error(ieo);
            return null;
        }
    }

    public Document getDocument(String name) {
        InputSource source = getInputSource(name);
        if (source == null) return null;
        try {
            XMLEntityResolver resolver = new XMLEntityResolver(true, null);
            DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(true, null/* no error handler */, resolver);
            if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
            if (log.isDebugEnabled()) log.debug("Reading " + source.getSystemId());
            return  dbuilder.parse(source);
        } catch(org.xml.sax.SAXException se) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + Logging.stackTrace(se));
        } catch(java.io.IOException ioe) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + ioe, ioe);
        }
    }

    /**
     * Store XML.
     */
    public StreamResult getStreamResult(String name)  throws IOException {
        OutputStream stream = createResourceAsStream(name);
        StreamResult streamResult = new StreamResult(stream);
        return streamResult;
    }

    public void storeSource(Source source, String name) throws IOException {
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

    public void  storeDocument(Document doc, String name) throws IOException {
        storeSource(new DOMSource(doc), name);
    }


    /**
     * @return A List of all files associated with the resource.
     *         Quick hack, until ResourceWatcher works.
     */
    public List getFiles(String name) {
        URL url = findResource(name);
        return getRootFiles(url.getPath());
    }

    protected List getClassLoaderResources(String name) {
        URL url = findResource(name);
        return getRootResources(url.getPath().substring(1));
    }



    public String toString() {
        return "" + context  + " fileroots:" + fileRoots + " resourceroots: " + resourceRoots + " classloaderroots: " + classLoaderRoots;
    }



    /***
     * The MMURLStreamHandler is a StreamHandler for the protocol PROTOCOL. 
     */
    
    protected static class MMURLStreamHandler extends URLStreamHandler {

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
         * ExternalForms are mainly used in entity-resolving.
         * {@inheritDoc}
         */
        protected String toExternalForm(URL u) {
            MMURLConnection con = new MMURLConnection(u);
            return con.toExternalForm();
        }
    }

    /**
     * Implements the logic for our MM protocol.
     */
    protected static class MMURLConnection extends URLConnection {           

        private URL url;

        MMURLConnection(URL url) {
            super(url);
            //log.debug("Connection to " + url + Logging.stackTrace(new Throwable()));
            if (! url.getProtocol().equals(PROTOCOL)) {
                throw new RuntimeException("Only supporting URL's with protocol " + PROTOCOL);
            }
            this.url = url;
        }

        /**
         * {@inheritDoc}
         */
        public void connect() throws IOException {
            // don't know..
            connected = true;
        }


        /**
         * Gets the Node associated with this URL if there is one.
         * @return MMObjectNode or <code>null</code>
         */
        protected MMObjectNode getResourceNode() {
            if (ResourceLoader.resourceBuilder != null) {
                try {
                    NodeSearchQuery query = new NodeSearchQuery(resourceBuilder);
                    BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(query.getField(resourceBuilder.getField(URL_FIELD)), url.getPath().substring(1));
                    query.setConstraint(constraint);
                    Iterator i = resourceBuilder.getNodes(query).iterator();
                    if (i.hasNext()) {
                        MMObjectNode node = (MMObjectNode) i.next();
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
         * @return File or <code>null</code>
         */
        protected File getResourceFile(boolean exists, boolean writeable) {
            Iterator files = ResourceLoader.getRootFiles(url.getPath()).iterator();
            while (files.hasNext()) {
                File file = (File) files.next();
                if (exists && file.exists()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found file " + file);
                    }
                    if (! writeable || file.canWrite()) {
                        return file;
                    }
                }
                if (writeable && ! file.exists()) {
                    try {
                        if (file.createNewFile()) {
                            return file;
                        }
                    } catch (IOException ioe) {
                    }
                }
            }
            return null;
        }

        /**
         * Gets a URL from ServletContext (if there is one).
         * @return URL or <code>null</code> if there is not ServletContext, or it does not have this resource.
         */
        protected URL getServletContextResource() {
            if (ResourceLoader.servletContext != null) {
                Iterator resources = ResourceLoader.resourceRoots.iterator();
                while (resources.hasNext()) {
                    String root = (String) resources.next();
                    try {
                        URL u  = servletContext.getResource(root + url.getPath());                    
                        if (u != null) {
                            return u;
                        }
                    } catch (MalformedURLException mfue) {
                        // should not happen
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
            Iterator resources  = ResourceLoader.classLoaderRoots.iterator();
            while (resources.hasNext()) {
                String root = (String) resources.next();
                URL u = ResourceLoader.class.getResource(root + url.getPath());
                if (u != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found resource " + root + url.getPath());
                    }
                    return u;
                }
            } 
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream() throws IOException  {
            {
                MMObjectNode node = getResourceNode();
                if (node != null) {
                    return new ByteArrayInputStream(node.getByteValue(HANDLE_FIELD));
                }
            }
            {
                File file = getResourceFile(true, false);
                if (file != null) {
                    return new FileInputStream(file);
                }
            }
            try {
                URL u = getServletContextResource();
                if (u != null) {
                    return u.openStream();
                }
            } catch (UnknownServiceException use) {
                // should not happen
                log.warn("" + use.getMessage());
            }
            try {
                URL u = getClassLoaderResource();
                if (u != null) {
                    return u.openStream();
                }
            } catch (UnknownServiceException use) {
                // should not happen
                log.warn("" + use.getMessage());
            }
            return null;
            
        }

        /**
         * implementation for MMUrlStreamHandler.
         */
        String toExternalForm() {
            {
                MMObjectNode node = getResourceNode();
                if (node != null) {
                    return "http://localhost/node/" + node.getNumber();
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
                        node.setValue(HANDLE_FIELD, b);
                        String type = guessContentTypeFromStream(new ByteArrayInputStream(b));
                        if (type == null) {
                            guessContentTypeFromName(url.getFile());
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
         * {@inheritDoc}
         */
        public OutputStream getOutputStream() throws IOException  {
            { // if already a node, change that.
                MMObjectNode node = getResourceNode();
                if (node != null) { // already a node, change this node
                    return getOutputStream(node);
                }
            }
            { // if already a file, change that
                File file = getResourceFile(true, true);
                if (file != null) { // already a file, rewrite this file
                    return new FileOutputStream(file);
                }
            } 
            try { // little hope that this would work, but lets try it any way
                URL u = getServletContextResource();
                if (u != null) {
                    return u.openConnection().getOutputStream();
                }
            } catch (UnknownServiceException use) {
                // will very probably happen, ignore
            }

            try { // little hope that this would work, but lets try it any way
                URL u = getClassLoaderResource();
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
                File file = getResourceFile(false, true);
                if (file != null) { // that would succeed!
                    return new FileOutputStream(file);
                }
            }
            // Could not create file, lets store it in the database then
            if (ResourceLoader.resourceBuilder != null) {
                MMObjectNode node = ResourceLoader.resourceBuilder.getNewNode(DEFAULT_CONTEXT);
                node.setValue(URL_FIELD, url.getPath().substring(1)); // minus the starting /
                node.insert(DEFAULT_CONTEXT);
                return getOutputStream(node);
            }

            // Can find no place to store this resource. Giving up.
            throw new IOException("Cannot create an OutputStream for " + url + "( " + getResourceFile(false, false) + " cannot be written, no resource-node could be created)");
        }

    }

    /**
     * For testing purposes only
     */
    public static void main(String[] argv) {
        ResourceLoader resourceLoader = getRoot();
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

