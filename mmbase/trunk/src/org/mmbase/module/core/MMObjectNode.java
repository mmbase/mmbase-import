/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.gui.html.*;


/**
 * MMObjectNode is the core of the MMBase system, this class is what its all 
 * about because the instances of this class hold the content we are using 
 * All active Nodes with data and relations are MMObjectNodes and make up the
 * object world that is MMBase (Creating, searching, removing is done by its
 * a class extened from MMObjectBuilder)
 * 
 * @author Daniel Ockeloen
 */
public class MMObjectNode {

	// values holds the name, value fields in this node
	public Hashtable values=new Hashtable();

	// properties holds the 'extra' name,value fields attached as properties
	public Hashtable properties;
	// object to sync access to properties
	private Object properties_sync=new Object();

	// changed vector stores the key's of the changed fields since last
	// commit
	public Vector changed=new Vector();

	// pointer to the parent builder that is responsible for this node
	public MMObjectBuilder parent;

	// Vector  with the related nodes to this node
	Vector relations=null; // possibly filled with insRels

	// temp hack to make multiple nodes (for multilevel for example) possible
	public String prefix="";


	/** 
	* empty constructor added for javadoc	
	*/
	public MMObjectNode() {
	}

	/** 
	* main contructor most of the time called by the parent
	* MMObjectBuilder.
	*/
	public MMObjectNode(MMObjectBuilder parent) {
		if (parent!=null) {	
			this.parent=parent;
		} else {
			System.out.println("MMObjectNode-> contructor called with partent=null");
			MMObjectNode bla=null;
			bla.toString();
		}
	}

	/** 
	* legacy contructor, useless will be removed soon (daniel)
	*/
	public MMObjectNode(int id,int type, String owner) {
	}

	/** 
	* commit : commits the node to the database or other storage system
	* this can only be done on a existing (inserted) node. it will use the
	* changed Vector as its base of what to commit/changed
	*/
	public boolean commit() {
		return(parent.commit (this));
	}

	/** 
	*  insert this node into the database or other storage system
	*  returns the new key
	*/
	public int insert(String userName) {
		return(parent.insert(userName,this));
	}

	/** 
	* insertDone added for frontend editors will be called 
	* input/temp variables to the known/valid name/value pairs
	*
	* this call will be changed to a system not depending on a
	* editor setup. Problem is that its a usefull/needed call
	* to for example to check if all is oke or delete some
	* caching in the editors
	*/
	public int insertDone(EditState ed) {
		return(parent.insertDone(ed,this));
	}

	/** 
	* call added for frontend editors to be able to convert
	* input/temp variables to the known/valid name/value pairs
	*
	* this call will be changed to a system not depending on a
	* editor setup. Problem is that its a usefull/needed call
	* to for example combine 2 temp values into a real value.
	* this happens alot with editors for example.
	*/
	public int preEdit(EditState ed) {
		return(parent.preEdit(ed,this));
	}



	/** 
	* return the core of this node in a human readable way 
	* only used for debugging for data exchange use toXML()
	* and getDTD()
	*/
	public String toString() {
		String result="";
		try {
		Enumeration e=values.keys();
		while (e.hasMoreElements()) {
			String key=(String)e.nextElement();
			String dbtype=getDBType(key);
			String value=""+values.get(key);
			if (result.equals("")) {
				result=key+"="+dbtype+":'"+value+"'";
			} else {
				result+=","+key+"="+dbtype+":'"+value+"'";
			}
		}	
		} catch(Exception e) {}	
		return(result);
	}


	/**
	* return the node as a XML file
	*/
	public String toXML() {

		// call is implemented by its builder so
		// call the builder with this node
		if (parent!=null) {
			return(parent.toXML(this));
		} else {
			return(null);
		}
	}

	/** 
	*  sets a key, value pair in the main values of this node.
	* (remark someone has to look at this caching thing, i think its lagecy, daniel)
	*/
	public boolean setValue(String fieldname,Object fieldvalue) {
		// put the key/value in the value hashtable
		values.put(fieldname,fieldvalue);

		// obtain the type of field this is 
		int state=getDBState(fieldname);


		// add it to the changed vector so we know that we have to update it
		// on the next commit
		if (!changed.contains(fieldname) && !fieldname.equals("CacheCount") && state==2) {
			changed.addElement(fieldname);
		}
	
		// is it a memory only field ? then send a fieldchange
		// a small test begin for transient fields
		//sendFieldChangeSignal(fieldname);

		return(true);
	}



