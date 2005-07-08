/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.LocalizedString;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen (MMFunctionParam)
 * @since  MMBase-1.8
 * @version $Id: AbstractField.java,v 1.2 2005-07-08 12:23:45 pierre Exp $
 */

abstract public class AbstractField extends AbstractDescriptor implements Field, Comparable {

    private static final Logger log = Logging.getLoggerInstance(AbstractField.class);
    protected DataType dataType = null;

    /**
     * Create a field object based on another field.
     * The newly created field shared the datatype of it's parent
     * (which means that any changes to the datatype affects both fields).
     * @param name the name of the field
     * @param field the parent field
     */
    protected AbstractField(String name, Field field) {
         this(name,field,false);
    }

    /**
     * Create a field object based on another field.
     * @param name the name of the field
     * @param field the parent field
     * @param copyDataTypeForRewrite determines whether the datatype of the parent field is copied (which means it can be altered
     *        without affecting the original datatype)
     */
    protected AbstractField(String name, Field field, boolean copyDataTypeForRewrite) {
        super(name);
        copy((Descriptor)field);
        if (copyDataTypeForRewrite) {
            dataType = field.getDataType().copy(field.getDataType().getName());
        } else {
            dataType = field.getDataType();
        }
    }

    /**
     * Create a field object
     * @param name the name of the field
     * @param dataType the data type of the field
     */
    protected AbstractField(String name, DataType dataType) {
        super(name);
        this.dataType = dataType;
    }

    abstract public NodeManager getNodeManager();

    public int compareTo(Object o) {
        if (o instanceof Field) {
            Field f = (Field) o;
            int compared = getName().compareTo(f.getName());
            if (compared == 0) compared = dataType.compareTo(f.getDataType());
            return compared;
        } else {
            throw new ClassCastException("Object is not of type Field");
        }
    }

    /**
     * Whether data type equals to other data type. Only key and type are considered. DefaultValue and
     * required propererties are only 'utilities'.
     * @return true if o is a DataType of which key and type equal to this' key and type.
     */
    public boolean equals(Object o) {
        if (o instanceof Field) {
            Field f = (Field) o;
            return getName().equals(f.getName()) && dataType.equals(f.getDataType());
        }
        return false;
    }

    public int hashCode() {
        return getName().hashCode() * 13 + dataType.hashCode();
    }

    abstract public int getState();

    public DataType getDataType() {
        return dataType;
    }

    public boolean hasIndex() {
        return (getType() == Field.TYPE_NODE) || getName().equals("number");
    }

    abstract public int getSearchPosition();

    abstract public int getListPosition();

    abstract public int getEditPosition();

    abstract public int getStoragePosition();

    // MMBase Type shortcut
    public int getType() {
        return dataType.getBaseType();
    }

    public Class getTypeAsClass() {
        return dataType.getTypeAsClass();
    }

    public void checkType(Object o) {
        dataType.checkType(o);
    }

    public Object autoCast(Object o) {
        return dataType.autoCast(o);
    }

    // deprecated methods

    public boolean isRequired() {
        return dataType.isRequired();
    }

    abstract public int getMaxLength();

    abstract public String getGUIType();

    abstract public boolean isUnique();

}
