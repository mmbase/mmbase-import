/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Iterator;

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
public interface NodeTypeInterface {

 	/**
     * Retrieves the Cloud to which this node type belongs
     */
    public CloudInterface getCloud();

 	/**
     * Retrieve the Graphical User Interface name of the NodeType
     * @param language the language in which you want the name
     */
    public String getGuiName(String language);

    /**
     * Retrieve the System name of the module
     */
    public String getName();

	/**
	 * Retrieve the description of the nodetype
	 * @param language the language in which you want the description
	 */
	public String getDescription(String language);

	/** 
	 * Retireve the description of the nodetype
	 */
	public String getDescription();

	/**
	 * Retrieve all field of this nodetype
	 * @param language the language in which you want the fields
	 */
	public Iterator getFields(String language);

	/** 
	 * Retrieve all fields of this nodetype (in the default language defined in mmbaseroot.xml)
	 */
	public Iterator getFields();
}
