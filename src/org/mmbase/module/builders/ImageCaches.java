/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: ImageCaches.java,v 1.14 2002-03-18 12:19:27 michiel Exp $
 */
public class ImageCaches extends AbstractImages {

    private static Logger log = Logging.getLoggerInstance(ImageCaches.class.getName());
    private LRUHashtable handlecache = new LRUHashtable(128); // a few images are in memory cache.


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

    protected String getGUIIndicatorWithAlt(MMObjectNode node, String title) {

        String servlet    = MMBaseContext.getHtmlRootUrlPath() + "img.db?";
        String imageThumb = servlet + originalImage(node).getIntValue("cache(s(100x60))");
        String image      = servlet + node.getNumber();
        return("<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imageThumb + "\" border=\"0\" alt=\"" + title + "\" /></a>");
    }

    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicatorWithAlt(node, originalImage(node).getStringValue("title"));
    }

    /**
     * Given a certain ckey, return the cached image node number, if there is one, otherwise return -1.
     * This functions always does a query. The caching must be done somewhere else.
     * This is done because caching on ckey is not necesarry when caching templates.
     *
     * @since MMBase-1.6
     **/
    int getCachedNodeNumber(String ckey) {
        int number = -1;
        try {
            MultiConnection con= mmb.getConnection();
            Statement stmt     = con.createStatement();                                
            
            ResultSet rs = stmt.executeQuery("SELECT " + mmb.getDatabase().getAllowedField("number")+" FROM "+mmb.baseName+"_icaches WHERE ckey='"+ckey+"'");
            if (rs.next()) {
                number = rs.getInt(1);
            } 
            stmt.close();
            con.close();
        } catch (java.sql.SQLException e) {
            log.error("getCkeyNode error " + ckey + ":" + toHexString(ckey));
            log.error(Logging.stackTrace(e));
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
     * Returns the bytes of a cached image. It accepts a vector, just
     * because it is also like this in Images.java. But of course a
     * cached image only uses the first element (number of the node).
     * It also works if the the node is a real image in stead of a
     * cached image, in which case simple the unconverted image is
     * returned.
     *
     * If the node does not exists, it returns empty byte array
     */
    public synchronized byte[] getImageBytes(Vector params) {        
        return getImageBytes("" + params.get(0));
    }

    /**
     * Return the bytes for the cached image with a certain ckey, or null, if not cached.
     **/

    public synchronized byte[] getCkeyNode(String ckey) {
        log.debug("getting ckey node with " + ckey);
        byte[] rtn = (byte []) handlecache.get(ckey);
        if (rtn != null) {
            log.debug("found in handle cache");
        } else {
            log.debug("not found in handle cache, getting it from database.");
            int number = getCachedNodeNumber(ckey);                
            if (number == -1) {
                
            }
            rtn = getImageBytes(number);
            if (rtn == null) { 
                // if it didn't work, also cache this result, to avoid concluding that again..               
                // handlecache.put(ckey, new byte[0]);
                // this should be done differenty.
            } else {
                // only cache small images.
                if (rtn.length< (100*1024)) handlecache.put(ckey, rtn);
            }
        }  
        if (rtn == null && log.isDebugEnabled()) {
            log.debug("getCkeyNode: empty array returned for ckey " + ckey);
        }
        return rtn;
    }
    /**
     * @javadoc
     **/

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
        super.removeNode(node);
	// also delete from LRU Cache
    	handlecache.remove(ckey);
        ((Images) mmb.getMMObject("images")).invalidateTemplateCacheNumberCache();
    }

    /**
     * Determine the MIME type of this Icache node. If the node is not
     * an icache node, but e.g. an images node, then it will return
     * the default mime format, which is 'jpg'. This should be done
     * better, since there is a field itype in the images table, isn't
     * it.?
     *
     *
     *
     * @since MMBase-1.6
     */
    public String getImageMimeType(MMObjectNode node) {
        // determine mimetype:
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
        if (log.isDebugEnabled()) log.debug("using format " + format);        
        return mmb.getMimeType(format);

    }
    /**    
     * Returns the Mime type of this cached image.
     *
     * @since MMBase-1.6
     */
    public String getImageMimeType(Vector params) {
        return getImageMimeType(getNode("" + params.get(0)));
    }

        
}
