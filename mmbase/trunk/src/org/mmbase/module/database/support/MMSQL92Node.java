/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMSQL92Node.java,v 1.9 2000-06-20 14:32:26 install Exp $

$Log: not supported by cvs2svn $
Revision 1.8  2000/06/20 09:32:46  wwwtech
fixed otype first run bug for xml config

Revision 1.7  2000/06/20 08:49:16  wwwtech
better config loading

Revision 1.6  2000/06/20 08:20:14  wwwtech
changed create calls to xml

Revision 1.5  2000/06/06 20:36:25  wwwtech
added XML create and convert code

Revision 1.4  2000/05/15 14:47:48  wwwtech
Rico: fixed double close() bug in getDBText en getDBBtye()

Revision 1.3  2000/04/18 23:16:17  wwwtech
new decodefield routine

Revision 1.2  2000/04/15 21:31:33  wwwtech
daniel: removed overrriden methods

Revision 1.1  2000/04/15 20:42:44  wwwtech
fixes for informix and split in sql92 poging 2

Revision 1.9  2000/04/12 11:34:57  wwwtech
Rico: built type of builder detection in create phase

Revision 1.8  2000/03/31 16:01:49  wwwtech
Davzev: Fixed insert() for when node builder is typedef

Revision 1.7  2000/03/31 15:15:27  wwwtech
Davzev: Changend insert() debug code for DBState checking

Revision 1.6  2000/03/30 13:11:43  wwwtech
Rico: added license

Revision 1.5  2000/03/29 10:44:51  wwwtech
Rob: Licenses changed

Revision 1.4  2000/03/20 16:16:43  wwwtech
davzev: Changed insert method, now insert will be done depending on DBState.

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;


//XercesParser
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

/**
* MMSQL92Node implements the MMJdbc2NodeInterface for
* sql92 types of database this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @author Daniel Ockeloen
* @version 12 Mar 1997
* @$Revision: 1.9 $ $Date: 2000-06-20 14:32:26 $
*/
public class MMSQL92Node implements MMJdbc2NodeInterface {

	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	String createString="CREATETABLE_SQL92";
	public String name="sql92";
	Hashtable typesmap = new Hashtable();
	XMLDatabaseReader parser;
	Hashtable typeMapping = new Hashtable();

	MMBase mmb;

	final static int TYPE_STRING = 1;
	final static int TYPE_INTEGER = 2;
	final static int TYPE_TEXT = 3;
	final static int TYPE_BLOB = 4;

	public MMSQL92Node() {
		typesmap.put("VARCHAR",new Integer(TYPE_STRING));
		typesmap.put("VARSTRING",new Integer(TYPE_STRING));
		typesmap.put("STRING",new Integer(TYPE_STRING));
		typesmap.put("LONG",new Integer(TYPE_INTEGER));
		typesmap.put("INTEGER",new Integer(TYPE_INTEGER));
		typesmap.put("text",new Integer(TYPE_TEXT));
		typesmap.put("TEXT",new Integer(TYPE_TEXT));
		typesmap.put("BLOB",new Integer(TYPE_BLOB));

	}

	public void init(MMBase mmb) {
		this.mmb=mmb;

		// start of new code for XML config support

		String path=MMBaseContext.getConfigPath()+("/databases/");
		if ((new File(path+name+".xml")).exists()) {
			parser=new XMLDatabaseReader(path+name+".xml");
			typeMapping=parser.getTypeMapping();
		} else {
			System.out.println("MMSQL92 -> Missing xml driver for database : "+name);

		}
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i) {
		return(decodeDBnodeField(node,fieldtype,fieldname,rs,i,""));
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix) {
		try {
		
		int type=((Integer)typesmap.get(fieldtype)).intValue();
		switch (type) {
			case TYPE_STRING:
				String tmp=rs.getString(i);
				if (tmp==null) { 
					node.setValue(prefix+fieldname,"");
				} else {
					node.setValue(prefix+fieldname,tmp);
				}
				return(node);
			case TYPE_INTEGER:
				node.setValue(prefix+fieldname,rs.getInt(i));
				return(node);
			case TYPE_BLOB:
				node.setValue(prefix+fieldname,"$SHORTED");
				return(node);
			case TYPE_TEXT:
				node.setValue(prefix+fieldname,"$SHORTED");
				return(node);
			}
		} catch(SQLException e) {
			System.out.println("MMSQL92Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
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
		//System.out.println("fieldname="+fieldname+" type="+dbtype);
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
			input.close(); // this also closes the underlying stream
		} catch (Exception e) {
			System.out.println("MMObjectBuilder -> MMMysql byte  exception "+e);
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
				//System.out.println("MMObjectBuilder -> MMysql42Node DBtext no ascii "+inp);
				 return("");
			}
			if (rs.wasNull()) {
				System.out.println("MMObjectBuilder -> MMysql42Node DBtext wasNull "+inp);
				return("");
			}
			siz=inp.available(); // DIRTY
			if (siz==0 || siz==-1) return("");
			input=new DataInputStream(inp);
			isochars=new byte[siz];
			input.readFully(isochars);
			str=new String(isochars,"ISO-8859-1");
			input.close(); // this also closes the underlying stream
		} catch (Exception e) {
			System.out.println("MMObjectBuilder -> MMMysql text  exception "+e);
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
		if (number == -1) return(-1);

		if (number == 0) return(insertRootNode(bul));

		// Create a String that represents the amount of DB fields to be used in the insert.
		// First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
		String fieldAmounts="?";

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

               	if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
					fieldAmounts+=",?";
				} else {
					debug("Insert: Error DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
               	}
			}
		}

