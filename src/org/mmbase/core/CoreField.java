/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.Storable;


/**

 * @since MMBase-1.8
 */
public interface CoreField extends FieldType, Storable {


    
    /*
     * @see org.mmbase.bridge.NodeManager#ORDER_NONE
     */
    final static int ORDER_NONE   = -1;
    /*
     * @see org.mmbase.bridge.NodeManager#ORDER_CREATE
     */
    final static int ORDER_CREATE = 0;
    /*
     * @see org.mmbase.bridge.NodeManager#ORDER_EDIT
     */
    final static int ORDER_EDIT   = 1;
    /*
     * @see org.mmbase.bridge.NodeManager#ORDER_LIST
     */
    final static int ORDER_LIST   = 2;
    /*
     * @see org.mmbase.bridge.NodeManager#ORDER_SEARCH
     */
    final static int ORDER_SEARCH = 3;



    org.mmbase.util.LocalizedString getLocalizedDescription();
    org.mmbase.util.LocalizedString getLocalizedGUIName();

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     */
    int getSearchPosition();

    void setSearchPosition(int i);
    
    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     */
    int getListPosition();

    void setListPosition(int i);
    
    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     */
    int getEditPosition();

    void setEditPosition(int i);

    /**
     * Retrieve the position of the field in the database table.
     */
    int getStoragePosition();

    void setStoragePosition(int i);


    /**
     * Retrieves the parent builder for this field
     */
    MMObjectBuilder getParent();

    /**
     * Set the parent builder for this field
     * @param parent the fielddefs parent
     */
    void setParent(MMObjectBuilder parent);
    


    void setGUIType(String g);
    void setGUIName(String g, Locale locale);

    void setMaxLength(int i);

    void setType(int i);
    void setState(int i);
    void setUnique(boolean b);
    void setRequired(boolean b);

    boolean storageEquals(CoreField field);
}