	/** 
	*  sets a key, value pair in the main values of this node where value String
	*/
	public boolean setValue(String fieldname,String fieldvalue) {

		// put the key/value in the value hashtable
		values.put(fieldname,fieldvalue);

		// obtain the type of field this is 
		int state=getDBState(fieldname);


		// add it to the changed vector so we know that we have to update it
		// on the next commit
		if (!changed.contains(fieldname) && state==2) {
			changed.addElement(fieldname);
		}

		// is it a memory only field ? then send a fieldchange
		//sendFieldChangeSignal(fieldname);
		return(true);
	}

	/** 
	*  sets a key, value pair in the main values of this node where value int
	*/
	public boolean setValue(String fieldname,int fieldvalue) {
		// put the key/value in the value hashtable
		values.put(fieldname,new Integer(fieldvalue));

		if (parent!=null) parent.setValue(this,fieldname);

		// obtain the type of field this is 
		int state=getDBState(fieldname);

		// add it to the changed vector so we know that we have to update it
		// on the next commit
		if (!changed.contains(fieldname) && !fieldname.equals("CacheCount") && state==2) { 
			changed.addElement(fieldname);
		}

		// is it a memory only field ? then send a fieldchange
		//sendFieldChangeSignal(fieldname);
		return(true);
	}

	/** 
	*  sets a key, value pair in the main values of this node where value Integer
	*/
	public boolean setValue(String fieldname,Integer fieldvalue) {

		// put the key/value in the value hashtable
		values.put(fieldname,fieldvalue);

		// obtain the type of field this is 
		int state=getDBState(fieldname);

		// add it to the changed vector so we know that we have to update it
		// on the next commit
		if (!changed.contains(fieldname) && state==2) {
			changed.addElement(fieldname);
		}

		// is it a memory only field ? then send a fieldchange
		//sendFieldChangeSignal(fieldname);
		return(true);
	}

	/** 
	*  sets a key, value pair in the main values of this node for ints and strings
	*/
	public boolean setValue(String fieldName, String fieldType, String value)
	// WH: This one will be moved/replaced soon...
	// Testing of db types will be moved to the DB specific classes
	// Called by
	// both versions of FieldEditor.setEditField.
	// MMBaseMultiCast.mergeXMLNode
	// MMImport.parseOneXML
	{
		if (fieldType==null) {
			System.out.println("MMObjectNode.setValue(): unsupported fieldtype null for field "+fieldName);
			return true;
		}
		if (fieldType.equals("text") || fieldType.equals("varchar")
			|| fieldType.equals("varchar_ex") || fieldType.equals("clob"))
			setValue( fieldName, value);
		else if (fieldType.equals("int") || fieldType.equals("integer"))
		{
			int i;
			try { i = Integer.parseInt(value); } 
			catch (NumberFormatException e)
			{ System.out.println( e.toString() ); e.printStackTrace(); return true; }
			setValue( fieldName, i );
		}
		else System.out.println("MMObjectNode.setValue(): unsupported fieldtype: "+fieldType+" for field "+fieldName);
		return true;
	}

	/** 
	* get a value by its given key, will be returned as a Object
	*/
	public Object getValue(String fieldname) {

		// get the value from the values table
		Object o=values.get(fieldname);

		// routine to check for indirect values
		// this are used for functions for example
		// its implemented per builder so lets give this
		// request to our builder
		if (o==null) return(parent.getValue(this,fieldname));
		
		// return the found object
		return(o);
	}

	/** 
	* get a value by its given key, will be returned must be String
	*/
	public String getStringValue(String fieldname) {

		// try to get the value from the values table
		// it might be using a prefix to allow multilevel
		// nodes to work (if not duplicate can not be stored)
		String tmp=(String)values.get(prefix+fieldname);

		// check if the object is shorted, shorted means that
		// because the value can be a large text/blob object its
		// not loaded into each object when its first obtained
		// from the database but that we instead out a text $SHORTED
		// in the field. Only when the field is really used does this
		// get mapped into a real value. this saves speed and memory
		// because every blob/text mapping is a extra request to the
		// database
		if (tmp!=null && tmp.indexOf("$SHORTED")==0) {

			// obtain the database type so we can check if what
			// kind of object it is. this have be changed for
			// multiple database support.
			String type=getDBType(fieldname);

			// check if for known mapped types
			if (type.equals("text") || type.equals("blob")
			|| type.equals("varchar") || type.equals("clob")) {

				int number=getIntValue("number");
				// check if its in a multilevel node (than we have no node number and
				// we need to guess it by xxx.number
				int pos=fieldname.indexOf('.');
				if (pos!=-1) {
					String tmptable=fieldname.substring(0,pos);
					number=this.getIntValue(tmptable+".number");
				}



				// call our builder with the convert request this will probably
				// map it to the database we are running.
				String tmp2=parent.getShortedText(fieldname,number);
	
				// did we get a result then store it in the values for next use
				// and return it.
				// we could in the future also leave it unmapped in the values
				// or make this programmable per builder ?
				if (tmp2!=null) {
					// store the unmapped value (replacing the $SHORTED text)
					values.put(prefix+fieldname,tmp2);
			
					// return the found and now unmapped value
					return(tmp2);
				} else {
					return(null);
				}
			}
		}

		// return the found value
		return(tmp);
	}

