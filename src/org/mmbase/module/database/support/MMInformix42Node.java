/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;


/**
* MMInformix42Node extends MMSQL92Node and implements the MMJdbc2NodeInterface. 
* This class overrides the methods which needed substitution to make mmbase work 
* with informix (tested on Informix Dynamic Server 9.2)
*
* @author Daniel Ockeloen
* @version 12 Mar 1997
* @$Revision: 1.22 $ $Date: 2000-10-18 17:15:11 $
*/
public class MMInformix42Node extends MMSQL92Node implements MMJdbc2NodeInterface {

	private String classname = getClass().getName();
	private boolean debug = false;
	private boolean keySupported = true;
	private void debug( String msg ) { System.out.println( classname +": "+ msg ); }

	private int currentdbkey=-1;
	private int currentdbkeyhigh=-1;

	public MMInformix42Node() {
		// Call the constructor of the parent
		super();
		name="informix";
		if (debug) debug("MMInformix42Node: Processing ...");

	}

	/* 
	*  Method: createObjectTable  
	*          Used to create toplevel database object
	*/
	public boolean createObjectTable(String baseName) {
		try {
			if (debug) debug("Method: CreateObjectTable()");
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+baseName+"_object_t (number integer not null, otype integer not null, owner nvarchar(12) not null);");
			stmt.executeUpdate("create table "+baseName+"_object of type "+baseName+"_object_t ( PRIMARY KEY (number) );");

			if (debug) debug("create row type "+baseName+"_object_t (number integer, otype integer not null, owner nvarchar(12) not null);");
			if (debug) debug("create table "+baseName+"_object of type "+baseName+"_object_t ( PRIMARY KEY (number) );");

			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("can't create table "+baseName+"_object");
			e.printStackTrace();
		}
		return(true);
	}

	/**
	* Method: create
	*         Used to create a new database object 
	*         will be removed once the xml setup system is done
	*/
	public boolean create(MMObjectBuilder bul) {
		if (debug) debug("Method: create()");
                if (!bul.isXMLConfig()) return(false);

		String createtype=null;
		String createtable=null;
		String keyString=null;

		// use the builder to get the fields are create a
                // valid create SQL string
		String tableName=bul.getTableName();

		// Get all the fields for this builder
		Vector sfields=bul.sortedDBLayout;

		// Add the fields to the createtype string
		if (sfields!=null) {
			for (Enumeration e=sfields.elements();e.hasMoreElements();) {
				String name=(String)e.nextElement();
				FieldDefs def=bul.getField(name);
				String part=convertXMLType(def);

				// If this field is marked as key we add it to the keyString
				// this is extremely informix-specific !
				if (keySupported && def.isKey()) {
					if (keyString==null) {
						keyString=" ( UNIQUE ( "+def.getDBName();
					} else {
						keyString+=", "+def.getDBName();	
					}
				}

				// Gather all the fields of this builder without the fields that allready are 
				// inherited from the parent object.

				if (!tableName.equals("insrel")&&!tableName.equals("typerel")&&!name.equals("owner")&&!name.equals("otype")&&!name.equals("snumber")&&!name.equals("dnumber")&&!name.equals("rnumber")) {
					if(createtype==null) {
						createtype=part;
					} else {
						createtype+=", "+part;
					}
				} 
				
				// - Typerel accidentally contains exactly the same fieldnames as insrel, 
				//   but it actually is not a relation, so, we may not exclude nnumber, dnumber and rnumber
				// - Insrel is the parent relation object, we may not exclude nnumber, dnumber and rnumber
	
				if ((tableName.equals("typerel")||tableName.equals("insrel"))&&!name.equals("owner")&&!name.equals("otype")) {
					if(createtype==null) {
						createtype=part;
					} else {
						createtype+=", "+part;
					}
				} 				
			}

			if (keyString!=null) {
				keyString+=" ) )";
			} else {
 				keyString=""; 
			}
		}

		// Create the row type for this database object
		if (createtype!=null && !createtype.equals("")) {
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				if (tableName.equals("object")) {
					stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+";");
					if (debug) debug("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+";");
				} else if (bul instanceof InsRel && !tableName.equals("insrel")) {
					stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t ("+createtype+") under "+mmb.baseName+"_insrel_t;");
					if (debug) debug("create row type "+mmb.baseName+"_"+tableName+"_t ("+createtype+") under "+mmb.baseName+"_insrel_t;");
				} else {
					stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t ("+createtype+") under "+mmb.baseName+"_object_t;");
					if (debug) debug("create row type "+mmb.baseName+"_"+tableName+"_t ("+createtype+") under "+mmb.baseName+"_object_t;");
				}
				stmt.close();
				con.close();
			} catch (SQLException e) {
				debug("can't create type "+tableName);
				debug("create row type "+mmb.baseName+"_"+tableName+"_t ("+createtype+") under "+mmb.baseName+"_object_t;");
				e.printStackTrace();
			}
		} else {
			debug("create(): Can't create table:  no type could be generated");
		}
		try {        
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			if (tableName.equals("object")) {
				stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t;");
				if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t;");
			} else if (bul instanceof InsRel && !tableName.equals("insrel")) {
				stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_insrel;");
				if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_insrel;");
			} else {
				stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_object;");
				if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_object;");
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			if (tableName.equals("object")) {
                                if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t;");
                        } else if (bul instanceof InsRel && !tableName.equals("insrel")) {
                                if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_insrel;");
                        } else {
                                if (debug) debug("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t"+ keyString +" under "+mmb.baseName+"_object;");
                        }

			debug("create(): Can't create table "+tableName);
			e.printStackTrace();
		}
		return(true);
	}

        public String convertXMLType(FieldDefs def) {

                // get the wanted mmbase type
                int type=def.getDBType();
                // get the wanted mmbase type
                String name=def.getDBName();

                // get the wanted size
                int size=def.getDBSize();

                // get the wanted notnull
                boolean notnull=def.getDBNotNull();

                //get the wanted key
                boolean iskey=def.isKey();

                if (name.equals("otype")) {
                        return("otype integer "+parser.getNotNullScheme());
                } else {
                        if (disallowed2allowed.containsKey(name)) {
                                name=(String)disallowed2allowed.get(name);
                        }
                        String result=name+" "+matchType(type,size,notnull);
                        if (notnull) result+=" "+parser.getNotNullScheme();
                        return(result);
                }
        }      

	/**
	* Method: insert
	*         This method inserts a new object, normally not used (only subtables are used)
	*         Only fields with DBState value = DBSTATE_PERSISTENT or DBSTATE_SYSTEM are inserted.
	*         Fields with DBstate values = DBSTATE_VIRTUAL or any other value are skipped.
	*         @param bul The MMObjectBuilder.
	*         @param owner The nodes' owner.
	*         @param node The current node that's to be inserted.
	*         @return The DBKey number for this node, or -1 if an error occurs.
	*/
	public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
		if (debug) debug("Method: insert()");

		if (debug) debug("Inserting node : "+node.toString());
	
		int number=node.getIntValue("number");
		// did the user supply a number allready, ifnot try to obtain one
		if (number==-1) number=getDBKey();
		// did it fail ? ifso exit
		if (number == -1) return(-1);

		// Create a String that represents the amount of DB fields to be used in the insert.
		// First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
		String fieldAmounts="?";

		// Append the DB elements to the fieldAmounts String.
		for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
			String key = (String)e.nextElement();
			int DBState = node.getDBState(key);
			if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
			|| (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
				if (debug) debug("Insert: DBState = "+DBState+", adding key: "+key);
				fieldAmounts+=",?";
			} else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
				if (debug) debug("Insert: DBState = "+DBState+", skipping key: "+key);
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
		if (debug) debug("insert(): Preparing statement using fieldamount String: "+fieldAmounts);
		if (debug) debug("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
		try {
			stmt.setEscapeProcessing(false);

			// First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
			stmt.setInt(1,number);

			// Prepare the statement for the DB elements to the fieldAmounts String.
			if (debug) debug("Insert: Preparing statement using fieldamount String: "+fieldAmounts);

			int j=2;
			for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
				String key = (String)e.nextElement();
				int DBState = node.getDBState(key);
				if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
				|| (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
					if (debug) debug("Insert: DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+j);
					setValuePreparedStatement( stmt, node, key, j );
					j++;
				} else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
					if (debug) debug("insert(): DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
				} else {
					if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
						setValuePreparedStatement( stmt, node, key, j );
						j++;
					} else {
						if (debug) debug("insert(): Error DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
					}
				}
			}
			
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("insert(): Error on : "+number+" "+owner+" fake");
			try {
				stmt.close();
				con.close();
			} catch(Exception t2) {}
			e.printStackTrace();
			return(-1);
		}

		//bul.signalNewObject(bul.tableName,number);
		if (bul.broadcastChanges) {
			if (bul instanceof InsRel) {
				bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"n");
				// figure out tables to send the changed relations
				MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
				MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
				n1.delRelationsCache();
				n2.delRelationsCache();
				mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
				mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
			} else {
				mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"n");
			}
		}
		node.setValue("number",number);
		if (debug) debug("INSERTED="+node);
		return(number);
	}

	/**
	* Method : setValuePreparedStatement
	*          set prepared statement field i with value of key from node
	*/
	private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i) throws SQLException {
		if (debug) debug("Method: setValuePreparedStatement()");

		int type = node.getDBType(key);
                if (type==FieldDefs.TYPE_INTEGER) {
                        stmt.setInt(i, node.getIntValue(key));
                } else if (type==FieldDefs.TYPE_FLOAT) {
                        stmt.setFloat(i, node.getFloatValue(key));
                } else if (type==FieldDefs.TYPE_DOUBLE) {
                        stmt.setDouble(i, node.getDoubleValue(key));
                } else if (type==FieldDefs.TYPE_LONG) {
                        stmt.setLong(i, node.getLongValue(key));
                } else if (type==FieldDefs.TYPE_TEXT) {
                        String tmp=node.getStringValue(key);
                        if (tmp!=null) {
							setDBText(i, stmt,tmp);
                        } else {
                            setDBText(i, stmt,"");
                        }
                } else if (type==FieldDefs.TYPE_BYTE) {
                                setDBByte(i, stmt, node.getByteValue(key));
                } else {
			
                        String tmp=node.getStringValue(key);
		
			String result=null;

			// Actually the following part needs revision 
			// I use setDBText to insert whatever kind of string 
			// into the database ... 
			// For database types nchar and nvarchar we should
			// use stmt.setString, and for clobs we have to
			// use setDBText. 
			// 
	
			if (tmp!=null) {
                                setDBText(i, stmt,tmp);
                        } else {
                                setDBText(i, stmt,"");
                        }
                       
/*  
			if (tmp!=null) {
                                stmt.setString(i, tmp);
                        } else {
                                stmt.setString(i, "");
                        }
*/
                }
        }

	/*
	* Method: decodeDBnodeField
	*         
	*/
	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
		try {
			if (node==null) {
				debug("decodeDBNodeField() ERROR node is null");
			}
			
			fieldname=fieldname.toLowerCase();

			// is this fieldname disallowed ? ifso map it back
			if (allowed2disallowed.containsKey(fieldname)) {
				fieldname=(String)allowed2disallowed.get(fieldname);
			}

			//int type=((Integer)typesmap.get(fieldtype)).intValue();

			int type=node.getDBType(prefix+fieldname);
			
			switch (type) {
			case FieldDefs.TYPE_STRING:
	
				/* Note by Mark: 
				   Fields of type nchar are fixed width fields. If the size of 
				   the character string is shorter than the actual "size", de
				   database extends the string with spaces. 
				   Therefore I need to trim strings that are stored in nchars?
				   But i have n't found a way to detect that? So, i'm trimming 
				   all strings ...

					Note by Rico: that's all fine an such, but would
					it not be handy to test for null before trimming ?
				
			    */

				String tmp=rs.getString(i);
				if (tmp==null) {
					node.setValue(prefix+fieldname,"");
				} else {
					node.setValue(prefix+fieldname,tmp.trim());
				}
				break;
			case FieldDefs.TYPE_INTEGER:
				node.setValue(prefix+fieldname,rs.getInt(i));
				break;
			case FieldDefs.TYPE_LONG:
                node.setValue(prefix+fieldname,(Long)rs.getObject(i));
                break;
            case FieldDefs.TYPE_FLOAT:
                // who does this now work ????
                //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                node.setValue(prefix+fieldname,new Float(rs.getFloat(i)));
                break;
            case FieldDefs.TYPE_DOUBLE:
                node.setValue(prefix+fieldname,(Double)rs.getObject(i));
                break;
            case FieldDefs.TYPE_BYTE:
                node.setValue(prefix+fieldname,"$SHORTED");
                break;
			case FieldDefs.TYPE_TEXT:
				node.setValue(prefix+fieldname,"$SHORTED");
				break;
			default:
				debug("decodeDBNodeField(): unknown type="+type+" fieldname="+fieldname);
				break;
			}			
		} catch(SQLException e) {
			debug("mmObject->"+fieldname+" node="+node.getIntValue("number"));
			e.printStackTrace();
		}
		return(node);
	}

	/*
	* Method: getDBText
	*
	*/
	public String getDBText(ResultSet rs,int idx) {
		if (debug) debug("Method: getDBText()");
                String str=null;
                InputStream inp;
                DataInputStream input;
                byte[] isochars;
                int siz;

                if (0==1) return("");
                try {
                        inp=rs.getAsciiStream(idx);
                        if (inp==null) {
                                if (debug) debug("Informix42Node DBtext no ascii "+inp);
                                 return("");
                        }
                        if (rs.wasNull()) {
                                if (debug) debug("Informix42Node DBtext wasNull "+inp);
                                return("");
                        }
                        siz=inp.available(); // DIRTY
                        if (debug) debug("Informix42Node DBtext SIZE="+siz);
                        if (siz==0 || siz==-1) return("");
                        input=new DataInputStream(inp);
                        isochars=new byte[siz];
                        input.readFully(isochars);
                        str=new String(isochars,"ISO-8859-1");
                        input.close(); // this also closes the underlying stream
                } catch (Exception e) {
                        debug("Informix42Node text  exception "+e);
                        e.printStackTrace();
                        return("");
                }
                return(str);
        }


	/*
	* Method: getDBByte
	*
	*/
	public byte[] getDBByte(ResultSet rs,int idx) {
		if (debug) debug("--- Now in getDBByte ---");
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
                        debug("Informix42Node byte  exception "+e);
                        e.printStackTrace();
                }
                return(bytes);
        }

	/*
	* Method: getMMNodeSearch2SQL
	*
	*/
	/* removed to compile for new version, daniel.
	public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul) {
		if (debug) debug("Method: getMMNodeSearch2SQL()");
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
	*/

	/*
	* Method: parseFieldPart
	*
	*/
	public String parseFieldPart(String fieldname,String dbtype,String part) {
		if (debug) debug("Method: parseFieldPart()");
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
                                if (debug) debug("etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))");
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
	* Method: getShortedByte
        *         get byte of a database blob
        */
        public byte[] getShortedByte(String tableName,String fieldname,int number) {
		if (debug) debug("Method: getShortedByte()");
                try {
                        byte[] result=null;
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        if (debug) debug("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
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
	* Method: getShortedText
        *         get text from blob
        */
        public String getShortedText(String tableName,String fieldname,int number) {
                try {
			if (debug) debug("Method: getShortedText()");
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
	* Method: setDBText
        *         set text array in database
        */
        public void setDBText(int i, PreparedStatement stmt,String body) {
		if (debug) debug("Method: setDBText(): pos "+i+" body '"+body+"'");
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
	* Method: setDBByte
        *         set byte array in database
        */
        public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
		if (debug) debug("Method: setDBByte()");
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
	* Method: commit
        *         commit this node to the database
        */
	/* removed to compile for new version, daniel
        public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
		if (debug) debug("Method: commit()");
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
                                if (debug) debug("commit(): changed(insrel,"+bul.tableName+","+num+")");
                                mmb.mmc.changedNode(num,bul.tableName,"c");

                                // figure out tables to send the changed relations
                                // -----------------------------------------------
                                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
                                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
                        } else {
                                int num = node.getIntValue("number");
                                if (debug) debug("commit(): changed("+bul.tableName+","+num+")");
                                if (mmb!=null && mmb.mmc!=null) {
                                        mmb.mmc.changedNode(num,bul.tableName,"c");
                                } else {
                                        debug("commit(): can't send change("+bul.tableName+","+num+"), mmb or mmb.mmc is null");
                                }
                        }
                }
                return(true);
        }
	*/

        /**
	* Method: removeNode
        *          
        */
        public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
		if (debug) debug("Method: removeNode()");
                java.util.Date d=new java.util.Date();
                int number=node.getIntValue("number");
                // temp removed (daniel) despr. if (debug) debug("removeNode(): delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number+" at "+d.toGMTString());
                if (debug) debug("removeNode(): SAVECOPY "+node.toString());
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

	/*
	* Method: getDBKey()
	*
	*/
	/* Commented out because it doesn't work very well. getDBKey() is not overridden, MMSQL92node is used
        public synchronized int getDBKey() {
		if (debug) debug("Method: getDBKey()");
                // get a new key

                if (currentdbkey!=-1) {
                        currentdbkey++;
                        if (currentdbkey<=currentdbkeyhigh) {
                                if (debug) debug("GETDBKEY="+currentdbkey);
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
                                        debug("getDBKey(): Waiting 2 seconds to allow database to unlock fetchrelkey()");
                                }
                                debug("getDBKey(): got key("+currentdbkey+")");
                                return(-1);
                        }
                }
                currentdbkey=number; // zeg 10
                currentdbkeyhigh=(number+9); // zeg 19 dus indien hoger dan nieuw
                if (debug) debug("getDBKey(): got key("+currentdbkey+")");
                return(number);
        }
	*/
	/*
	* Method: getAllNames()
	*
	*/
        public synchronized Vector getAllNames() {
		if (debug) debug("Method: getAllNames()");
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
