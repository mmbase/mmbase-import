/*
$Id: MMInformix42Node.java,v 1.4 2000-03-29 10:44:50 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.3  2000/03/20 14:28:23  wwwtech
davzev: Changed insert method, now insert will be done depending on DBState.

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.corebuilders.InsRel;

/**
* MMInformix42Node implements the MMJdbc2NodeInterface for
* informix this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @author Daniel Ockeloen
* @version 12 Mar 1997
* @$Revision: 1.4 $ $Date: 2000-03-29 10:44:50 $
*/
public class MMInformix42Node implements MMJdbc2NodeInterface {

	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	MMBase mmb;
	static Vector nameCache=null;
	private int currentdbkey=-1;
	private int currentdbkeyhigh=-1;

	public MMInformix42Node() {
	}

	public void init(MMBase mmb) {
		this.mmb=mmb;
	}

	public boolean create(String tableName) {
			// get us a propertie reader	
			ExtendedProperties Reader=new ExtendedProperties();

			// load the properties file of this server

			String root=System.getProperty("mmbase.config");
		
			debug("create(): reading defines from '"+root+"/defines/"+tableName+".def'");

			Hashtable prop = Reader.readProperties(root+"/defines/"+tableName+".def");
	
			String createtype=(String)prop.get("CREATETYPE_INFORMIX");
			String createtable=(String)prop.get("CREATETABLE_INFORMIX");

			if (createtype!=null && !createtype.equals("")) {	
	    		createtype = Strip.DoubleQuote(createtype,Strip.BOTH);
				try {
					MultiConnection con=mmb.getConnection();
					Statement stmt=con.createStatement();
					if (tableName.equals("authrel")) {
						stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_insrel_t");
					} else {
						stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_object_t");
					}
					debug("Created type "+tableName);
					stmt.close();
					con.close();
				} catch (SQLException e) {
					debug("can't create type "+tableName);
					debug("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_object_t");
					e.printStackTrace();
				}
			}
			else {
				debug("create(): Can't create table no CREATETABLE_ defined");
			}

			if (createtable!=null && !createtable.equals("")) {	
	    		createtable = Strip.DoubleQuote(createtable,Strip.BOTH);
				try {
					MultiConnection con=mmb.getConnection();
					Statement stmt=con.createStatement();
					stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable+" under "+mmb.baseName+"_object");
					debug("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
					//stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
					stmt.close();
					con.close();
				} catch (SQLException e) {
					debug("can't create table "+tableName);
					e.printStackTrace();
				}
			} else {
				debug("create(): Can't create table no CREATETABLE_ defined");
			}
		return(true);
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i) {
		return(decodeDBnodeField(node,fieldtype,fieldname,rs,i,""));
	}

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix) {
			try {
				if (fieldtype.equals("varchar")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp);
					} 
				} else if (fieldtype.equals("varchar_ex")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp);
					}
				} else if (fieldtype.equals("char")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp.trim());
					}
				} else if (fieldtype.equals("lvarchar")) {
					String tmp=rs.getString(i);
					if (tmp==null) {
						node.setValue(prefix+fieldname,"");
					} else {
						node.setValue(prefix+fieldname,tmp.trim());
					}
				} else if (fieldtype.equals("int")) {
					node.setValue(prefix+fieldname,rs.getInt(i));
				} else if (fieldtype.equals("text") || fieldtype.equals("clob")) {
					//node.setValue(prefix+fieldname,getDBText(rs,i));
					node.setValue(prefix+fieldname,"$SHORTED");
				} else if (fieldtype.equals("byte")) {
					//node.setValue(prefix+fieldname,getDBByte(rs,i));
					node.setValue(prefix+fieldname,"$SHORTED");
				} else {
					//debug("Informix42Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
				}
			} catch(SQLException e) {
				debug("Informix42Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
				e.printStackTrace();	
			}
			return(node);
	}


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
				debug("Informix42Node DBtext no ascii "+inp);
				 return("");
			}
			if (rs.wasNull()) {
				debug("Informix42Node DBtext wasNull "+inp);
				return("");
			}
			siz=inp.available(); // DIRTY
		//	debug("Informix42Node DBtext SIZE="+siz);
			if (siz==0 || siz==-1) return("");
			input=new DataInputStream(inp);
			isochars=new byte[siz];
			input.readFully(isochars);
			str=new String(isochars,"ISO-8859-1");
			input.close();
			inp.close();
		} catch (Exception e) {
			debug("Informix42Node text  exception "+e);
			e.printStackTrace();
			return("");
		}
		return(str);
		//return("temp test");
	}


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
			debug("Informix42Node byte  exception "+e);
			e.printStackTrace();
		}
		return(bytes);
	}

	public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul) {
		String result="";
		where=where.substring(7);
		//StringTokenizer parser = new StringTokenizer(where, "+- \n\r",true);
		StringTokenizer parser = new StringTokenizer(where, "+-\n\r",true);
		while (parser.hasMoreTokens()) {
			String part=parser.nextToken();
			String cmd=null;
			if (parser.hasMoreTokens()) {
				cmd=parser.nextToken();
			} 
			//debug("CMD="+cmd+" PART="+part);
			// do we have a type prefix (example episodes.title==) ?
			int pos=part.indexOf('.');
			if (pos!=-1) {
				part=part.substring(pos+1);
			}
			//debug("PART="+part);
			
			// remove fieldname  (example title==) ?
			pos=part.indexOf('=');
			if (pos!=-1) {
				String fieldname=part.substring(0,pos);
				String dbtype=bul.getDBType(fieldname);
				//debug("TYPE="+dbtype);
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
		//debug("char="+operatorChar);
		String value=part.substring(1);
		int pos=value.indexOf("*");
		if (pos!=-1) {
			value=value.substring(pos+1,value.length()-1);
			like=true;
		}
		if (dbtype.equals("varchar")) {
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
		} else if (dbtype.equals("varchar_ex")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				result+="etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))";
				debug("etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))");
				break;
			}

		} else if (dbtype.equals("int")) {
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
	* get byte of a database blob
	*/
	public byte[] getShortedByte(String tableName,String fieldname,int number) {
		try {
			byte[] result=null;
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			debug("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
			ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
			if (rs.next()) {
				result=getDBByte(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			debug("getShortedByte(): trying to load bytes");
			e.printStackTrace();
		}

		return(null);
	}

	/**
	* get text from blob
	*/
	public String getShortedText(String tableName,String fieldname,int number) {
		try {
			String result=null;
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
			if (rs.next()) {
				result=getDBText(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			debug("getShortedText(): trying to load text");
			e.printStackTrace();
		}
		// return "" instead of null, not sure if this is oke
		return("");
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
		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			/* $Id: MMInformix42Node.java,v 1.4 2000-03-29 10:44:50 wwwtech Exp $
			// Original code, not deleted for savety reasons (will be deleted soon),davzev
			String tmp="";
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
					debug("Insert: Error DBState = "+DBState+" unknown!, skipping key: "+key);
				}
			}

			/* $Id: MMInformix42Node.java,v 1.4 2000-03-29 10:44:50 wwwtech Exp $
			// Original code, not deleted for savety reasons (will be deleted soon),davzev
			MultiConnection con=bul.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+tmp+")");
			stmt.setInt(1,number);
			int i=2;
			for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
				String key = (String)e.nextElement();	
				setValuePreparedStatement( stmt, node, key, i );
				i++;
			}
			stmt.executeUpdate();
			stmt.close();
			con.close();
			*/

			// Create the DB statement with DBState values in mind. 
			MultiConnection con=bul.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
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
					debug("Insert: Error DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key);
				}
			}
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("Error on : "+number+" "+owner+" fake");
			e.printStackTrace();
			return(-1);
		}


		bul.signalNewObject(bul.tableName,number);

		node.setValue("number",number);
		debug("INSERTED="+node);
		return(number);	
	}

	
	/**
	* set text array in database
	*/
	public void setDBText(int i, PreparedStatement stmt,String body) {
		byte[] isochars=null;
		try {
			isochars=body.getBytes("ISO-8859-1");
		} catch (Exception e) {
			debug("setDBText(): String contains odd chars");
			debug(body);
			e.printStackTrace();
		}
		try {
			ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
			stmt.setAsciiStream(i,stream,isochars.length);
			stream.close();
		} catch (Exception e) {
			debug("setDBText(): Can't set ascii stream");
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
			debug("getDBByte(): Can't set byte stream");
			e.printStackTrace();
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
						setValuePreparedStatement( stmt, node, key, i );
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
				int num = node.getIntValue("number");
				debug("commit(): changed(insrel,"+bul.tableName+","+num+")");
				mmb.mmc.changedNode(num,bul.tableName,"c");

				// figure out tables to send the changed relations
				// -----------------------------------------------
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			} else {
				int num = node.getIntValue("number");
				debug("commit(): changed("+bul.tableName+","+num+")");
				if (mmb!=null && mmb.mmc!=null) {
					mmb.mmc.changedNode(num,bul.tableName,"c");
				} else {
					debug("commit(): can't send change("+bul.tableName+","+num+"), mmb or mmb.mmc is null");
				}
			}
		}
		return(true);
	}


	/**
	* removeNode
	*/
	public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
		java.util.Date d=new java.util.Date();
		int number=node.getIntValue("number");
		debug("removeNode(): delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number+" at "+d.toGMTString());
		debug("removeNode(): SAVECOPY "+node.toString());
		Vector rels=bul.getRelations_main(number);
		if (rels!=null && rels.size()>0) {
			debug("removeNode("+bul.tableName+","+number+"): PROBLEM! still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
		} else {
			if (number!=-1) {
				try {
					MultiConnection con=mmb.getConnection();
					Statement stmt=con.createStatement();
					stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
					stmt.close();
					con.close();
				} catch (SQLException e) {
					debug("removeNode("+bul.tableName+","+number+"): ERROR: ");
					e.printStackTrace();
				}
			}
			else
				debug("removeNode("+bul.tableName+","+number+"): ERROR: number not valid(-1)!");
		}
		if (bul.broadcastChanges) {
			mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"d");
			if (bul instanceof InsRel) {
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			}
			else
				debug("removeNode("+bul.tableName+","+number+"): WARNING: want to remove it, but not an insrel (not implemented).");
		}

	}

	public synchronized int getDBKey() {
		// get a new key

		if (currentdbkey!=-1) {
			currentdbkey++;
			if (currentdbkey<=currentdbkeyhigh) {
				debug("GETDBKEY="+currentdbkey);
				return(currentdbkey);
			}
		}
	    int number=-1; // not 100% sure if function returns 1 first time
		while (number==-1) {
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("execute function fetchrelkey(10)");
				while (rs.next()) {
					number=rs.getInt(1);
				}
				stmt.close();
				con.close();
			} catch (SQLException e) {
				debug("getDBKey(): ERROR: while getting a new key number");
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException re){
					debug("getDBKey(): Waiting 2 seconds to allow databvase to unlock fetchrelkey()");
				}
				debug("getDBKey(): got key("+currentdbkey+")");
				return(-1);
			}
		}
		currentdbkey=number; // zeg 10
		currentdbkeyhigh=(number+9); // zeg 19 dus indien hoger dan nieuw
		debug("getDBKey(): got key("+currentdbkey+")");
		return(number);
	}

	public boolean created(String tableName) {
		if (nameCache==null) {
			nameCache=getAllNames();
		}
		if (nameCache.contains(tableName)) {
			return(true);
		} else {
			if (tableName.length()>0) {
				debug("created("+tableName+"): ERROR: Not Found '"+tableName+"'");
				return(false);
			} else {
				debug("created("+tableName+"): ERROR: Not Found '"+tableName+"'");
				return(true);
			}
		}
	}


	public synchronized Vector getAllNames() {
		Vector results=new Vector();
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT tabname FROM systables where tabid>99;");
			String s;
			while (rs.next()) {
				s = rs.getString(1);
				if (s!=null) s = s.trim();
				results.addElement(s);
			}	
			stmt.close();
			con.close();
			return(results);
		} catch (Exception e) {
			//e.printStackTrace();
			return(results);
		}
	}
}
