/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 * @version 3-Feb-1999
 */

public class OAlias extends MMObjectBuilder {

	public final static String buildername = "OAlias";
	private final static LRUHashtable numbercache=new LRUHashtable(128);

	public OAlias() {
	}

	public OAlias(MMBase m) {
		this.mmb=m;
		this.tableName="oalias";
		this.description="Object Aliases name substitution for objects";
		this.dutchSName="Object Alias";
		init();
		m.mmobjs.put(tableName,this);
	}

	/*	
	public boolean create() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (name varchar(64) not null"
				+", description varchar(128)"
				+", destination integer not null) under "+mmb.baseName+"_object_t");
			System.out.println("Created "+tableName);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create type "+tableName);
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t (unique (name)"
				+", primary key(number)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+tableName);
			e.printStackTrace();
		}
		return(false);
	}
	*/

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String name=node.getStringValue("name");
		String description=node.getStringValue("description");
		int destination=node.getIntValue("destination");
		
		if (name==null) name="";
		if (description==null) description="";

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,name);
				stmt.setString(5,description);
				stmt.setInt(6,destination);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error on : "+number+" "+owner);
			return(-1);
		}
		signalNewObject(tableName,number);
		return(number);	
	}
	*/

	public int getNumber(String name) {
		int rtn=-1;
		MMObjectNode node;

		node=(MMObjectNode)numbercache.get(name);
		if (node==null) {
			Enumeration e=search("WHERE name='"+name+"'");
			if (e.hasMoreElements()) {
				node=(MMObjectNode)e.nextElement();
				rtn=node.getIntValue("destination");
				numbercache.put(name,node);
			}
		} else {
			rtn=node.getIntValue("destination");
		}
		return(rtn);
	}

	public MMObjectNode getAliasedNode(String nodename) {
		int nr;
		MMObjectNode node=null;

		nr=getNumber(nodename);
		if (nr>0) {
			node=getNode(nr);
		}
		return(node);
	}
}
