/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
//import java.io.*;
//import java.sql.*;

import org.mmbase.module.builders.*;
//import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: Images.java,v 1.52 2002-04-12 08:53:00 pierre Exp $
 */
public class Images extends AbstractImages {
    private static Logger log = Logging.getLoggerInstance(Images.class.getName());

    // This cache connects templates (or ckeys, if that occurs), with node numbers,
    // to avoid querying icaches.
    private LRUHashtable templateCacheNumberCache = new LRUHashtable(500);

    ImageConvertInterface imageconvert=null;
    Hashtable ImageConvertParams = new Hashtable();

    // Currenctly only ImageMagick works / this gets parameterized soon
    protected static String ImageConvertClass="org.mmbase.module.builders.ConvertImageMagick";
    protected int MaxConcurrentRequests=2;

    protected int MaxRequests=32;
    protected Queue imageRequestQueue=new Queue(MaxRequests);
    protected Hashtable imageRequestTable=new Hashtable(MaxRequests);
    protected ImageRequestProcessor ireqprocessors[];

    /**
     * @javadoc
     */
    public boolean init() {
        if (!super.init()) return false;

        String tmp;
        int itmp;
        tmp=getInitParameter("ImageConvertClass");
        if (tmp!=null) ImageConvertClass=tmp;
        getImageConvertParams(getInitParameters());
        tmp=getInitParameter("MaxConcurrentRequests");
        if (tmp!=null) {
            try {
                itmp=Integer.parseInt(tmp);
            }
            catch (NumberFormatException e) {
                itmp=2;
            } MaxConcurrentRequests=itmp;
        }

        imageconvert=loadImageConverter(ImageConvertClass);
        imageconvert.init(ImageConvertParams);

        ImageCaches bul=(ImageCaches)mmb.getMMObject("icaches");
        if(bul==null) {
            log.error("Error: Place icaches in objects.def before images");
        }
        // Startup parrallel converters
        ireqprocessors=new ImageRequestProcessor[MaxConcurrentRequests];
        log.info("Starting "+MaxConcurrentRequests+" Converters");
        for (int i=0;i<MaxConcurrentRequests;i++) {
            ireqprocessors[i]=new ImageRequestProcessor(bul,imageconvert,imageRequestQueue,imageRequestTable);
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
    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if ("cache".equals(function)) {
            return new Integer(cacheImage(node, field));
        } else {
            return super.executeFunction(node, function, field);
        }
    }

    /**
     * @javadoc
     */
    public void setDefaults(MMObjectNode node) {
        node.setValue("description","");
    }

    /**
     * @since MMBase-1.6
     */
    protected String getGUIIndicatorWithAlt(MMObjectNode node, String title) {
        int num = node.getNumber();
        if (num == -1 ) {   // img.db cannot handle uncommited images..
            return null; // ObjectBuilder itself will handle this case.
        }
        // NOTE that this has to be configurable instead of static like this
        String servlet    = getServlet();
        String imageThumb = servlet + node.getIntValue("cache(s(100x60))");
        String image      = servlet + node.getNumber();
        return "<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imageThumb + "\" border=\"0\" alt=\"" + title + "\" /></a>";
    }

    /**
     * @javadoc
     */
    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicatorWithAlt(node, node.getStringValue("title"));
    }

    // called by init..used to retrieve all settings
    private void getImageConvertParams(Hashtable params) {
        String key;
        for (Iterator e=params.keySet().iterator();e.hasNext();) {
            key=(String)e.next();
            if (key.startsWith("ImageConvert.")) {
                ImageConvertParams.put(key,params.get(key));
            }
        }
    }

    private ImageConvertInterface loadImageConverter(String classname) {
        Class cl;
        ImageConvertInterface ici=null;

        try {
            cl=Class.forName(classname);
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
     * Will return "jpg" as default type, or one of the strings in params, must contain the following "f(type)" where type will be returned
     * @param params a <code>List</code> of <code>String</code>s, which could contain the "f(type)" string
     * @return "jpg" by default, or the first occurence of "f(type)"
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
        if (format==null) format="jpg";
        String mimetype=mmb.getMimeType(format);
        log.debug("getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
        return mimetype;
    }

    /**
     * Explicity cache this image with the given template and return the cached node number.
     *
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

        List params = new Vector();
        params.add("" + node.getNumber());
        if (template != null) {
            StringTokenizer tok=new StringTokenizer(template,"+");
            while(tok.hasMoreTokens()) {
                params.add(tok.nextToken());
            }
        }
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
        if (log.isDebugEnabled()) log.debug("Caching image " + params);


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
        log.debug("getting cached image" + ckey);
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");
            return null;
        }

        // now get the actual bytes
        byte[] cachedPicture = null;
        cachedPicture = imageCacheBuilder.getCkeyNode(ckey);
        return cachedPicture;
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
        log.service("getting image bytes of " + params);
        if (params==null || params.size() == 0) {
            log.debug("getOriginalImage: no parameters");
            return null;
        }

        // get our hashcode
        String ckey = flattenParameters(params);
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");
            return null;
        }

        // try to resolve the number of our object (first param) (could also be the name)
        int objectId = convertAlias((String)params.get(0));
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
                log.info("ConvertImage: a conversion in progress ("+ckey+")...  (requests="+(req.count()+1)+")");
            } else {
                req = new ImageRequest(objectId, ckey, params, inputPicture);
                imageRequestTable.put(ckey,req);
                imageRequestQueue.append(req);
            }
        }
        return req.getOutput();
    }

    /**
     * Check if its a number if not check for name.
     * @javadoc Not clear enough
     */
    public int convertAlias(String num) {
        int number=-1;
        try {
            number=Integer.parseInt(num);
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
     * Override the MMObjectBuilder commit, to invalidate the Image Cache AFTER a modifation to the
     * image node.
     * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
     * Use only to commit changes - for adding node, use {@link #insert}.
     * @param node The node to be committed
     * @return The committed node.
     */
    public boolean commit(MMObjectNode node) {
        // look if we need to invalidate the image cache...
        boolean imageCacheInvalid = node.getChanged().contains("handle");
        // do the commit
        if(super.commit(node)) {
            // when cache is invalide, invalidate
            if(imageCacheInvalid) {
                invalidateImageCache(node);
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
    void invalidateTemplateCacheNumberCache() {
        templateCacheNumberCache.clear();
    }
}

