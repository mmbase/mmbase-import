/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;

/**
 *
 * InsRel, the main relation object, holds relations, and methods to
 * handle them. An insrel defines a relation between two objects.
 * <p>
 * This class can be extended to create insrels that can also hold
 * extra values and custom methods (named relations for example).
 * </p>
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version 3 jan 2001
 */
public class InsRel extends MMObjectBuilder {

	public String classname = getClass().getName();
	public boolean debug = false;
	public void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	
	/**
	 * Hold the relnumber to use when creating a node of this builder.
	 */
	public int relnumber=-1;

	/** Cache system, holds the relations from the 25
	 *  most used relations
	 */
	LRUHashtable relatedCache=new LRUHashtable(25);

	/** Cache table that holds yes/no if a relation direction
	 * question was correct or not.
	 * This is needed to make sure that a relation is correctly inserted.
	 */
	Hashtable relDefConnectCache=new Hashtable(10);

	/**
	* empty constructor needed for autoload
	*/
	public InsRel() {
	}

	/**
	* Fixes a relation node.
	* Determines the source and destination numbers, and checks the object types against the types specified in
	* the relation definition ( {@link TypeRel} ).
	* If the types differ, the source and destination are likely mis-aligned, and
	* are swapped to produce a correct relation node.
	* @param node The node to fix
	**/
	public MMObjectNode alignRelNode(MMObjectNode node) {
		MMObjectNode result = null;

		if( node != null ) { 
			MMObjectNode n1=getNode(node.getIntValue("snumber"));
			if( n1 != null ) {
				MMObjectNode n2=getNode(node.getIntValue("dnumber"));
				if( n2 != null ) { 

					int rnumber = node.getIntValue("rnumber");
					if( rnumber != -1 ) { 
						if (reldefCorrect(n1.getIntValue("otype"),n2.getIntValue("otype"),node.getIntValue("rnumber"))) {
							//debug("InsRel -> node was aligned");
							result = node;
						} else {
							//debug("InsRel -> node needs swap");
							int s=node.getIntValue("snumber");
							int d=node.getIntValue("dnumber");
							node.setValue("snumber",d);
							node.setValue("dnumber",s);
							result = node;
						}
					} else 
						debug("alignRelNode("+node+"): ERROR: HUGE ERROR: rnumber("+rnumber+") is -1! ");	
				} else 
					debug("alignRelNode("+node+"): ERROR: cant find node by this dnumber("+node.getIntValue("dnumber")+")! ");
			} else 
				debug("alignRelNode("+node+"): ERROR: cant find node by this snumber("+node.getIntValue("snumber")+")!");
		} else 
			debug("alignRelNode("+node+"): ERROR: parameter node is null!");

		return result;
	}


	/**
	* Insert a new Instance Relation.
	* @param owner Administrator
	* @param snumber Identifying number of the source object
	* @param dnumber Identifying number of the destination object
	* @param rnumber Identifying number of the relation defintition
	* @return A <code>integer</code> value identifying the newly inserted relation
	* @deprecated Use insert(String, MMObjectNode) instead.
	**/
	public int insert(String owner,int snumber,int dnumber, int rnumber) {
		int result = -1;
		if( owner != null ) { 
			if( snumber >= 0 ) {
				if( dnumber >= 0 ) { 
					if( rnumber > 0 ) { 
						MMObjectNode node=getNewNode(owner);
						if( node != null ) {
							node.setValue("snumber",snumber);
							node.setValue("dnumber",dnumber);
							node.setValue("rnumber",rnumber);
							result = insert(owner,node);
						} else 
							debug("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): ERROR: Cannot create new node("+node+")!");
					} else
						debug("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): ERROR: param4 rnumber not > 0!");
				} else
					debug("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): ERROR: param3 dnumber not > 0!");
			} else
				debug("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): ERROR:s param2 snumber not > 0!");
		} else 
			debug("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): ERROR: param1 owner not set!");

		return(result);
	}


	/**
	* Insert a new Instance Relation.
	* @param owner Administrator
	* @param node Relation node to add
	* @return A <code>integer</code> value identifying the newly inserted relation
	**/
	public int insert(String owner, MMObjectNode node) {
		int result = -1;
		if( owner != null ) { 
			if( node != null ) { 
				int snumber=node.getIntValue("snumber");
				if( snumber >= 0 ) {
					int dnumber=node.getIntValue("dnumber");
					if( dnumber >= 0 ) { 
						int rnumber=node.getIntValue("rnumber");
						if( rnumber >= 0 ) { 	
							node=alignRelNode(node);

							if( debug ) 
								debug("insert("+owner+","+node+")");

							result=super.insert(owner,node);
					
						    MMObjectNode n1=getNode(snumber);
							MMObjectNode n2=getNode(dnumber);
				
							// Gerard: temporary removed here, should be removed from databaselayer!!!!
							/*
							mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
							mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
   					   		*/
						} else 
							debug("insert("+owner+","+node+"): ERROR: rnumber("+rnumber+") is not greater than 0! (something is seriously wrong)");
					} else 
						debug("insert("+owner+","+node+"): ERROR: dnumber("+dnumber+") is not greater than 0! (something is seriously wrong)");
				} else 
					debug("insert("+owner+","+node+"): ERROR: snumber("+snumber+") is not greater than 0! (something is seriously wrong)");
			} else
				debug("insert("+owner+","+node+"): ERROR: param2 node not set!");
		} else 	
			debug("insert("+owner+","+node+"): ERROR: param1 owner not set!");
		return(result);
	}

