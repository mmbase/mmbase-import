/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.ParseException;
import org.mmbase.module.database.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.support.*;
import org.mmbase.module.database.MultiConnection;

/**
 *
 * Main Builder will be extended for different types builders are the
 * core of the MMBase system they create, delete and search the MMObjectNodes
 * Unlike MMObjectNodes they are implemented per object type (url, images etc etc)
 * normally these are only defined as Dummy object or in org.mmase.builders.*
 * 
 * @author Daniel Ockeloen
 * @version 14 Sept 1997
 */
public class MMObjectBuilder extends MMTable {
	private String classname = getClass().getName();
	public boolean debug=false;

	public static LRUHashtable obj2type;
	public static LRUHashtable nodeCache = new LRUHashtable(1024*4);
	static String currentPreCache=null;
	private static Hashtable fieldDefCache=new Hashtable(40);
	public int oType=0; // type of the object in database (overloaded).
	public String description="Base Object"; // description of this type (overloaded)
	private Hashtable fields;
//	public Hashtable fields = new Hashtable();
	Vector sortedEditFields = null;
	Vector sortedListFields = null;
	Vector sortedFields = null;

	public Vector sortedDBLayout = null;

	String GUIIndicator="no info";
	public static MMJdbc2NodeInterface database = null;
	public String searchAge="31";
	private String dutchSName="onbekend";
	Hashtable singularNames;
	Hashtable pluralNames;
	public String className="onbekend";
 	public boolean replaceCache=true;
	public boolean broadcastChanges=true;
	Vector remoteObservers = new Vector();
	Vector localObservers = new Vector();
	Statistics statbul;
	private Vector qlist=new Vector();
	Hashtable nameCache=new Hashtable();
	private boolean isXmlConfig=false;

	/**
	* base object, should not be used
	*/
	//  Needs to be fixed for 1.2 only one constructor !!
	public MMObjectBuilder() {
	}	

	/**
	* init this builder
	*/
	public boolean init() {
		database=mmb.getDatabase();

		if (!created()) {
			debug("init(): Create "+tableName);
			create();
		}
		if (!tableName.equals("object") && mmb.TypeDef!=null) {
			oType=mmb.TypeDef.getIntValue(tableName);
			if (oType==-1) {
				//mmb.TypeDef.insert("system",tableName,description);
				MMObjectNode node=mmb.TypeDef.getNewNode("system");
				node.setValue("name",tableName);
				if (description==null) description="not defined in this langauge";
				node.setValue("description",description);
				node.insert("system");
				oType=mmb.TypeDef.getIntValue(tableName);
			}
		} else {
			if(!tableName.equals("typedef")) {
				debug("init(): for tablename("+tableName+") -> can't get to typeDef");
			}
		}
		// hack to override the hard  fields by database (bootstrap)
		// Hashtable tmp=initFields();
		// if (tmp.size()>0) fields=tmp;
		//if (fieldDefCache==null) initAllFields();
		if (obj2type==null) init_obj2type(); // RICO switched ON
		//if (fields==null) initFields(true);
		return(true);
	}

	/**
	* create new object type , normally not used (only subtables are used)
	*/
	public boolean create() {
		return(database.create(this));
	}

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public int insert(int oType,String owner) {
		return(-1);
	}

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public int insert(String owner, MMObjectNode node) {
		// test with counting
		statCount("insert");
		
		try {
			int n;
			n=database.insert(this,owner,node);
			if (n>=0) nodeCache.put(new Integer(n),node);
			return(n);
		} catch(Exception e) {
			debug("ERROR INSERT PROBLEM !");
			debug("Error node="+node);
			e.printStackTrace();
			return(-1);
		}
	}

	/**
	* ones a insert is done in the editor this method is called
	*/
	public int insertDone(EditState ed, MMObjectNode node) {
		return(-1);	
	}

	/**
	* Commit object to the database
	*/
	public int preEdit(EditState ed, MMObjectNode node) {
		return(-1);	
	}

	/**
	* precommit is called before commit by the editor
	*/
	public MMObjectNode preCommit(MMObjectNode node) {
		return(node);
	}

	/**
	* commit this node to the database
	*/
	public boolean commit(MMObjectNode node) {
		return(database.commit(this,node));
	}


	/**
	* Create cache for obj2type
	*/
	public synchronized void init_obj2type() {

		if (obj2type!=null) return;
		obj2type=new LRUHashtable(20000);

		if (false) {
	
			// do the query on the database
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("SELECT number,otype  FROM "+mmb.baseName+"_object;");
				while(rs.next()) {
					obj2type.put(new Integer(rs.getInt(1)),new Integer(rs.getInt(2)));	
				}	
				stmt.close();
				con.close();
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=new MMObjectNode(this);
		node.setValue("number",-1);
		node.setValue("owner",owner);
		node.setValue("otype",oType);
		setDefaults(node);
		return(node);
	}

	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
	}

	/**
	* removeNode
	*/
	public void removeNode(MMObjectNode node) {
/*
		// check if node.name in nameCache, remove that also.
		// --------------------------------------------------
		String name = node.getStringValue("name");
		if( name != null && !name.equals("")) {
			String sNumber = (String)nameCache.get(name);
			try {
				int number = Integer.parseInt( sNumber );	
				if( number == node.getIntValue("number")) {
					nameCache.remove( name );	
				} 
			} catch( NumberFormatException e ) {
				debug("removeNode("+node+"): ERROR: snumber("+sNumber+") from nameCache not valid number!");
			}
		}
*/
		database.removeNode(this,node);
	}

