/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * If this class is used as the class for your builder, then an
 * 'handle' byte field is assumed to contain an image. You builder
 * will work together with 'icaches', and with imagemagick (or jai).
 *
 * This means that is has the following properties: ImageConvertClass,
 * ImageConvert.ConverterCommand, ImageConvert.ConverterRoot and
 * MaxConcurrentRequests
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: Images.java,v 1.84 2003-12-17 20:59:37 michiel Exp $
 */
public class Images extends AbstractImages {

    private static final Logger log = Logging.getLoggerInstance(Images.class);

    // This cache connects templates (or ckeys, if that occurs), with node numbers,
    // to avoid querying icaches.
    private CKeyCache templateCacheNumberCache = new CKeyCache(500) {
        public String getName()        { return "CkeyNumberCache"; }
        public String getDescription() { return "Connection between image conversion templates and icache node numbers"; }
        };

    public final static Parameter[] CACHE_PARAMETERS = {
        new Parameter("template",  String.class)
    };

    public final static Parameter[] GUI_PARAMETERS = { 
        new Parameter.Wrapper(MMObjectBuilder.GUI_PARAMETERS),
        new Parameter("template", String.class)
    };




    public Images() {
        templateCacheNumberCache.putCache();
    }

    protected Map imageConvertParams = new Hashtable();

    /**
     * The ImageConvertInterface implementation to be used (defaults to ConvertImageMagic)
     */
    protected static final Class DEFAULT_IMAGECONVERTCLASS = ConvertImageMagick.class;

    protected int maxConcurrentRequests = 2;

    /**
     * Supposed image type if not could be determined (configurable)
     */
    protected String defaultImageType = "jpg";

    protected static final int maxRequests = 32;
    protected Queue imageRequestQueue     = new Queue(maxRequests);
    protected Hashtable imageRequestTable = new Hashtable(maxRequests);
    protected ImageRequestProcessor ireqprocessors[];

    /**
     * Read configurations (imageConvertClass, maxConcurrentRequest),
     * checks for 'icaches', inits the request-processor-pool.
     */
    public boolean init() {
        if (!super.init()) return false;

        String tmp;
        int itmp;
        String imageConvertClass  = DEFAULT_IMAGECONVERTCLASS.getName();
        tmp = getInitParameter("ImageConvertClass");
        if (tmp != null) imageConvertClass = tmp;
        getImageConvertParams(getInitParameters());
        tmp = getInitParameter("MaxConcurrentRequests");
        if (tmp!=null) {
            try {
                itmp=Integer.parseInt(tmp);
            }
            catch (NumberFormatException e) {
                itmp=2;
            } maxConcurrentRequests=itmp;
        }
        tmp = getInitParameter("DefaultImageType");
        if (tmp!=null) {
            defaultImageType = tmp;
        }

        ImageConvertInterface imageConverter = loadImageConverter(imageConvertClass);
        imageConverter.init(imageConvertParams);

        ImageCaches bul = (ImageCaches)mmb.getMMObject("icaches");
        if(bul==null) {
            throw new RuntimeException("builder with name 'icaches' wasnt loaded");
        }
        // Startup parrallel converters
        ireqprocessors = new ImageRequestProcessor[maxConcurrentRequests];
        log.info("Starting "+maxConcurrentRequests+" Converters");
        for (int i=0;i < maxConcurrentRequests; i++) {
            ireqprocessors[i] = new ImageRequestProcessor(bul, imageConverter, imageRequestQueue, imageRequestTable);
        }
        return true;
    }

