/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.List;
import org.mmbase.module.core.*;

/**
 * This interface represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface NodeType {

    /**
     * Gets a new (initialized) node
     */
    public Node createNode();
 	
 	/**
     * Retrieves the Cloud to which this node type belongs
     */
    public Cloud getCloud();

	/**
     * Retrieve the type name (identifying name) of the nodetype
     */
    public String getName();
 	
 	/**
	 * Retrieve the name of the nodetype
	 * @param language the language in which you want the name
	 */
	public String getGUIName(String language);
	
	/**
     * Retrieve the name of the nodetype (in the default language defined in mmbaseroot.xml)
     */
    public String getGUIName();

	/**
	 * Retrieve the description of the nodetype
	 * @param language the language in which you want the description
	 */
	public String getDescription(String language);

	/** 
	 * Retrieve the description of the nodetype
	 */
	public String getDescription();

	/**
	 * Retrieve all field types of this nodetype
	 * @return a <code>List</code> of <code>FieldType</code> objects
	 */
	public List getFieldTypes();

	/**
     * search nodes of this type
     * @param where the contraint
     * @param order the field on which you want to sort
     * @param direction true=UP false=DOWN
     */
    public List search(String where, String sorted, boolean direction);

}