	/** 
	* get a value by its given key, will be returned must be byte array
	*/
	public byte[] getByteValue(String fieldname) {
	

		// try to get the value from the values table
		// it might be using a prefix to allow multilevel
		// nodes to work (if not duplicate can not be stored)
		Object obj=values.get(prefix+fieldname);

		// well same as with strings we only unmap byte values when
		// we really use them since they mean a extra request to the
		// database most of the time. 

		// we signal with a empty byte[] that its not obtained yet.
		if (obj instanceof byte[]) {

			// was allready unmapped so return the value
			return((byte[])values.get(prefix+fieldname));
		} else {

			// call our builder with the convert request this will probably
			// map it to the database we are running.
			byte[] b=parent.getShortedByte(fieldname,getIntValue("number"));

			
			// we could in the future also leave it unmapped in the values
			// or make this programmable per builder ?
			values.put(prefix+fieldname,b);

			// return the unmapped value
			return(b);
		}
	}

	/** 
	* get a value by its given key, will be returned must be int
	*/
	public int getIntValue(String fieldname) {
		Integer i=(Integer)values.get(prefix+fieldname);
		if (i!=null) {
			return(i.intValue());
		} else {
			return(-1);
		}
	}


	/** 
	* get a value by its given key, will be returned must be Integer
	*/
	public Integer getIntegerValue(String fieldname) {
		Integer i=(Integer)values.get(prefix+fieldname);
		if (i!=null) {
			return(i);
		} else {
			return(new Integer(-1));
		}
	}

	/** 
	* Get a string or int value as String by its given fieldname
	*/
	public String getValueAsString(String fieldName)
	// WH Will remove/replace this one soon
	// Testing of db types will be moved to the DB specific classes
	// Currently used by:
	// Music.getObjects and getObjects2 (last is dead code)
	// FieldEditor.getEditField
	// HTMLBase.getNodeStringValue
	// ObjectSelector.getObjectFields
	// MMObjectBuilder.getGUIIndicator
	// Forums.getObjectField
	// Teasers.doTSearch

	{
		String result = null;
		String dbType = getDBType(fieldName);
		if (dbType==null) result = "" + getValue(fieldName);
		else if ( dbType.equals("varchar") || dbType.equals("varchar_ex") || dbType.equals("clob")
			 || dbType.equals("text") || dbType.equals("blob") )
				result = getStringValue(fieldName);
		else if (dbType.equals("int") || dbType.equals("integer"))
				result = "" + getIntValue(fieldName);
		if ((result==null) || result.equals("null")) result = "";
		return result;
	}


	/** 
	* returns the DBType (as defined in JDBC mostly, needs some work)
	*/
	public String getDBType(String fieldname) {
		return(parent.getDBType(fieldname));
	}


	/** 
	* returns the DBState
	*/
	public int getDBState(String fieldname) {
		if (parent!=null)  {
			return(parent.getDBState(fieldname));
		} else {
			return(-1);
		}
	}

	/** 
	* return all the keys of the changed values in this node
	*/
	public Vector getChanged() {
		return(changed);
	}

	/** 
	* is one of the values of this node since the last commit/insert ?
	*/
	public boolean isChanged() {
		if (changed.size()>0) {
			return(true);
		} else {
			return(false);
		}
	}

	/** 
	* clear the 'signal' Vector with the changed keys since last commit/insert
	*/
	public boolean clearChanged() {
		changed=new Vector();
		return(true);
	}

	/** 
	* return the values of this node, this should not be used normally used
	* by the system itself (thus it have to be public then?, daniel).
	*/
	public Hashtable getValues() {
		return(values);
	}

	/** 
	* delete Propertie cache for this node, will force a relead of the
	* properties on next use.
	*/
	public void delPropertiesCache() {
		synchronized(properties_sync) {
			properties=null;
		}
	}

	/** 
	* return a Hashtable with the properties nodes for this node
	*/
	public Hashtable getProperties() {
		synchronized(properties_sync) {
			if (properties==null) {
				properties=new Hashtable();
				MMObjectBuilder bul=parent.mmb.getMMObject("properties");
				Enumeration e=bul.search("WHERE parent="+getIntValue("number"));
				while (e.hasMoreElements()) {
					MMObjectNode pnode=(MMObjectNode)e.nextElement();
					String key=pnode.getStringValue("key");
					properties.put(key,pnode);
				}
			}
		}
		return(properties);
	}


