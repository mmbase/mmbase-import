/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;

/**
 * This interface represents a node's field type information object.
 * @author Pierre van Rooden
 */

public class BasicFieldType implements FieldType {

    NodeType nodeType=null;
    FieldDefs field=null;

  	BasicFieldType(FieldDefs field, NodeType nodeType) {
  	    this.nodeType=nodeType;
  	    this.field=field;
  	}
  	
    /**
     * Gets the node type this field belongs to
     * @return the <code>NodeType</code> object for this field
     */
    public NodeType getNodeType() {
        return nodeType;
    }

	/**
     * Retrieve the field name (identifying name)
     */
    public String getName() {
        return field.getDBName();
    }
 	
 	/**
	 * Retrieve the field's GUI name
	 */
    public String getGUIName() {
        return field.getGUIName(((BasicCloudContext)nodeType.getCloud().getCloudContext()).mmb.getLanguage());
    }

	/**
	 * Retrieve the field type
	 */
	public int getType() {
        return field.getDBType();
	}

	/** 
	 * Retrieve the field state
	 */
	public int getState() {
        return field.getDBState();
	}

}
