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
 * @version 12 Mar 1997
 */
public class ImageCaches extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(ImageCaches.class.getName());
    private LRUHashtable handlecache=new LRUHashtable(128);
    
    public String getGUIIndicator(MMObjectNode node) {
        // code copied from Images.java
        int num = node.getIntValue("id");
        if (num == -1 ) {   // img.db cannot handle uncommited images..
            return null; // ObjectBuilder itself will handle this case.
        }
        // NOTE that this has to be configurable instead of static like this
        String servlet    = MMBaseContext.getHtmlRootUrlPath() + "img.db";
        String image      = servlet + "?" + num;
        String imagethumb = image   + "+s(100x60)";
        String title      = node.getStringValue("title");
        return ("<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imagethumb + "\" border=\"0\" alt=\"" + title + "\" /></a>");
        /*
          String str=node.getStringValue("title");
          if (str.length()>15) {
          return(str.substring(0,12)+"...");
          } else {
          return(str);
          }
        */
    }
    
    public String getGUIIndicator(String field,MMObjectNode node) {
        // code was copied from Images.java, yeach...
        if (field.equals("handle")) { 
            int num = node.getIntValue("id");
            if(num == -1) { // img.db cannot handle uncommited images..
                return null;
            }
            // NOTE that this has to be configurable instead of static like this
            String servlet    = MMBaseContext.getHtmlRootUrlPath() + "img.db";
            String image      = servlet + "?" + num;
            String imagethumb = servlet + "?" + num + "+s(100x60)";
            
            return("<a href=\"" + image + "\" target=\"_new\"><img src=\"" + imagethumb + "\" border=\"0\" alt=\"*\" /></a>");
        }
        // other fields can be handled by the gui function...
        return null;
    }
    
    public String getDefaultUrl(int src) {
        MMObjectNode node=getNode(src);
        String url=node.getStringValue("url");
        return(url);
    }
    
    public synchronized byte[] getCkeyNode(String ckey) {
        ByteArray b;
        byte[] rtn=null;
		
        b=(ByteArray)handlecache.get(ckey);
        if (b==null) {
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt2=con.createStatement();                                

                ResultSet rs=stmt2.executeQuery("SELECT "+mmb.getDatabase().getAllowedField("number")+" FROM "+mmb.baseName+"_icaches WHERE ckey=\""+ckey+"\"");
                if (rs.next()) {
                    int number=rs.getInt(1);
                    MMObjectNode n2=getNode(number);
                    byte[] bytes=n2.getByteValue("handle");	
                    rtn=bytes;
                    if (rtn!=null && bytes.length<(100*1024)) handlecache.put(ckey,new ByteArray(rtn));
                }
                stmt2.close();
                con.close();
            } catch (Exception e) {
                log.error("getCkeyNode error "+ckey+":"+toHexString(ckey));
                e.printStackTrace();
            }
        } else {
            rtn=b.getBytes();
        }
        if (debug && rtn==null) {
            log.debug("getCkeyNode: empty array returned for ckey "+ckey);
        }
        return(rtn);
    }

    private String toHexString(String str) {
        StringBuffer b=new StringBuffer();
        char[] chb;
        chb=str.toCharArray();
        for (int i=0;i<chb.length;i++) {
            b.append(Integer.toString((int)chb[i],16)+",");
        }
        return(b.toString());
    }	

    /**
     * Invalidate the Image Cache for a specific Node
     * method only accessable on package level, since only Images should call it..
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
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
    	String ckey = node.getStringValue("ckey");
        super.removeNode(node);
	// also delete from LRU Cache
    	handlecache.remove(ckey);
    }        
}

class ByteArray {
    byte[] bytes;

    ByteArray(byte[] b) {
        bytes=b;
    }

    public byte[] getBytes() {
        return(bytes);
    }
}
