/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
/*
	$Id: FieldDef.java,v 1.3 2000-03-07 09:20:41 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.2  2000/02/25 12:52:15  wwwtech
	Rico: removed the insert method.
	
*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;

/**
 * FieldDef, one of the meta stucture nodes it is used to define the
 * fields (using its nodes) of object types at this point has its
 * own nodes (FieldDefs) instead of MMObjectNodes but that will change
 *
 *
 * @author Daniel Ockeloen
 * @version $Id: FieldDef.java,v 1.3 2000-03-07 09:20:41 wwwtech Exp $
 */
public class FieldDef extends MMObjectBuilder {

	public FieldDef(MMBase m) {
		this.mmb=m;
		this.tableName="fielddef";
		this.description="Field defs";
		init();
		m.mmobjs.put(tableName,this);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("dbtable")) {
			int val=node.getIntValue("dbtable");
			return(""+val+"="+mmb.getTypeDef().getValue(val));
		}
		return(null);
	}

 	/**
 	* insert a new object, normally not used (only subtables are used)
 	*/
 	public int insert(String owner,MMObjectNode node) {
 		int dbtable=node.getIntValue("dbtable");
 		String dbname=node.getStringValue("dbname");
 		String dbtype=node.getStringValue("dbtype");
 		String guiname=node.getStringValue("guiname");
 		String guitype=node.getStringValue("guitype");
 		int guipos=node.getIntValue("guipos");
 		int guilist=node.getIntValue("guilist");
 		int guisearch=node.getIntValue("guisearch");
 		int dbstate=node.getIntValue("dbstate");
 
 		int number=getDBKey();
 		try {
 			MultiConnection con=mmb.getConnection();
 			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_fielddef values(?,?,?,?,?,?,?,?,?,?,?,?)");
 			stmt.setInt(1,number);
 			stmt.setInt(2,oType);
 			stmt.setString(3,owner);
 			stmt.setInt(4,dbtable);
 			stmt.setString(5,dbname);
 			stmt.setString(6,dbtype);
 			stmt.setString(7,guiname);
 			stmt.setString(8,guitype);
 			stmt.setInt(9,guipos);
 			stmt.setInt(10,guilist);
 			stmt.setInt(11,guisearch);
 			stmt.setInt(12,dbstate);
 			stmt.executeUpdate();
 			stmt.close();
 			con.close();
 		} catch (SQLException e) {
 			e.printStackTrace();
 			System.out.println("Error on : "+number+" "+owner+" fake");
 			return(-1);
 		}
 			
 		// THIS MUST BE CHANGED TO SUPPORT MULTI DATABASE
		// LEAVE THIS INSERT IN FOR NOW
 		try {
 			MultiConnection con=mmb.getConnection();
 			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
 			stmt.setInt(1,number);
 			stmt.setInt(2,oType);
 			stmt.setString(3,owner);
 			stmt.executeUpdate();
 			stmt.close();
 			con.close();
 		} catch (SQLException e) {
 			e.printStackTrace();
 			System.out.println("Error on : "+number+" "+owner+" fake");
 			return(-1);
 		}
 		return(number);
 	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
			// figure out the table type to reload the correct FieldDefs
			int otype=node.getIntValue("dbtable");
			TypeDef tbul=(TypeDef)mmb.getMMObject("typedef");
			String bulname=tbul.getValue(otype);
			//System.out.println("Change node ! fielddef DBTABLLE="+otype+" bul="+bulname);
			if (bulname!=null) {
				MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(bulname);
				if (bul!=null) {
					bul.initFields(false);
				} 
			}
		}
		return(true);
	}

}
