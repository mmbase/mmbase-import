/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMPostgres42Node.java,v 1.5 2000-03-31 15:15:42 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.4  2000/03/30 13:11:42  wwwtech
Rico: added license

Revision 1.3  2000/03/29 10:44:51  wwwtech
Rob: Licenses changed

Revision 1.2  2000/03/20 15:40:03  wwwtech
davzev: Changed insert method, now insert will be done depending on DBState.

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.*;


/**
* MMPostgres42Node implements the MMJdbc2NodeInterface for
* mysql this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @author Carlo E. Prelz
* @version 6 Mar 2000
* @$Revision: 1.5 $ $Date: 2000-03-31 15:15:42 $
*/
public class MMPostgres42Node implements MMJdbc2NodeInterface {

	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	MMBase mmb;

	public MMPostgres42Node() {
	}

	public void init(MMBase mmb) {
		this.mmb=mmb;
	}

	public boolean create(String tableName) {
			// get us a propertie reader	
			ExtendedProperties Reader=new ExtendedProperties();

			// load the properties file of this server

			String root=System.getProperty("mmbase.config");
			Hashtable prop = Reader.readProperties(root+"/defines/"+tableName+".def");
		
			String createtable=(String)prop.get("CREATETABLE_PG");


			if (createtable!=null && !createtable.equals("")) {	
    		createtable = Strip.DoubleQuote(createtable,Strip.BOTH);
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				// informix	stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable+" under "+mmb.baseName+"_object");
				//System.out.println("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
/*KP.dbg("GOD: "+"create table "+mmb.baseName+"_"+tableName+" "+createtable+";");*/
				stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
				stmt.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("can't create table "+tableName);
				e.printStackTrace();
			}
			} else {
				System.out.println("MMObjectBuilder-> Can't create table no CREATETABLE_ defined");
			}
		return(true);
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i) {
		return(decodeDBnodeField(node,fieldtype,fieldname,rs,i,""));
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix) {
/*KP.dbg("//// decodeDBnodeField: field is <"+fieldname+"/"+fieldtype+">"); */
			try {
				if (fieldtype.equalsIgnoreCase("VARSTRING") || fieldtype.equalsIgnoreCase("STRING") ||
						fieldtype.equalsIgnoreCase("VARCHAR")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp);
					} 
				} else if (fieldtype.equalsIgnoreCase("VARSTRING_EX")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp);
					}
				} else if (fieldtype.equalsIgnoreCase("lvarchar")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp);
					}
				} else if (fieldtype.equalsIgnoreCase("LONG") || fieldtype.equalsIgnoreCase("int4")) {
					node.setValue(prefix+fieldname,rs.getInt(i));
				} else if (fieldtype.equalsIgnoreCase("TEXT")) {
					//node.setValue(prefix+fieldname,getDBText(rs,i));
					node.setValue(prefix+fieldname,"$SHORTED");
				} else if (fieldtype.equalsIgnoreCase("BLOB")) {
					//node.setValue(prefix+fieldname,getDBByte(rs,i));
					node.setValue(prefix+fieldname,"$SHORTED");
				} else {
					System.out.println("MPostgres42Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
				}
			} catch(SQLException e) {
				System.out.println("MPostgres42Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
				e.printStackTrace();	
			}
			return(node);
	}


	public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul) {
		String result="";
		where=where.substring(7);
		StringTokenizer parser = new StringTokenizer(where, "+-\n\r",true);
		while (parser.hasMoreTokens()) {
			String part=parser.nextToken();
			String cmd=null;
			if (parser.hasMoreTokens()) {
				cmd=parser.nextToken();
			} 
			//System.out.println("CMD="+cmd+" PART="+part);
			// do we have a type prefix (example episodes.title==) ?
			int pos=part.indexOf('.');
			if (pos!=-1) {
				part=part.substring(pos+1);
			}
			//System.out.println("PART="+part);
			
			// remove fieldname  (example title==) ?
			pos=part.indexOf('=');
			if (pos!=-1) {
				String fieldname=part.substring(0,pos);
				String dbtype=bul.getDBType(fieldname);
				//System.out.println("TYPE="+dbtype);
				result+=parseFieldPart(fieldname,dbtype,part.substring(pos+1));
				if (cmd!=null) {
					if (cmd.equals("+")) {
						result+=" AND ";
					} else {
						result+=" AND NOT ";
					}
				}
			}
		}
		return(result);
	}

	public String parseFieldPart(String fieldname,String dbtype,String part) {
		String result="";
		boolean like=false;
		char operatorChar = part.charAt(0);
		//System.out.println("char="+operatorChar);
		String value=part.substring(1);
		int pos=value.indexOf("*");
		if (pos!=-1) {
			value=value.substring(pos+1,value.length()-1);
			like=true;
		}
		// System.out.println("fieldname="+fieldname+" type="+dbtype);
		if (dbtype.equals("var") || dbtype.equals("varchar")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				if (like) {	
					result+="lower("+fieldname+") LIKE '%"+value+"%'";
				} else {
					result+="lower("+fieldname+") LIKE '%"+value+"%'";
				}
				break;
			}
		} else if (dbtype.equals("VARSTRING_EX")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				result+="etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))";
				break;
			}

		} else if (dbtype.equals("LONG") || dbtype.equals("int")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				result+=fieldname+"="+value;
				break;
			case 'N':
				// NOTEQUAL;
				result+=fieldname+"<>"+value;
				break;
			case 'G':
				// GREATER;
				result+=fieldname+">"+value;
				break;
			case 'g':
				// GREATEREQUAL;
				result+=fieldname+">="+value;
				break;
			case 'S':
				// SMALLER;
				result+=fieldname+"<"+value;
				break;
			case 's':
				// SMALLEREQUAL;
				result+=fieldname+"<="+value;
				break;
			}
		}
		return(result);
	}

	/**
	* get text from blob
	*/
	public String getShortedText(String tableName,String fieldname,int number) {
		try {
			String result=null;
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			// System.out.println("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
/*KP.dbg("getShortedText: calling <SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number+">");*/
			ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
			if (rs.next()) {
				result=getDBText(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			System.out.println("MMObjectBuilder : trying to load text");
			e.printStackTrace();
		}
		return(null);
	}


	/**
	* get byte of a database blob
	*/
	public byte[] getShortedByte(String tableName,String fieldname,int number) {
		try {
			byte[] result=null;
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
			if (rs.next()) {
				result=getDBByte(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			System.out.println("MMObjectBuilder : trying to load bytes");
			e.printStackTrace();
		}
		return(null);
	}


	/**
	* get byte of a database blob
	*/
	public byte[] getDBByte(ResultSet rs,int idx) {
		String str=null;
		InputStream inp;
		DataInputStream input;
		byte[] bytes=null;
		int siz;
		try {
			inp=rs.getBinaryStream(idx);
			siz=inp.available(); // DIRTY
			input=new DataInputStream(inp);
			bytes=new byte[siz];
			input.readFully(bytes);
			input.close();
			inp.close();
		} catch (Exception e) {
			System.out.println("MMObjectBuilder -> MMPostgres byte  exception "+e);
			e.printStackTrace();
		}
		return(bytes);
	}

	/**
	* get text of a database blob
	*/
	public String getDBText(ResultSet rs,int idx) {
		String str=null;
		InputStream inp;
		DataInputStream input;
		byte[] isochars;
		int siz;

		if (0==1) return("");
		try {
			inp=rs.getAsciiStream(idx);
			if (inp==null) {
				//System.out.println("MMObjectBuilder -> MPostgres42Node DBtext no ascii "+inp);
				 return("");
			}
			if (rs.wasNull()) {
				System.out.println("MMObjectBuilder -> MPostgres42Node DBtext wasNull "+inp);
				return("");
			}
			siz=inp.available(); // DIRTY
			if (siz==0 || siz==-1) return("");
			input=new DataInputStream(inp);
			isochars=new byte[siz];
			input.readFully(isochars);
			str=new String(isochars,"ISO-8859-1");
			input.close();
			inp.close();
		} catch (Exception e) {
			System.out.println("MMObjectBuilder -> MMPostgres text  exception "+e);
			e.printStackTrace();
			return("");
		}
		return(str);
	}

	/**
	* Insert: This method inserts a new object, normally not used (only subtables are used)
	* Only fields with DBState value = DBSTATE_PERSISTENT or DBSTATE_SYSTEM are inserted.
	* Fields with DBstate values = DBSTATE_VIRTUAL or any other value are skipped.
	* @param bul The MMObjectBuilder.
	* @param owner The nodes' owner.
	* @param node The current node that's to be inserted.
	* @return The DBKey number for this node, or -1 if an error occurs.
	*/
	public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
		int number=node.getIntValue("number");
		// did the user supply a number allready, ifnot try to obtain one
		if (number==-1) number=getDBKey();

		// did it fail ? ifso exit 
		if (number==-1) return(-1);

		if (number==0) return(insertRootNode(bul));
		String fieldAmounts="";
		// String tmp="";
		MultiConnection con=null;
		PreparedStatement stmt=null;
		try {
			con=bul.mmb.getConnection();
		} catch(Exception t) {
			t.printStackTrace();
		}
		if(bul.sortedDBLayout!=null) {
			/* $Id: MMPostgres42Node.java,v 1.5 2000-03-31 15:15:42 wwwtech Exp $
			// This was the original code, can be deleted.
			for (int i=0;i<(bul.sortedDBLayout.size()+1);i++) {
				if (tmp.equals("")) {
					tmp+="?";
				} else {
					tmp+=",?";
				}
			}
			*/

			// Create a String that represents the amount of DB fields to be used in the insert.
			// First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
			fieldAmounts="?";

			// Append the DB elements to the fieldAmounts String.
			for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
				String key = (String)e.nextElement();
				int DBState = node.getDBState(key);
				if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
				  || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
					// debug("Insert: DBState = "+DBState+", adding key: "+key);
					fieldAmounts+=",?";
				} else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
					// debug("Insert: DBState = "+DBState+", skipping key: "+key);
				} else {
					debug("Insert: Error DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
				}
			}	

			/* $Id: MMPostgres42Node.java,v 1.5 2000-03-31 15:15:42 wwwtech Exp $
			// This was the original code, can be deleted.
			try {
				stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+tmp+")");
			} catch(Exception t2) {
				t2.printStackTrace();
			}
			try {
				stmt.setEscapeProcessing(false);
				stmt.setInt(1,number);
				int i=2;
				
				if(bul.sortedDBLayout!=null) {
					for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
						String key = (String)e.nextElement();	
						setValuePreparedStatement( stmt, node, key, i );
						i++;
					}
				}
				stmt.executeUpdate();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("Error on : "+number+" "+owner+" fake");
				try {
					stmt.close();
					con.close();
				} catch(Exception t2) {}
				e.printStackTrace();
				return(-1);
			}
			*/

			try {
				stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
			} catch(Exception t2) {
				t2.printStackTrace();
			}
			try {
				stmt.setEscapeProcessing(false);
				// First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
				stmt.setInt(1,number);

				// Prepare the statement for the DB elements to the fieldAmounts String.
				// debug("Insert: Preparing statement using fieldamount String: "+fieldAmounts);
				int j=2;
				if(bul.sortedDBLayout!=null) {
					for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
						String key = (String)e.nextElement();	
						int DBState = node.getDBState(key);
						if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
						  || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
							// debug("Insert: DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+j);
							setValuePreparedStatement( stmt, node, key, j );
							j++;
						} else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
							// debug("Insert: DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
						} else {
							debug("Insert: Error DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
						}
					}
				}
				stmt.executeUpdate();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("Error on : "+number+" "+owner+" fake");
				try {
					stmt.close();
					con.close();
				} catch(Exception t2) {}
				e.printStackTrace();
				return(-1);
			}
		}
		
		if (node.parent!=null && (node.parent instanceof InsRel) && !bul.tableName.equals("insrel")) {
			try {
				con=mmb.getConnection();
				stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,node.getIntValue("otype"));
				stmt.setString(3,node.getStringValue("owner"));
				stmt.setInt(4,node.getIntValue("snumber"));
				stmt.setInt(5,node.getIntValue("dnumber"));
				stmt.setInt(6,node.getIntValue("rnumber"));
				stmt.executeUpdate();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error on : "+number+" "+owner+" fake");
				return(-1);
			}
		}


		try {
			con=mmb.getConnection();
			stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
			stmt.setInt(1,number);
			stmt.setInt(2,node.getIntValue("otype"));
			stmt.setString(3,node.getStringValue("owner"));
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error on : "+number+" "+owner+" fake");
			return(-1);
		}


		//bul.signalNewObject(bul.tableName,number);
		if (bul.broadcastChanges) {
			if (bul instanceof InsRel) {
				bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
				// figure out tables to send the changed relations
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				n1.delRelationsCache();
				n2.delRelationsCache();
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			} else {
				mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
			}
		}
		node.setValue("number",number);
		//System.out.println("INSERTED="+node);
		return(number);	
	}


	public int insertRootNode(MMObjectBuilder bul) {
		try {
			System.out.println("P4");
			MultiConnection con=bul.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_typedef values(?,?,?,?,?)");
			stmt.setEscapeProcessing(false);
			stmt.setInt(1,0);
			stmt.setInt(2,0);
			stmt.setString(3,"system");
			stmt.setString(4,"typedef");
			stmt.setString(5,"Type definition builder");
			stmt.executeUpdate();
			stmt.close();
			con.close();
			System.out.println("P5");
		} catch (SQLException e) {
			System.out.println("Error on root node");
			e.printStackTrace();
			return(-1);
		}

		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
			stmt.setInt(1,0);
			stmt.setInt(2,0);
			stmt.setString(3,"system");
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error on root node");
			return(-1);
		}
		return(0);	
	}


	/**
	* set text array in database
	*/
	public void setDBText(int i, PreparedStatement stmt,String body) {
		byte[] isochars=null;
		try {
			isochars=body.getBytes("ISO-8859-1");
		} catch (Exception e) {
			System.out.println("MMObjectBuilder -> String contains odd chars");
			System.out.println(body);
			e.printStackTrace();
		}
		try {
			ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
			stmt.setAsciiStream(i,stream,isochars.length);
			stream.close();
		} catch (Exception e) {
			System.out.println("MMObjectBuilder : Can't set ascii stream");
			e.printStackTrace();
		}
	}


	/**
	* set byte array in database
	*/
	public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
		try {
			ByteArrayInputStream stream=new ByteArrayInputStream(bytes);
			stmt.setBinaryStream(i,stream,bytes.length);
			stream.close();
		} catch (Exception e) {
			System.out.println("MMObjectBuilder : Can't set byte stream");
			e.printStackTrace();
		}
	}

	/**
	* commit this node to the database
	*/
	public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
		//  precommit call, needed to convert or add things before a save
		bul.preCommit(node);
		// commit the object
		String values="";
		String key;
		// create the prepared statement
		for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
				key=(String)e.nextElement();
				// a extra check should be added to filter temp values
				// like properties

				// check if its the first time for the ',';
				if (values.equals("")) {
					values+=" "+key+"=?";
				} else {
					values+=", "+key+"=?";
				}
		}
		if (values.length()>0) {
			values="update "+mmb.baseName+"_"+bul.tableName+" set"+values+" WHERE number="+node.getValue("number");
			try {
				MultiConnection con=mmb.getConnection();
				PreparedStatement stmt=con.prepareStatement(values);
				String type;int i=1;
				for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
						key=(String)e.nextElement();
						type=node.getDBType(key);
						if (type.equals("int") || type.equals("integer")) {
							stmt.setInt(i,node.getIntValue(key));
						} else if (type.equals("text")) {
							setDBText(i,stmt,node.getStringValue(key));
						} else if (type.equals("byte")) {
							setDBByte(i,stmt,node.getByteValue(key));
						} else {
							stmt.setString(i,node.getStringValue(key));
						}
						i++;
				}
				stmt.executeUpdate();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return(false);
			}
		}

		node.clearChanged();
		if (bul.broadcastChanges) {
			if (bul instanceof InsRel) {
				bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
				// figure out tables to send the changed relations
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			} else {
				mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
			}
		}
		return(true);
	}


	/**
	* removeNode
	*/
	public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
		int number=node.getIntValue("number");
		System.out.println("MMObjectBuilder -> delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
		System.out.println("SAVECOPY "+node.toString());
		Vector rels=bul.getRelations_main(number);
		if (rels!=null && rels.size()>0) {
			System.out.println("MMObjectBuilder ->PROBLEM! still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
		} else {
		if (number!=-1) {
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (node.parent!=null && (node.parent instanceof InsRel) && !bul.tableName.equals("insrel")) {
				try {
					MultiConnection con=mmb.getConnection();
					Statement stmt=con.createStatement();
					stmt.executeUpdate("delete from "+mmb.baseName+"_insrel where number="+number);
					stmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				stmt.executeUpdate("delete from "+mmb.baseName+"_object where number="+number);
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		}
		if (bul.broadcastChanges) {
			mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"d");
			if (bul instanceof InsRel) {
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			}
		}

	}

	public synchronized int getDBKey() {
		// get a new key
		int number=-1;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select max(number) from "+mmb.getBaseName()+"_object");
			if (rs.next()) {
				number=rs.getInt(1);
				number++;
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("MMBase -> Error getting a new key number");
			return(1);
		}
		return(number);
	}


	/**
	* return the number of relation types in this mmbase and table
	*/
	public boolean created(String tableName) {
		if (size(tableName)==-1) {
			// System.out.println("TABLE "+tableName+" NOT FOUND");
			return(false);
		} else {
			//System.out.println("TABLE "+tableName+" FOUND");
			return(true);
		}
	}


	/**
	* return the number of relation types in this mmbase and table
	*/
	public int size(String tableName) {
		MultiConnection con=null;
		Statement stmt=null;
		try {
			con=mmb.getConnection();
			stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+tableName+";");
			int i=-1;
			while(rs.next()) {
				i=rs.getInt(1);
			}	
			stmt.close();
			con.close();
			return i;
		} catch (Exception e) {
			try {
			stmt.close();
			con.close();
			} catch(Exception t) {}
			return(-1);
		}
	}



	/**
	* set prepared statement field i with value of key from node
	*/
	private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
		throws SQLException
	{
/*KP.dbg("*** setValuePreparedStatement with key="+key);*/
try
{
	Thread.sleep(1);
}
catch(Exception e)
{
}
		String type = node.getDBType(key);
		if(type==null)
			stmt.setString(i, "");
		else if (type.equals("int") || type.equals("integer"))
			stmt.setInt(i, node.getIntValue(key));
		else if (type.equals("text") || type.equals("clob"))
		{
			String tmp=node.getStringValue(key);
			if (tmp!=null)
				setDBText(i, stmt,tmp);
			else
				setDBText(i, stmt,"");
		}
		else if (type.equals("byte"))  	
				setDBByte(i, stmt, node.getByteValue(key));
		else
		{ 
			String tmp=node.getStringValue(key);
			if (tmp!=null) 
				stmt.setString(i, tmp);
			else 
				stmt.setString(i, "");
		}
	}
}
