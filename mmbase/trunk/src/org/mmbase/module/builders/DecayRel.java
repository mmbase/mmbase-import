package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Hans Speijer
 */
public class DecayRel extends InsRel {

	int relnumber=-1;

	public DecayRel() {
	}

	/*
	public DecayRel(MMBase m) {
		this.mmb = m;
		this.tableName = "decayrel";
		this.description = "The relations that match teasers to a pool";
		init();
		m.mmobjs.put(tableName,this);
	}
	*/

	
	/**
	* create an new table in the database
	*/
	/*
	public boolean create() {
		// create the main object table
		// informix
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (online integer not null"
				+", basedecay integer not null"
				+", decay integer not null) under "+mmb.baseName+"_insrel_t");
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
				+"primary key(number)) under "+mmb.baseName+"_insrel");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+tableName);
			e.printStackTrace();
		}
		return (false);
	}
	*/

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		node.setValue("rnumber",634);
		node=alignRelNode(node);		
		int snumber = node.getIntValue("snumber");
		int dnumber = node.getIntValue("dnumber");
		int rnumber = node.getIntValue("rnumber");
		int online = node.getIntValue("online");
		int basedecay = node.getIntValue("basedecay");
		int decay = node.getIntValue("decay");
		return(super.insert(owner,""+snumber+","+dnumber+","+rnumber+","+online+","+basedecay+","+decay));

	}
	*/


	
	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("AuthRel-> Can not guess name");
			} else {
				System.out.println("AuthRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}
}