    /**
     * The executeFunction of this builder adds the 'cache' function.
     * The cache function accepts a conversion template as argument and returns the cached image
     * node number. Using this you order to pre-cache an image.
     *
     * @since MMBase-1.6
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("executeFunction " + function + "(" + args + ") of images builder on node " + node.getNumber());
        }
        if ("info".equals(function)) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("cache", "" + CACHE_PARAMETERS + " The node number of the cached converted image (icaches node)");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if ("cache".equals(function)) {
            if (args == null || args.size() != 1)
                throw new RuntimeException("Images cache functions needs 1 argument (now: " + args + ")");
            return new Integer(cacheImage(node, (String) args.get(0)));
        } else {
            return super.executeFunction(node, function, args);
        }
    }

    /**
     * @javadoc
     */
    public void setDefaults(MMObjectNode node) {
    }

    /**
     * The GUI-indicator of an image-node also needs a res/req object.
     * @since MMBase-1.6
     */
    protected String getGUIIndicatorWithAlt(MMObjectNode node, String title, Parameters args) {
        int num = node.getNumber();
        if (num == -1 ) {   // img.db cannot handle uncommited images..
            return "...";
        }
        // NOTE that this has to be configurable instead of static like this
        String ses = (String) args.get("session");


        StringBuffer servlet = new StringBuffer();
        HttpServletRequest req = (HttpServletRequest) args.get("request");
        if (req != null) {
            servlet.append(getServletPath(UriParser.makeRelative(new java.io.File(req.getServletPath()).getParent(), "/")));
        } else {
            servlet.append(getServletPath());
        }
        servlet.append(usesBridgeServlet && ses != null ? "session=" + ses + "+" : "");
        String template = (String) args.get("template");
        if (template == null) template = ImageCaches.GUI_IMAGETEMPLATE;
        List cacheArgs =  new Parameters(CACHE_PARAMETERS).set("template", template);
        String imageThumb = servlet.toString() + executeFunction(node, "cache", cacheArgs);
        servlet.append(node.getNumber());
        String image;
        HttpServletResponse res = (HttpServletResponse) args.get("response");
        if (res != null) {
            imageThumb = res.encodeURL(imageThumb);
            image      = res.encodeURL(servlet.toString());
        } else {
            image = servlet.toString();
        }
        return "<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imageThumb + "\" border=\"0\" alt=\"" + title + "\" /></a>";
    }

