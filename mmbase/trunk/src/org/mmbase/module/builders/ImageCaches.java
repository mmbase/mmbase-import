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
		int num=node.getIntValue("id");
		if (num!=-1) {
			return("<IMG SRC=\"/img.db?"+node.getIntValue("id")+"+s(100x60)\">");
		}
		return(null);
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
		if (field.equals("handle")) {
			int num=node.getIntValue("id");
			if (num!=-1) {
				return("<IMG SRC=\"/img.db?"+num+"+s(100x60)\">");
			}
		}
		return(null);
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



				ResultSet rs=stmt2.executeQuery("SELECT "+mmb.getDatabase().getAllowedField("number")+" FROM "+mmb.baseName+"_icaches WHERE ckey='"+ckey+"'");
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
