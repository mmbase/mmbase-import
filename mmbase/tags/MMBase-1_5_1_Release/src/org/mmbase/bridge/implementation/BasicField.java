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
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicField.java,v 1.5 2002-01-31 10:05:11 pierre Exp $
 */
public class BasicField implements Field {

    NodeManager nodeManager=null;
    FieldDefs field=null;

    BasicField(FieldDefs field, NodeManager nodeManager) {
        this.nodeManager=nodeManager;
        this.field=field;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public String getName() {
        return field.getDBName();
    }

    public String getGUIType() {
        return field.getGUIType();
    }

    public String getGUIName() {
        return field.getGUIName(((BasicCloud)nodeManager.getCloud()).language);
    }

    public int getType() {
        return field.getDBType();
    }

    public int getState() {
        return field.getDBState();
    }

    public int getMaxLength() {
        return field.getDBSize();
    }

    /**
    * Compares two fields, and returns true if they are equal.
    * @param o the object to compare it with
    */
    public boolean equals(Object o) {
        return (o instanceof Field) && (o.hashCode()==hashCode());
    };

    /**
    * Returns the object's hashCode.
    * This effectively returns the objectnode's number
    */
    public int hashCode() {
        return ((getNodeManager().hashCode()) * 100) + field.getDBPos();
    };
}
