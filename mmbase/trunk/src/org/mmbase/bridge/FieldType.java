/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.module.core.*;

/**
 * This interface represents a node's field type information object.
 * @author Pierre van Rooden
 */
public interface FieldType {

	public final static int FIELDSTATE_VIRTUAL = 0;
	public final static int FIELDSTATE_PERSISTENT = 2;
	public final static int FIELDSTATE_SYSTEM = 3;
	public final static int FIELDSTATE_UNKNOWN = -1;


	public final static int FIELDTYPE_STRING = 1;
	public final static int FIELDTYPE_INTEGER = 2;
	public final static int FIELDTYPE_BYTE = 4;
	public final static int FIELDTYPE_FLOAT = 5;
	public final static int FIELDTYPE_DOUBLE = 6;
	public final static int FIELDTYPE_LONG = 7;
	public final static int FIELDTYPE_UNKNOWN = -1;
    /**
     * Gets the NodeManager this field belongs to
     * @return the <code>NodeType</code> object for this field
     */
    public NodeManager getNodeManager();

	/**
     * Retrieve the field name (identifying name)
     */
    public String getName();
 	
 	/**
	 * Retrieve the field's GUI type
	 */
    public String getGUIType();
 	
 	/**
	 * Retrieve the field's GUI name
	 */
    public String getGUIName();

	/**
	 * Retrieve the field type
	 */
	public int getType();

	/** 
	 * Retrieve the field state
	 */
	public int getState();

}
