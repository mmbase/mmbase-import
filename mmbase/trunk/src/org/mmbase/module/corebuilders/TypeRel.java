/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;

/**
 * TypeRel, one of the meta stucture nodes it is used to define the
 * allowed relations between two object types this is used by editors
 * and other software to see/enforce the wanted structure
 *
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class TypeRel extends MMObjectBuilder {

	LRUHashtable artCache=new LRUHashtable(128);

	/**
	* a core object, defined the allowed relations between different typedefs
	* so for example images are allowed to be linked to groups etc etc
	*/
	public TypeRel(MMBase m) {
		this.mmb=m;
		this.tableName="typerel";
		init();
		m.mmobjs.put(tableName,this);
	}

	

	/**
	* Get a TypeRel
	*/
	/*
	public MMObjectNode get(int reltypenumber) {
		try {
			int number=1;
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tableName+" where rnumber="+reltypenumber);
			if (rs.next()) {
				TypeRelNode node=new TypeRelNode(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4));
				stmt.close();
				con.close();
				return(node);
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
		return(null);
	}
	*/

	public Enumeration getAllowedRelations(MMObjectNode mmnode) {
		int number=mmnode.getIntValue("otype");
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+number+" OR dnumber="+number+";");
			MMObjectNode node;
			Vector results=new Vector();
			while(rs.next()) {
				node=new MMObjectNode(this);
				node.setValue("number",rs.getInt(1));
				node.setValue("otype",rs.getInt(2));
				node.setValue("owner",rs.getString(3));
				node.setValue("snumber",rs.getInt(4));
				node.setValue("dnumber",rs.getInt(5));
				node.setValue("rnumber",rs.getInt(6));
				node.setValue("max",rs.getInt(4));
				results.addElement(node);
			}	
			stmt.close();
			con.close();
			return(results.elements());
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
	}


	public int getAllowedRelationType(int snum,int dnum) {
		try {
			// putting a cache here is silly but makes editor faster !
			Integer i=(Integer)artCache.get(""+snum+" "+dnum);
			if (i!=null) return(i.intValue());
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT rnumber FROM "+mmb.baseName+"_"+tableName+" WHERE (snumber="+snum+" AND dnumber="+dnum+") OR (dnumber="+snum+" AND snumber="+dnum+");");
			MMObjectNode node;
			Vector results=new Vector();
			if (rs.next()) {
				int j=rs.getInt(1);
				artCache.put(""+snum+" "+dnum,new Integer(j));
				stmt.close();
				con.close();
				return(j);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return(-1);
		}
		return(-1);
	}


	public Enumeration getAllowedRelations(MMObjectNode n1,MMObjectNode n2) {
		int number1=n1.getIntValue("otype");
		int number2=n2.getIntValue("otype");
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE (snumber="+number1+" AND dnumber="+number2+") OR (snumber="+number2+" AND dnumber="+number1+");");
			MMObjectNode node;
			Vector results=new Vector();
			while(rs.next()) {
				node=new MMObjectNode();
				node.setValue("number",rs.getInt(1));
				node.setValue("otype",rs.getInt(2));
				node.setValue("owner",rs.getString(3));
				node.setValue("snumber",rs.getInt(4));
				node.setValue("dnumber",rs.getInt(5));
				node.setValue("rnumber",rs.getInt(6));
				node.setValue("max",rs.getInt(7));
				results.addElement(node);
			}	
			stmt.close();
			con.close();
			return(results.elements());
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
	}


	public String getGUIIndicator(String field,MMObjectNode node) {
		try {
		if (field.equals("snumber")) {
			return(mmb.getTypeDef().getValue(node.getIntValue("snumber")));
		} else if (field.equals("dnumber")) {
			return(mmb.getTypeDef().getValue(node.getIntValue("dnumber")));
		} else if (field.equals("rnumber")) {
			MMObjectNode node2=mmb.getRelDef().getNode(node.getIntValue("rnumber"));
			return(node2.getGUIIndicator());
		}
		} catch (Exception e) {}
		return(null);
	}

}
