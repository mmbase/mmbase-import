/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;

import org.mmbase.module.core.*;
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
 * @version $Id: CoreField.java,v 1.1 2005-05-09 21:41:02 michiel Exp $
 * @see    org.mmbase.bridge.Field
 * @package org.mmbase.core?
 * @since MMBase-1.8
 */
public class CoreField extends org.mmbase.bridge.implementation.AbstractDataType implements org.mmbase.bridge.Field, Storable {

    protected LocalizedString guiName     = null;
    protected String guiType;
    protected int    guiSearch = -1;
    protected int    guiList   = -1;
    protected int    guiPos    = -1;
    protected int     pos     = -1;
    protected int     maxLength    = -1;

    protected int     typeInt  = TYPE_UNKNOWN;
    protected String  typeString;
    protected int     state   = STATE_UNKNOWN;

    protected boolean required = false;
    protected boolean unique   = false;

    protected MMObjectBuilder parent = null;
    protected Object storageIdentifier = null;
    protected int     storageType = TYPE_UNKNOWN;

    /**
     * Constructor for default FieldDefs.
     */
    public CoreField() {
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param guiSearch position in the editor for this field when searching
     * @param guiList position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     */
    public CoreField(String guiName, String guiType, int search, int list, String name, int type) {
        this(guiName, guiType, search, list, name, type, 2, STATE_PERSISTENT);
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param guiSearch position in the editor for this field when searching
     * @param guiList position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     * @param guiPos position in the editor for this field when editing
     * @param state the state of the field (persistent, virtual, etc.)
     */
    public CoreField(String guiName, String guiType, int guiSearch, int guiList, String name, int type, int guiPos, int state) {
        this.key = name;
        this.state = state;
        this.typeInt = type;
        this.guiType = guiType;
        this.guiName     = new LocalizedString(guiName);
        this.description = new LocalizedString(guiName);
        this.guiSearch = guiSearch;
        this.guiList = guiList;
        this.guiPos  = guiPos;
    }

    /**
     * Provide a description for the current type.
     * @return the description of the type.
     */
    public String getDBTypeDescription() {
        return typeString;
        //return FieldDefs.getDBTypeDescription(type);
    }

    /**
     * Provide a description for the current state.
     * @return the description of the state.
     */
    public String getDBStateDescription() {
        return FieldDefs.getDBStateDescription(state);
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

    /**
     * Retrieves the basic MMBase type of the field.
     *
     * @return The type, this is one of the values defined in this class.
     */
    public int getType() {
        return typeInt;
    }

    /**
     * Retrieve whether the field can be left blank.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Retrieve whether the field is a key and thus need be unique.
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates the field is unavailable during search.
     */
    public int getGUISearch() {
        return guiSearch;
    }

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     */
    public int getGUIList() {
        return guiList;
    }

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     */
    public int getGUIPos() {
        return guiPos;
    }

    /**
     * Retrieve the position of the field in the database table.
     */
    public int getDBPos() {
        return pos;
    }

    public void setDefaultValue(Object def) {
    }
    public Object getDefaultValue() {
        return null;
    }

    public String getDescription() {
        return getDescription(null);
    }
    /**
    /**
     * Retrieves the parent builder for this field
     */
    public MMObjectBuilder getParent() {
        return parent;
    }

    public org.mmbase.bridge.NodeManager getNodeManager() {
        throw new UnsupportedOperationException("Cannot get NodeManager from CoreField. You can use getParent to obtain the MMObjectBuilder");
    }


    /**
     * Returns a description for this field.
     */
    public String toString() {
        return "DEF GUIName=" + getGUIName() + " GUIType=" + guiType +
               " Input=" + guiPos + " Search=" + guiSearch + " List=" + guiList +
               " DBname=" + key +
               " DBType=" + getDBTypeDescription()+
               " DBSTATE=" + getDBStateDescription() +
               " DBNOTNULL=" + required + " DBPos=" + pos + " DBSIZE=" + maxLength +
               " unique=" + unique;
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
                && description.equals(f.description)
                && guiName.equals(f.guiName)
                && guiType.equals(f.guiType)
                && guiSearch == f.guiSearch
                && guiList  ==  f.guiList
                && guiPos   == f.guiPos
                ;
        } else {
            return false;
        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean hasIndex() {
        return (typeInt == TYPE_NODE) || key.equals("number");
    }

    public int getState() {
        return state;
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
        result = HashCodeUtil.hashCode(result, pos);
        return result;
    }
    
    /**
     * Whether this FieldDefs object is equal to another for storage purposes (so, ignoring gui and documentation fields)
     * @since MMBase-1.7
     */
    public boolean storageEquals(CoreField f) {
        return
            key.equals(f.key)
            && typeInt == f.typeInt
            && state == f.state
            && required == f.required
            && unique  == f.unique
            && maxLength == f.maxLength
            && (parent == null ? f.parent == null : parent.equals(f.parent))
            && (storageIdentifier == null ? f.storageIdentifier == null : storageIdentifier.equals(f.storageIdentifier))
            && storageType == f.storageType
            && pos == f.pos
            ;
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
        int pos1 = getDBPos();
        int pos2 = ((FieldDefs)o).getDBPos();
        if (pos1 < pos2) {
            return -1;
        } else if (pos1 > pos2) {
            return 1;
        } else {
            return 0;
        }
    }

}