    // javadoc copied from parent
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters args) {
        return getGUIIndicatorWithAlt(node, node.getStringValue("title"), args);
    }

    /**
     * called by init..used to retrieve all settings
     */
    private void getImageConvertParams(Hashtable params) {
        String key;
        for (Iterator e=params.keySet().iterator();e.hasNext();) {
            key = (String)e.next();
            if (key.startsWith("ImageConvert.")) {
                imageConvertParams.put(key, params.get(key));
            }
        }
        imageConvertParams.put("configfile", getConfigFile());
    }

    private ImageConvertInterface loadImageConverter(String classname) {
        Class cl;
        ImageConvertInterface ici=null;

        try {
            cl = Class.forName(classname);
            ici=(ImageConvertInterface)cl.newInstance();
            log.info("loaded '"+classname+"' for builder '" + getTableName() + "'");
        }
        catch (ClassNotFoundException e) {
            log.error("is classname in " + getTableName() + ".xml correct? ('not found class "+classname+"')");
            log.error(Logging.stackTrace(e));
        }
        catch (InstantiationException e) {
            log.error("something wend wrong ('could not instantiate class "+classname+"')");
            log.error(Logging.stackTrace(e));
        }
        catch (java.lang.IllegalAccessException e) {
            log.error("something wend wrong ('illegal access class "+classname+"')");
            log.error(Logging.stackTrace(e));
        }
        catch (NoClassDefFoundError e) {
            log.error("are all lib's available? ('missing class used by class"+classname+"')");
            log.error(Logging.stackTrace(e));
        }
        return ici;
    }

    /**
    * Will return {@link #defaultImageType} as default type, or one of the strings in params, must contain the following "f(type)" where type will be returned
     * @param params a <code>List</code> of <code>String</code>s, which could contain the "f(type)" string
     * @return {@link #defaultImageType} by default, or the first occurence of "f(type)"
     *
     *
     */
    public String getImageMimeType(List params) {
        String format=null;
        String key;

        // WHY the itype colomn isn't used?

        for (Iterator e=params.iterator();e.hasNext();) {
            key=(String)e.next();

            // look if our string is long enough...
            if(key != null && key.length() > 2) {
                // first look if we start with an "f("... format is f(gif)
                if(key.startsWith("f(")) {
                    // one search function remaining...
                    int pos=key.lastIndexOf(')');
                    // we know for sure that our "(" is at pos 1, so we can define this hard...
                    format = key.substring(2, pos);
                    break;
                }
            }
        }
        if (format==null) format=defaultImageType;
        String mimetype=mmb.getMimeType(format);
        log.debug("getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
        return mimetype;
    }


    /**
     * Returns the image format.
     * If the node is not an icache node, but e.g. an images node, then
     * it will return either the node's 'itype' field, or
     * (if that field is empty) the default image format, which is {@link #defaultImageType}.
     * @since MMBase-1.7
     */
    protected String getImageFormat(MMObjectNode node) {
        String format = node.getStringValue("itype");
        if (format == null || format.equals("")) format = defaultImageType;
        return format;
    }

    /**
     * Parses the 'image conversion template' to a List. I.e. it break
     * it up in substrings, with '+' delimiter. However a + char does
     * not count if it is somewhere between brackets (). Brackets nor
     * +-chars count if they are in quotes (single or double)
     *
     * @since MMBase-1.7
     */
    // @author michiel
    protected static final char NOQUOTING = '-';
    protected List parseTemplate(MMObjectNode node, String template) {
        if (log.isDebugEnabled()) log.debug("parsing " + template);
        List params = new ArrayList();
        params.add("" + node.getNumber());
        if (template != null) {
            int bracketDepth = 0;
            char quoteState = NOQUOTING; // can be - (not in quote), ' or ".
            StringBuffer buf = new StringBuffer();

            int i = 0;
            while (i < template.length() && template.charAt(i) == '+') i++; // ignoring leading +'es (can sometimes be one)
            for (; i < template.length(); i++) {
                char c = template.charAt(i);
                switch(c) {
                case '\'':
                case '"':
                    if (quoteState == c) {
                        quoteState = NOQUOTING;
                    } else if (quoteState == NOQUOTING) {
                        quoteState = c;
                    }
                    break;
                case '(': if (quoteState == NOQUOTING) bracketDepth++; break;
                case ')': if (quoteState == NOQUOTING) bracketDepth--; break;
                case '+': // command separator
                    if (bracketDepth == 0  // ignore if between brackets
                        && quoteState == NOQUOTING // ignore if between quotes
                        ) {
                        removeSurroundingQuotes(buf);
                        params.add(buf.toString());
                        buf = new StringBuffer();
                        continue;
                    }
                    break;
                }

                buf.append(c);
            }
            if (bracketDepth != 0) log.warn("Unbalanced brackets in " + template);
            if (quoteState != NOQUOTING) log.warn("Unbalanced quotes in " + template);

            removeSurroundingQuotes(buf);
            if (! buf.toString().equals("")) params.add(buf.toString());
        }
        return params;
    }
    /**
     * Just a utitility function, used by the function above.
     * @since MMBase-1.7
     */
    protected void removeSurroundingQuotes(StringBuffer buf) {
        // remove surrounding quotes --> "+contrast" will be changed to +contrast
        if (buf.length() >= 2 && (buf.charAt(0) == '"' || buf.charAt(0) == '\'') && buf.charAt(buf.length() - 1) == buf.charAt(0)) {
            buf.deleteCharAt(0);
            buf.deleteCharAt(buf.length() - 1);
        }
    }


    /**
     * Explicity cache this image with the given template and return the cached node number.
     * Called by the cache() function.
     *
     * @since MMBase-1.6
     */
    private int cacheImage(MMObjectNode node, String template) {
        String cacheKey = "" + node.getNumber() + template;
        Integer i = (Integer) templateCacheNumberCache.get(cacheKey);
        if (i != null) {
            if (log.isDebugEnabled()) log.debug("found image in cache " + i);
            return i.intValue();
        }

        List params = parseTemplate(node, template);
        i = new Integer(cacheImage(params));
        templateCacheNumberCache.put(cacheKey, i);
        return i.intValue();
    }

    /**
     * Explicity cache this image with params and return the cached node number.
     *
     * This function is called by servdb. So when servdb is not used
     * for images anymore, this function can be deprecated (and the
     * functionality moved to cacheImage(node, template).
     *
     * @since MMBase-1.6
     */
    public int cacheImage(List params) {
        if (log.isDebugEnabled()) {
            log.debug("Caching image " + params);
        }


        if (getImageBytes(params) != null) {
            // this will also calculate ckey, so it is not optimally efficient now.
            // but at least it will make sure that the image is cached.

            String ckey = flattenParameters(params);

            // Using the cache which is also used for templates (this
            // avoids the SQL statement in getCachedNodeNumber)
            // Templates and ckeys are not excactly the same, but
            // well, this function is only used in servdb.

            Integer cachedNodeNumber = (Integer) templateCacheNumberCache.get(ckey);
            if (cachedNodeNumber == null ) {
                // get a connection to the cache module
                ImageCaches imageCacheBuilder = (ImageCaches) mmb.getMMObject("icaches");
                cachedNodeNumber = new Integer(imageCacheBuilder.getCachedNodeNumber(ckey));
                templateCacheNumberCache.put(ckey, cachedNodeNumber);
            }
            return cachedNodeNumber.intValue();
        } else {
            return -1;
        }
    }

    /**
     * @deprecated Use getImageBytes(params);
     */
    public byte[] getImageBytes5(scanpage sp, List params) {
        return getImageBytes(params);
    }

    /**
     * @deprecated Use getImageBytes(params);
     */
    public byte[] getImageBytes(scanpage sp, List params) {
        return getImageBytes(params);
    }

    /**
     * Returns a picture wich belongs to the given param line, with caching.
     *
     * @param params The name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     */
    public byte[] getImageBytes(List params) {

        byte[] picture = getCachedImage(params);
        if(picture != null && picture.length > 0) {
            if (log.isDebugEnabled()) log.debug("Image was cached already");
            return picture;
        } else {
            if (log.isDebugEnabled()) log.debug("Image was not cached, caching now (from getCachedImage: " + picture + ")");
            return getOriginalImage(params);
        }
    }

    /**
     * This function will flatten the parameters to an unique key, so that an image can be found in the cache.
     * In other words, this function could have been called 'getCKey'.
     *
     * @param params a <code>List</code> of <code>String</code>s, with a size greater then 0 and not null
     * @return a string containing the key for this List, or <code>null</code>,....
     */
    private String flattenParameters(List params) {
        if (params==null || params.size() == 0) {
            log.debug("flattenParameters: no parameters");
            return null;
        }
        // flatten parameters as a 'hashed' key;
        StringBuffer sckey = new StringBuffer("");
        Iterator enum=params.iterator();
        while(enum.hasNext()) {
            sckey.append(enum.next().toString());
        }
        // skip spaces at beginning and ending, URL param escape to avoid everything strange in it.
        String ckey = "";
        try {
            ckey = new String(sckey.toString().trim().getBytes("US-ASCII")).replace('"', 'X').replace('\'', 'X');
        } catch (java.io.UnsupportedEncodingException e) {
            log.error(e.toString());
        }
        // of course it is not a very good idea to convert to US-ASCII, but
        // in ImageCaches this string is used in a select statement, without using
        // a database layer. So we must have something which works always.
        // Some texts, however will lead to the same ckey now.

        if(log.isDebugEnabled()) log.debug("using ckey " + ckey);
        if(ckey.length() > 0) {
            return ckey;
        } else {
            log.debug("flattenParameters: empty parameters");
            return null;
        }
    }

    /**
     * Determins the ckey, and asks the bytes from icaches.  Will
     * return null when not in cache, and otherwise a byte []
     * representing the picture..
     * @param params a <code>List</code> of <code>String</code>s, containing the name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     */
    protected byte[] getCachedImage(List params) {
        // get a connection to the cache module
        ImageCaches imageCacheBuilder = (ImageCaches) mmb.getMMObject("icaches");
        if(imageCacheBuilder == null) {
            log.error("getCachedImage(): ERROR builder icaches not loaded, load it by setting it active in icaches.xml");
            return null;
        }

        // get our hashcode
        String ckey = flattenParameters(params);
        if (log.isDebugEnabled()) {
            log.debug("getting cached image " + ckey);
        }
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");
            return null;
        }

        // now get the actual bytes
        return imageCacheBuilder.getCkeyNode(ckey);
    }


    /**
     * This function should be called when an image/template does not
     * have a cached version yet.
     *
     * Will return null when something goes wrong otherwise, a byte[]
     * which represents the picture.
     *
     * @param params a <code>List</code> of <code>String</code>s,
     *                containing the name/id of the picture, followed by operations,
     *                which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     */
    protected byte[] getOriginalImage(List params) {
        if (log.isServiceEnabled()) {
            log.service("getting image bytes of " + params + " (" + params.size() + ")");
        }
        if (params == null || params.size() == 0) {
            log.debug("getOriginalImage: no parameters");
            return null;
        }

        // get our hashcode
        String ckey = flattenParameters(params);
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");
            return null;
        }

        // try to resolve the number of our object (first param) (could also be the title)
        int objectId = convertAlias((String)params.get(0)); // arch, deprecated!

        if ( objectId < 0 ) {
            // why is 0 a valid object number???
            log.warn("getOriginalImage: Parameter is not a valid image "+objectId);
            return null;
        }

        // retrieve the original image
        MMObjectNode node = getNode(objectId);

        // get the Object...
        if(node == null) {
            log.warn("ConvertImage: Image node not found "+objectId);
            return null;
        }

        // get  the bytes from the Object (assume in field handle)
        byte[] inputPicture = node.getByteValue("handle");
        if(inputPicture == null) {
            log.warn("ConvertImage: Image Node is bad "+objectId);
            return null;
        }

        ImageRequest req;
        // convert the image, this will be done in an special thread,...
        synchronized(imageRequestTable) {
            req = (ImageRequest) imageRequestTable.get(ckey);
            if (req != null) {
                log.info("ConvertImage: a conversion is already in progress (" + ckey + ")...  (requests="+ ( req.count() + 1) + ")");
            } else {
                req = new ImageRequest(objectId, ckey, params, inputPicture);
                imageRequestTable.put(ckey,req);
                imageRequestQueue.append(req);
            }
        }
        return req.getOutput();
    }

    /**
     * Converts a string into a node-number, in the following a
     * way. If the string represents an integer, this integer is
     * returned, otherwise the String is used to do a search on
     * title. Note that if you use this, the _oalias_ is ignored.
     *
     * @deprecated This is hackery, and un-mmbase-like.
     */
    protected int convertAlias(String num) {
        int number = -1;
        try {
            number = Integer.parseInt(num);
        } catch(NumberFormatException e) {
            if (num!=null && !num.equals("")) {
                Enumeration g=search("WHERE title='"+num+"'");
                while (g.hasMoreElements()) {
                    MMObjectNode imgnode=(MMObjectNode)g.nextElement();
                    number=imgnode.getIntValue("number");
                }
            }
        }
        return number;
    }

    /**
     * @javadoc
     */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
        Vector devices = new Vector();

        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();

            if (cmd.equals("devices")) {
                if(mmb.getMMObject("scanners")!=null) {
                    getDevices("scanners",devices);
                }
                if(mmb.getMMObject("cameras")!=null) {
                    getDevices("cameras",devices);
                }
                if(mmb.getMMObject("pccards")!=null) {
                    getDevices("pccards",devices);
                }
                tagger.setValue("ITEMS","2");
                return devices;
            }
        }
        return null;
    }

    /**
     * get all devices of given devicetype
     * e.g. give all scanners.
     */
    private void getDevices(String devicetype, List devices) {
        MMObjectBuilder mmob = mmb.getMMObject(devicetype);
        List v = mmob.searchVector("");
        Iterator e = v.iterator();

        while (e.hasNext()) {
            MMObjectNode mmon = (MMObjectNode)e.next();
            String name  = "" + mmon.getValue("name");
            devices.add(devicetype);
            devices.add(name);
        }
    }

    /**
     * Determines the image type of an object and stores the content in the itype field.
     * @param owner The administrator creating the node
     * @param node The object to change
     */
    protected void determineImageType(MMObjectNode node) {
        String itype = node.getStringValue("itype");
        if (itype == null || itype.equals("")) {
            itype = "";
            try {
                MagicFile magicFile = MagicFile.getInstance();
                String mimetype=magicFile.getMimeType(node.getByteValue("handle"));
                if (!mimetype.equals(MagicFile.FAILED)) {
                    // determine itype
                    if (mimetype.startsWith("image/")) {
                        itype = mimetype.substring(6);
                        log.debug("set itype to " + itype);
                    } else {
                        log.warn("Mimetype "+mimetype+" is not an image type");
                    }
                } else {
                    log.warn(MagicFile.FAILED);
                }
            } catch (Exception e) {
                log.warn("Error while determining image mimetype : "+Logging.stackTrace(e));
            }
            node.setValue("itype",itype);
        }
    }

    /**
     * Insert a new object (content provided) in the cloud.
     * This method attempts to determine the image type of the object.
     * @param owner The administrator creating the node
     * @param node The object to insert. The object need be of the same type as the current builder.
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     */
    public int insert(String owner, MMObjectNode node) {
        determineImageType(node);
        return super.insert(owner, node);
    }

    /**
     * Override the MMObjectBuilder commit, to invalidate the Image Cache AFTER a modifation to the
     * image node.
     * This method attempts to determine the image type of the object.
     * @param node The node to be committed
     * @return The committed node.
     */
    public boolean commit(MMObjectNode node) {
        // look if we need to invalidate the image cache...
        boolean imageCacheInvalid = node.getChanged().contains("handle");
        if(imageCacheInvalid) {
            node.setValue("itype", "");
            determineImageType(node);
        }
        // do the commit
        if(super.commit(node)) {
            // when cache is invalide, invalidate
            if(imageCacheInvalid) {
                invalidateImageCache(node);
                templateCacheNumberCache.remove(node.getNumber());
            }
            return true;
        }
        return false;
    }

    /**
     * Override the MMObjectBuilder removeNode, to invalidate the Image Cache AFTER a delete-ion of the
     * image node.
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        super.removeNode(node);
        invalidateImageCache(node);
        templateCacheNumberCache.remove(node.getNumber());
    }

    /**
     * Invalidate the Image Cache, if there is one, for a specific ImageNode
     * @param node The image node, which is the original
     */
    private void invalidateImageCache(MMObjectNode node) {
        ImageCaches icache = (ImageCaches) mmb.getMMObject("icaches");
        if(icache != null) {
            // we have a icache that is active...
            icache.invalidate(node);
        }
    }

    /**
     * @javadoc
     */
    void invalidateTemplateCacheNumberCache(int number) {
        templateCacheNumberCache.remove(number);
    }
}

