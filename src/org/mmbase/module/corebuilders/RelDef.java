/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;


/**
 * RelDef is one of the meta stucture node.
 * It is used to define the possible relation types.
 *
 * A Relation Definition consists of a source and destination, and a descriptor
 * (direction) for it's use (uni-directional or bi-directional).
 *
 * Relations are often mapped to a builder (by name).
 * This is so that additonal functionality can be added by means of a builder (i.e. AuthRel)
 * Unfortunately, this means that some care need be taken when naming relations, as unintentionally
 * naming a relation to a builder can give bad (if not disastrous) results.
 * Relations that are not directly mapped to a builder are mapped (internally) to the {@link insrel} builder instead.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadoc)
 * @version 21 Sept 1997
 */
public class RelDef extends MMObjectBuilder {

    Hashtable relCache=new Hashtable();

    /**
    *  Contruct the table
    */
    public RelDef() {
    }

    /**
    *  Initializes the builder by reading the cache
    *  @return A <code>boolean</code> value, always success
    *  @see readCache
    **/
    public boolean init() {
	super.init();
	return readCache();
    }
	
    /**
    * Reads all relation definition names in an internal cache.
    * The cache is used by {@link isRelationTable}
    * @return A <code>boolean</code> value, always success (<code>true</code>), as any exceptions are
    *         caught and logged.
    **/
    private boolean readCache() {
	MultiConnection con=null;
	Statement stmt=null;
	try {
	    con=mmb.getConnection();
	    stmt=con.createStatement();
	    ResultSet rs=stmt.executeQuery("SELECT * FROM "+getFullTableName());
	    relCache=new Hashtable();
	    Integer number;
	    String name;
	    while(rs.next()) {
		number=new Integer(rs.getInt(1)); // Number
		name=rs.getString(4); // sname
		relCache.put(name,number);
	    }	
	    relCache.put("insrel",new Integer(-1)); // Hack HACK Hack
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    mmb.closeConnection(con,stmt);
	}
	return true;
    }

    /**
    * Returns a GUI description of a relation definition.
    * The description is dependent on the direction (uni/bi) of the relation
    * @param node Relation definition to describe
    * @return A <code>String</code> of descriptive text
    **/
    public String getGUIIndicator(MMObjectNode node) {
    	int dir=node.getIntValue("dir");
        if (dir==2) {
            return(node.getStringValue("sguiname"));   
        } else if (dir==1) {
            String st1=node.getStringValue("sguiname");
            String st2=node.getStringValue("dguiname");
            return (st1+"/"+st2);
        }
        return "";
    }	

    /**
    * Insert a new object, and updated the cache after an insert.
    * This method indirectly calls {@link #precommit}.
    * @param owner The administrator creating the node
    * @param node The object to insert. The object need be of the same type as the current builder.
    * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
    */
    public int insert(String owner, MMObjectNode node) {
	int number=super.insert(owner,node);
	if (number!=-1) {
		relCache.put(node.getStringValue("sname"),new Integer(number));
	}
	return number;
    };

	
    /**
    * Commit changes to this node and updated the cache. This method indirectly calls {@link #precommit}.
    * This method does not remove names from the cache, as currently, unique names are not enforced.
    * @param node The node to be committed
    * @return a <code>boolean</code> indicating success
    */
    public boolean commit(MMObjectNode node) {
    	boolean success = super.commit(node);
    	if (success) {
    	    String newname=node.getStringValue("sname");
    	    relCache.put(newname,new Integer(node.getIntValue("number")));
    	}
    	return success;
   }

    /**
    * Sets defaults for a new relation definition.
    * Initializes a relation to be bi-directional.
    *	@param node Node to be initialized
    **/
    public void setDefaults(MMObjectNode node) {
	node.setValue("dir",2);
    }
	
    /**
    * Retrieve descriptors for a relation definition's fields,
    * specifically a descriptive text for the relation's direction (dir)
    * @param field Name of the field whose description should be returned.
    *              valid values : 'dir'
    * @param node Relation definition containing the field's information
    * @return A descriptive text for the field's contents, or null if no description could be generated
    **/
	
