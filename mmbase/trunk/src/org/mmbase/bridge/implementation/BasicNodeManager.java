/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This class represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types. It also contains some maintenance code - code
 * to create new nodes, en code to query objects belonging to the same manager.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNodeManager implements NodeManager {

    // Cloud for this nodetype
    protected BasicCloud cloud=null;

    // builder on which the type is based
    protected MMObjectBuilder builder=null;

    // field types
    protected Hashtable fieldTypes = new Hashtable();

    // empty constructor for overriding constructors
    BasicNodeManager() {
    }

    // constructor for creating a Manager for specific cloud
    BasicNodeManager(MMObjectBuilder builder, Cloud cloud) {
        init(builder, cloud);
    }

    protected void init(MMObjectBuilder builder, Cloud cloud) {
        this.cloud=(BasicCloud)cloud;
        this.builder=builder;
        if (!builder.tableName.equals("multirelations")) {
    	    for(Iterator i=builder.getFields().iterator(); i.hasNext();){
	            FieldDefs f=(FieldDefs)i.next();
	            FieldType ft= new BasicFieldType(f,this);
	            fieldTypes.put(ft.getName(),ft);
    	    }
        }
    }

    /**
     * Gets a new (initialized) node
     */
    public Node createNode() {
        // create object as a temporary node
        int id = cloud.uniqueId();
        System.out.println();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(builder.getTableName(), cloud.getAccount(), ""+id);
        // if we are in a transaction, add the node to the transaction;
        if (cloud instanceof BasicTransaction) {
            ((BasicTransaction)cloud).add(currentObjectContext);
        }
        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), ""+id);
        // set the owner to userName instead of account
        node.setValue("owner",cloud.getUserName());
        return new BasicNode(node, this, id);
    }

 	/**
     * Retrieves the Cloud to which this manager belongs
     */
    public Cloud getCloud() {
        return cloud;
    }

	/**
     * Retrieve the identifying name of the NodeManager
     */
    public String getName() {
        return builder.getTableName();
    }

	/**
     * Retrieve the descriptive name of the NodeManager
     * Note: currently, this method returns the nodetype/builder table name!
     */
    public String getGUIName() {
	    Hashtable singularNames=builder.getSingularNames();
        if (singularNames!=null) {
            String tmp=(String)singularNames.get(cloud.getLanguage());
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getTableName();
	}

	/** 
	 * Retrieve the description of the NodeManager.
	 */
	public String getDescription() {
	    Hashtable descriptions=builder.getDescriptions();
        if (descriptions!=null) {
            String tmp=(String)descriptions.get(cloud.getLanguage());
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getDescription();
	}

	/**
	 * Retrieve all field types of this NodeManager.
	 * @return a <code>List</code> of <code>FieldType</code> objects
	 */
	public List getFieldTypes() {
	    Vector res= new Vector(fieldTypes.values());
	    return res;
	}

	/**
	 * Retrieve the field type for a given fieldname.
	 * @param fieldName name of the field to retrieve
	 * @return the requested <code>FieldType</code>
	 */
	public FieldType getFieldType(String fieldName) {
	    FieldType ft= (FieldType)fieldTypes.get(fieldName);
	    return ft;
	}
	
	/**
     * Search nodes beloingin to this NodeManager.
     * @param where The contraint. this is in essence a SQL where clause.
     *      Examples: "email IS NOT NULL", "lastname='admin' OR lastname = 'sa'"
     * @param order the fieldname on which you want to sort.
     *      Examples: 'lastname', 'number'
     * @param direction indicates whether the sort is ascending (true) or descending (false).
     * @return a <code>List</code> of found nodes
     */
    public List search(String where, String sorted, boolean direction) {
  		Vector retval = new Vector();
  		Enumeration nodeEnum = null;
  		if ((where!=null) && (!where.trim().equals(""))) {
		    where="WHERE "+where;
  		}
  		if ((sorted!=null) && (!sorted.trim().equals(""))) {
		    nodeEnum = builder.searchVector(where,sorted,direction).elements();
	    } else {
		    nodeEnum = builder.searchVector(where).elements();
	    }
        while(nodeEnum.hasMoreElements()){
			retval.addElement(new BasicNode((MMObjectNode)nodeEnum.nextElement(),this));
		}
  		return retval;
    }
}
