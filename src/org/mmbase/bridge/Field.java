/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;

/**
 * This interface represents a node's field type information object.
 *
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id: Field.java,v 1.20 2005-06-28 14:01:40 pierre Exp $
 */
public interface Field extends Descriptor, MMBaseType {

    /** A field's state is 'virtual' if it is not persistent in storage. */
    public final static int STATE_VIRTUAL    = 0;
    /** A field's state is 'persistent' if it is persistent in storage, and editable. */
    public final static int STATE_PERSISTENT = 2;
    /** A field's state is 'system' if it is persistent in storage, but not editable by users. */
    public final static int STATE_SYSTEM     = 3;
    /** A field's state is 'system virtual' if it is not persistent in storage, nor editable by users.
     *  @todo reserved but not used yet
     */
    public final static int STATE_SYSTEM_VIRTUAL= 4;
    /** The field's state when it is not (yet) known. */
    public final static int STATE_UNKNOWN    = -1;

    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    public NodeManager getNodeManager();

    /**
     * Returns this field's state identifier (virtual, persistent, system).
     *
     * @return  an <code>int</code> which identifies the state of this field
     */
    public int getState();

    /**
     * Returns the data type this field contains.
     *
     * @return  a <code>DataType</code>  object describing the constraints on this field.
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
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     */
    public int getSearchPosition();

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     */
    public int getListPosition();

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     */
    public int getEditPosition();

    /**
     * Retrieve the position of the field in the database table.
     */
    public int getStoragePosition();

    // methods and constants below are now in the MMBaseType or DataType interfaces.
    // They are maintained for backward compatibility but likely should become deprecated

    public final static int TYPE_STRING  = MMBaseType.TYPE_STRING;
    public final static int TYPE_INTEGER = MMBaseType.TYPE_INTEGER;
    public final static int TYPE_BINARY  = MMBaseType.TYPE_BINARY;
    public final static int TYPE_BYTE    = MMBaseType.TYPE_BINARY;
    public final static int TYPE_FLOAT   = MMBaseType.TYPE_FLOAT;
    public final static int TYPE_DOUBLE  = MMBaseType.TYPE_DOUBLE;
    public final static int TYPE_LONG    = MMBaseType.TYPE_LONG;
    public final static int TYPE_XML     = MMBaseType.TYPE_XML;
    public final static int TYPE_NODE    = MMBaseType.TYPE_NODE;
    public final static int TYPE_UNKNOWN = MMBaseType.TYPE_UNKNOWN;

    /**
     * Returns the GUI name for the data type this field contains.
     * @deprecated use {@link #getDataType } and {@link DataType.getName}
     * @see #getDataType
     */
    public String getGUIType();

    /**
     * Returns the identifier for the data type this field contains.
     *
     * @return  an <code>int</code> which identifies the type of data this field
     *          contains
     */
    public int getType();

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

}