	/** 
	*  node the property node for this node defined by key
	*/
	public MMObjectNode getProperty(String key) {
		MMObjectNode n;
		synchronized(properties_sync) {
			if (properties==null) {
				getProperties();
			}
			n=(MMObjectNode)properties.get(key);
		}
		if (n!=null) {
			return(n);
		} else {
			return(null);
		}
	}


	/** 
	*  set the property node for this node (needs work why doesn't it save itself
	*  by a commit ? or should it be commited by the one who puts it in ? (daniel).
	*/
	public void putProperty(MMObjectNode node) {
		synchronized(properties_sync) {
			if (properties==null) {
				getProperties();
			}
			properties.put(node.getStringValue("key"),node);
		}
	}

	/**
	* return the GUI indicator for this node, a String that represents
	* the contents of this node.
	*/	
	public String getGUIIndicator() {
		if (parent!=null) {
			return(parent.getGUIIndicator(this));
		} else {
			System.out.println("MMObjectNode -> can't get parent");
			return("problem");
		}
	}

	/**
	* return the Dutch Single name for this node, should be build in
	* fielddef in the next rewrite (daniel).
	*/
	public String getDutchSName() {
		if (parent!=null) {
			return(parent.dutchSName);
		} else {
			System.out.println("MMObjectNode -> can't get parent");
			return("problem");
		}
	}

	/**
	* return the buildername of this node
	*/

	public String getName() {
		return(parent.tableName);
	}


	/**
	* set the parent builder for this node
	*/
	public void setParent(MMObjectBuilder b) {
		parent=b;
	}


	/**
	* delete the relation cache for this node so it will be
	* reloaded from the database/storage on next use.
	*/
	public void delRelationsCache() {
		relations=null;
	}

	/**
	* return a the related nodes of this node
	*/	
	public Enumeration getRelations() {
		if (relations==null) {	
			Vector re=parent.getRelations_main(this.getIntValue("number"));
			if (re!=null) {
				relations=re;
				return(relations.elements());
			}
			return(null);
		} else {
			return(relations.elements());
		}
	}


	/**
	* return a the related nodes of this node
	*/	
	public int getRelationCount() {
		if (relations==null) {	
			Vector re=parent.getRelations_main(this.getIntValue("number"));
			if (re!=null) {
				relations=re;
				return(relations.size());
			}
			return(0);
		} else {
			return(relations.size());
		}
	}


	/**
	* return a the related nodes of this node
	*/	
	public Enumeration getRelations(int otype) {
		if (relations==null) {	
			Vector re=parent.getRelations_main(this.getIntValue("number"));
			if (re!=null) {
				relations=re;
			}
		} 
		if (relations==null) return(null);
		Vector result=new Vector();
		Enumeration e=relations.elements();
		while (e.hasMoreElements()) {
			MMObjectNode tnode=(MMObjectNode)e.nextElement();
			if (tnode.getIntValue("otype")==otype) {
				result.addElement(tnode);
			}
		}
		return(result.elements());
	}


	/**
	* return a the related nodes of this node
	*/	
	public int getRelationCount(String wantedtype) {
//		System.out.println("MMObjectNode-> wanted type : "+wantedtype);
		int otype=parent.mmb.TypeDef.getIntValue(wantedtype);
		if (otype==0) {
			return(0);
		}
//		System.out.println("MMObjectNode-> wanted otype : "+otype);
		if (relations==null) {	
			Vector re=parent.getRelations_main(this.getIntValue("number"));
//			System.out.println("MMObjectNode-> vector : "+re);
			if (re!=null) {
				relations=re;
			}
		} 
//		System.out.println("MMObjectNode-> vector : "+relations);
		if (relations==null) return(0);
		Vector result=new Vector();
		Enumeration e=relations.elements();
		while (e.hasMoreElements()) {
			MMObjectNode tnode=(MMObjectNode)e.nextElement();
			int snumber=tnode.getIntValue("snumber");
			MMObjectNode nnode=null;
			if (snumber==this.getIntValue("number")) {
				nnode=(MMObjectNode)parent.getNode(tnode.getIntValue("dnumber"));
			} else {
				nnode=(MMObjectNode)parent.getNode(tnode.getIntValue("snumber"));
			}

			if (nnode.getIntValue("otype")==otype) {
				result.addElement(nnode);
			}
		}
		return(result.size());
	}


	public Enumeration getRelations(String wantedtype) {
		int otype=parent.mmb.TypeDef.getIntValue(wantedtype);
		if (otype!=-1) {
			return(getRelations(otype));
		}
		return(null);
	}
	
	public int getAge() {
		return(parent.getAge(this));
	}

	public String getTableName() {
		return(parent.tableName);
	}

	public boolean sendFieldChangeSignal(String fieldname) {
		return(parent.sendFieldChangeSignal(this,fieldname));	
	}

}
