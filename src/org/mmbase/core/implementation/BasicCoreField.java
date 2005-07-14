/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.implementation;

import org.mmbase.bridge.DataType;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.datatypes.*;
import org.mmbase.bridge.util.DataTypes;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.core.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.*;
import org.mmbase.util.*;

/**
 * The core-implementation of a field-type.
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: BasicCoreField.java,v 1.6 2005-07-14 11:37:53 pierre Exp $
 * @see    org.mmbase.bridge.Field
 * @package org.mmbase.core?
 * @since MMBase-1.8
 */
public class BasicCoreField extends org.mmbase.bridge.implementation.AbstractField implements CoreField {

    private static final int NO_POSITION = -1;

    private int storageType; // JDBC type of this field
    private int searchPosition = NO_POSITION;
    private int listPosition = NO_POSITION;
    private int editPosition = NO_POSITION;
    private int size = NO_POSITION;

    private boolean unique = false;

    private MMObjectBuilder parent = null;
    private int storagePosition = -1;
    private Object storageIdentifier = null;

    /**
     * Create a core object
     * @param name the name of the data type
     * @param dataType the data type for this field
     */
    protected BasicCoreField(String name, int type, int listItemType, int state, DataType dataType) {
        super(name, type, listItemType, state, dataType);
    }

    /**
     * Create a core object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicCoreField(String name, CoreField coreField) {
        super(name, coreField, true);
        setSearchPosition(coreField.getSearchPosition());
        setEditPosition(coreField.getEditPosition());
        setListPosition(coreField.getListPosition());
        setStoragePosition(coreField.getStoragePosition());
        setParent(coreField.getParent());
        setSize(coreField.getSize());
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

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     */
    public int getSearchPosition() {
        return searchPosition;
    }

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

    public void setEditPosition(int i) {
        editPosition = i;
    }

    /**
     * Retrieve the position of the field in the database table.
     */
    public int getStoragePosition() {
        return storagePosition;
    }

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
        this.state = state;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setListItemType(int listItemType) {
        this.listItemType = listItemType;
    }

    /**
     * Returns a description for this field.
     */
    public String toString() {

        return getName() + "(" + getDataType() + ")";
    }

    /**
     * Whether this CoreField is equal to another for storage purposes (so, ignoring gui and documentation fields)
     * @since MMBase-1.7
     */
    public boolean storageEquals(CoreField f) {
        return
            getName().equals(f.getName())
            && state == f.getState()
            && getDataType().isRequired() == f.getDataType().isRequired()
            && unique  == f.isUnique()
            && size == f.getSize()
            && (parent == null ? f.getParent() == null : parent.equals(f.getParent()))
            && (storageIdentifier == null ? f.getStorageIdentifier() == null : storageIdentifier.equals(f.getStorageIdentifier()))
            && getStorageType() == f.getStorageType() // implues equal MMBase types
            && storagePosition == f.getStoragePosition();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @since MMBase-1.7
     */
    public boolean equals(Object o) {
        if (o instanceof BasicCoreField) {
            BasicCoreField f = (BasicCoreField) o;
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
        result = HashCodeUtil.hashCode(result, state);
        result = HashCodeUtil.hashCode(result, getDataType().isRequired());
        result = HashCodeUtil.hashCode(result, unique);
        result = HashCodeUtil.hashCode(result, parent);
        result = HashCodeUtil.hashCode(result, storagePosition);
        return result;
    }

    /**
     * Compare this object to the supplied one (should be a CoreField)
     * @param the object to compare to
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

    // Storable interfaces
    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public Object getStorageIdentifier() throws StorageException {
        // determine the storage identifier from the name
        if (storageIdentifier == null) {
            storageIdentifier = parent.getMMBase().getStorageManagerFactory().getStorageIdentifier(this);
        }
        return storageIdentifier;
    }

    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public boolean inStorage() {
        return (state == STATE_PERSISTENT || state == STATE_SYSTEM);
    }

    public int getStorageType() {
        return storageType;
    }

    public void setStorageType(int type) {
        storageType = type;
    }

    public void finish() {
        if (dataType instanceof AbstractDataType) {
            ((AbstractDataType)dataType).finish(this);
        }
    }

    public void rewrite() {
        if (dataType instanceof AbstractDataType) {
            ((AbstractDataType)dataType).rewrite(this);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        if (dataType instanceof BigDataType && size < ((BigDataType)dataType).getMaxLength()) {
            ((BigDataType)dataType).setMaxLength(size);
        } else if (dataType instanceof ListDataType && size < ((ListDataType)dataType).getMaxSize()) {
            ((ListDataType)dataType).setMaxSize(size);
        }
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
        // datatype can be influenced by size
        setSize(getSize());
    }

    // deprecated methods
    /**
     * Retrieve the GUI type of the field.
     */
    public String getGUIType() {
        return dataType.getName();
    }

    /**
     * Retrieve whether the field is a key and thus need be unique.
     */
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean b) {
        unique = b;
    }

    public int getMaxLength() {
        return getSize();
    }

}
