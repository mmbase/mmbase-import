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
 * RelDef ,one of the meta stucture nodes, is used to define the possible relation types.
 * <p>
 * A Relation Definition consists of a source and destination, and a descriptor
 * (direction) for it's use (uni-directional or bi-directional).
 * </p><p>
 * Relations are mapped to a builder.<br />
 * This is so that additonal functionality can be added by means of a builder (i.e. AuthRel)<br />
 * The old system mapped the relations to a builder by name.
 * Unfortunately, this means that some care need be taken when naming relations, as unintentionally
 * naming a relation to a builder can give bad (if not disastrous) results.<br />
 * Relations that are not directly mapped to a builder are mapped (internally) to the {@link InsRel} builder instead.
 * </p><p>
 * The new system uses an additional field to map to a builder.
 * This 'builder' field contains a reference (otype) to teh builder to be used.
 * If null or 0, the builder is assumed to refer to the {@link InsRel} builder.
 * <code>sname</code> is now the name of the relation and serves no function.
 * </p><p>
 * This patched version of RelDef can make use of either direct builder references (through the builder field), or the old system of using names.
 * The system used is determined by examining whether the builder field has been defined in the builder's configuration (xml) file.
 * See the documentation of the relations project at http://www.mmbase.org for more info.
 * </p>
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version 3 jan 2001
 */

public class RelDef extends MMObjectBuilder {

    /*
    * Indicates whether the relationdefinitions use the 'builder' field (that is, whether the
    * field has been defined in the xml file). Used for backward compatibility.
    */
    public static boolean usesbuilder = false;

    // cache of relation definitions
    private Hashtable relCache=new Hashtable();

    // cache of valid relationbuilders
    private Hashtable relBuilderCache=null;

    /**
    *  Contruct the builder
    */
    public RelDef() {
    }

    /**
    *  Initializes the builder by reading the cache. Also determines whether the 'builder' field is used.
    *  @return A <code>boolean</code> value, always success (<code>true</code>), as any exceptions are
    *         caught and logged.
    **/
    public boolean init() {
       super.init();
       usesbuilder = getField("builder")!=null;
       return readCache();
    }

