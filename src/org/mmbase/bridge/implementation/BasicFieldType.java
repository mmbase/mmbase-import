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
 * This class represents a node's field type information object.
 * @author Pierre van Rooden
 */

public class BasicFieldType implements FieldType {

    NodeManager nodeManager=null;
    FieldDefs field=null;

  	BasicFieldType(FieldDefs field, NodeManager nodeManager) {
  	    this.nodeManager=nodeManager;
  	    this.field=field;
  	}
  	
    /**
     * Gets the NodeManager this field belongs to
     * @return the <code>NodeManager</code> object for this field
     */
    public NodeManager getNodeManager() {
        return nodeManager;
    }

	/**
     * Retrieve the field name (identifying name)
     */
    public String getName() {
        return field.getDBName();
    }
 	
 	/**
	 * Retrieve the field's GUI type
	 */
    public String getGUIType() {
        return field.getGUIType();
    }
 	
 	/**
	 * Retrieve the field's GUI name
	 */
    public String getGUIName() {
        return field.getGUIName(nodeManager.getCloud().getLanguage());
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
