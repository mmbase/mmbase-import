/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.List;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This interface represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (whcih is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNodeType implements NodeType {

    // Cloud for this nodetype
    protected Cloud cloud=null;

    // builder on which the type is based
    protected MMObjectBuilder builder=null;


    BasicNodeType(MMObjectBuilder builder, Cloud cloud) {
        this.cloud=cloud;
        this.builder=builder;
    }

    /**
     * Gets a new (initialized) node
     */
    public Node createNode() {
        MMObjectNode node= builder.getNewNode("system");
        if (node==null) {
	        return null;
	    } else {
	        return new BasicNode(node, this);
	    }
    }

 	/**
     * Retrieves the Cloud to which this node type belongs
     */
    public Cloud getCloud() {
        return cloud;
    }

	/**
     * Retrieve the type name (identifying name) of the nodetype
     */
    public String getName() {
        return builder.getTableName();
    }

 	/**
	 * Retrieve the name of the nodetype
	 * @param language the language in which you want the name
	 */
	public String getGUIName(String language) {
	    Hashtable singularNames=builder.getSingularNames();
        if (singularNames!=null) {
            String tmp=(String)singularNames.get(language);
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getTableName();
	}

	/**
     * Retrieve the name of the nodetype (in the default language defined in mmbaseroot.xml)
     * Note: currently, this method returns the nodetype/builder table name!
     */
    public String getGUIName() {
	    return getGUIName(((BasicCloudContext)cloud.getCloudContext()).mmb.getLanguage());
	}

	/**
	 * Retrieve the description of the nodetype
	 * @param language the language in which you want the description
	 */
	public String getDescription(String language) {
	    Hashtable descriptions=builder.getDescriptions();
        if (descriptions!=null) {
            String tmp=(String)descriptions.get(language);
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getDescription();
	}

	/** 
	 * Retrieve the description of the nodetype
	 */
	public String getDescription() {
	    return getDescription(((BasicCloudContext)cloud.getCloudContext()).mmb.getLanguage());
	}

	/**
	 * Retrieve all field of this nodetype
	 * @param the language in which you want the fields
	 * @return a <code>List</code> of field names
	 */
	public List getFields(String language) {
	    Vector res= new Vector();
	    for(Iterator i=builder.getFields().iterator(); i.hasNext();){
	        FieldDefs f=(FieldDefs)i.next();
	        String tmp=f.getGUIName(language);	
	        res.add(tmp);
	    }
	    return res;
	}

	/** 
	 * Retrieve all fields of this nodetype (in the default language defined in mmbaseroot.xml)
	 * @return a <code>List</code> of field names
	 */
	public List getFields() {
	    return getFields(((BasicCloudContext)cloud.getCloudContext()).mmb.getLanguage());
	}

	/**
     * search nodes of this type
     * @param where the contraint
     * @param order the field on which you want to sort
     * @param direction true=UP false=DOWN
     */
    public List search(String where, String sorted, boolean direction) {
        return builder.searchVector(where,sorted,direction);
    }

}