	/**
	* removeRelations
	*/
	public void removeRelations(MMObjectNode node) {
		int number=node.getIntValue("number");
		if (number!=-1) {
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				stmt.executeUpdate("delete from "+mmb.baseName+"_insrel where snumber="+number+" or dnumber="+number);
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* get ObjectNode
	*/
	public MMObjectNode getNode(int number) {
		return(getNode(String.valueOf(number)));
	}



	/**
	* is this node cached at this moment ?
	*/
	public boolean isNodeCached(int number) {
		if (nodeCache.containsKey(new Integer(number))) {
			return(true);
		} else {
			return(false);
		}
	}

	/**
	* get ObjectNode
	*/
	public synchronized MMObjectNode getNode(String number) {

		if( number == null )
		{
			debug("getNode("+number+"): ERROR: for tablename("+tableName+"): node is null!");
			return null;
		}

		// test with counting
		statCount("getnode");
		if (number.equals("-1")) {
			debug(" ("+tableName+") nodenuber == -1");
			return(null);
		}

		// cache setup
		MultiConnection con;
		MMObjectNode node=(MMObjectNode)nodeCache.get(new Integer(number));
		if (node!=null) { 
			// lets add a extra asked counter to make a smart cache
			int c=node.getIntValue("CacheCount");
			c++;
			node.setValue("CacheCount",c);
			return(node);	
		}

		// do the query on the database
		try {
			String bul="typedef";
			// first try our mega cache for the convert
			int cached=-1;
			if (obj2type!=null) {
				Integer tmpv=(Integer)obj2type.get(new Integer(number));	
				if (tmpv!=null) cached=tmpv.intValue();	
				if (cached>0) {
					bul=mmb.getTypeDef().getValue(cached);
				}
			}
			int bi=0;
			if (cached==-1 || cached==0) {	
			// first get the otype to select the correct builder
				con=mmb.getConnection();
				Statement stmt2=con.createStatement();
				ResultSet rs=stmt2.executeQuery("SELECT otype FROM "+mmb.baseName+"_object WHERE number="+number);
				if (rs.next()) {
					bi=rs.getInt(1);
					// hack hack need a better way
					if (bi!=0) {
						bul=mmb.getTypeDef().getValue(bi);
						if (obj2type!=null) obj2type.put(new Integer(Integer.parseInt(number)),new Integer(bi));	
					}
				}
				stmt2.close();
				con.close();
			}
			if (bul==null) {
				debug("getNode(): got a null type table ("+bi+") on node ="+number+", possible non table query blocked !!!");
				return(null);
			}

			// weird hack needed on vpro site !
			if (number.equals("14") || number.equals("13")) bul="reldef";
		
			con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+bul+" WHERE number="+number);
			if (rs.next()) {
				// create a new object and add it to the result vector
				MMObjectBuilder bu=mmb.getMMObject(bul);
				if (bu==null) debug("getMMObject did not return builder on : "+bul);
				node=new MMObjectNode(bu);
				ResultSetMetaData rd=rs.getMetaData();
				String fieldname;String fieldtype;
				for (int i=1;i<=rd.getColumnCount();i++) {
					fieldname=rd.getColumnName(i);	
					fieldtype=rd.getColumnTypeName(i);	
					node=database.decodeDBnodeField(node,fieldtype,fieldname,rs,i);
				}
				nodeCache.put(new Integer(number),node);
				stmt.close();
				con.close();
				// clear the changed signal
				node.clearChanged();
			} else {
				stmt.close();
				con.close();
				debug("getNode(): Node not found "+number);
				node=null; // not found
			}

			// return the results
			return(node);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(null);
		}
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where) {
		return searchVector(where).elements();
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where) {
		// do the query on the database
		if (where==null) where="";
		if (where.indexOf("MMNODE")!=-1) {
			where=convertMMNode2SQL(where);
		} else {
			//where=QueryConvertor.altaVista2SQL(where);
			where=QueryConvertor.altaVista2SQL(where,database);
		}
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+where;
		return(basicSearch(query));
	}

	/**
	* Enumerate all the objects that are within this set
	*/
	public Vector searchVectorIn(String in) {
		// do the query on the database
		if (in==null || in.equals("")) return(new Vector());
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" where number in ("+in+")";
		return(basicSearch(query));
	}

	private Vector basicSearch(String query) {
		// test with counting
		statCount("search");
	
		MultiConnection con=null;
		Statement stmt=null;
		try {
			con=mmb.getConnection();
			stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			Vector results=readSearchResults(rs);
			stmt.close();
			con.close();
			// return the results
			return(results);
		} catch (Exception e) {
			// something went wrong print it to the logs
			debug("basicSearch(): ERROR in search "+query);
			try {
				if (stmt!=null) stmt.close();
			} catch(Exception g) {}
			try {
				if (con!=null) con.close();
			} catch(Exception g) {}
			//e.printStackTrace();
		}
		return new Vector(); // Return an empty Vector
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchNumbers(String where) {
		// do the query on the database
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
		//	ResultSet rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where));
			ResultSet rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where,database));
			Vector results=new Vector();
			Integer number;
			String tmp;
			while(rs.next()) {
				results.addElement(new Integer(rs.getInt(1)));
			}	
			stmt.close();
			con.close();
			return(results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(null);
		}
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where,String sort) {
		return(searchVector(where,sort).elements());
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String sort,String in) {
		return(searchVectorIn(where,sort,in).elements());
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String in) {
		return(searchVectorIn(where,in).elements());
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where,String sort,boolean direction) {
		return(searchVector(where,sort,direction).elements());
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String sort,boolean direction,String in) {
		return(searchVectorIn(where,sort,direction,in).elements());
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where,String sorted) {
		// do the query on the database
		if (where==null) {
			where="";
		} else if (where.indexOf("MMNODE")!=-1) {
			where=convertMMNode2SQL(where);
		} else {
			//where=QueryConvertor.altaVista2SQL(where);
			where=QueryConvertor.altaVista2SQL(where,database);
		}
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+where+" ORDER BY "+sorted;
		return(basicSearch(query));
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVectorIn(String where,String sorted,String in) {
		// do the query on the database
		if (in!=null && in.equals("")) return(new Vector());
		//String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where)+" AND number in ("+in+") ORDER BY "+sorted;
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where,database)+" AND number in ("+in+") ORDER BY "+sorted;
		return(basicSearch(query));
	}

