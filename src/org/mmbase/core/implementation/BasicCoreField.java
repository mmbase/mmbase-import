/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.implementation;

import java.util.*;

import org.mmbase.core.*;
import org.mmbase.core.util.Fields;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.*;
import org.mmbase.util.*;


/**
 * The core-implementation of a field-type. This is experimental. The package name will probably
 * change. FieldDefs (its extension) will be deprecated and removed.
 *
 * @todo There must perhaps be a new interface:
  interface org.mmbase.core.CoreField (with much of the methods of this class)
 
  interface org.mmbase.core.AdminField extends CoreField {
   // the setMethods (now still in FieldDefs)
  }
  interface org.mmbase.bridge.Field extends CoreField {
     NodeManager getNodeManager();
  }
  org.mmbase.core.implementation BasicCoreField implements AdminField {
  // this class + the set-methods.
  }

 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: BasicCoreField.java,v 1.1 2005-05-10 22:44:59 michiel Exp $
 * @see    org.mmbase.bridge.Field
 * @package org.mmbase.core?
 * @since MMBase-1.8
 */
public class BasicCoreField extends org.mmbase.bridge.implementation.AbstractDataType implements org.mmbase.core.CoreField, Storable, Comparable {

    protected LocalizedString guiName     = null;


    private static final int NO_POSITION = -1;
    private String guiType;
    private int    searchPosition   = NO_POSITION;
    private int    listPosition     = NO_POSITION;
    private int    editPosition     = NO_POSITION;
    private int    maxLength        = NO_POSITION;

    private int     typeInt  = TYPE_UNKNOWN;
    private int     state   = STATE_UNKNOWN;

    private boolean required = false;
    private boolean unique   = false;

    private MMObjectBuilder parent = null;
    private int    storagePosition  = -1;
    private Object  storageIdentifier = null;
    private int     storageType = TYPE_UNKNOWN;

    private Object defaultValue = null;

    protected BasicCoreField() {
    }

    /**
     * Constructor for default FieldDefs.
     */
    public BasicCoreField(String name) {
        key = name;
        this.guiName     = new LocalizedString(key);
        this.description = new LocalizedString(key);
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param searchPosition position in the editor for this field when searching
     * @param listPosition position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     */
    public BasicCoreField(String guiName, String guiType, int search, int list, String name, int type) {
        this(guiName, guiType, search, list, name, type, 2, STATE_PERSISTENT);
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param searchPosition position in the editor for this field when searching
     * @param listPosition position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     * @param editPosition position in the editor for this field when editing
     * @param state the state of the field (persistent, virtual, etc.)
     */
    public BasicCoreField(String guiName, String guiType, int searchPosition, int listPosition, String name, int type, int editPosition, int state) {
        this.key = name;
        this.state = state;
        this.typeInt = type;
        this.guiType = guiType;
        this.guiName     = new LocalizedString(key);
        this.description = new LocalizedString(key);
        this.searchPosition = searchPosition;
        this.listPosition = listPosition;
        this.editPosition  = editPosition;
    }


    /**
     * Retrieve the GUI name of the field depending on specified langauge.
     * If the language is not available, the "en" value is returned instead.
     * If that one is unavailable the internal fieldname is returned.
     * @return the GUI Name
     */
    public String getGUIName(Locale locale) {
        return guiName.get(locale);
    }
    public void setGUIName(String g, Locale locale) {
        guiName.set(g, locale);
    }
    public LocalizedString getLocalizedGUIName() {
        return guiName;
    }

    /**
     * Retrieve the GUI name of the field.
     * If possible, the "en" value is returned.
     * If that one is unavailable the internal fieldname is returned.
     * @return the GUI Name
     */
    public String getGUIName() {
        return guiName.get(null);
    }


    /**
     * Retrieve the GUI type of the field.
     */
    public String getGUIType() {
        return guiType;
    }

    public void setGUIType(String g) {
        guiType = g;
    }

    /**
     * Retrieves the basic MMBase type of the field.
     *
     * @return The type, this is one of the values defined in this class.
     */
    public int getType() {
        return typeInt;
    }

    public void setType(int i) {
        typeInt = i;
    }
    /**
     * Retrieve whether the field can be left blank.
     */
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean b) {
        required = b;
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

    public void setDefaultValue(Object def) {
        defaultValue = def;
    }
    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return getDescription(null);
    }
    public LocalizedString getLocalizedDescription() {
        return description;
    }
    /**
    /**
     * Retrieves the parent builder for this field
     */
    public MMObjectBuilder getParent() {
        return parent;
    }
    /**
     * Set the parent builder for this field
     * @param parent the fielddefs parent
     */
    public void setParent(MMObjectBuilder parent) {
        this.parent = parent;
    }


    /**
     * Returns a description for this field.
     */
    public String toString() {
        return Fields.getTypeDescription(typeInt) + "/" + guiType + " " + key;
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
                && description.equals(f.description)
                && guiName.equals(f.guiName)
                && guiType.equals(f.guiType)
                && searchPosition == f.searchPosition
                && listPosition  ==  f.listPosition
                && editPosition   == f.editPosition
                ;
        } else {
            return false;
        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int i) {
        maxLength = i;
    }


    public boolean hasIndex() {
        return (typeInt == TYPE_NODE) || key.equals("number");
    }

    public int getState() {
        return state;
    }
    public void setState(int i) {
        state = i;
    }


    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, key);
        result = HashCodeUtil.hashCode(result, typeInt);
        result = HashCodeUtil.hashCode(result, state);
        result = HashCodeUtil.hashCode(result, required);
        result = HashCodeUtil.hashCode(result, unique);
        result = HashCodeUtil.hashCode(result, parent);
        result = HashCodeUtil.hashCode(result, storageType);
        result = HashCodeUtil.hashCode(result, storagePosition);
        return result;
    }
    
    /**
     * Whether this FieldDefs object is equal to another for storage purposes (so, ignoring gui and documentation fields)
     * @since MMBase-1.7
     */
    public boolean storageEquals(CoreField f) {
        return
            key.equals(f.getName())
            && typeInt == f.getType()
            && state == f.getState()
            && required == f.isRequired()
            && unique  == f.isUnique()
            && maxLength == f.getMaxLength()
            && (parent == null ? f.getParent() == null : parent.equals(f.getParent()))
            && (storageIdentifier == null ? f.getStorageIdentifier() == null : storageIdentifier.equals(f.getStorageIdentifier()))
            && storageType == f.getStorageType()
            && storagePosition == f.getStoragePosition();
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

    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public int getStorageType() {
        return storageType;
    }

    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public void setStorageType(int value) {
        storageType = value;
    }



    /**
     * Compare this object to the supplied one (should be a FieldDefs)
     * @param the object to compare to
     * @return -1,1, or 0 according to wether this object is smaller, greater, or equals
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

}
