/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.List;
import java.util.Iterator;
import org.mmbase.module.core.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.UriParser;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * ImageCaches (aka as 'icaches') is a system-like builder used by
 * builders with the 'Images' class. It contains the converted images.
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: ImageCaches.java,v 1.35 2004-02-06 14:26:03 pierre Exp $
 */
public class ImageCaches extends AbstractImages {

    private static Logger log = Logging.getLoggerInstance(ImageCaches.class);

    static final String GUI_IMAGETEMPLATE = "s(100x60)";

    private CKeyCache handleCache = new CKeyCache(128) {  // a few images are in memory cache.
            public String getName()        { return "ImageHandles"; }
            public String getDescription() { return "Handles of Images (ckey -> handle)"; }
        };

    public ImageCaches() {
        handleCache.putCache();
    }

    /**
     * Returns the original images, for which this node is a cached image.
     *
     * @since MMBase-1.6
     **/
    private MMObjectNode originalImage(MMObjectNode node) {
        return getNode(node.getIntValue("id"));
    }

    /**
     * The GUI indicator of an image can have an alt-text.
     *
     * @since MMBase-1.6
     **/

    protected String getGUIIndicatorWithAlt(MMObjectNode node, String title, Parameters a) {
        StringBuffer servlet = new StringBuffer();
        HttpServletRequest req = (HttpServletRequest) a.get("request");
        if (req != null) {
            servlet.append(getServletPath(UriParser.makeRelative(new java.io.File(req.getServletPath()).getParent(), "/")));
        } else {
            servlet.append(getServletPath());
        }
        String ses = (String) a.get("session");
        servlet.append(usesBridgeServlet && ses != null ? "session=" + ses + "+" : "");
        MMObjectNode origNode = originalImage(node);
        String imageThumb;
        HttpServletResponse res = (HttpServletResponse) a.get("response");
        if (origNode != null) {
            List cacheArgs =  new Parameters(Images.CACHE_PARAMETERS).set("template", GUI_IMAGETEMPLATE);
            imageThumb = servlet.toString() + origNode.getFunctionValue("cache", cacheArgs);
            if (res != null) imageThumb = res.encodeURL(imageThumb);
        } else {
            imageThumb = "";
        }
        String image      = servlet.toString() + node.getNumber();
        if (res != null) image = res.encodeURL(image);
        return "<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imageThumb + "\" border=\"0\" alt=\"" + title + "\" /></a>";
    }

