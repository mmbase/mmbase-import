/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;


/**
 * RelDef, one of the meta stucture nodes it is used to define the
 * possible relation types and are allways mapped to builder (by name
 * this is bad design and will change ones we know how).
 *
 * @author Daniel Ockeloen
 * @version 21 Sept 1997
 */
public class RelDef extends MMObjectBuilder {

	Hashtable relCache=new Hashtable();

	/**
	*  Contruct the table
	*/
	public RelDef() {
	}

	public boolean init() {
		super.init();
		readCache();
		return(true);
	}
	
	private boolean readCache() {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName);
			relCache=new Hashtable();
			Integer number;
			String name;
			while(rs.next()) {
				number=new Integer(rs.getInt(1)); // Number
				name=rs.getString(4); // sname
				relCache.put(name,number);
			}	
			relCache.put("insrel",new Integer(-1)); // Hack HACK Hack
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(true);
	}

    public String getGUIIndicator(MMObjectNode node) {
        int dir=node.getIntValue("dir");
        if (dir==2) {
            return(node.getStringValue("sguiname"));   
 
            // *** was: return(node.getStringValue("SGUIname"));
            // ***  returned, IMC, an empty string.

        } else if (dir==1) {
            String st1=node.getStringValue("sguiname");
            String st2=node.getStringValue("dguiname");
            return(st1+"/"+st2);
        }
        return("");
    }	

	public boolean isRelationTable(String name) {
		boolean rtn=false;
		Object ob;
		ob=relCache.get(name);
		if (ob!=null) {
			rtn=true;
		}
		return(rtn);
	}

	/**
	* uses the name mapping trick to convert from name to number
	* should be removed soon because its bogus way to do this. daniel.
	*/
	public int getGuessedNumber(String name) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select number from "+mmb.baseName+"_"+tableName+" where sname='"+name+"'");
			if (rs.next()) {
				int i=rs.getInt(1);
				stmt.close();
				con.close();
				return(i);
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return(-1);
		}
		return(-1);
	}

	/**
	 * Searches for the relationnummber by finding a reldef where the sname or dname is equal to buildername.
	 * It's recommented to use getGuessedByNames instead, because it's possible to have multiple reldefs with
	 * the same sname or dname.
	 */
 	public int getGuessedByName(String buildername) {
	        Enumeration e=search("WHERE sname='"+buildername+"' OR dname='"+buildername+"'");
 
	        // *** was: Enumeration e=search("WHERE sname='"+buildername+"' AND dname='"+buildername+"'");
	        // *** doesn't work when you have a different sname/dname.
	
	        if (e.hasMoreElements()) {
	            MMObjectNode node=(MMObjectNode)e.nextElement();
	            return(node.getIntValue("number"));
	        } else {
	            return(-1);
        	}
	}

	/**
	 * Searches for the relation number by sname and dname in reldef.
	 * When there's no match found in this order a search with swapped sname and dname will be done.
	 * If no match is found the result will be -1.
	 */
	public int getGuessedByNames(String sname, String dname)
	{	Enumeration e = search("WHERE sname='" + sname + "' AND dname='" + dname + "'");
		if (!e.hasMoreElements()) e = search("WHERE sname='" + dname + "' AND dname='" + sname + "'");

		if (e.hasMoreElements())
		{
	            MMObjectNode node = (MMObjectNode)e.nextElement();
	            return(node.getIntValue("number"));
	        }         

		return(-1);
	}
}