    /**
    * Reads all relation definition names in an internal cache.
    * The cache is used by {@link #isRelationTable}
    * @return A <code>boolean</code> value, always success (<code>true</code>), as any exceptions are
    *         caught and logged.
    **/
    private boolean readCache() {
        // add insrel (default behavior)
	    relCache.put("insrel",new Integer(-1));
	    // add relation definiation names
	    for (Enumeration e=search(null);e.hasMoreElements();) {
	        MMObjectNode n= (MMObjectNode)e.nextElement();
	        relCache.put(n.getStringValue("sname"),new Integer(n.getIntValue("number")));
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
        if (dir==1) {
            return node.getStringValue("sguiname");
        } else {
            String st1=node.getStringValue("sguiname");
            String st2=node.getStringValue("dguiname");
            return st1+"/"+st2;
        }
    }	


    /**
    * Returns the builder of a relation definition.
    * @return the builder
    **/
    public MMObjectBuilder getBuilder(MMObjectNode node) {
        String bulname=null;
  	    if (usesbuilder) {
  	        int builder = node.getIntValue("builder");
  	        bulname=mmb.getTypeDef().getValue(builder);
  	    } else {
  	        bulname=node.getStringValue("sname");
  	    }
  	    if (bulname==null) {
  	        return mmb.getInsRel();
  	    } else {
  	        return mmb.getMMObject(bulname);
  	    }
    }

    /*
    * Tests whether the data in a node is valid (throws an exception if this is not the case).
    * @param node The node whose data to check
    */
	public void testValidData(MMObjectNode node) throws InvalidDataException{
        int dir=node.getIntValue("dir");
        if ((dir!=1) && (dir!=2)) {
            throw new InvalidDataException("Invalid directionality ("+dir+") specified","dir");
        }
        if (usesbuilder) {
            int builder=node.getIntValue("builder");
            if (builder<=0) {
                builder=mmb.getTypeDef().getIntValue("insrel");
            }
            if (!isRelationBuilder(builder)) {
                throw new InvalidDataException("Builder ("+builder+") is not a relationbuilder","builder");
            }
        }
    };

    /**
    * Insert a new object, and updated the cache after an insert.
    * This method indirectly calls {@link #preCommit}.
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
    * Commit changes to this node and updated the cache. This method indirectly calls {@link #preCommit}.
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
    * Initializes a relation to be bi-directional, and, if applicable, to use the 'insrel' builder.
    *	@param node Node to be initialized
    **/
    public void setDefaults(MMObjectNode node) {
        node.setValue("dir",2);
        if (usesbuilder) {
            node.setValue("builder",mmb.getTypeDef().getIntValue("insrel"));
        }
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
	        } else if (field.equals("builder")) {
                int builder=node.getIntValue("builder");
                if (builder<=0) {
                    return "insrel";
                } else {
                    return mmb.getTypeDef().getValue(builder);
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
        return ob!=null;
    }

    // Retrieves the relationbuildercache (initializes a new cache if the old one is empty)
    private Hashtable getRelBuilderCache() {
        // first make sure the buildercache is loaded
        if (relBuilderCache==null) {
            relBuilderCache=new Hashtable();
            // add all builders that descend from InsRel
			Enumeration buls = mmb.mmobjs.elements();
			while (buls.hasMoreElements()) {
				MMObjectBuilder fbul=(MMObjectBuilder)buls.nextElement();
				if (fbul instanceof InsRel) {
					relBuilderCache.put(new Integer(fbul.oType),fbul);
				}
			}
        }
        return relBuilderCache;
    }

    /**
    * Checks to see if a given builder (otype) is known to be a relation builder.
    * @param number The otype of the builder
    * @returns: a <code>boolean</code> indicating success if the builder exists in the cache
    **/
	
    public boolean isRelationBuilder(int number) {
        Object ob;
        ob=getRelBuilderCache().get(new Integer(number));
        return ob!=null;
    }
	
    /**
    * Returns a list of builders currently implementing a relation node.
    * @returns: an <code>Iteration</code> containing the builders (as otype)
    **/
	
    public Enumeration getRelationBuilders() {
	    return getRelBuilderCache().elements();
    }

    /**
    * Search the relation definition table for the identifying number of
    * a relation, by name of the builder to use
    * Similar to {@link #getGuessedByName} (but does not make use of dname)
    * @ param name The builder name on which to search for the relation
    * @ return A <code>int</code> value indicating the relation's object number, or -1 if not found. If multiple relations use the
    * 	indicated buildername, the first one found is returned.
    * @deprecated Not very suitable to use, as success is dependent on the uniqueness of the builder in the table (not enforced, so unpredictable).
    **/
    public int getGuessedNumber(String name) {
	    Integer number;
        number=(Integer)relCache.get(name);
        if (number==null) {
            return -1;
        } else {
            return number.intValue();
        }
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
    * Searches for the relation number on the combination of sname and dname.
    * When there's no match found in this order a search with a swapped sname and dname will be done.
    * @ param sname The first name on which to search for the relation (preferred as the source)
    * @ param dname The second name on which to search for the relation (preferred as the destination)
    * @ return A <code>int</code> value indicating the relation's object number, or -1 if not found. If multiple relations use the
    * 	indicated buildername, the first one found is returned.
    * @deprecated Not very suitable to use, as success is dependent on the uniqueness of the builder in the table (not enforced, so unpredictable).
    */
    public int getRelNrByName(String sname, String dname) {
        Enumeration e = search("WHERE sname='" + sname + "' AND dname='" + dname + "'");
        if (!e.hasMoreElements()) {
            e = search("WHERE sname='" + dname + "' AND dname='" + sname + "'");
        }
	
        if (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            return node.getIntValue("number");
        }
        return -1;
    }
}




