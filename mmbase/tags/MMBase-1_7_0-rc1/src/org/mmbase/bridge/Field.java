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
 * @version $Id: Field.java,v 1.15 2003-11-26 17:58:27 michiel Exp $
 */
public interface Field {

    public final static int STATE_VIRTUAL    = 0;
    public final static int STATE_PERSISTENT = 2;
    public final static int STATE_SYSTEM     = 3;
    public final static int STATE_UNKNOWN    = -1;


    public final static int TYPE_STRING  = 1;
    public final static int TYPE_INTEGER = 2;
    public final static int TYPE_BYTE    = 4;
    public final static int TYPE_FLOAT   = 5;
    public final static int TYPE_DOUBLE  = 6;
    public final static int TYPE_LONG    = 7;
    public final static int TYPE_XML     = 8;
    public final static int TYPE_NODE    = 9;
    public final static int TYPE_UNKNOWN = -1;

    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    public NodeManager getNodeManager();

    /**
     * Returns the name of this field.
     *
     * @return  the name of this field
     */
    public String getName();


    /**
     * Returns the GUI name for the data type this field contains.
     *
     * @return  the GUI name for the data type this field contains
     */
    public String getGUIType();

    /**
     * Returns the GUI name for this field.
     *
     * @return  the GUI name for this field
     */
    public String getGUIName();

    /**
     * Returns the GUI name for this field in a specified preferred language.
     *
     * @param locale the locale that determines the language for the GUI name
     * @return  the GUI name for this field
     * @since MMBase-1.7
     */
    public String getGUIName(Locale locale);

    /**
     * Returns the description for this field.
     *
     * @return  the description for this field
     */
    public String getDescription();

    /**
     * Returns the description for this field in a specified preferred language.
     *
     * @param locale the locale that determines the language for the description
     * @return  the description for this field
     * @since MMBase-1.7
     */
    public String getDescription(Locale locale);

    /**
     * Returns the identifier for the data type this field contains.
     *
     * @return  an <code>int</code> which identifies the type of data this field
     *          contains
     */
    public int getType();

    /**
     * Returns this field's state identifier.
     *
     * @return  an <code>int</code> which identifies the state of this field
     */
    public int getState();

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
     * Returns the maximum length of data this field can contain.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1.
     *
     * @return  the maximum length of data this field can contain
     */

    public int getMaxLength();

}