	/*
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVectorIn(String where,String in) {
		// do the query on the database
		if (in==null || in.equals("")) return(new Vector());
		//String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where)+" AND number in ("+in+")";
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where,database)+" AND number in ("+in+")";
		return(basicSearch(query));
	}


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where,String sorted,boolean direction) {
		// do the query on the database
		if (where==null) {
			where="";
		} else if (where.indexOf("MMNODE")!=-1) {
			where=convertMMNode2SQL(where);
		} else {
			//where=QueryConvertor.altaVista2SQL(where);
			where=QueryConvertor.altaVista2SQL(where,database);
		}
		if (direction) {	
			String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+where+" ORDER BY "+sorted+" ASC";
			return(basicSearch(query));
		} else {
			String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+where+" ORDER BY "+sorted+" DESC";
			return(basicSearch(query));
		}
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVectorIn(String where,String sorted,boolean direction,String in) {
		// do the query on the database
		if (in==null || in.equals("")) return(new Vector());
		if (direction) {	
			//String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where)+" AND number in ("+in+") ORDER BY "+sorted+" ASC";
			String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where,database)+" AND number in ("+in+") ORDER BY "+sorted+" ASC";
			return(basicSearch(query));
		} else {
			//String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where)+" AND number in ("+in+") ORDER BY "+sorted+" DESC";
			String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" "+QueryConvertor.altaVista2SQL(where,database)+" AND number in ("+in+") ORDER BY "+sorted+" DESC";
			return(basicSearch(query));
		}
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchWithWhere(String where) {
		// do the query on the database
		String query="SELECT * FROM "+mmb.baseName+"_"+tableName+" where "+where;
		Vector results=basicSearch(query);
		if (results!=null) {
			return(results.elements());
		} else {
			return(null);
		}
	}


	/**
	* read the result into a vector
	*/
	private Vector readSearchResults(ResultSet rs) {
		MMObjectNode node=null;
		Vector results=new Vector();
		Integer number;
		String tmp;
		try {
			while(rs.next()) {
				// create a new object and add it to the result vector
                node=new MMObjectNode(this);
				ResultSetMetaData rd=rs.getMetaData();
				String fieldname;String fieldtype;
				for (int i=1;i<=rd.getColumnCount();i++) {
					fieldname=rd.getColumnName(i);	
					fieldtype=rd.getColumnTypeName(i);	
					node=database.decodeDBnodeField(node,fieldtype,fieldname,rs,i);
				}
				// clear the changed signal
				node.clearChanged(); // huh ?
				results.addElement(node);
	
				// huge trick to fill the caches does it make sense ?
				number=new Integer(node.getIntValue("number"));
				if (!nodeCache.containsKey(number) || replaceCache) {
					nodeCache.put(number,node);	
				} else {
					node=(MMObjectNode)nodeCache.get(number);	
				}
			}	
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return(results);
	} 


	/**
	* read the result into a sorted vector
	* (Called by nl.vpro.mmbase.module.search.TeaserSearcher.createShopResult)
	*/
	public SortedVector readSearchResults(ResultSet rs, SortedVector sv) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();	
			MMObjectNode node;
			
			while(rs.next()) {
				node = new MMObjectNode(this);
				for (int index = 1; index <= numberOfColumns; index++) {
					String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,type,fieldname,rs,index);
				}
				sv.addUniqueSorted(node);
			}	

