/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;

import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.NodeManager;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.storage.util.Index;

/**
 * One of the core objects. It is not itself a builder, but is used by builders. Defines one field
 * of a object type / builder.
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @version $Id$
 * @see    org.mmbase.bridge.Field
 * @deprecated use {@link CoreField}
 */
public class FieldDefs extends org.mmbase.core.CoreField {

    public final static int DBSTATE_VIRTUAL     = 0;
    public final static int DBSTATE_PERSISTENT  = 2;
    public final static int DBSTATE_SYSTEM      = 3;

    public final static int DBSTATE_UNKNOWN     = -1;


    public final static int ORDER_CREATE = NodeManager.ORDER_CREATE;
    public final static int ORDER_EDIT   = NodeManager.ORDER_EDIT;
    public final static int ORDER_LIST   = NodeManager.ORDER_LIST;
    public final static int ORDER_SEARCH = NodeManager.ORDER_SEARCH;

    public FieldDefs(String name, int type, int listItemType, int state, DataType dataType ) {
        super(name, type, listItemType, state, dataType);
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param searchPos position in the editor for this field when searching
     * @param listPos position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     */
    public FieldDefs(String guiName, String guiType, int searchPos, int listPos, String name, int type) {
        this(guiName, guiType, searchPos, listPos, name, type, -1, STATE_PERSISTENT);
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param guiName the default GUIName for a field
     * @param guiType  the GUI type (i.e. "integer' or 'field')
     * @param searchPos position in the editor for this field when searching
     * @param listPos position in the editor for this field when listing
     * @param name the actual name of the field in the database
     * @param type the basic MMBase type of the field
     * @param guiPos position in the editor for this field when editing
     * @param state the state of the field (persistent, virtual, etc.)
     */
    public FieldDefs(String guiName, String guiType, int searchPos, int listPos, String name, int type, int guiPos, int state) {
        super(name, type, TYPE_UNKNOWN, state, (DataType)DataTypes.getDataTypeInstance(guiType,type));
        setGUIName(guiName);
        setSearchPosition(searchPos);
        setEditPosition(guiPos);
        setListPosition(listPos);
    }

    /**
     * Retrieve the database name of the field.
     * @deprecated use {@link #getName}
     */
    public String getDBName() {
        return getName();
    }

    /**
     * Retrieves the basic MMBase type of the field.
     *
     * @return The type, this is one of the values defined in this class.
     * @deprecated to access type constraints, use {@link #getDataType}
     */
    public int getDBType() {
        return getType();
    }

    /**
     * Retrieve size of the field.
     * This may not be specified for some field types.
     * @deprecated Use {@link #getMaxLength}
     */
    public int getDBSize() {
        return getMaxLength();
    }

    /**
     * Retrieve whether the field can be left blank.
     * @deprecated use {@link #isRequired}
     */
    public boolean getDBNotNull() {
        return getDataType().isRequired();
    }

    /**
     * Retrieve whether the field is a key and thus need be 'unique'.
     * @deprecated use {@link #isUnique} to determine if a field is unique,
     *             use getIndexes() to return set of Index objects to which this key belongs
     */
    public boolean isKey() {
        return getParent() != null && getParent().getStorageConnector().isInIndex(Index.MAIN,this);
    }
    /**
     * Temporary implementation for backwards-compatibility.
     * I18n stuff in FieldDefs used to use String->String maps. We now have Locale->String maps
     * available.
     * This maps new situation to old situation.
     */
    protected class LocaleToStringMap extends AbstractMap {
        private final Map map;
        public LocaleToStringMap(Map m) {
            map = m;
        }
        public Set entrySet() {
            return new AbstractSet() {
                    public Iterator iterator() {
                        return new Iterator() {
                                private final Iterator i = map.entrySet().iterator();
                                public boolean hasNext() {
                                    return i.hasNext();
                                }
                                public void remove() {
                                    throw new UnsupportedOperationException("");
                                }
                                public Object next() {
                                    final Map.Entry entry = (Map.Entry) i.next();
                                    return new Map.Entry() {
                                            public Object getKey() {
                                                return ((Locale) entry.getKey()).getLanguage();
                                            }
                                            public Object getValue() {
                                                return entry.getValue();
                                            }
                                            public Object setValue(Object o) {
                                                return entry.setValue(o);
                                            }
                                        };

                                }
                            };
                    }
                    public int size() {
                        return map.size();
                    }
                };
        }
    }

    /**
     * @deprecated use {@link #getGUIName(Locale locale)}
     */
    public String getGUIName(String lang) {
        return getGUIName(new Locale(lang, ""));
    }
    /**
     * @deprecated use {@link #getGUIName()}
     */
    public Map getGUINames() {
        return new LocaleToStringMap(getLocalizedGUIName().asMap());
    }

    /**
     * @deprecated use {@link #getDescription()}
     */
    public Map getDescriptions() {
        return new LocaleToStringMap(getLocalizedDescription().asMap());
    }

    /**
     * @deprecated use {@link #getDescription(Locale locale)}
     */
    public String getDescription(String lang) {
        return getDescription(new Locale(lang, ""));
    }

    /**
     * @deprecated use {@link #getState}
     */
    public int getDBState() {
        return getState();
    }

    /**
     * @deprecated should not be called, name need be specified in the constructor
     */
    /*
    public void setDBName(String name) {
        key = name;
        setLocalizedDescription(new org.mmbase.util.LocalizedString(key));
        setLocalizedGUIName(new org.mmbase.util.LocalizedString(key));
    }
    */

    /**
     * SetUI the GUI name of the field for a specified langauge.
     * @param lang the language to set the name for
     * @param value the value to set
     * @deprecated to access type constraints, use {@link #getDataType}
     */
    public void setGUIName(String lang, String value) {
        setGUIName(value, new Locale(lang, ""));
    }

    /**
     * Set the description of the field for a specified langauge.
     * @param lang the language to set the description for
     * @param value the value to set
     * @deprecated use {@link #getLocalizedDescription}
     */
    public void setDescription(String lang, String value) {
        setDescription(value, new Locale(lang, ""));
    }

    /**
     * Set size of the field.
     * @param value the value to set
     * @deprecated use {@link CoreField#setMaxLength}
     */
    public void setDBSize(int value) {
        setMaxLength(value);
    }


    /**
     * Set the position of the field in the database table.
     * @param value the value to set
     * @deprecated use {@link #setStoragePosition}
     */
    public void setDBPos(int value) {
        setStoragePosition(value);
    }

    /**
     * @deprecated use {@link #getStoragePosition}
     */
    public int getDBPos() {
        return getStoragePosition();
    }

    /**
     * Set the position of the field when listing.
     * A value of -1 indicates teh field is unavailable in a list.
     * @param value the value to set
     * @deprecated use {@link #setListPosition}
     */
    public void setGUIList(int value) {
        setListPosition(value);
    }

    /**
     * @deprecated use {@link #getListPosition}
     */
    public int getGUIList() {
        return getListPosition();
    }

    /**
     * Set the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     * @param value the value to set
     * @deprecated use {@link #setEditPosition}
     */
    public void setGUIPos(int value) {
        setEditPosition(value);
    }

    /**
     * @deprecated use {@link #getEditPosition}
     */
    public int getGUIPos() {
        return getEditPosition();
    }
    /**
     * Set the position of the field when searching.
     * A value of -1 indicates teh field is unavailable during search.
     * @param value the value to set
     * @deprecated use {@link #setSearchPosition}
     */
    public void setGUISearch(int value) {
        setSearchPosition(value);
    }

    /**
     * @deprecated use {@link #getSearchPosition}
     */
    public int getGUISearch() {
        return getSearchPosition();
    }

    /**
     * Set the basic MMBase state of the field, using the state description
     * @param value the name of the state
     * @deprecated use {@link #setState}
     */
    public void setDBState(String value) {
        setState(Fields.getState(value));
    }

    /**
     * @deprecated use {@link #getState}
     */
    public void setDBState(int i) {
        setState(i);
    }

    /**
     * Set whether the field is a key and thus needs to be 'unique'.
     * @param value the value to set
     * @deprecated use {@link CoreField#setUnique} to make a field unique.
     */
    //use {@link org.mmbase.module.core.MMTable#addToIndex} to make the field part of an index
    public void setDBKey(boolean value) {
        if (getParent() != null) {
            getParent().getStorageConnector().addToIndex(Index.MAIN, this);
        }
    }

    /**
     * Set whether the field can be left blank.
     * @param value the value to set
     * @deprecated to access type constraints, use {@link #getDataType}
     */
    public void setDBNotNull(boolean value) {
        getDataType().setRequired(value);
    }

    /**
     * Sorts a list with FieldDefs objects, using the default order (ORDER_CREATE)
     * @param fielddefs the list to sort
     * @deprecated use Collections.sort
     */
    public static void sort(List fielddefs) {
        Collections.sort(fielddefs);
    }

    /**
     * Sorts a list with FieldDefs objects, using the specified order
     * @param fielddefs the list to sort
     * @param order one of ORDER_CREATE, ORDER_EDIT, ORDER_LIST,ORDER_SEARCH
     * @deprecated use {@link Fields#sort}
     */
    public static void sort(List fielddefs, int order) {
        Fields.sort(fielddefs, order);
    }

}
