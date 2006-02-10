/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.datatypes.*;
import org.mmbase.core.*;
import org.mmbase.core.util.Fields;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.*;
import org.mmbase.util.*;
import java.util.Collection;

import org.mmbase.util.logging.*;

/**
 * @since MMBase-1.8
 */
public class CoreField extends AbstractField implements Field, Storable, Cloneable {

    private static final Logger log = Logging.getLoggerInstance(CoreField.class);

    private static final int NO_POSITION = -1;

    private int storageType; // JDBC type of this field
    private int searchPosition = NO_POSITION;
    private int listPosition = NO_POSITION;
    private int editPosition = NO_POSITION;


    private int maxLength = -1;

    private MMObjectBuilder parent = null;
    private int storagePosition = -1;
    private Object storageIdentifier = null;

    private boolean notNull = false;

    /**
     * Create a core object
     * @param name the name of the data type
     * @param dataType the data type for this field
     */
    protected CoreField(String name, int type, int listItemType, int state, DataType dataType) {
        super(name, type, listItemType, state, dataType);
    }

    /**
     * Copy constructor.
     * @param name the name of the data type
     * @param coreField
     */
    protected CoreField(String name, CoreField coreField) {
        super(name, coreField, true);
        setSearchPosition(coreField.getSearchPosition());
        setEditPosition(coreField.getEditPosition());
        setListPosition(coreField.getListPosition());
        setStoragePosition(coreField.getStoragePosition());
        setParent(coreField.getParent());
        setMaxLength(coreField.getMaxLength());
        setUnique(coreField.isUnique());
    }

    public NodeManager getNodeManager() {
        throw new UnsupportedOperationException("Core fields currently do not support calls to getNodeManager.");
    }

    public Object clone() {
        return clone (null);
    }

    public Object clone(String name) {
        return super.clone(name, true);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setNotNull(boolean nl) {
        notNull = nl;
    }

    public boolean isNotNull() {
        return notNull;
    }

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     */
    public int getSearchPosition() {
        return searchPosition;
    }

    /**
     * Set the position of the field when searching.
     * @see #getSearchPosition
     */
    public void setSearchPosition(int i) {
        searchPosition = i;
    }

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     */
    public int getListPosition() {
        return listPosition;
    }

    /**
     * Set the position of the field when listing.
     * @see #getListPosition
     */
    public void setListPosition(int i) {
        listPosition = i;
    }

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     */
    public int getEditPosition() {
        return editPosition;
    }

    /**
     * Set the position of the field when editing.
     * @see #getEditPosition
     */
    public void setEditPosition(int i) {
        editPosition = i;
    }

    /**
     * Retrieve the position of the field in the database table.
     */
    public int getStoragePosition() {
        return storagePosition;
    }

    /**
     * Set the position of the field in the database table.
     */
    public void setStoragePosition(int i) {
        storagePosition = i;
    }

    /**
     * Retrieves the parent builder for this field
     */
    public MMObjectBuilder getParent() {
        return parent;
    }

    /**
     * Set the parent builder for this field
     * @param parent the parent builder
     */
    public void setParent(MMObjectBuilder parent) {
        this.parent = parent;
    }

    public void setState(int state) {
        super.setState(state);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setListItemType(int listItemType) {
        this.listItemType = listItemType;
    }

    public Collection validate(Object value) {
        Collection errors = getDataType().validate(value, null, this);
        return LocalizedString.toStrings(errors, parent.getMMBase().getLocale());
    }

    /**
     * Whether this CoreField is equal to another for storage purposes (so, ignoring gui and documentation fields)
     * @since MMBase-1.7
     */
    public boolean storageEquals(CoreField f) {
        return
            getName().equals(f.getName())
            && readOnly == f.isReadOnly()
            && state == f.getState()
            && getDataType().isRequired() == f.getDataType().isRequired()
            && getDataType().isUnique()  == f.getDataType().isUnique()
            && maxLength == f.getMaxLength()
            && (parent == null ? f.getParent() == null : parent.equals(f.getParent()))
            && getStorageIdentifier().equals(f.getStorageIdentifier())
            && getStorageType() == f.getStorageType() // implies equal MMBase types
            && storagePosition == f.getStoragePosition();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @since MMBase-1.7
     */
    public boolean equals(Object o) {
        if (o instanceof CoreField) {
            CoreField f = (CoreField) o;
            return
                storageEquals(f)
                && getLocalizedDescription().equals(f.getLocalizedDescription())
                && getLocalizedGUIName().equals(f.getLocalizedGUIName())
                && searchPosition == f.searchPosition
                && listPosition  ==  f.listPosition
                && editPosition   == f.editPosition
                ;
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, getName());
        result = HashCodeUtil.hashCode(result, getType());
        result = HashCodeUtil.hashCode(result, getState());
        result = HashCodeUtil.hashCode(result, getDataType().isRequired());
        result = HashCodeUtil.hashCode(result, getDataType().isUnique());
        result = HashCodeUtil.hashCode(result, parent);
        result = HashCodeUtil.hashCode(result, storagePosition);
        return result;
    }

    /**
     * Compare this object to the supplied one (should be a CoreField)
     * @param o the object to compare to
     * @return -1,1, or 0 according to wether this object is smaller, greater, or equal
     *         to the supplied one.
     */
    public int compareTo(Object o) {
        int pos1 = getStoragePosition();
        int pos2 = ((CoreField)o).getStoragePosition();
        if (pos1 < pos2) {
            return -1;
        } else if (pos1 > pos2) {
            return 1;
        } else {
            return 0;
        }
    }

    public void finish() {
        dataType.finish(this);
    }

    public void rewrite() {
        dataType.rewrite(this);
    }

    /**
     * Returns the (maximum) size of this field, as determined by the storage layer.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1.
     *
     * @return  the maximum size of data this field can contain
     */
    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int size) {
        this.maxLength = size;
        if (dataType instanceof LengthDataType  && size < ((LengthDataType)dataType).getMaxLength()) {
            if (dataType.isFinished()) {
                dataType = (DataType) dataType.clone();
            }
            ((LengthDataType)dataType).setMaxLength(size);
        }
    }

    public void setDataType(DataType dataType) throws IllegalArgumentException {
        int dataTypeType = Fields.classToType(dataType.getTypeAsClass());
        if (dataTypeType != type) {
            throw new IllegalArgumentException("DataType (" + Fields.getTypeDescription(dataTypeType) + ") is differnent from db type (" + Fields.getTypeDescription(type) + "). Cannot set DataType " + dataType);
        }
        this.dataType = dataType;
        // datatype can be influenced by size
        setMaxLength(maxLength);
    }

    public void setUnique(boolean unique) {
        dataType.setUnique(unique);
    }

    // Storable interface
    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public Object getStorageIdentifier() throws StorageException {
        // determine the storage identifier from the name
        if (storageIdentifier == null) {
            storageIdentifier = MMBase.getMMBase().getStorageManagerFactory().getStorageIdentifier(this);
        }
        return storageIdentifier;
    }

    public int getStorageType() {
        return storageType;
    }

    public void setStorageType(int type) {
        storageType = type;
    }

    public boolean inStorage() {
        return !isVirtual();
    }

    // deprecated methods
    /**
     * Retrieve the GUI type of the field.
     */
    public String getGUIType() {
        return dataType.getName();
    }

    public String toString() {
        return super.toString() + (parent != null ? " of " + parent.getTableName() : " without parent");
    }



}
