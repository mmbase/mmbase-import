/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.List;
import java.util.Enumeration;

import java.sql.*;  // sql
import org.mmbase.module.database.*;  // sql

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import javax.servlet.http.HttpServletResponse;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: ImageCaches.java,v 1.25.2.2 2003-12-12 13:05:43 vpro Exp $
 */
public class ImageCaches extends AbstractImages {

    private static Logger log = Logging.getLoggerInstance(ImageCaches.class.getName());

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
    protected String getGUIIndicatorWithAlt(MMObjectNode node, String title, HttpServletResponse res, String sessionName) {
        String servlet    = getServletPath() + (usesBridgeServlet ? sessionName : "");
        MMObjectNode origNode = originalImage(node);
        String imageThumb;
        if (origNode != null) {
            List args = new java.util.Vector();
            args.add("s(100x60)");
            imageThumb = servlet + origNode.getFunctionValue("cache", args);
            if (res != null) imageThumb = res.encodeURL(imageThumb);
        } else {
            imageThumb = "";
        }
        String image      = servlet + node.getNumber();
        if (res != null) image = res.encodeURL(image);
        return "<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imageThumb + "\" border=\"0\" alt=\"" + title + "\" /></a>";
    }

    protected String getSGUIIndicator(String session, HttpServletResponse res, MMObjectNode node) {
        MMObjectNode origNode = originalImage(node);
        return getGUIIndicatorWithAlt(node, (origNode != null ? origNode.getStringValue("title") : ""), res, session);
    }

    /*
    public String getTitle(MMObjecNode node) {
        return originalImage(node).getStringValue("title");
    }
    public String getDescription(MMObjectNode node) {
        return originalImage(node);
    }
    */

    /**
     * Given a certain ckey, return the cached image node number, if there is one, otherwise return -1.
     * This functions always does a query. The caching must be done somewhere else.
     * This is done because caching on ckey is not necesarry when caching templates.
     * @sql
     * @since MMBase-1.6
     **/
    int getCachedNodeNumber(String ckey) {
        int number = -1;
        MultiConnection con = null;
        Statement stmt = null;
        try {
            con = mmb.getConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + mmb.getDatabase().getNumberString()+" FROM "+mmb.baseName+"_icaches WHERE ckey='"+ckey+"'");
            if (rs.next()) {
                number = rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            log.error("getCkeyNode error " + ckey + ":" + toHexString(ckey));
            log.error(Logging.stackTrace(e));
        } finally {
            mmb.closeConnection(con, stmt);
        }
        return number;
    }

    /**
     * Gets the handle bytes from a node.
     * @param n The node to receive the bytes from. It might be null, then null is returned.
     */
    private  synchronized byte[] getImageBytes(MMObjectNode n) {
        if (n == null) {
            log.debug("node was not found");
            return null;
        } else {
            if (log.isDebugEnabled()) log.debug("node was found " + n.getNumber());
            byte[] bytes = n.getByteValue("handle");
            if (bytes == null) {
                log.debug("handle was null!");
                return null;
            }
            if (log.isDebugEnabled()) log.debug("found " + bytes.length + " bytes");
            return bytes;
        }
    }

    private  synchronized byte[] getImageBytes(int number) {
        return getImageBytes(getNode(number));
    }

    private synchronized byte[] getImageBytes(String number) {
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
    public synchronized byte[] getImageBytes(List params) {
        return getImageBytes("" + params.get(0));
    }

    /**
     * Return the bytes for the cached image with a certain ckey, or null, if not cached.
     */
    public synchronized byte[] getCkeyNode(String ckey) {
        log.debug("getting ckey node with " + ckey);
	if(handleCache.contains(ckey)) {
	    // found the node in the cache..
	    return (byte []) handleCache.get(ckey);
	}
	log.debug("not found in handle cache, getting it from database.");
	int number = getCachedNodeNumber(ckey);

	if (number == -1) {
	    // we dont have a cachednode yet, return null	    
	    log.info(ckey+" was not cached.");
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
    void invalidate(MMObjectNode node) {
        log.debug("gonna invalidate the node, where the original node # " + node.getNumber());
        // first get all the nodes, which are currently invalid....
        // this means all nodes from icache where the field 'ID' == node it's number
        Enumeration invalidNodes = search("WHERE id=" + node.getNumber());
        while(invalidNodes.hasMoreElements()) {
            // delete the icache node
            MMObjectNode invalidNode = (MMObjectNode) invalidNodes.nextElement();
            removeNode(invalidNode);
            log.debug("deleted node with id#" + node.getNumber());
        }
        handleCache.remove(node.getNumber());
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
 
