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
 * <p>Resource can  programmaticly created or changed by the use of {@link #createResourceAsStream}, or something like {@link #getWriter}.</p>
 *
 * <p>If you want to check beforehand if a resource can be changed, then something like <code>resourceLoader.getResource().openConnection().getDoOutput()</code> can be used.</p>
 * <p>That is also valid if you want to check for existance. <code>resourceLoader.getResource().openConnection().getDoInput()</code>.</p>
 * <p>If you want to remove a resource, you must write <code>null</code> to all URL's returned by {@link #findResources} (Do for every URL:<code>url.openConnection().getOutputStream().write(null);</code>)</p>
 * <h3>Encodings</h3>
 * <p>ResourceLoader is well aware of encodings. You can open XML's as Reader, and this will be done using the encoding specified in the XML itself. When saving an XML using a Writer, this will also be done using the encoding specified in the XML.</p>
 * <p>For property-files, the java-unicode-escaping is undone on loading, and applied on saving, so there is no need to think of that.</p>
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ResourceLoader.java,v 1.22 2004-11-03 17:41:59 michiel Exp $
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
    public static final URL NODE_URL_CONTEXT;
    static {
        URL temp = null;
        try {
            temp = new URL("http", "localhost", "/node/");
        } catch (MalformedURLException mfue) {
            assert false : mfue;
        }
        NODE_URL_CONTEXT = temp;
    }


    /**
     * Used when using getResourcePaths for normal class-loaders.
     */
    protected static final String INDEX            = "INDEX";

    private static  ResourceLoader configRoot = null;
    private static  ResourceLoader webRoot = null;
    private static ServletContext  servletContext = null;


    static MMObjectBuilder resourceBuilder = null;


    /**
     * The URLStreamHandler for 'mm' URL's.
     */
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


    private List /* <ResolverFactory> */ roots;


    static {
        // make sure it works a bit before servlet-startup.
        init(null);
    }



    /**
     * Initializes the Resourceloader using a servlet-context (makes resolving relatively to WEB-INF/config possible).
     * @param sc The ServletContext used for determining the mmbase configuration directory. Or <code>null</code>.
     */
    public static  void init(ServletContext sc) {
        servletContext = sc;
        // reset both roots, they will be redetermined using servletContext.
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
     * Utility method to return the 'directory' part of a resource-name.
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
     * The one ResourceLoader which loads from the mmbase config root is static, and can be obtained with this method
     */
    public static ResourceLoader getConfigurationRoot() {
        if (configRoot == null) {
            configRoot = new ResourceLoader();

            configRoot.roots.add(configRoot.new NodeURLStreamHandler(Resources.TYPE_CONFIG));

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
                configRoot.roots.add(configRoot.new FileURLStreamHandler(new File(configPath), true));
            }

            if (servletContext != null) {
                String s = servletContext.getRealPath(RESOURCE_ROOT);
                if (s != null) {
                    configRoot.roots.add(configRoot.new FileURLStreamHandler(new File(s), true));
                }
                configRoot.roots.add(configRoot.new ServletResourceURLStreamHandler(RESOURCE_ROOT));
            }

            if (servletContext != null) {
                String s = servletContext.getRealPath("/WEB-INF/classes" + CLASSLOADER_ROOT); // prefer opening as a files.
                if (s != null) {
                    configRoot.roots.add(configRoot.new FileURLStreamHandler(new File(s), false));
                }
            }

            configRoot.roots.add(configRoot.new ClassLoaderURLStreamHandler(CLASSLOADER_ROOT));

        }
        return configRoot;
    }



    /**
     * The one ResourceLoader which loads from the mmbase web root is static, and can be obtained with this method
     */
    public static ResourceLoader getWebRoot() {
        if (webRoot == null) {
            webRoot = new ResourceLoader();

            //webRoot.roots.add(webRoot.new NodeURLStreamHandler(Resource.TYPE_WEB));


            String htmlRoot = null;
            if (servletContext != null) {
                htmlRoot = servletContext.getInitParameter("mmbase.htmlroot");
            }

            if (htmlRoot == null) {
                htmlRoot = System.getProperty("mmbase.htmlroot");
            }
            if (htmlRoot != null) {
                webRoot.roots.add(webRoot.new FileURLStreamHandler(new File(htmlRoot), true));
            }

            if (servletContext != null) {
                String s = servletContext.getRealPath("/");
                if (s != null) {
                    webRoot.roots.add(webRoot.new FileURLStreamHandler(new File(s), true));
                }
                webRoot.roots.add(webRoot.new ServletResourceURLStreamHandler("/"));
            }
        }

        return webRoot;
    }


    /**
     * The URL relative to which this class-loader resolves. Cannot be <code>null</code>.
     */
    private URL context;


    /**
     * Child resourceloaders have a parent.
     */
    private ResourceLoader parent = null;

    /**
     * This constructor instantiates a new root resource-loader. This constructor is protected (so you may use it in an extension), but normally use:
     * {@link #getConfigurationRoot} or {@link #getWebRoot}.
     */
    protected ResourceLoader() {
        super();
        roots        = new ArrayList();
        try {
            context = newURL(PROTOCOL + ":/");
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }



    /**
     * Instantiates a ResourceLoader for a 'sub directory' of given ResourceLoader. Used by {@link #getChildResourceLoader}.
     */
    protected  ResourceLoader(final ResourceLoader cl, final String context)  {
        super(ResourceLoader.class.getClassLoader());
        this.context = cl.findResource(context + "/");
        roots   = new ArrayList();
        Iterator i = cl.roots.iterator();
        // hmm, don't like this code, but don't know how else to copy the inner object.
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof FileURLStreamHandler) {
                roots.add(new FileURLStreamHandler((FileURLStreamHandler) o));
            } else if (o instanceof NodeURLStreamHandler) {
                roots.add(new NodeURLStreamHandler((NodeURLStreamHandler) o));
            } else if (o instanceof ServletResourceURLStreamHandler) {
                roots.add(new ServletResourceURLStreamHandler((ServletResourceURLStreamHandler) o));
            } else if (o instanceof ClassLoaderURLStreamHandler) {
                roots.add(new ClassLoaderURLStreamHandler((ClassLoaderURLStreamHandler) o));
            } else {
                assert false;
            }
        }
        parent  = cl;
    }



    /**
     * If name starts with '/' or 'mm:/' the 'parent' resourceloader is used.
     *
     * Otherwise the name is resolved relatively. (For the root ResourceLoader that it the same as starting with /)
     *
     * {@inheritDoc}
     */
    public URL findResource(final String name) {
        try {
            if (name.startsWith("/")) {
                return newURL(PROTOCOL + ":" + name);
            } else if (name.startsWith(PROTOCOL + ":")) {
                return newURL(name);
            } else {
                return new URL(context, name);
            }
        } catch (MalformedURLException mfue) {
            log.info(mfue + Logging.stackTrace(mfue));
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * @see #findResourceList
     */
    public Enumeration findResources(final String name) {
        return Collections.enumeration(findResourceList(name));
    }

    /**
     * Returns a List, containing all URL's which may present the
     * given resource. This can be used to show what happens.
     */
    public List findResourceList(final String name) {
        List list = new ArrayList();
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            try {
                PathURLStreamHandler sh = (PathURLStreamHandler) i.next();
                URLConnection uc = sh.openConnection(name);
                if (uc.getDoInput() || uc.getDoOutput()) { // if not at least readable or writeable it is extremely uninteresting.
                    list.add(uc.getURL());
                }
            } catch (Exception e) {
                log.warn(e);
            }

        }
        return list;
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
     * Returns the 'parent' ResourceLoader. Or <code>null</code> if this ClassLoader has no parent. You can create a ResourceLoader with a parent by
     * {@link #getChildResourceLoader(String)}.
     */
    public ResourceLoader getParentResourceLoader() {
        return parent;
    }

    /**
     * Returns a 'child' ResourceLoader, or a parent if the context is "..".
     * the {@link #ResourceLoader(ResourceLoader, String)} constructor.
     */
    public ResourceLoader getChildResourceLoader(String context) {
        if (context.equals("..")) { // should be made a bit smarter, (also recognizing "../..", "/" and those kind of things).
            return getParentResourceLoader();
        }
        return new ResourceLoader(this, context);
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
     * (see {@link #getChildResourceLoader(String)}).
     * @param pattern   A Regular expression pattern to which  the file-name must match, or <code>null</code> if no restrictions apply
     * @param recursive If true, then also subdirectories are searched.
     */
    public Set getChildContexts(final Pattern pattern, final boolean recursive) {
        return getResourcePaths(pattern, recursive, true);
    }

    /**
     * Used by {@link #getResourcePaths(Pattern, boolean)} and {@link #getResourceContexts(Pattern, boolean)}
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
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            PathURLStreamHandler cf = (PathURLStreamHandler) i.next();
            cf.getPaths(results, pattern, recursive, directories);
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
     * (like e.g. XML's or property-files), so this method may be useful.
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
                // performs \ u escaping.
                return new TransformingWriter(new OutputStreamWriter(os, "UTF-8"), new UnicodeEscaper());
            }
        } catch (UnsupportedEncodingException uee) {
            log.error("uee " + uee);
        }
        return new EncodingDetectingOutputStreamWriter(os);
    }

    /**
     * Returns an abstract URL for a resource with given name, <code>findResource(name).toString()</code> would give an 'external' form.
     */
    public String toInternalForm(String name) {
       return toInternalForm(findResource(name));

    }

    public static String toInternalForm(URL u) {
        return u.getProtocol() + ":" + u.getPath();
    }

    /**
     * Used by {@link ResourceWatcher}. And by some deprecated code that wants to produce File objects.
     * @return A List of all files associated with the resource.
     */
    public List getFiles(String name) {

        List result = new ArrayList();
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof FileURLStreamHandler) {
                result.add(((FileURLStreamHandler) o).getFile(name));
            }
        }
        return result;

    }


    /**
     * @return A Node associated with the resource.
     *         Used by {@link ResourceWatcher}.
     */
    MMObjectNode getResourceNode(String name) {
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof NodeURLStreamHandler) {
                return ((NodeConnection) (((PathURLStreamHandler) o).openConnection(name))).getResourceNode();
            }
        }
        return null;
    }

    /**
     * Logs warning if 'newer' resources are shadowed by older ones.
     */

    void checkShadowedNewerResources(String name) {
        long lastModified = -1;
        URL  usedUrl = null;

        Iterator i = roots.iterator();
        while (i.hasNext()) {
            PathURLStreamHandler cf = (PathURLStreamHandler) i.next();
            URLConnection con = cf.openConnection(name);
            if (con.getDoInput()) {
                long lm = con.getLastModified();
                if (lm  > 0 && usedUrl != null  && lastModified > 0 && lm > lastModified) {
                    log.warn("File " + con.getURL() + " is newer (" + new Date(lm) + " then " + usedUrl + "(" + new Date(lastModified) + ") but shadowed by it");
                }
                if (usedUrl == null && lm > 0) {
                    usedUrl = con.getURL();
                    lastModified = lm;
                }
            }
        }
    }

    /**
     * Determine wether File f is shadowed.
     * @param name Check for resource with this name
     * @param file The file to check for this resource.
     * @return The URL for the shadowing resource, or <code>null</code> if not shadowed.
     * @throws IllegalArgumentException if <code>file</code> is not a file associated with the resource with given name.
     */
    URL shadowed(File f, String name) {
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            PathURLStreamHandler cf = (PathURLStreamHandler) i.next();
            if (cf instanceof NodeURLStreamHandler) {
                URLConnection con = cf.openConnection(name);
                if (con.getDoInput()) {
                    return con.getURL();
                }
            } else if (cf instanceof FileURLStreamHandler) {
                FileConnection con = (FileConnection) cf.openConnection(name);
                File file = con.getFile();
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
        }
        // did not find f as a file for this resource
        throw new IllegalArgumentException("File " + f + " is not a file for resource "  + name);
    }



    public String toString() {
        return "" + context.getPath() + " in "  + roots;
    }

    public boolean equals(Object o) {
        if(this == o) { // if same object, true!
            return true;
        }
        if (parent == null) { // if this is a 'root' loader, then the only equal object should be the object itself!
            return false;
        }
        if (o instanceof ResourceLoader) {
            ResourceLoader rl = (ResourceLoader) o;
            return rl.parent == parent && rl.context.sameFile(context);
        } else {
            return false;
        }
    }


    /**
     * ================================================================================
     * INNER CLASSES, all private, protected
     * ================================================================================
     */


    /**
     * Extension URLStreamHandler, used for the 'sub' Handlers, entries of 'roots' in ResourceLoader are of this type.
     */
    protected abstract class PathURLStreamHandler extends URLStreamHandler {
        /**
         * We need an openConnection by name only, and public.
         */
        abstract public URLConnection openConnection(String name);

        /**
         * When a URL has been created, in {@link #openConnection(String)}, this method can make a 'name' of it again.
         */
        abstract protected String getName(URL u);

        protected URLConnection openConnection(URL u) throws IOException {
            return openConnection(getName(u));
        }

        /**
         *
         */
        abstract Set getPaths(Set results, Pattern pattern,  boolean recursive,  boolean directories);
    }


    protected  class FileURLStreamHandler extends PathURLStreamHandler {
        private File fileRoot;
        private boolean writeable;
        FileURLStreamHandler(File root, boolean w) {
            fileRoot = root;
            writeable = w;
            
        }
        FileURLStreamHandler(FileURLStreamHandler f) {
            fileRoot  = f.fileRoot;
            writeable = f.writeable;
        }

        public File getFile(String name) {
            String fileName = fileRoot + ResourceLoader.this.context.getPath() + name;
            if (! File.separator.equals("/")) { // windows compatibility
                fileName = fileName.replace('/', File.separator.charAt(0)); // er
            }
            return new File(fileName);
        }
        public String getName(URL u) {
            int l = (fileRoot + ResourceLoader.this.context.getPath()).length();
            String path = u.getPath();
            return l < path.length() ? path.substring(l) : path;
        }
        public URLConnection openConnection(String name)  {
            URL u;
            try {
                u = new URL(null, "file:" + getFile(name), this);
            } catch (MalformedURLException mfue) {
                throw new AssertionError(mfue.getMessage());
            }
            return new FileConnection(u, getFile(name), writeable);
        }
        public Set getPaths(final Set results, final Pattern pattern,  final boolean recursive, final boolean directories) {
            return getPaths(results, pattern, recursive ? "" : null, directories);
        }
        private  Set getPaths(final Set results, final Pattern pattern,  final String recursive, final boolean directories) {
            FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        File f = new File(dir, name);
                        return pattern == null || (f.isDirectory() && recursive != null) || pattern.matcher(f.toString()).matches();
                    }
                };
            File f = getFile(ResourceLoader.this.context.getPath());
            if (recursive != null) {
                f =  new File(f, recursive);
            }

            if (f.isDirectory()) { // should always be true
                File [] files = f.listFiles(filter);
                if (files == null) return results;
                for (int j = 0; j < files.length; j++) {
                    if (files[j].getName().equals("")) continue;
                    if (recursive != null && files[j].isDirectory()) {
                        getPaths(results, pattern, recursive + files[j].getName() + "/", directories);
                    }
                    if (files[j].canRead() && (directories == files[j].isDirectory())) {
                        results.add((recursive == null ? "" : recursive) + files[j].getName());
                    }

                }
            }

            return results;
        }
        public String toString() {
            return fileRoot.toString();
        }


    }


    /**
     * A URLConnection for connecting to a File.  Of course SUN ships an implementation as well
     * (File.getURL), but Sun's implementation sucks. You can't use it for writing a file, and
     * getDoInput always gives true, even if the file does not even exist.  This version supports
     * checking by <code>getDoInput()</code> (read rights) and <code>getDoOutput()</code> (write
     * rights) and deleting by <code>getOutputStream().write(null)</code>
     */
    private class FileConnection extends URLConnection {
        private File file;
        private boolean writeable;
        FileConnection(URL u, File f, boolean w) {
            super(u);
            this.file = f;
            this.writeable = w;
        }
        public void connect() throws IOException {
            connected = true;
        }

        public File getFile() {
            return file;
        }

        public boolean getDoInput() {
            return file.canRead();
        }
        public boolean getDoOutput() {
            if (! writeable) return false;
            if (file.exists()) {
                return file.canWrite();
            } else {
                return file.getParentFile().canWrite(); // may create
            }
        }

        public InputStream getInputStream() throws IOException {
            if (! connected) connect();
            return new FileInputStream(file);
        }
        public OutputStream getOutputStream() throws IOException {
            if (! connected) connect();
            if (! writeable) {
                throw new UnknownServiceException("This file-connection does not allow writing");
            }
            return new FileOutputStream(file) {
                    public void write(byte[] b) throws IOException {
                        if (b == null) {
                            file.delete();
                        } else {
                            super.write(b);
                        }
                    }
                };
        }
        public long getLastModified() {
            return file.lastModified();
        }

        public String toString() {
            return "FileConnection " + file.toString();
        }

    }


    /**
     * URLStreamHandler for NodeConnections.
     */
    protected class NodeURLStreamHandler extends PathURLStreamHandler {
        private int type;
        NodeURLStreamHandler(int type) {
            this.type    = type;
        }
        NodeURLStreamHandler(NodeURLStreamHandler nf) {
            this.type = nf.type;
        }

        protected String getName(URL u) {
            return u.getPath().substring(NODE_URL_CONTEXT.getPath().length());
        }
        public URLConnection openConnection(String name) {
            URL u;
            try {
                u = new URL(NODE_URL_CONTEXT, name, this);
            } catch (MalformedURLException mfue) {
                throw new AssertionError(mfue.getMessage());
            }
            return new NodeConnection(u, name, type);
        }
        public Set getPaths(final Set results, final Pattern pattern,  final boolean recursive, final boolean directories) {
            if (ResourceLoader.resourceBuilder != null) {
            try {
                NodeSearchQuery query = new NodeSearchQuery(ResourceLoader.resourceBuilder);
                BasicFieldValueConstraint typeConstraint = new BasicFieldValueConstraint(query.getField(resourceBuilder.getField(Resources.TYPE_FIELD)), new Integer(type));
                BasicFieldValueConstraint nameConstraint = new BasicFieldValueConstraint(query.getField(resourceBuilder.getField(Resources.RESOURCENAME_FIELD)), ResourceLoader.this.context.getPath().substring(1) + "%");
                nameConstraint.setOperator(FieldCompareConstraint.LIKE);

                BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

                constraint.addChild(typeConstraint).addChild(nameConstraint);


                query.setConstraint(constraint);
                Iterator i = resourceBuilder.getNodes(query).iterator();
                while (i.hasNext()) {
                    MMObjectNode node = (MMObjectNode) i.next();
                    String url = node.getStringValue(Resources.RESOURCENAME_FIELD);
                    String subUrl = url.substring(ResourceLoader.this.context.getPath().length() - 1);
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
        public String toString() {
            return "nodes of type " + type;
        }

    }

    /** 
     * A URLConnection base on an MMBase node.
     * @see FileConnection
     */
    private class NodeConnection extends URLConnection {
        MMObjectNode node;
        String name;
        int type;
        NodeConnection(URL url, String name, int t) {
            super(url);
            this.name = name;
            this.type = t;
        }
        public void connect() throws IOException {
            if (ResourceLoader.resourceBuilder == null) {
                throw new IOException("No such builder");
            }
            connected = true;
        }
        /**
         * Gets the Node associated with this URL if there is one.
         * @return MMObjectNode or <code>null</code>
         */
        public  MMObjectNode getResourceNode() {
            if (node != null) return node;
            if (name.equals("")) return null;
            String realName = (ResourceLoader.this.context.getPath() + name).substring(1);
            if (ResourceLoader.resourceBuilder != null) {
                try {
                    NodeSearchQuery query = new NodeSearchQuery(resourceBuilder);
                    StepField urlField = query.getField(resourceBuilder.getField(Resources.RESOURCENAME_FIELD));

                    BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(urlField, realName);

                    StepField typeField = query.getField(resourceBuilder.getField(Resources.TYPE_FIELD));
                    BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(typeField, new Integer(type));

                    BasicCompositeConstraint  constraint  = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                    constraint.addChild(constraint1);
                    constraint.addChild(constraint2);

                    query.setConstraint(constraint);
                    Iterator i = resourceBuilder.getNodes(query).iterator();
                    if (i.hasNext()) {
                        node = (MMObjectNode) i.next();
                        return node;
                    }
                } catch (org.mmbase.storage.search.SearchQueryException sqe) {
                    log.warn(sqe);
                }
            }
            return null;
        }

        public boolean getDoInput() {
            return getResourceNode() != null;
        }

        public boolean getDoOutput() {
            return ResourceLoader.resourceBuilder != null;
        }

        public InputStream getInputStream() throws IOException {
            getResourceNode();
            if (node != null) {
                return new ByteArrayInputStream(node.getByteValue(Resources.HANDLE_FIELD));
            } else {
               throw new IOException("No such resource");
            }
        }
        public OutputStream getOutputStream() throws IOException {
            if (getResourceNode() == null) {
                if (ResourceLoader.resourceBuilder == null) return null;

                node = ResourceLoader.resourceBuilder.getNewNode(Resources.DEFAULT_CONTEXT);
                String resourceName = (ResourceLoader.this.context.getPath() + name).substring(1);
                node.setValue(Resources.RESOURCENAME_FIELD, resourceName);
                node.setValue(Resources.TYPE_FIELD, type);
                log.info("Creating node " + resourceName + " " + name + " " + type);
                node.insert(Resources.DEFAULT_CONTEXT);
            }
            return new OutputStream() {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    public void close() throws IOException {
                        byte[] b = bytes.toByteArray();
                        node.setValue(Resources.HANDLE_FIELD, b);
                        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(b));
                        if (mimeType == null) {
                            URLConnection.guessContentTypeFromName(name);
                        }
                        node.setValue("mimetype", mimeType);
                        node.setValue(Resources.LASTMODIFIED_FIELD, new Date());
                        node.commit();
                    }
                    public void write(int b) {
                        bytes.write(b);
                    }
                    public void write(byte[] b) throws IOException {
                        if (b == null) {
                            node.parent.removeNode(node);
                            node = null;
                        } else {
                            super.write(b);
                        }
                    }
                };
        }
        public long getLastModified() {
            getResourceNode();
            if (node != null) {
                Date lm = node.getDateValue(Resources.LASTMODIFIED_FIELD);
                if (lm != null) {
                    return lm.getTime();
                }
            }
            return -1;
        }

        public String toString() {
            return "NodeConnection " + node;
        }

    }

    private static boolean warned23 = false;
    protected  class ServletResourceURLStreamHandler extends PathURLStreamHandler {
        private String root;
        ServletResourceURLStreamHandler(String r) {
            root = r;
        }
        ServletResourceURLStreamHandler(ServletResourceURLStreamHandler f) {
            root = f.root;
        }


        protected String getName(URL u) {
            return u.getPath().substring(root.length());
        }
        public URLConnection openConnection(String name) {
            try {
                URL u = ResourceLoader.this.servletContext.getResource(root + ResourceLoader.this.context.getPath() + name);
                if (u == null) return NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
                return u.openConnection();
            } catch (IOException ioe) {
                return NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
            }
        }
        public Set getPaths(final Set results, final Pattern pattern,  final boolean recursive, final boolean directories) {
            return getPaths(results, pattern, recursive ? "" : null, directories);
        }
        private  Set getPaths(final Set results, final Pattern pattern,  final String recursive, final boolean directories) {
            if (servletContext != null) {
                try {
                    String currentRoot  = root + ResourceLoader.this.context.getPath();
                    String resourcePath = currentRoot + (recursive == null ? "" : recursive);
                    Collection c = servletContext.getResourcePaths(resourcePath);
                    if (c == null) return results;
                    Iterator j = c.iterator();
                    while (j.hasNext()) {
                        String newResourcePath = ((String) j.next()).substring(currentRoot.length());
                        boolean isDir = newResourcePath.endsWith("/");
                        if (isDir) {
                            // subdirs
                            if (recursive != null) {
                                getPaths(results, pattern, newResourcePath.substring(0, newResourcePath.length() - 1), directories);
                            }
                            if (newResourcePath.equals("/")) continue;
                        }
                        if ((pattern == null || pattern.matcher(newResourcePath).matches()) && (directories == isDir)) {
                            if (isDir) newResourcePath = newResourcePath.substring(0, newResourcePath.length() - 1) ;
                            results.add(newResourcePath);
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

        public String toString() {
            return "ServletResource " + root;
        }
    }


    protected class ClassLoaderURLStreamHandler extends PathURLStreamHandler {
        private String root;
        ClassLoaderURLStreamHandler(String r) {
            root = r;
        }
        ClassLoaderURLStreamHandler(ClassLoaderURLStreamHandler f) {
            root = f.root;
        }
        protected String getName(URL u) {
            return u.getPath().substring((root +  ResourceLoader.this.context.getPath()).length());
        }
        public URLConnection openConnection(String name) {
            try {
                URL u = ResourceLoader.class.getResource(root + ResourceLoader.this.context.getPath() + name);
                if (u == null) return NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
                return u.openConnection();
            } catch (IOException ioe) {
                return NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
            }
        }
        public Set getPaths(final Set results, final Pattern pattern,  final boolean recursive, final boolean directories) {
            InputStream inputStream = ResourceLoader.this.getResourceAsStream(INDEX);
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

        public String toString() {
            return "ClassLoader " + root;
        }
    }


    private static String NOT_FOUND = "/localhost/NOTFOUND/";


    /**
     * URLStreamHandler for URL's which can do neither input, nor output. Such an URL can be
     * returned by other PathURLStreamHandlers too.
     */
    private  PathURLStreamHandler NOT_AVAILABLE_URLSTREAM_HANDLER = new PathURLStreamHandler() {

            protected String getName(URL u) {
                return u.getPath().substring(NOT_FOUND.length());
            }
            
            public URLConnection openConnection(String name) {
                URL u;
                try {
                    u = new URL(null, "http:/" + NOT_FOUND + name, this);
                } catch (MalformedURLException mfue) {
                    throw new AssertionError(mfue.getMessage());
                }
                return new NotAvailableConnection(u, name);
            }
            
            public Set getPaths(final Set results, final Pattern pattern,  final boolean recursive, final boolean directories) {
                return new HashSet();
            }
        };
    


    /**
     * A connection which can neither do input, nor output. 
     */
    private class NotAvailableConnection extends URLConnection {

        private String name;

        private NotAvailableConnection(URL u, String n) {
            super(u);
            name = n;
        }
        public void connect() throws IOException {  throw new IOException("No such resource " + name); };
        public boolean getDoInput() { return false; }
        public boolean getDoOutput() { return false; }
        public InputStream getInputStream() throws IOException { connect(); return null;}
        public OutputStream getOutputStream() throws IOException { connect(); return null; }
        public String toString() {
            return "NOTAVAILABLECONNECTION " + name;
        }
    };


    /**
     * The MMURLStreamHandler is a StreamHandler for the protocol 'mm' (which is only for internal
     * use). It combines the Connection types implented here above.
     */

    private class MMURLStreamHandler extends URLStreamHandler {

        MMURLStreamHandler() {
            super();
        }
        protected URLConnection openConnection(URL u) throws IOException {
            return new MMURLConnection(u);
        }
        /**
         * ExternalForms are mainly used in entity-resolving and URL.toString()
         * {@inheritDoc}
         */
        protected String toExternalForm(URL u) {
            return new MMURLConnection(u).getInputConnection().getURL().toExternalForm();
        }
    }

    /**
     * Implements the logic for our MM protocol. This logic consists of iterating in <code>ResourceLoader.this.roots</code>.
     */
    private class MMURLConnection extends URLConnection {

        URLConnection inputConnection  = null;
        URLConnection outputConnection = null;
        String name;


        MMURLConnection(URL u) {
            super(u);
            name = url.getPath().substring(1);
            //log.debug("Connection to " + url + Logging.stackTrace(new Throwable()));
            if (! url.getProtocol().equals(PROTOCOL)) {
                throw new RuntimeException("Only supporting URL's with protocol " + PROTOCOL);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void connect() {
            connected = true;
        }

        /**
         * Returns first possible connection which can be read.
         */
        protected URLConnection getInputConnection() {
            if (inputConnection != null) {
                return inputConnection;
            }
            Iterator i = ResourceLoader.this.roots.iterator();
            while(i.hasNext()) {
                PathURLStreamHandler cf = (PathURLStreamHandler) i.next();
                URLConnection c = cf.openConnection(name);
                if (c.getDoInput()) {
                    inputConnection = c;
                    break;
                }
            }
            if (inputConnection == null) {
                setDoInput(false);
                inputConnection = NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
            } else {
                setDoInput(true);
            }
            connect();
            return inputConnection;
        }



        /**
         * Returns <code>true</true> if you can successfully use getInputStream();
         */
        public boolean getDoInput() {
            return getInputConnection().getDoInput();
        }


        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream() throws IOException  {
            return getInputConnection().getInputStream();
        }

        /**
         * Returns last URL which can be written, and which is still earlier the the first URL which can be read (or the same URL).
         * This ensures that when used for writing, it will then be the prefered one for reading.
         */
        protected URLConnection getOutputConnection() {
            if (outputConnection != null) {
                return outputConnection;
            }

            // search connection which will be used for reading, and check if it can be used for writing
            ListIterator i = ResourceLoader.this.roots.listIterator();
            while (i.hasNext()) {
                PathURLStreamHandler cf = (PathURLStreamHandler) i.next();
                URLConnection c = cf.openConnection(name);
                if (c.getDoInput()) {
                    if(c.getDoOutput()) { // prefer the currently read one.
                        outputConnection = c;
                    }
                    break;
                }
            }
            if (outputConnection == null) {
                // the URL used for reading, could not be written.
                // Now iterate backwards, and search one which can be.
                while (i.hasPrevious()) {
                    PathURLStreamHandler cf = (PathURLStreamHandler) i.previous();
                    URLConnection c = cf.openConnection(name);
                    if (c.getDoOutput()) {
                        outputConnection = c;
                        break;
                    }
                }
            }

            if (outputConnection == null) {
                setDoOutput(false);
                outputConnection =  NOT_AVAILABLE_URLSTREAM_HANDLER.openConnection(name);
            } else {
                setDoOutput(true);
            }
            connect();
            return outputConnection;
        }

        /**
         * Returns <code>true</true> if you can successfully use getOutputStream();
         */
        public boolean getDoOutput() {
            return getOutputConnection().getDoOutput();
        }
        /**
         * {@inheritDoc}
         */
        public OutputStream getOutputStream() throws IOException  {
            OutputStream os = getOutputConnection().getOutputStream();
            if (os == null) {
                // Can find no place to store this resource. Giving up.
                throw new IOException("Cannot create an OutputStream for " + url + " cannot be written, no resource-node could be created)");
            } else {
                return os;
            }
        }
        /**
         * {@inheritDoc}
         */
        public long getLastModified() {
            return getInputConnection().getLastModified();
        }

    }

    /**
     * ================================================================================
     * Main
     *
     * For testing purposes only
     * ================================================================================
     */
    public static void main(String[] argv) {
        ResourceLoader resourceLoader;

        if (System.getProperty("mmbase.htmlroot") != null) {
            resourceLoader = getWebRoot();
        } else {
            resourceLoader = getConfigurationRoot();
        }
        try {
            if (argv.length == 0) {
                System.err.println("useage: java [-Dmmbase.config=<config dir>|-Dmmbase.htmlroot=<some other dir>] " + ResourceLoader.class.getName() + " [<sub directory>] <resource-name>");
                return;
            }
            String arg = argv[0];
            if (argv.length > 1) {
                resourceLoader = getConfigurationRoot().getChildResourceLoader(argv[0]);
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
// --------------------------------------------------------------------------------

/**
 * Like {@link java.io.OutputStreamWriter} but it tries to autodetect the encoding of the
 * OutputStream. This works at least if the OutputStream is XML, which is a very common thing to be for Resources.
 *
 * For this to work at least the first part (e.g. the first 100 bytes) need to be buffered.
 *
 * If determining the encoding did not succeed it is supposed to be 'UTF-8', which is (should be) an
 * acceptable encoding, and also the default encoding for XML streams.
 * @todo This class should probably be stand-alone.
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