			return (sv);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();	
		}
		return (null);
	}


	/**
	* build a set command string from a set nodes ( should be moved )
	*/
	public String buildSet(Vector nodes, String fieldName){
		String result = "(";
		Enumeration enum = nodes.elements();
		MMObjectNode node;

		while (enum.hasMoreElements()){
			node = (MMObjectNode)enum.nextElement();

			if(enum.hasMoreElements()) {
				result += node.getValue(fieldName) + ", ";
			}
			else {
				result += node.getValue(fieldName); 
			}
			
		}
		result += ")";
		return (result);
	}

	/**
	* return all fielddefs of this objecttype
	*/
	public Vector getFields() {
		Vector	results=new Vector();
		FieldDefs node;
		for (Enumeration e=fields.elements();e.hasMoreElements();) {
			node=(FieldDefs)e.nextElement();
			results.addElement(node);
		}
		return(results);
	}


	/**
	* return the fieldnames of this objecttype
	*/
	public Vector getFieldNames() {
		Vector	results=new Vector();
		FieldDefs node;
		for (Enumeration e=fields.elements();e.hasMoreElements();) {
			node=(FieldDefs)e.nextElement();
			results.addElement(node.getDBName());
		}
		return(results);
	}

	/**
	* return the fielddefs of a fieldname
	*/
	public FieldDefs getField(String fieldName) {
		FieldDefs node=(FieldDefs)fields.get(fieldName);
		return(node);
	}


	/**
	* return the database type of the objecttype
	*/
	public String getDBType(String fieldName) {
		if (fields==null) debug("getDBType(): fielddefs are null on object : "+tableName);
		FieldDefs node=(FieldDefs)fields.get(fieldName);
		if (node==null) {
			if (debug) debug("getDBType(): PROBLEM Can't find fielddef on : "+fieldName+" builder="+tableName);
			return(null);
		}
		return(node.getDBType());
	}

	/**
	* return the database state of the objecttype
	*/
	public int getDBState(String fieldName) {
        	if (fields==null) return(2);
		FieldDefs node=(FieldDefs)fields.get(fieldName);
		if (node==null) return(-1);
		return(node.getDBState());
	}

	/**
	* what should a gui display when asked for this node/field combo
	* Default is the first non system field (first field after owner)
	* override this to display your own choice (see Images.java)
	*/
	public String getGUIIndicator(MMObjectNode node) {
	
		// do the best we can because this method was not implemeted
		// we get the first field in the object and try to make it
		// to a string we can return
		
		if (sortedDBLayout.size()>0) {
			String fname=(String)sortedDBLayout.elementAt(2);
			/*
			WH: Quoted original code talks about db type "var". Assumed to be a slip of the tongue.
			If it was intended to be "char" please add it to the string types recognized 
			by MMObjectNode.getValueAsString()
			Original code:

			String type=(String)getDBType(fname);
			String str=GUIIndicator;
			if (type.equals("var") || type.equals("varchar") || type.equals("text") || type.equals("varchar_ex")) {
        		str=node.getStringValue(fname);
			} else if (type.equals("int") || type.equals("integer")) {
        		str=""+node.getIntValue(fname);
			}

			New code:
			*/
			String str = node.getValueAsString( fname );
        	if (str.length()>15) {
           	 return(str.substring(0,12)+"...");
        	}
           	return(str);
		} else {
			return(GUIIndicator);		
		}
	}

	/**
	* what should a gui display when asked for this node/field combo
	*/
	public String getGUIIndicator(String field,MMObjectNode node) {
		return(null);		
	}

	/**
	* get the fielddefs but sorted
	*/
	public Vector getEditFields() {
		// hack hack
		if (sortedEditFields == null) {
			sortedEditFields=new Vector();
			FieldDefs node;
			for (int i=1;i<20;i++) {
				for (Enumeration e=fields.elements();e.hasMoreElements();) {
					node=(FieldDefs)e.nextElement();
					if (node.GUISearch==i) sortedEditFields.addElement(node);
				}
			}
		}
		return (sortedEditFields);
	}

	/**
	* get the fielddefs but sorted
	*/
	public Vector getSortedListFields() {
		// hack hack
		if (sortedListFields == null) {
			sortedListFields = new Vector();
			FieldDefs node;
			for (int i=1;i<20;i++) {
				for (Enumeration e=fields.elements();e.hasMoreElements();) {
					node=(FieldDefs)e.nextElement();
					if (node.GUIList==i) sortedListFields.addElement(node);
				}
			}
		}
		return (sortedListFields);
	}


	/**
	* get the fielddefs but sorted
	*/
	public Vector getSortedFields() {
		// hack hack
		if (sortedFields == null) {
			sortedFields = new Vector();
			FieldDefs node;
			for (int i=1;i<20;i++) {
				for (Enumeration e=fields.elements();e.hasMoreElements();) {
					node=(FieldDefs)e.nextElement();
					if (node.GUIPos==i) sortedFields.addElement(node);
				}
			}
		}
		return (sortedFields);
	}

	/**
	* returns the next field as defined by its fielddefs
	*/
	public FieldDefs getNextField(String currentfield) {
		FieldDefs cdef=getField(currentfield);
		int pos=sortedFields.indexOf(cdef);
		if (pos!=-1  && (pos+1)<sortedFields.size()) {
			return((FieldDefs)sortedFields.elementAt(pos+1));
		} 
		return(null);
	}

	/**
	* return table name
	*/
	public String getTableName() {
		return(tableName);
	}

	/**
	* return the full table name
	*/
	public String getFullTableName() {
		return(mmb.baseName+"_"+tableName);
	}

	/**
	* should be overriden if you want to define derived fields in a object
	*/	
	public Object getValue(MMObjectNode node,String field) {
//		if (debug) debug("getValue() "+node+" --- "+field);
		Object rtn=null;
		int pos2,pos1=field.indexOf('(');
		String name,function,val;
		if (pos1!=-1) {
			pos2=field.indexOf(')');
			if (pos2!=-1) {
				name=field.substring(pos1+1,pos2);
				function=field.substring(0,pos1);
				rtn=executeFunction(node,function,name);
			}
		}
		// Old code
		if (field.indexOf("short_")==0) {
			val=node.getStringValue(field.substring(6));
			val=getShort(val,34);
			rtn=val;
		}  else if (field.indexOf("html_")==0) {
			val=node.getStringValue(field.substring(5));
			val=getHTML(val);
			rtn=val;
		} else if (field.indexOf("wap_")==0) {
			val=node.getStringValue(field.substring(4));
			val=getWAP(val);
			rtn=val;
		} 
		// end old
		return(rtn);
	}


	private Object executeFunction(MMObjectNode node,String function,String field) {
		Object rtn=null;

//		System.out.println("Builder ("+tableName+") execute "+function+" on "+field);

		// time functions 
		if(function.equals("date")) {					// date
			int v=node.getIntValue(field);
			rtn=DateSupport.date2string(v);
		} else if (function.equals("time")) {			// time hh:mm
			int v=node.getIntValue(field);
			rtn=DateSupport.getTime(v);
		} else if (function.equals("timesec")) {		// timesec hh:mm:ss
			int v=node.getIntValue(field);
			rtn=DateSupport.getTimeSec(v);
		} else if (function.equals("longmonth")) {		// longmonth September
			int v=node.getIntValue(field);
			rtn=DateStrings.longmonths[DateSupport.getMonthInt(v)];
		} else if (function.equals("month")) {			// month Sep
			int v=node.getIntValue(field);
			rtn=DateStrings.months[DateSupport.getMonthInt(v)];
		} else if (function.equals("weekday")) {		// weekday Sunday
			int v=node.getIntValue(field);
			rtn=DateStrings.longdays[DateSupport.getWeekDayInt(v)];
		} else if (function.equals("shortday")) {		// shortday Sun
			int v=node.getIntValue(field);
			rtn=DateStrings.days[DateSupport.getWeekDayInt(v)];
		} else if (function.equals("day")) {			// day 4
			int v=node.getIntValue(field);
			rtn=""+DateSupport.getDayInt(v);
		} else if (function.equals("year")) {			// year 2001
			int v=node.getIntValue(field);
			rtn=DateSupport.getYear(v);

		// text convertion  functions
		} else if (function.equals("wap")) {
			String val=node.getStringValue(field);
			rtn=getWAP(val);
		} else if (function.equals("html")) {
			String val=node.getStringValue(field);
			rtn=getHTML(val);
		} else if (function.equals("shorted")) {
			String val=node.getStringValue(field);
			rtn=getShort(val,32);

		} else {
			System.out.println("Builder ("+tableName+") unknown function '"+function+"'");
		}

		return(rtn);
	}


	// called main to prevent override by insrel;
	public Vector getRelations_main(int src) {
		InsRel bul=(InsRel)mmb.getMMObject("insrel");
		if (bul==null) debug("getMMObject(): InsRel not yet loaded");
		return(bul.getRelationsVector(src));
	}

	/**
	* return the default url of this object (should be redone)
	*/	
	public String getDefaultUrl(int src) {
		return(null);
	}	


	

	
	/**
	* return the number of nodes in the cache of one objecttype
	*/
	public int getCacheSize() {
	 return(nodeCache.size());
	}


	/**
	* return the number of nodes in the cache of one objecttype
	*/
	public int getCacheSize(String type) {
	 	int i=mmb.TypeDef.getIntValue(type);
		int j=0;
		for (Enumeration e=nodeCache.elements();e.hasMoreElements();) {
				MMObjectNode n=(MMObjectNode)e.nextElement();
				int c=n.getIntValue("CacheCount");
				if (n.getIntValue("otype")==i && c!=-1) j++;
		}
	 	return(j);
	}

	/**
	* get the number of the nodes cached (will be removed)
	*/
	public String getCacheNumbers() {
		String results="";
		for (Enumeration e=nodeCache.elements();e.hasMoreElements();) {
			MMObjectNode n=(MMObjectNode)e.nextElement();
			int c=n.getIntValue("CacheCount");
			if (c!=-1) {
			if (!results.equals("")) {
				results+=","+n.getIntValue("number");
			} else {
				results+=n.getIntValue("number");
			}
			}
		}
	 	return(results);
	}

	/**
	* delete the nodes cache
	*/
	public void deleteNodeCache() {
		nodeCache.clear();
	}

	/**
	* get the next DB key
	*/
	public int getDBKey() {
		return(mmb.getDBKey());
	}



	/**
	* set text array in database
	*/
	/*
	public void setDBText(int i, PreparedStatement stmt,String body) {
		byte[] isochars=null;
		try {
			isochars=body.getBytes("ISO-8859-1");
		} catch (Exception e) {
			debug("setDBText(): String contains odd chars");
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
	*/


	/**
	* set byte array in database
	*/
	/*
	public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
		try {
			ByteArrayInputStream stream=new ByteArrayInputStream(bytes);
			stmt.setBinaryStream(i,stream,bytes.length);
			stream.close();
		} catch (Exception e) {
			debug("setDBByte(): Can't set byte stream");
			e.printStackTrace();
		}
	}
	*/

	/**
	* return the age in days of the node
	*/
	public int getAge(MMObjectNode node) {
		return(((DayMarkers)mmb.getMMObject("daymarks")).getAge(node));
	}

	/**
	* return the name of this mmserver
	*/
	public String getMachineName() {
		return(mmb.getMachineName());
	}

	/**
	* called when a remote node is changed, should be called by subclasses
	* if they override it
	*/
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		// overal cache control, this makes sure that the caches
		// provided by mmbase itself (on nodes and relations)
		// are kept in sync is other servers add/change/delete them.
		// System.out.println("MMObjectBuilder -> CHECK REMOTE remove from cache node="+tableName+" nr="+number);
		if (ctype.equals("c") || ctype.equals("d")) {
			try {
				Integer i=new Integer(number);
				if (nodeCache.containsKey(i)) {
					nodeCache.remove(i);	
				}
			} catch (Exception e) {
				debug("nodeRemoteChanged(): Not a number");
			}
		} else if (ctype.equals("r")) {
			try {
				Integer i=new Integer(number);
				MMObjectNode node=(MMObjectNode)nodeCache.get(i);
				if (node!=null) {
					node.delRelationsCache();
				}
			} catch (Exception e) {
			}
		}

		// signal all the other objects that have shown interest in changes of nodes of this builder type.
		// System.out.println("DEBUG OBSERVERS="+remoteObservers.size());
		for (Enumeration e=remoteObservers.elements();e.hasMoreElements();) {
			MMBaseObserver o=(MMBaseObserver)e.nextElement();
			o.nodeRemoteChanged(number,builder,ctype);	
		}
		return(true);
	}

	/**
	* called when a local node is changed, should be called by subclasses
	* if they override it
	*/
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		// overal cache control, this makes sure that the caches
		// provided by mmbase itself (on nodes and relations)
		// are kept in sync is other servers add/change/delete them.
		// System.out.println("MMObjectBuilder -> CHECK LOCAL remove from cache node="+tableName+" nr="+number);
		if (ctype.equals("r")) {
			try {
				Integer i=new Integer(number);
				MMObjectNode node=(MMObjectNode)nodeCache.get(i);
				if (node!=null) {
					node.delRelationsCache();
				}
			} catch (Exception e) {
			}
		}
		// signal all the other objects that have shown interest in changes of nodes of this builder type.
		for (Enumeration e=localObservers.elements();e.hasMoreElements();) {
			MMBaseObserver o=(MMBaseObserver)e.nextElement();
			o.nodeLocalChanged(number,builder,ctype);	
		}
		return(true);
	}


	/**
	* called then a local field is changed
	*/
	public boolean fieldLocalChanged(String number,String builder,String field,String value) {
		debug("FLC="+number+" BUL="+builder+" FIELD="+field+" value="+value);
		return(true);
	}

	/**
	* add object to the remote change list of this object
	*/
	public boolean addRemoteObserver(MMBaseObserver obs) {
		if (!remoteObservers.contains(obs)) {
			remoteObservers.addElement(obs);
		}
		return(true);
	}

	/**
	* add object to the local change list of this object
	*/
	public boolean addLocalObserver(MMBaseObserver obs) {
		if (!localObservers.contains(obs)) {
			localObservers.addElement(obs);
		}
		return(true);
	}

	/**
	*  used to create a default teaser by any builder (will be removed?)
	*/
	public MMObjectNode getDefaultTeaser(MMObjectNode node,MMObjectNode tnode) {
		debug("getDefaultTeaser(): Generate Teaser,Should be overridden");
		return(tnode);
	}
	
	/**
	* waits until a node is changed (multicast)
	*/
	public boolean waitUntilNodeChanged(MMObjectNode node) {
		return(mmb.mmc.waitUntilNodeChanged(node));
	}

	/**
	* getList all for frontend code
	*/
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws ParseException {
		throw new ParseException(classname +" does not implement the LIST command");
	}


	/**
	* replace all for frontend code
	*/
	public String replace(scanpage sp, StringTokenizer tok) {
		debug("replace(): replace called should be overridden");
		return("");
	}

	/**
	* set debug state
	*/	
	public void setDebug(boolean state) {
		debug=state;
	}


	public MMObjectNode getAliasedNode(String key) {
		int nr;
		MMObjectNode node;

		try {
			nr=Integer.parseInt(key);
		} catch (Exception e) {
			nr=-1;
		}
		if (nr>0) {
			node=mmb.OAlias.getNode(nr);
		} else {
			node=mmb.OAlias.getAliasedNode(key);
		}
		return(node);
	}

	/**
	* convert mmnode2sql still new should replace the old mapper soon
	*/	
	public String convertMMNode2SQL(String where) {
		if (debug) debug("convertMMNode2SQL(): "+where);
		String result="WHERE "+database.getMMNodeSearch2SQL(where,this);
		if (debug) debug("convertMMNode2SQL(): results : "+result);
		return(result);
	}


	/**
	* set the MMBase object
	*/
	public void setMMBase(MMBase m) {
		this.mmb=m;
	}

	/**
	* set DBLayout
	* needs to be replaced soon if i know how
	*/
	public void setDBLayout(Vector vec) {
		sortedDBLayout=new Vector();
		sortedDBLayout.addElement("otype");
		sortedDBLayout.addElement("owner");
		for (Enumeration e=vec.elements();e.hasMoreElements();) 
		{
        	StringTokenizer tok = new StringTokenizer((String)e.nextElement(),",\n\r");
			if(tok.hasMoreTokens())
			{
				String dbtype=tok.nextToken();
				if(tok.hasMoreTokens())
				{
					String guiname=tok.nextToken();	
					if(tok.hasMoreTokens())
					{
						String guitype=tok.nextToken();	
						if(tok.hasMoreTokens())
						{
							String guipos=tok.nextToken();	
							if(tok.hasMoreTokens())
							{
								String guilist=tok.nextToken();	
								if(tok.hasMoreTokens())
								{
									String guisearch=tok.nextToken();	
									if(tok.hasMoreTokens())
									{
										String dbstate=tok.nextToken();	
										if(tok.hasMoreTokens())
										{
											String dbname=tok.nextToken();	
											if (!dbname.equals("number") && !dbname.equals("owner")) 
											{
												sortedDBLayout.addElement(dbname);
											}
										}
										else
											debug("setDBLayout(): ERROR: 'dbname' not defined (while reading defines?)");
									}
									else
										debug("setDBLayout(): ERROR: 'dbstate' not defined (while reading defines?)");
								}
								else
									debug("setDBLayout(): ERROR: 'guisearch' not defined (while reading defines?)");
							}
							else
								debug("setDBLayout(): ERROR: 'guilist' not defined (while reading defines?)");
						}
						else
							debug("setDBLayout(): ERROR: 'guipos' not defined (while reading defines?)");
					}
					else
						debug("setDBLayout(): ERROR: 'guitype' not defined (while reading defines?)");
				}
				else
					debug("setDBLayout(): ERROR: 'guiname' not defined (while reading defines?)");
			}
			else
				debug("setDBLayout(): ERROR: 'dbname' not defined (while reading defines?)");
		}
	}


	/**
	* set DBLayout
	* needs to be replaced soon if i know how
	*/
	public void setDBLayout_xml(Hashtable fields) {
		sortedDBLayout=new Vector();
		sortedDBLayout.addElement("otype");
		sortedDBLayout.addElement("owner");

		FieldDefs node;
		for (int i=1;i<20;i++) {
			for (Enumeration e=fields.elements();e.hasMoreElements();) {
				node=(FieldDefs)e.nextElement();
				if (node.DBPos==i) {
					String name=node.getDBName();		
					if (name!=null && !name.equals("number") && !name.equals("otype") && !name.equals("owner")) {
						sortedDBLayout.addElement(name);
					}
				}
			}
		}
	}

	private boolean check( String method, String name, String value )
	{
		boolean result = false;
		if( value==null )
			debug(method+"(): ERROR: "+name+"("+value+") is null!");
		else
		if( value.equals("") )
			debug(method+"(): ERROR: "+name+"("+value+") is null!");
		else
			return result;	
		return result;
	}

	/**
	* set tablename of the builder
	*/
	public void setTableName(String tableName) {
		this.tableName=tableName;
	}

	/**
	* set description of the builder
	*/
	public void setDescription(String e) {
		this.description=e;
	}

	/**
	* get description of the builder
	*/
	public String getDescription() {
		return(description);
	}

	/**
	* set Dutch Short name (will be removed soon)
	*/
	public void setDutchSName(String d) {
		this.dutchSName=d;
	}


	/**
	* set search Age
	*/
	public void setSearchAge(String age) {
		this.searchAge=age;
	}


	/**
	* set search Age
	*/
	public String getSearchAge() {
		return(searchAge);
	}

	/**
	* get Dutch Short name (will be removed soon)
	*/
	public String getDutchSName() {
		if (singularNames!=null) {
			String tmp=(String)singularNames.get(mmb.getLanguage());
			if (tmp!=null) return(tmp);
			tmp=(String)singularNames.get("us");
			if (tmp!=null) return(tmp);
			return(null);
		}
		return(dutchSName);
	}

	/**
	* set classname of the builder
	*/
	public void setClassName(String d) {
		this.className=d;
	}

	/**
	* return classname of this builder
	*/
	public String getClassName() {
		return(className);
	}

	/**
	* send a signal to other servers of this fieldchange
	*/
	public boolean	sendFieldChangeSignal(MMObjectNode node,String fieldname) {
		// we need to find out what the DBState is of this field so we know
		// who to notify of this change
		int state=getDBState(fieldname);
		debug("Changed field="+fieldname+" dbstate="+state);

		// still a large hack need to figure out remote changes
		if (state==0) {
		}
			// convert the field to a string
			String type=getDBType(fieldname);
			String value="";
			if (type.equals("int")) {
				value=""+node.getIntValue(fieldname);
			} else if (type.equals("varchar")) {
				value=node.getStringValue(fieldname);
			} else {
				// should be mapped to the builder
			}
			fieldLocalChanged(""+node.getIntValue("number"),tableName,fieldname,value); 
		//mmb.mmc.changedNode(node.getIntValue("number"),tableName,"f");
		return(true);
	}

	public boolean signalNewObject(String tableName,int number) {
		if (mmb.mmc!=null) {
			mmb.mmc.changedNode(number,tableName,"n");
		}
		return(true);
	}


	public String toXML(MMObjectNode node) {
		String body="<?xml version=\"1.0\"?>\n";
		body+="<!DOCTYPE mmnode."+tableName+" SYSTEM \""+mmb.getDTDBase()+"/mmnode/"+tableName+".dtd\">\n";
		body+="<"+tableName+">\n";
		body+="<number>"+node.getIntValue("number")+"</number>\n";
		for (Enumeration e=sortedDBLayout.elements();e.hasMoreElements();) {
			String key=(String)e.nextElement();	
			String type=node.getDBType(key);
			if (type.equals("int") || type.equals("integer")) {
				body+="<"+key+">"+node.getIntValue(key)+"</"+key+">\n";
			} else if (type.equals("text") || type.equals("clob")) {
				body+="<"+key+">"+node.getStringValue(key)+"</"+key+">\n";
			} else if (type.equals("byte")) {
				body+="<"+key+">"+node.getByteValue(key)+"</"+key+">\n";
			} else {
				body+="<"+key+">"+node.getStringValue(key)+"</"+key+">\n";
			}
		}
		body+="</"+tableName+">\n";
		return(body);
	}

	public void setSingularNames(Hashtable names) {
		singularNames=names;
	}

	public Hashtable getSingularNames() {
		return(singularNames);
	}

	public void setPluralNames(Hashtable names) {
		pluralNames=names;
	}

	public Hashtable getPluralNames() {
		return(pluralNames);
	}
		
	/**
	* get text from blob
	*/
	public String getShortedText(String fieldname,int number) {
		return(database.getShortedText(tableName,fieldname,number));
	}

	/**
	* get byte of a database blob
	*/
	public byte[] getShortedByte(String fieldname,int number) {
		return(database.getShortedByte(tableName,fieldname,number));
	}


	/**
	* get byte of a database blob
	*/
	public byte[] getDBByte(ResultSet rs,int idx) {
		return(database.getDBByte(rs,idx));
	}

	/**
	* get text of a database blob
	*/
	public String getDBText(ResultSet rs,int idx) {
		return(database.getDBText(rs,idx));
	}

	private void statCount(String type) {
		if (1==1) return; // problems with shadow nodes

		if (statbul==null) statbul=(Statistics)mmb.getMMObject("statistics");
		if (statbul!=null) {
			if (statbul!=this && mmb.getMMObject("sshadow")!=this) {
			String name=mmb.getMachineName()+"_"+type;
			String nr=statbul.getAliasNumber(name);
			if (nr!=null) {
				statbul.setCount(nr,1);	
			} else {
				MMObjectNode node=statbul.getNewNode("system");
				node.setValue("name",name);
				node.setValue("description","");
				node.setValue("count",1);
				node.setValue("timeslices",144);
				node.setValue("timeinterval",600);
				node.setValue("timesync",0);
				node.setValue("data","");
				node.setValue("start",0);
				node.setValue("timeslice",0);
				statbul.insert("system",node);
			}			
			}
		}
	}


	public boolean created() {
		if (database!=null) {
			return(database.created(mmb.getBaseName()+"_"+tableName));
		} else {
			return(super.created());
		}
	}
	
	protected void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}


	public String getNumberFromName(String name) {
		String number = null;

		//String number=(String)nameCache.get(name);
		//if (number!=null) {
		//	return(number);	
		//} else {
			Enumeration e=search("WHERE name='"+name+"'");
            if (e.hasMoreElements()) {
              	MMObjectNode node=(MMObjectNode)e.nextElement();
				number=""+node.getIntValue("number");
				//nameCache.put(name,number);
			}
		//}
		return(number);
	}


	public boolean setValue(MMObjectNode node,String fieldname) {
		// can be overriden to do precommit changes
		// return true means the call will continue
		// return false means that we have handled all
		return(true);
	}


	/**
	* this call will be removed once the new xml configs work
	* it provides a way to simulate the xml files (like url.xm).
	*/
	public Hashtable getXMLSetup() {
		// return null unless overridden
		return(null);
	}


	//************************************************************ 

	protected String getHTML(String body) {
		String rtn="";
		if (body!=null) {
			StringObject obj=new StringObject(body);
			obj.replace("<","&lt;");
			obj.replace(">","&gt;");
			obj.replace("$","&#36;");

			obj.replace("\r\n\r\n","<P>");
			obj.replace("\n\n","<P>");
			obj.replace("\r\n","<BR>");
			obj.replace("\n","<BR>");
			rtn=obj.toString();
		}
		return(rtn);
	}

	protected String getWAP( String body ) {
		String result = "";
		if( body != null ) {
			StringObject obj=new StringObject(body);
			obj.replace("\"","&#34;");
			obj.replace("&","&#38;#38;");
			obj.replace("'","&#39;");
			obj.replace("<","&#38;#60;");
			obj.replace(">","&#62;");
			result = obj.toString();
		}
		return result;
	}

	/**
	* support routine to return shorter strings (will be removed)
	*/
	public String getShort(String str,int len) {
        if (str.length()>len) {
            return(str.substring(0,(len-3))+"...");
        } else {
            return(str);
        }
	}

	/**
	 * End functions
	 */

	//************************************************************ 


	public void setXMLValues(Vector xmlfields) {
		//sortedEditFields = null;
		//sortedListFields = null;
		//sortedFields = null;
		//sortedDBLayout=new Vector();

		fields=new Hashtable();

		Enumeration enum = xmlfields.elements();
		while (enum.hasMoreElements()){
			FieldDefs def=(FieldDefs)enum.nextElement();
			String name=(String)def.getDBName();
			fields.put(name,def);
		}

		// default ones
		//FieldDefs def=new FieldDefs("Nummer","integer",-1,-1,"number","int",-1,3);
		//fields.put("number",def);	
		FieldDefs def=new FieldDefs("Type","integer",-1,-1,"otype","int",-1,3);
		fields.put("otype",def);	
		//def=new FieldDefs("Eigenaar","string",-1,-1,"owner","varchar",-1,3);
		//fields.put("owner",def);	
		setDBLayout_xml(fields);
	}

	public void setXmlConfig(boolean state) {
		isXmlConfig=state;
	}

	public boolean isXMLConfig() {
		return(isXmlConfig);
	}
}