    public String getGUIIndicator(String field,MMObjectNode node) {
	try {
	    if (field.equals("dir")) {
		int dir=node.getIntValue("dir");
		if (dir==2) {
		    return "bi-directional";
		} else if (dir==1) {
		    return "uni-directional";
		} else {
		    return "unknown";
		}
	    }
	} catch (Exception e) {}
	return null;
    }

    /**
    * Checks to see if a given relation definition is stored in the cache.
    * @param name A <code>String</code> of the relation definitions' name
    * @returns: a <code>boolean</code> indicating success if the relationname exists
    **/

    public boolean isRelationTable(String name) {
	Object ob;
	ob=relCache.get(name);
	return (ob!=null);
    }

    /**
    * Search the relation definition table for the identifying number of
    * a relation, by name of the builder to use
    * Similar to {@link #getGuessedbyName} (but does not make use of sname)
    * @ param name The builder name on which to search for the relation
    * @ return A <code>int</code> value indicating the relation's object number, or -1 if not found. If multiple relations use the
    * 	indicated buildername, the first one found is returned.
    * @deprecated Not very suitable to use, as success is dependent on the uniqueness of the builder in the table (not enforced, so unpredictable).
    **/
    public int getGuessedNumber(String name) {
	MultiConnection con=null;
	Statement stmt=null;
	try {
	    con=mmb.getConnection();
	    stmt=con.createStatement();
	    ResultSet rs=stmt.executeQuery("select "+mmb.getDatabase().getNumberString()+" from "+getFullTableName()+" where sname='"+name+"'");
	    if (rs.next()) {
		int i=rs.getInt(1);
		return i;
	    }
	} catch (SQLException e) {
			e.printStackTrace();
	} finally {
	    mmb.closeConnection(con,stmt);
	}
	return -1;
     }

    /**
    * Search the relation definition table for the identifying number of
    * a relation, by name of the builder to use
    * This function is used by descendants of Insrel to determine a default reference to a 'relation definition' (reldef entry).
    * The 'default' is the relation with the same name as the builder. If no such relation exists, there is no default.
    *
    * @ param name The builder name on which to search for the relation
    * @ return A <code>int</code> value indicating the relation's object number, or -1 if not found. If multiple relations use the
    * 	indicated buildername, the first one found is returned.
    * @deprecated Not very suitable to use, as success is dependent on the uniqueness of the builder in the table (not enforced, so unpredictable).
    **/

    public int getGuessedByName(String buildername) {
	Enumeration e=search("WHERE (sname='"+buildername+"') OR (dname='"+buildername+"')");
	if (e.hasMoreElements()) {
	    MMObjectNode node=(MMObjectNode)e.nextElement();
	    return node.getIntValue("number");
	} else {
	    return -1;
	}
    }                        	
	
    /**
    * Searches for the relation number on ethe combiantion of sname and dname.
    * When there's no match found in this order a search with a swapped sname and dname will be done.
    * @ param sname The first name on which to search for the relation (preferred as the source)
    * @ param dname The second name on which to search for the relation (preferred as the destination)
    * @ return A <code>int</code> value indicating the relation's object number, or -1 if not found. If multiple relations use the
    * 	indicated buildername, the first one found is returned.
    * @deprecated Not very suitable to use, as success is dependent on the uniqueness of the builder in the table (not enforced, so unpredictable).
    */
    public int getRelNrByName(String sname, String dname) {
    	Enumeration e = search("WHERE sname='" + sname + "' AND dname='" + dname + "'");
	if (!e.hasMoreElements()) e = search("WHERE sname='" + dname + "' AND dname='" + sname + "'");

	if (e.hasMoreElements()) {
	    MMObjectNode node = (MMObjectNode)e.nextElement();
	    return(node.getIntValue("number"));
	}
	return(-1);
    }
}




