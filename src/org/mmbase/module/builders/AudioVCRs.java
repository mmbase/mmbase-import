/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 */
public class AudioVCRs extends MMObjectBuilder {

	public AudioVCRs (MMBase m) {
		this.mmb = m;
		this.tableName = "avcrs";
		this.description = "VCR like recorder commands for Armin the DJ vwm";
		init ();
	}

	
	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public boolean create () {
		// create the main object table
		// informix
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (title varchr(64) not null"
				+", prognr int"
				+", episodenr int"
				+", bron int not null"
				+", starttime int not null"
				+", endtime int not null"
				+", repeattime int not null"
				+") under "+mmb.baseName+"_object_t");
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
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t ("
				+"primary key(number)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+tableName);
			e.printStackTrace();
		}
		return (false);
	}


	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public int insert(String owner,MMObjectNode node) {
		String title=node.getStringValue("title");
		int prognr=node.getIntValue("prognr");
		int episodenr=node.getIntValue("episodenr");
		int bron=node.getIntValue("bron");
		int begintime=node.getIntValue("begintime");
		int endtime=node.getIntValue("endtime");
		int repeattime=node.getIntValue("repeattime");

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,title);
				stmt.setInt(5,prognr);
				stmt.setInt(6,episodenr);
				stmt.setInt(7,bron);
				stmt.setInt(8,begintime);
				stmt.setInt(9,endtime);
				stmt.setInt(10,repeattime);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("AudioVCR -> Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		return(number);	
	}

	public String getGUIIndicator (MMObjectNode node) {
		String str = node.getStringValue ("title");
		if (str.length () > 15) {
			return (str.substring (0,12) + "...");
		} else {
			return (str);
		}
	}
}
