/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.Storable;


/**
 * @since MMBase-1.8
 */
public interface CoreField extends Field, Storable {

    /**
     * Retrieves the parent builder for this field
     */
    public MMObjectBuilder getParent();

    /**
     * Set the parent builder for this field
     * @param parent the fielddefs parent
     */
    public void setParent(MMObjectBuilder parent);

    /**
     * Set the position of the field when searching.
     * @see #getSearchPosition
     */
    public void setSearchPosition(int position);

    /**
     * Set the position of the field when listing.
     * @see #getListPosition
     */
    public void setListPosition(int position);

    /**
     * Set the position of the field when editing.
     * @see #getEditPosition
     */
    public void setEditPosition(int position);

    /**
     * Set the position of the field in the database table.
     */
    public void setStoragePosition(int position);

    /**
     * Returns the (maximum) size of this field, as determined by the storage layer.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1.
     *
     * @return  the maximum size of data this field can contain
     */
    public int getSize();

    public void setSize(int i);

    public void setType(int i);

    public void setState(int i);

    public void setUnique(boolean b);

    public boolean storageEquals(CoreField field);

    public void finish();

    public void rewrite();

    public CoreField copy(String name);

    /**
     * Returns the GUI name for the data type this field contains.
     * @deprecated use {@link #getDataType } and {@link DataType.getName}
     * @see #getDataType
     */
    public void setGUIType(String type);

}
