/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Locale;
import java.util.ResourceBundle;
import org.mmbase.bridge.*;
import org.mmbase.core.CoreField;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicField.java,v 1.19 2005-05-10 22:57:11 michiel Exp $
 */
public class BasicField implements Field, Comparable {

    NodeManager nodeManager = null;
    CoreField field = null;

    BasicField(CoreField field, NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.field = field;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public String getName() {
        return field.getName();
    }

    public String getGUIType() {
        return field.getGUIType();
    }

    public String getGUIName() {
        return getGUIName(null);
    }

    public String getGUIName(Locale locale) {
        return field.getGUIName(locale);
    }

    public String getDescription() {
        return getDescription(null);
    }

    public String getDescription(Locale locale) {
        return field.getDescription(locale);
    }

    public int getType() {
        return field.getType();
    }

    public int getState() {
        return field.getState();
    }

    public int getMaxLength() {
        return field.getMaxLength();
    }

    public boolean isRequired() {
        return field.isRequired();
    }

    public boolean isUnique() {
        return field.isUnique();
    }

    public boolean hasIndex() {
        return field.hasIndex();
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
     * @return 0 if they are equal, -1 if the object passed is a Field and larger than this field,
     * and +1 if the object passed is a Field and smaller than this field.
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

    public void checkType(Object value) {
    }

    public Object autoCast(Object value){
        return value;
    }
    public Class getTypeAsClass() {
        switch(getType()) {
        case TYPE_STRING: return String.class;
        case TYPE_INTEGER : return Integer.class;
        case TYPE_BYTE: return byte[].class;
        case TYPE_FLOAT: return Float.class;
        case TYPE_DOUBLE: return Double.class;
        case TYPE_LONG: return Long.class;
        case TYPE_XML: return org.w3c.dom.Document.class;
        case TYPE_NODE: return Node.class;
        case TYPE_DATETIME: return java.util.Date.class;
        case TYPE_BOOLEAN: return Boolean.class;
        default: return null;
        }
    }

    public String toString() {
        return getName() + "(" + getTypeAsClass().toString() + " /  " + getGUIType() + ")";
    }

    /**
     * Sets the default value of this data type.
     * @param def the default value
     */
    public Object getDefaultValue() {
        return null;
    }

    public void setDefaultValue(Object def) {
        throw new UnsupportedOperationException("Cannot change default value of a field type");
    }
    public void setBundle(String bundle) {
        throw new UnsupportedOperationException("Cannot change description of a field type");
    }
    public void setDescription(String description,  Locale locale) {
        throw new UnsupportedOperationException("Cannot change description of a field type");
    }
    
    /**
     * Compares this field to the passed object, and returns true if they are equal.
     * @param o the object to compare it with
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        return (o instanceof Field) &&
               nodeManager.equals(((Field)o).getNodeManager()) &&
               getName().equals(((Field)o).getName());
    }
}
