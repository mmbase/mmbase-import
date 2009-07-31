/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.core;

import org.mmbase.bridge.*;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id$
 */

abstract public class AbstractField<D extends Object> extends AbstractDescriptor implements Field {

    private static final Logger log = Logging.getLoggerInstance(AbstractField.class);

    protected DataType<D> dataType = null;
    protected int type = TYPE_UNKNOWN;
    protected int state = STATE_UNKNOWN;
    protected int listItemType = TYPE_UNKNOWN;
    protected boolean readOnly = true;

    /**
     * Create a field object based on another field.
     * The newly created field shared the datatype of it's parent
     * (which means that any changes to the datatype affects both fields).
     * @param name the name of the field
     * @param field the parent field
     */
    protected AbstractField(String name, Field field) {
         this(name, field, false);
    }

    /**
     * Create a field object based on another field.
     * @param name the name of the field
     * @param field the parent field
     * @param cloneDataForRewrite determines whether the datatype of the parent field is copied (which means it can be altered
     *        without affecting the original datatype)
     */
    protected AbstractField(String name, Field field, boolean cloneDataForRewrite) {
        super(name, field, cloneDataForRewrite);
        type = field.getType();
        setState(field.getState());
        readOnly = field.isReadOnly();
        listItemType = field.getListItemType();
        if (cloneDataForRewrite) {
            setDataType((DataType<D>)field.getDataType().clone());
        } else {
            setDataType(dataType = field.getDataType());
        }
    }

    /**
     * Create a field object
     * @param name the name of the field
     * @param type the identifier for the MMBase base type for this field
     * @param listItemType If the type of this field is TYPE_LIST, then this is the MMBase base type for the list elements.
     * @param state identifier (virtual, persistent, system, systemvirtual)
     * @param dataType the data type of the field
     */
    protected AbstractField(String name, int type, int listItemType, int state, DataType<D> dataType) {
        super(name);
        this.type = type;
        this.listItemType = listItemType;
        setState(state);
        setDataType(dataType);
    }

    abstract public NodeManager getNodeManager();

    public int compareTo(Field f) {
        int compared = getName().compareTo(f.getName());
        if (compared == 0) compared = dataType.compareTo(f.getDataType());
        return compared;
    }

    /**
     * Whether data type equals to other data type. Only key and type are considered. DefaultValue and
     * required properties are only 'utilities'.
     * @param o the reference object with which to compare.
     * @return true if o is a DataType of which key and type equal to this' key and type.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Field) {
            Field f = (Field) o;
            return getName().equals(f.getName()) && dataType.equals(f.getDataType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * 13 + dataType.hashCode();
    }

    public final int getState() {
        return state;
    }

    protected void setState(int state) {
        if (this.state == STATE_UNKNOWN) {
          readOnly = state == STATE_SYSTEM || state == STATE_SYSTEM_VIRTUAL;
        }
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public int getListItemType() {
        return listItemType;
    }

    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the datatype of a field.
     * It is possible that the datatype of a field is different from the actual field type.
     * @param dataType
     * @throws IllegalArgumentException
     * @see #getType
     */
    public void setDataType(DataType<D> dataType) throws IllegalArgumentException {
        int dataTypeType = dataType.getBaseType();
        if (dataTypeType != type) {
            log.debug("DataType (" + dataType.getBaseTypeIdentifier() + ") is different from db type (" + Fields.getTypeDescription(type) + ").");
        }
        this.dataType = dataType;
    }

    public boolean hasIndex() {
        return (getType() == Field.TYPE_NODE) || getName().equals("number");
    }

    abstract public int getSearchPosition();

    abstract public int getListPosition();

    abstract public int getEditPosition();

    abstract public int getStoragePosition();

    /**
     * Retrieve whether the field is a key and thus need be unique.
     * @return <code>true</code> when field is unique
     */
    public boolean isUnique() {
        return dataType.isUnique();
    }

    abstract public int getMaxLength();

    /**
     * @see org.mmbase.bridge.Field#isRequired()
     */
    public boolean isRequired() {
        return dataType.isRequired();
    }

    /**
     * @see org.mmbase.bridge.Field#isVirtual()
     */
    public boolean isVirtual() {
       return state == STATE_VIRTUAL || state == STATE_SYSTEM_VIRTUAL;
    }

    /**
     * Returns whether a field is a temporary field.
     * Temporary fields hold data needed by a builder to facilitate certain functionality,
     * such as holding node references in a transaction.
     * Temporary fields are never persistent and should normally be ignored.
     * @return <code>true</code> when temporary
     * @since MMBase-1.8
     */
    public boolean isTemporary() {
        // this way of determining if a field is temporary is a bit odd,
        // but it is how it worked in the past - should be changed in the future
        // with possibly a setTemporary() method.
        String fieldName = getName();
        return fieldName != null ? fieldName.startsWith("_") : false;
    }

    /**
     * Returns whether a field is a read only.
     * @return <code>true</code> when read only
     * @see org.mmbase.bridge.Field#isVirtual()
     */
    public boolean isReadOnly() {
       return readOnly;
    }

    abstract public String getGUIType();

    /**
     * Returns a description for this field.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return getName() + ":" +
            Fields.getTypeDescription(getType()) + " / " +
            Fields.getStateDescription(getState())+ "/" +
            getDataType();
    }

    @Override
    public AbstractField clone() {
        return clone (null, false);
    }

    public AbstractField clone(String name, boolean copyDataTypeForRewrite) {
        AbstractField clone = (AbstractField)super.clone(name);
        if (copyDataTypeForRewrite) {
            clone.dataType = (DataType<Object>) dataType.clone();
        }
        return clone;
    }

}