		MultiConnection con=null;
		PreparedStatement stmt=null;
		try {
           // Create the DB statement with DBState values in mind.
			con=bul.mmb.getConnection();
			stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
		} catch(Exception t) {
			t.printStackTrace();
		}
		try {
			stmt.setEscapeProcessing(false);
			// First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
			stmt.setInt(1,number);

            // Prepare the statement for the DB elements to the fieldAmounts String.
            // debug("Insert: Preparing statement using fieldamount String: "+fieldAmounts);
			int j=2;
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
				    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
						setValuePreparedStatement( stmt, node, key, j );
						j++;
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
		String type = node.getDBType(key);
		if (type.equals("int") || type.equals("integer")) {
			stmt.setInt(i, node.getIntValue(key));
		} else if (type.equals("text") || type.equals("clob")) {
			String tmp=node.getStringValue(key);
			if (tmp!=null) {
				setDBText(i, stmt,tmp);
			} else {
				setDBText(i, stmt,"");
			}
		} else if (type.equals("byte")) {	
				setDBByte(i, stmt, node.getByteValue(key));
		} else { 
			String tmp=node.getStringValue(key);
			if (tmp!=null) {
				stmt.setString(i, tmp);
			} else {
				stmt.setString(i, "");
			}
		}
	}


 	/**
 	* insert a new object, normally not used (only subtables are used)
 	*/
 	public int fielddefInsert(String baseName, int oType, String owner,MMObjectNode node) {
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

	/**
	* will be removed once the xml setup system is done
	*/
	public boolean create(MMObjectBuilder bul) {
	
		if (!bul.isXMLConfig()) return(false);
	
		// use the builder to get the fields are create a
		// valid create SQL string
		String result=null;

		Vector sfields=bul.sortedDBLayout;
	
		if (sfields!=null) {
			for (Enumeration e=sfields.elements();e.hasMoreElements();) {
				String name=(String)e.nextElement();
				FieldDefs def=bul.getField(name);
				String part=convertXMLType(def);
				if (result==null) {
					result=part;
				} else {
					result+=", "+part;
				}	
			}
		}
		result=getMatchCREATE(bul.getTableName())+"( number integer not null, "+result+" );";
		System.out.println("XMLCREATE="+result);

		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate(result);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+bul.getTableName());
			e.printStackTrace();
			return(false);
		}
		return(true);
	}


	public boolean createObjectTable(String baseName) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+baseName+"_object (number integer not null, otype integer not null, owner varchar(12) not null);");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+baseName+"_object");
			e.printStackTrace();
		}
		return(true);
	}

	public String convertXMLType(FieldDefs def) {
		
		// get the wanted mmbase type
		String type=def.getDBType();
		// get the wanted mmbase type
		String name=def.getDBName();

		// weird extra code to map to old types
		if (type.equals("varchar")) type="VARCHAR";
		if (type.equals("int")) type="INTEGER";
		// end of weird map
	
		// get the wanted size
		int size=def.getDBSize();

		// get the wanted notnull
		boolean notnull=def.getDBNotNull();
		if (name.equals("otype")) { 
			notnull=true;
			type="INTEGER";
		}

		System.out.println("BBBB1="+name+" "+type);
		String result=name+" "+matchType(type,size,notnull);
		System.out.println("BBBB2="+name+" "+type+" "+result);
		if (notnull) result+=" "+parser.getNotNullScheme();
		return(result);
	}	

	
	String matchType(String type, int size, boolean notnull) {
		String result=null;
		if (typeMapping!=null) {
			dTypeInfos  typs=(dTypeInfos)typeMapping.get(type);
			if (typs!=null) {
				for (Enumeration e=typs.maps.elements();e.hasMoreElements();) {
					 dTypeInfo typ = (dTypeInfo)e.nextElement();
					// System.out.println("WWW="+size+" "+typ.minSize+" "+typ.maxSize+typ.dbType+" "+typs.maps.size());
					// needs smart mapping code
					if (typ.minSize!=-1) {
						if (size>=typ.minSize) {
							if (typ.maxSize!=-1) {
								if (size<=typ.maxSize) {
									result=mapSize(typ.dbType,size);
								}
							} else {
								result=mapSize(typ.dbType,size);
							}
						}	
					} else if (typ.maxSize!=-1) {
						if (typ.maxSize!=-1) {
							if (size<=typ.maxSize) {
								result=mapSize(typ.dbType,size);
							}
						}
					} else {
						result=typ.dbType;
					}
				}
			} 
		}
		return(result);
	}

	String mapSize(String line, int size) {
		int pos=line.indexOf("size");
		if (pos!=-1) {
			String tmp=line.substring(0,pos)+size+line.substring(pos+4);
			return(tmp);
		}
		return(line);
	}

	public String getMatchCREATE(String tableName) {
		return(parser.getCreateScheme()+" "+mmb.baseName+"_"+tableName+" ");
	}

}
