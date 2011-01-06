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
public interface Field extends Descriptor, Comparable<Field> {

    /** MMBase base type identifier for the String data type */
    final static int TYPE_STRING = 1;
    /** MMBase base type identifier for the Integer data type */
    final static int TYPE_INTEGER = 2;
    /** MMBase base type identifier for the binary (byte[]) data type */
    final static int TYPE_BINARY = 4;
    /**
     * MMBase base type identifier for the binary (byte[]) data type
     * @deprecated use {@link #TYPE_BINARY}
     */
    @Deprecated
    final static int TYPE_BYTE = TYPE_BINARY;
    /** MMBase base type identifier for the Float data type */
    final static int TYPE_FLOAT = 5;
    /** MMBase base type identifier for the Double data type */
    final static int TYPE_DOUBLE = 6;
    /** MMBase base type identifier for the Long data type */
    final static int TYPE_LONG = 7;
    /** MMBase base type identifier for the DOM Document data type */
    final static int TYPE_XML = 8;
    /** MMBase base type identifier for the Node data type */
    final static int TYPE_NODE = 9;
    /**
     * MMBase base type identifier for the Date data type
     * @since MMBase-1.8
     */
    final static int TYPE_DATETIME = 10;
    /**
     * MMBase base type identifier for the Boolean data type
     * @since MMBase-1.8
     */
    final static int TYPE_BOOLEAN = 11;
    /**
     * MMBase base type identifier for the List data type
     * @since MMBase-1.8
     */
    final static int TYPE_LIST = 12;

    /**
     * @since MMBase-1.9.1
     */
    final static int TYPE_DECIMAL = 13;


    /**
     * MMBase base type identifier for data types whose type is unknown
     */
    final static int TYPE_UNKNOWN = -1;


    /**
     * A field's {@link #getState state} is 'virtual' if it is not persistent in storage. The value of such a field may be implicitely
     * calculated from the values of other fields. This can e.g. be done with {@link org.mmbase.datatypes.processors.Processor}'s which are associated
     * with the {@link #getDataType DataType} of this field.
     */
    final static int STATE_VIRTUAL = 0;

    /**
     * A field's {@link #getState state} is 'persistent' if it is persistent in storage. Most normal fields are this.
     */
    final static int STATE_PERSISTENT = 2;

    /**
     * A field's {@link #getState state} is 'system' if it is persistent in storage, but probably not editable by users (The default value
     * of {@link #isReadOnly} is true). It is used to
     * (automatically) administrate special properties of a Node, like its number, owner and type (these fields are
     * available always), but also e.g. 'created' and 'lastmodified' fields can be marked as 'system' fields.
     */
    final static int STATE_SYSTEM = 3;

    /**
     * A field's {@link #getState state} is 'system virtual' if it is not persistent in storage, and probably used for administration
     * purposes only. In other words the field is both {@link #STATE_SYSTEM} and {@link #STATE_VIRTUAL}.
     * @see #STATE_VIRTUAL
     * @see #STATE_SYSTEM
     */
    final static int STATE_SYSTEM_VIRTUAL = 4;

    /**
     * The field's {@link #getState state} when it is not (yet) known.
     */
    final static int STATE_UNKNOWN = -1;


    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    NodeManager getNodeManager();

    /**
     * Returns this field's state identifier ({@link #STATE_VIRTUAL}, {@link #STATE_PERSISTENT}, {@link #STATE_SYSTEM} or
     * {@link #STATE_SYSTEM_VIRTUAL}, in erroneous situations it may return {@link #STATE_UNKNOWN})
     * @see #isReadOnly();
     * @return  an <code>int</code> which identifies the state of this field
     */
    int getState();

    /**
     * Returns the data type this field contains.
     *
     * @return  a <code>DataType</code>  object describing the constraints on this field.
     * @since MMBase-1.8
     */
    DataType getDataType();

    // DataType<?> getDataType();  // This opens a can of worms



    /**
     * Returns whether this field is part of a unique key (a set of fields whose combined content should
     * occur only once).
     * Note that MMBase lets the storage layer handle this. If your storage implementation or configuration does
     * not support this the uniqueness may not be enforced.
     *
     * @return  <code>true</code> if the field is part of a unique key
     * @since  MMBase-1.6
     */
    boolean isUnique();

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
    boolean hasIndex();

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
    int getType();

    /**
     * If the type of this field is TYPE_LIST, this method returns the MMBase base type for the list elements.
     * This represents one of field type constants. This basic type determines how data is stored in MMBase.
     * For any field types other that TYPE_LIST, this method returns TYPE_UNKNOWN.
     * @return  an <code>int</code> which identifies the base type
     */
    int getListItemType();

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     * @return position of the field when searching
     * @since MMBase-1.8
     */
    int getSearchPosition();

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     * @return position of the field when listing
     * @since MMBase-1.8
     */
    int getListPosition();

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     * @return  position of the field when editing
     * @since MMBase-1.8
     */
    int getEditPosition();

    /**
     * Retrieve the position of the field in the database table.
     * @return position in the database table
     * @since MMBase-1.8
     */
    int getStoragePosition();

    /**
     * Returns the GUI name for the data type this field contains.
     * @return the GUI name
     * @deprecated use {@link #getDataType } and {@link Descriptor#getName}
     * @see #getDataType
     */
    @Deprecated
    String getGUIType();

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
    boolean isRequired();

    /**
     * Returns the maximum length of data this field can contain.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1. For a 'decimal' type
     * though, this returns the same as the precision of the associated datatype.
     *
     * @return  the maximum length of data this field can contain
     */
    int getMaxLength();

    /**
     * Checks whether a given value is valid for this field.
     * @param value value to validate
     * @return Collection of error-strings (describing the problem) in the current locale, or an empty collection if the value is ok.
     * @since MMBase-1.8
     */
    java.util.Collection<String> validate(Object value);

    /**
     * A field's state is 'virtual' if it is not persistent in storage.
     * @return <code>true</code> when a field is virtual
     * @since MMBase-1.8
     */
    boolean isVirtual();

    /**
     * Returns whether a field is 'read only' - that is, a user cannot edit it.
     * In general, fields with state SYSTEM or SYSTEM_VIRTUAL are defined as read only, while others are not.
     * It is possible to override this behaviour per field.
     * @return <code>true</code> when a field is read only
     * @since MMBase-1.8
     */
    boolean isReadOnly();

}