    // javadoc inherited
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters a) {
        MMObjectNode origNode = originalImage(node);
        return getGUIIndicatorWithAlt(node, (origNode != null ? origNode.getStringValue("title") : ""), a);
    }


    /**
     * Given a certain ckey, return the cached image node number, if there is one, otherwise return -1.
     * This functions always does a query. The caching must be done somewhere else.
     * This is done because caching on ckey is not necesarry when caching templates.
     * @since MMBase-1.6
     **/
    protected int getCachedNodeNumber(String ckey) {
        List nodes;
        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            query.setMaxNumber(2); // to make sure this is a cheap query.
            StepField ckeyField = query.getField(getField("ckey"));
            query.setConstraint(new BasicFieldValueConstraint(ckeyField, ckey));
            nodes = getNodes(query);
        } catch (SearchQueryException e) {
            log.error(e.toString());
            return -1;
        }

        if (nodes.size() == 0) {
            log.debug("Did not find cached images with key ("+ ckey +")");
            return -1 ;
        }
        if (nodes.size() > 1) {
            log.warn("found more then one cached image with key ("+ ckey +")");
        }
        MMObjectNode node = (MMObjectNode) nodes.get(0);
        return node.getNumber();
    }

    /**
     * Gets the handle bytes from a node.
     * @param n The node to receive the bytes from. It might be null, then null is returned.
     */
    private  byte[] getImageBytes(MMObjectNode n) {
        if (n == null) {
            log.warn("method called with null MMObjectNode, returing null");
            return null;
        } else {
            byte[] bytes = n.getByteValue("handle");
            if (bytes == null) {
                log.warn("handle was null for node with number ("+ n.getNumber() +")!");
                return null;
            }
            if (log.isDebugEnabled()) log.debug("found " + bytes.length + " bytes");
            return bytes;
        }
    }

    private  byte[] getImageBytes(int number) {
        return getImageBytes(getNode(number));
    }

    private byte[] getImageBytes(String number) {
        return getImageBytes(getNode(number));
    }
    /**
     * Returns the bytes of a cached image. It accepts a list, just
     * because it is also like this in Images.java. But of course a
     * cached image only uses the first element (number of the node).
     * It also works if the the node is a real image in stead of a
     * cached image, in which case simple the unconverted image is
     * returned.
     *
     * If the node does not exists, it returns empty byte array
     */
    public byte[] getImageBytes(List params) {
        return getImageBytes("" + params.get(0));
    }

    /**
     * Return the bytes for the cached image with a certain ckey, or null, if not cached.
     */
    public byte[] getCkeyNode(String ckey) {
        log.debug("getting ckey node with " + ckey);
        if(handleCache.contains(ckey)) {
            // found the node in the cache..
            return (byte []) handleCache.get(ckey);
        }
        log.debug("not found in handle cache, getting it from database.");
        int number = getCachedNodeNumber(ckey);

        if (number == -1) {
            // we dont have a cachednode yet, return null
            log.debug("cached node not found for key (" + ckey + "), returning null");
            return null;
        }

        // cached node can be found with the number nunmber
        byte data[] = getImageBytes(number);

        if (data == null) {
            // if it didn't work, also cache this result, to avoid concluding that again..
            // should this trow an exception every time? I think so, otherwise we would generate an
            // image every time it is requested, which also net very handy...
            // handleCache.put(ckey, new byte[0]);
            // this should be done differenty.
            String msg = "The node(#"+number+") which should contain the cached result for ckey:" + ckey + " had as value <null>, this means that something is really wrong.(how can we have an cache node with node value in it?)";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        // is this not configurable?
        // only cache small images.
        if (data.length< (100*1024))  {
            handleCache.put(ckey, data);
        }
        return data;
    }

    /**
     * It is unknown where this is good for.
     * @javadoc
     */
    private String toHexString(String str) {
        StringBuffer b=new StringBuffer();
        char[] chb;
        chb=str.toCharArray();
        for (int i=0;i<chb.length;i++) {
            b.append(Integer.toString((int)chb[i],16)+",");
        }
        return b.toString();
    }

    /**
     * Invalidate the Image Cache for a specific Node
     * method only accessable on package level, since only Images should call it..
     *
     * @param node The image node, which is the original of the cached modifications
     */
    protected void invalidate(MMObjectNode imageNode) {
        if (log.isDebugEnabled()) {
            log.debug("Going to invalidate the node, where the original node # " + imageNode.getNumber());
        }
        // first get all the nodes, which are currently invalid....
        // this means all nodes from icache where the field 'ID' == node it's number
        List nodes;
        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField idField = query.getField(getField("id"));
            query.setConstraint(new BasicFieldValueConstraint(idField, new Integer(imageNode.getNumber())));
            nodes = getNodes(query);
        } catch (SearchQueryException e) {
            log.error(e.toString());
            nodes = new java.util.ArrayList(); // do nothing
        }
        Iterator i = nodes.iterator();
        while(i.hasNext()) {
            // delete the icache node
            MMObjectNode invalidNode = (MMObjectNode) i.next();
            removeNode(invalidNode);
            if (log.isDebugEnabled()) {
                log.debug("deleted node with number#" + invalidNode.getNumber());
            }
        }
        handleCache.remove(imageNode.getNumber());
    }

    /**
     * Override the MMObjectBuilder removeNode, to invalidate the LRU ImageCache, when a node gets deleted.
     * Remove a node from the cloud.
     *
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        String ckey = node.getStringValue("ckey");
        log.service("Icaches: removing node " + node.getNumber() + " " + ckey);
        ((Images) mmb.getMMObject("images")).invalidateTemplateCacheNumberCache(node.getIntValue("id"));
        // also delete from LRU Cache
        handleCache.remove(ckey);
        super.removeNode(node);

    }

    /**
     * Returns the image format.
     *
     * @since MMBase-1.6
     */
    protected String getImageFormat(MMObjectNode node) {
        String format = "jpg";
        if (node != null) {
            String ckey    = node.getStringValue("ckey");
            // stupid method, I think the format must be a field of iaches table.
            int fi = ckey.indexOf("f(");
            if (fi > -1) {
                int fi2 = ckey.indexOf(")", fi);
                format = ckey.substring(fi + 2, fi2);
            }
        }
        return format;
    }

    public String getImageMimeType(List params) {
        return getImageMimeType(getNode("" + params.get(0)));
    }


}