	/**
	* Get relation(s) for a MMObjectNode
	* @param src Identifying number of the object to find the relations of.
	* @return If succesful, an <code>Enumeration</code> listing the relations.
	*         If no relations exist, the method returns <code>null</code>.
	* @see getRelationsVector
	**/
	public Enumeration getRelations(int src) {
		Vector re=getRelationsVector(src);
		if (re!=null) return(re.elements());
		return(null);	
	}

	/**
	* Get relation(s) for a MMObjectNode
	* This function returns all relations in which the node is either a source, or where the node is
	* the destination, but the direction is uni-directional.
	* @param src Identifying number of the object to find the relations of.
	* @return If succesful, a <code>Vector</code> containing the relations.
	*       Each element in the vector's enumeration is a node object retrieved from the
	*       associated table (i.e. 'insrel'), containing the node's fields.
	*       If no relations exist (or a database exception occurs), the method returns <code>null</code>.
	**/
	public Vector getRelationsVector(int src) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
//			debug("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" OR dnumber="+src+";");
			//ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" UNION SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE dnumber="+src+";");
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" OR dnumber="+src+";");
//			debug("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" OR dnumber="+src+";");
			MMObjectNode node;
			Vector results=new Vector();
			while(rs.next()) {
				// create a new object and add it to the result vector
				node=new MMObjectNode(this);
				node.setValue("number",rs.getInt(1));
				node.setValue("otype",rs.getInt(2));
				node.setValue("owner",rs.getString(3));
				node.setValue("snumber",rs.getInt(4));
				node.setValue("dnumber",rs.getInt(5));
				node.setValue("rnumber",rs.getInt(6));
				results.addElement(node);
			}	
			stmt.close();	
			con.close();
			return(results);
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
	}

	/**
	* get MMObjectNodes related to a specified MMObjectNode
	* @param sourceNode this is the source MMObjectNode 
	* @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
	*/
	public Enumeration getRelated(String sourceNode,String wtype) {
		try {
			int src=Integer.parseInt(sourceNode);
			int otype=mmb.TypeDef.getIntValue(wtype);
			return(getRelated(src,otype));
		} catch(Exception e) {}
		return(null);
	}


	/**
	* get MMObjectNodes related to a specified MMObjectNode
	* @param src this is the number of the source MMObjectNode 
	* @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
	*/
	public Enumeration getRelated(int src,String wtype) {
		try {
			int otype=mmb.TypeDef.getIntValue(wtype);
			return(getRelated(src,otype));
		} catch(Exception e) {}
		return(null);
	}

	/**
	* Get MMObjectNodes of a specified type related to a specified MMObjectNode
	* @param src this is the number of the source MMObjectNode
	* @param otype the object type of the nodes you want to have
	* @returns An <code>Enumeration</code> of <code>MMObjectNode</code> object related to the source
	*/
	public Enumeration getRelated(int src,int otype) {
		Vector se=getRelatedVector(src,otype);
		if (se!=null) return(se.elements());
		return(null);
	}

	/**
	* Get MMObjectNodes related to a specified MMObjectNode
	* @param src this is the number of the MMObjectNode requesting the relations
	* @param otype the object type of the nodes you want to have. -1 means any node.
	* @returns A <code>Vector</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
	*   according to the specified filter(s).
	**/
	public Vector getRelatedVector(int src,int otype) {

		Vector list=(Vector)relatedCache.get(new Integer(src));
		if (list==null) {
		// do the query on the database
		try {
			list=new Vector();
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			// informix ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" UNION SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE dnumber="+src+";");
			ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName+" WHERE snumber="+src+" OR dnumber="+src+";");
			MMObjectNode node,node2;
			int other;
			while(rs.next()) {
					// create a new object and add it to the result vector
					node=new MMObjectNode(this);
					node.setValue("number",rs.getInt(1));
					node.setValue("otype",rs.getInt(2));
					node.setValue("owner",rs.getString(3));
					node.setValue("snumber",rs.getInt(4));
					node.setValue("dnumber",rs.getInt(5));
					node.setValue("rnumber",rs.getInt(6));
					if (rs.getInt(4)==src) {
						other=rs.getInt(5);
					} else {
						other=rs.getInt(4);
					}
					node2=getNode(other);
					if(node2!=null) {
						// illustra node2=mmb.castNode(node2);
						list.addElement(node2);
					}
			}	
			stmt.close();
			con.close();
			relatedCache.put(new Integer(src),list);
		} catch (Exception e) {
			e.printStackTrace();
			return(null);
		}
		} else if (debug) debug("Retrieved relations for node "+src+" from relation cache");
		// oke got the Vector now lets get the correct otypes
		Vector results=new Vector();
		for (Enumeration e=list.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			if (node.getIntValue("otype")==otype) {
				results.addElement(node);
			}
		}
		return(results);	
	}


    /**
    * Get the display string for a given field of this node.
    * Returns for 'snumber' the name of the source object,
    * for 'dnumber' the name of the destination object, and
    * for 'rnumber' the name of the relation definition.
    * @param field name of the field to describe.
    * @param node Node containing the field data.
    * @return A <code>String</code> describing the requested field's content
    **/
	public String getGUIIndicator(String field,MMObjectNode node) {
		try {
		if (field.equals("snumber")) {
			MMObjectNode node2=getNode(node.getIntValue("snumber"));
			String ty="="+mmb.getTypeDef().getValue(node2.getIntValue("otype"));
			node2=mmb.castNode(node2);
			if (node2!=null) {
					return(""+node.getIntValue("snumber")+ty+"("+node2.getGUIIndicator()+")");
			}
		} else if (field.equals("dnumber")) {
			MMObjectNode node2=getNode(node.getIntValue("dnumber"));
			String ty="="+mmb.getTypeDef().getValue(node2.getIntValue("otype"));
			node2=mmb.castNode(node2);
			if (node2!=null) {
					return(""+node.getIntValue("dnumber")+ty+"("+node2.getGUIIndicator()+")");
			}
		} else if (field.equals("rnumber")) {
			MMObjectNode node2=mmb.getRelDef().getNode(node.getIntValue("rnumber"));
			return(""+node.getIntValue("rnumber")+"="+node2.getGUIIndicator());
		}
		} catch (Exception e) {}
		return(null);
	}

    /**
    * Checks whether a specific relation exists.
    * Maintains a cache containing the last checked relations
    * @param n1 Number of the source node
    * @param n2 Number of the destination node
    * @param r  Number of the relation definition
    * @return A <code>boolean</code> indicating success when the relation exists, failure if it does not.
    */
	public boolean reldefCorrect(int n1,int n2, int r) {
		// do the query on the database
		Boolean b=(Boolean)relDefConnectCache.get(""+n1+" "+n2+" "+r);
		boolean rtn=false;
		if (b!=null) {
			return(b.booleanValue());
		} else {
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_typerel WHERE snumber="+n1+" AND dnumber="+n2+" AND rnumber="+r);
				if (rs.next()) {
					relDefConnectCache.put(""+n1+" "+n2+" "+r,new Boolean(true));
					rtn=true;
				} else {
					stmt.close();
					stmt=con.createStatement();
					rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_typerel WHERE dnumber="+n1+" AND snumber="+n2+" AND rnumber="+r);
					if (rs.next()) {
						relDefConnectCache.put(""+n1+" "+n2+" "+r,new Boolean(false));
						rtn=false;
					} else {
						debug("reldefCorrect("+n1+","+n2+","+r+"): ERROR: HUGE ERROR: does not exist in TypeRel!");
					}
				}
				stmt.close();
				con.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return(rtn);
		}
	}
	
	/**
	* Deletes the Relation cache.
	* This is to be called if caching gives problems.
	* Make sure that you can't use the deleteRelationCache(int src) instead.
	**/
	public void deleteRelationCache() {
		relatedCache.clear();
	}

	/**
	* Delete a specified relation from the relationCache
	* @param src the number of the relation to remove from the cache
	**/
	public void deleteRelationCache(int src) {
		relatedCache.remove(new Integer(src));
	}

	/**
	* Search the relation definition table for the identifying number of
	* a relation, by name.
	* Success is dependent on the uniqueness of the relation's name (not enforced, so unpredictable).
	* @param name The name on which to search for the relation
	* @return A <code>int</code> value indicating the relation's object number, or -1 if not found.
	**/
	public int getGuessedNumber(String name) {
		RelDef bul=(RelDef)mmb.getMMObject("reldef");
		if (bul!=null) {
			return(bul.getGuessedNumber(name));
		}
		return(-1);
	}


	/**
	* Set defaults for a node.
	* Tries to determine a default for 'relnumber' by searching the RelDef table for an occurrence of the node's builder.
	* Uses the table-mapping system, and should be replaced.
	* @param node The node whose defaults to set.
	*/
	public void setDefaults(MMObjectNode node) {
		if (tableName.equals("insrel")) return;

		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("InsRel-> Can not guess name ("+tableName+")");
			} else {
				System.out.println("InsRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}
}
