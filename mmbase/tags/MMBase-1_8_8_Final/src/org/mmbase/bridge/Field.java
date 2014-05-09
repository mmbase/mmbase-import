/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.datatypes.DataType;

/**
 * This interface represents a node's field type information object.
 *
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id$
 */
public interface Field extends Descriptor {

    /** MMBase base type identifier for the String data type */
    public final static int TYPE_STRING  = 1;
    /** MMBase base type identifier for the Integer data type */
    public final static int TYPE_INTEGER = 2;
    /** MMBase base type identifier for the binary (byte[]) data type */
    public final static int TYPE_BINARY    = 4;
    /**
     * MMBase base type identifier for the binary (byte[]) data type
     * @deprecated use {@link #TYPE_BINARY}
     */
    public final static int TYPE_BYTE    = TYPE_BINARY;
    /** MMBase base type identifier for the Float data type */
    public final static int TYPE_FLOAT   = 5;
    /** MMBase base type identifier for the Double data type */
    public final static int TYPE_DOUBLE  = 6;
    /** MMBase base type identifier for the Long data type */
    public final static int TYPE_LONG    = 7;
    /** MMBase base type identifier for the DOM Document data type */
    public final static int TYPE_XML     = 8;
    /** MMBase base type identifier for the Node data type */
    public final static int TYPE_NODE    = 9;
    /**
     * MMBase base type identifier for the Date data type
     * @since MMBase-1.8
     */
    public final static int TYPE_DATETIME  = 10;
    /**
     * MMBase base type identifier for the Boolean data type
     * @since MMBase-1.8
     */
    public final static int TYPE_BOOLEAN   = 11;
    /**
     * MMBase base type identifier for the List data type
     * @since MMBase-1.8
     */
    public final static int TYPE_LIST      = 12;
    /** MMBase base type identifier for data types whose type is unknown */
    public final static int TYPE_UNKNOWN = -1;


    /** A field's state is 'virtual' if it is not persistent in storage. */
    public final static int STATE_VIRTUAL    = 0;
    /** A field's state is 'persistent' if it is persistent in storage, and editable. */
    public final static int STATE_PERSISTENT = 2;
    /** A field's state is 'system' if it is persistent in storage, but not editable by users. */
    public final static int STATE_SYSTEM     = 3;
    /** A field's state is 'system virtual' if it is not persistent in storage, nor editable by users.
     *  @todo reserved but not used yet
     */
    public final static int STATE_SYSTEM_VIRTUAL = 4;
    /** The field's state when it is not (yet) known. */
    public final static int STATE_UNKNOWN    = -1;


    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    public NodeManager getNodeManager();

    /**
     * Returns this field's state identifier (virtual, persistent, system, systemvirtual).
     *
     * @return  an <code>int</code> which identifies the state of this field
     */
    public int getState();

    /**
     * Returns the data type this field contains.
     *
     * @return  a <code>DataType</code>  object describing the constraints on this field.
     * @since MMBase-1.8
     */
    public DataType getDataType();

    /**
     * Returns whether this field is part of a unique key (a set of fields whose combined content should
     * occur only once).
     * Note that MMBase lets the storage layer handle this. If your storage implementation or configuration does
     * not support this the uniqueness may not be enforced.
     *
     * @return  <code>true</code> if the field is part of a unique key
     * @since  MMBase-1.6
     */
    public boolean isUnique();

    /**
     * Returns whether this field is a key field, meaning that the storage layer should define an index for it, allowing
     * optimization with search and sort actions.
     * Note that MMBase lets the storage layer decide whether an index is actually defined.
     * Some implementations or configurations may not do this.
     * Note: Currently, this method only returns true if the field is the primary key (number field) or a Node field.
     *
     * @return  <code>true</code> if the field has a key defined
     * @since  MMBase-1.7
     */
    public boolean hasIndex();

    /**
     * Returns the identifier for the MMBase base type for this field.
     * This represents one of field type constants. This basic type determines how data is stored in MMBase.
     * Note that it is possible that the datatype for a field (used for validation and in/out put) can be of a different
     * basic type than how it is stored in the database. This shoudl not occur often, but is possible in some cases, such
     * as when you use older clod models (which used INTEGER fields for dates).
     * In general this should not prove a [problem - however you shoudl not assumeto know the classtype iof data of a
     * field based on this method.
     * To acquire the datatype's type, use <code>getDataType.getBaseType()</code> instead.
     * @return  an <code>int</code> which identifies the base type
     */
    public int getType();

    /**
     * If the type of this field is TYPE_LIST, this method returns the MMBase base type for the list elements.
     * This represents one of field type constants. This basic type determines how data is stored in MMBase.
     * For any field types other that TYPE_LIST, this method returns TYPE_UNKNOWN.
     * @return  an <code>int</code> which identifies the base type
     */
    public int getListItemType();

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     * @since MMBase-1.8
     */
    public int getSearchPosition();

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     * @since MMBase-1.8
     */
    public int getListPosition();

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     * @since MMBase-1.8
     */
    public int getEditPosition();

    /**
     * Retrieve the position of the field in the database table.
     * @since MMBase-1.8
     */
    public int getStoragePosition();

    /**
     * Returns the GUI name for the data type this field contains.
     * @deprecated use {@link #getDataType } and {@link DataType#getName}
     * @see #getDataType
     */
    public String getGUIType();

    /**
     * Returns whether this field is required (should have content).
     * Note that MMBase does not generally enforce required fields to be filled -
     * If not provided, a default value (generally an empty string or the integer value -1)
     * is filled in by the system.
     * As such, isRequired will mostly be used as an indicator for (generic) editors.
     *
     * @return  <code>true</code> if the field is required
     * @since  MMBase-1.6
     */
    public boolean isRequired();

    /**
     * Returns the maximum length of data this field can contain.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1.
     *
     * @return  the maximum length of data this field can contain
     */
    public int getMaxLength();

    /**
     * Checks whether a given value is valid for this field.
     * @return Collection of error-strings (describing the problem) in the current locale, or an empty collection if the value is ok.
     * @since MMBase-1.8
     */
    public java.util.Collection validate(Object value);

    /**
     * A field's state is 'virtual' if it is not persistent in storage.
     * @return <code>true</code> when a field is virtual
     * @since MMBase-1.8
     */
    public boolean isVirtual();

    /**
     * Returns whether a field is 'read only' - that is, a user cannot edit it.
     * In general, fields with state SYSTEM or SYSTEM_VIRTUAL are defined as read only, while others are not.
     * It is possible to override this behaviour per field.
     * @return <code>true</code> when a field is read only
     * @since MMBase-1.8
     */
    public boolean isReadOnly();

}
