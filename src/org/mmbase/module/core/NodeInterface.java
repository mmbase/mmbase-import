/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;

/**
 *
 * @author Rob Vermeulen
 */
public interface NodeInterface {
	
	/** 
	 * set value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, String value); 

	/** 
	 * get value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public String getValue(String attribute);

	/**
	 * commit the Node to the database
	 */
	public void commit();

	/**
	 * removes the Node
	 */
	public void remove(); 

	/**
	 * converts Node to string
	 */
	 public String toString();

	/**
	 * removes all relations of Node
	 */
	public void removeRelations();

	/**
 	 * removes all relations of certain type
	 * @param type of relation
	 */
	public void removeRelations(String type);

	/**
	 * gets all relations of Node
	 * @return all relations of Node
	 */
	public Enumeration getRelations();

	/**
	 *gets all relations of certain type
	 * @param type of relation
	 * @return all relations of the Node of a certain type
	 */
	public Enumeration getRelations(String type);
}
