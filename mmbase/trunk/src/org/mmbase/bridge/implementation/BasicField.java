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
 * @version $Id: BasicField.java,v 1.10 2002-10-03 12:28:10 pierre Exp $
 */
public class BasicField implements Field, Comparable {

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
        return field.getGUIName(((BasicCloud)nodeManager.getCloud()).getLocale().getLanguage());
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

    public boolean isRequired() {
        return field.getDBNotNull();
    }
    
    public boolean isUnique() {
        return field.isKey();
    }

    /**
     * Compares this field to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a Field and larger than this field,
     * and +1 if the object passed is a Field and smaller than this field.
     * A field is 'larger' than another field if its preferred GUIName is larger (alphabetically, case sensitive)
     * than that of the other field. If GUINames are the same, the fields are compared on internal field name,
     * and (if needed) on their NodeManagers.
     *
     * @param o the object to compare it with
     */
    public int compareTo(Object o) {
        Field f= (Field)o;
        int res=getGUIName().compareTo(f.getGUIName());
        if (res!=0) {
            return res;
        } else {
            res=getName().compareTo(f.getName());
            if (res!=0) {
                return res;
            } else {
                return ((Comparable)nodeManager).compareTo(f.getNodeManager());
            }
        }
    }

    /**
     * Compares this field to the passed object, and returns true if they are equal.
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Field) && 
               nodeManager.equals(((Field)o).getNodeManager()) &&
               getName().equals(((Field)o).getName());
    }
}
