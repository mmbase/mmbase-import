package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class ImageCaches extends MMObjectBuilder {
	private LRUHashtable handlecache=new LRUHashtable(128);

	

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String ckey=node.getStringValue("ckey");
		int id=node.getIntValue("id");
		byte[] handle=node.getByteValue("handle");
		int filesize=node.getIntValue("filesize");

		int number=getDBKey();
		if (number==-1) return(-1);
		MultiConnection con=mmb.getConnection();
		try {
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?)");
				stmt.setEscapeProcessing(false);
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,ckey);
				stmt.setInt(5,id);
				mmb.getDatabase().setDBByte(6,stmt,handle);
				stmt.setInt(7,filesize);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ImageCaches -> Error on : "+number+" "+owner+" fake");
			System.out.println("ImageCaches -> Forced database close");
			try {
			con.close();
			
			} catch(Exception f) {}
			return(-1);
		}
		return(number);
	}
	*/

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
			/*
			String val=node.getStringValue("handle");
			if (val!=null) {
				if (val.indexOf('I')==0) {
					int num=node.getIntValue("number");
					if (num!=-1) {
						return("<IMG SRC=\"/img.db?"+node.getIntValue("number")+"+s(100x60)\">");
					} else {
						return("<IMG SRC=\"/img.db?"+val+"+s(100x60)\">");
					}
				} else {
					return("<IMG SRC=\"/pictures.db?"+val+"+s(100x60)\">");
				}	
			}
			*/
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
				ResultSet rs=stmt2.executeQuery("SELECT handle FROM "+mmb.baseName+"_icaches WHERE ckey='"+ckey+"'");
				if (rs.next()) {
					byte[] bytes=mmb.getDatabase().getDBByte(rs,1);
					rtn=bytes;
					if (rtn!=null && bytes.length<(100*1024)) handlecache.put(ckey,new ByteArray(rtn));
				}
				stmt2.close();
				con.close();
			} catch (Exception e) {
				System.out.println("getCkeyNode error "+ckey+":"+toHexString(ckey));
				e.printStackTrace();
			}
		} else {
			rtn=b.getBytes();
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
