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
import org.mmbase.util.HashCodeUtil;

/**
 * One of the core objects. It is not itself a builder, but is used by builders. Defines one field
 * of a object type / builder.
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @version $Id: FieldDefs.java,v 1.44 2005-05-09 21:41:02 michiel Exp $
 * @see    org.mmbase.bridge.Field
 * @deprecated use {@link CoreField}
 */
public class FieldDefs extends CoreField implements Comparable {
    public final static int DBSTATE_MINVALUE    = 0;
    public final static int DBSTATE_VIRTUAL     = 0;
    public final static int DBSTATE_PERSISTENT  = 2;
    public final static int DBSTATE_SYSTEM      = 3;
    public final static int DBSTATE_MAXVALUE    = 3;
    public final static int DBSTATE_UNKNOWN     = -1;

    public final static int TYPE_MINVALUE    = 1;
    public final static int TYPE_MAXVALUE    = 12;


    public final static int ORDER_NONE   = -1;
    public final static int ORDER_CREATE = 0;
    public final static int ORDER_EDIT   = 1;
    public final static int ORDER_LIST   = 2;
    public final static int ORDER_SEARCH = 3;

    private final static String[] DBSTATES = {
        "unknown", "virtual", "unknown", "persistent", "system"
    };

    private final static String[] DBTYPES = {
        "UNKNOWN", "STRING", "INTEGER", "UNKNOWN", "BYTE", "FLOAT", "DOUBLE", "LONG", "XML", "NODE", "DATETIME", "BOOLEAN", "LIST"
    };

    /**
     * Constructor for default FieldDefs.
     */
    public FieldDefs() {
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
    public FieldDefs(String guiName, String guiType, int search, int list, String name, int type) {
        super(guiName, guiType, search, list, name, type);
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
    public FieldDefs(String guiName, String guiType, int guiSearch, int guiList, String name, int type, int guiPos, int state) {
        super(guiName, guiType, guiSearch, guiList, name, type, guiPos, state);
    }

    /**
     * Provide a description for the specified type.
     * Useful for debugging, errors or presenting GUI info.
     * @param type the type to get the description of
     * @return the description of the type.
     */
    public static String getDBTypeDescription(int type) {
       if (type < TYPE_MINVALUE || type > TYPE_MAXVALUE) {
            return DBTYPES[0];
       }

       return DBTYPES[type - TYPE_MINVALUE + 1];
    }

    /**
     * Provide a description for the specified state.
     * Useful for debugging, errors or presenting GUI info.
     * @param state the state to get the description of
     * @return the description of the state.
     */
    public static String getDBStateDescription(int state) {
       if (state < DBSTATE_MINVALUE || state > DBSTATE_MAXVALUE) {
            return DBSTATES[0];
       }
       return DBSTATES[state - DBSTATE_MINVALUE + 1];
    }

    /**
     * Provide an id for the specified mmbase type description
     * @param type the type description to get the id of
     * @return the id of the type.
     */
    public static int getDBTypeId(String type) {
        if (type == null) return STATE_UNKNOWN;
        // XXX: deprecated VARCHAR
        type = type.toUpperCase();
        if (type.equals("VARCHAR")) return TYPE_STRING;
        if (type.equals("STRING"))  return TYPE_STRING;
        if (type.equals("XML"))     return TYPE_XML;
        if (type.equals("INTEGER")) return TYPE_INTEGER;
        if (type.equals("BYTE"))    return TYPE_BYTE;
        if (type.equals("FLOAT"))   return TYPE_FLOAT;
        if (type.equals("DOUBLE"))  return TYPE_DOUBLE;
        if (type.equals("LONG"))    return TYPE_LONG;
        if (type.equals("NODE"))    return TYPE_NODE;
        if (type.equals("DATETIME"))return TYPE_DATETIME;
        if (type.equals("BOOLEAN")) return TYPE_BOOLEAN;
        if (type.startsWith("LIST"))    return TYPE_LIST;
        return TYPE_UNKNOWN;
    }

    /**
     * Provide an id for the specified mmbase state description.
     * @param type the state description to get the id of
     * @return the id of the state.
     */
    public static int getDBStateId(String state) {
        if (state == null) return STATE_UNKNOWN;
        state = state.toLowerCase();
        if (state.equals("persistent"))  return STATE_PERSISTENT;
        if (state.equals("virtual"))     return STATE_VIRTUAL;
        if (state.equals("system"))      return STATE_SYSTEM;
        return STATE_UNKNOWN;
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
     * @deprecated use {@link #getType}
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
        return isRequired();
    }

    /**
     * Retrieve whether the field is a key and thus need be unique.
     * @deprecated use {@link #isUnique}
     */
    public boolean isKey() {
        return isUnique();
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
     * @deprecated
     */
    public String getGUIName(String lang) {
        return getGUIName(new Locale(lang, ""));
    }
    /**
     * @deprecated
     */
    public Map getGUINames() {
        return new LocaleToStringMap(guiName.asMap());
    }
    public Map getDescriptions() {
        return new LocaleToStringMap(description.asMap());
    }
    /**
     * @deprecated
     */
    public String getDescription(String lang) {
        return getDescription(new Locale(lang, ""));
    }
    /**
     * @deprecated
     */
    public int getDBState() {
        return getState();
    }
    public void setDBName(String name) {
        key = name;
        description = new org.mmbase.util.LocalizedString(key);
        guiName = new org.mmbase.util.LocalizedString(key);
    }

    public void setGUIType(String g) {
        guiType = g;
    }

    /**
     * SetUI the GUI name of the field for a specified langauge.
     * @param lang the language to set the name for
     * @param value the value to set
     */
    public void setGUIName(String lang, String value) {
        guiName.set(value, new Locale(lang, ""));
    }

    /**
     * Set the description of the field for a specified langauge.
     * @param lang the language to set the description for
     * @param value the value to set
     * @deprecated use {@link #setDescription(Locale, value)}
     */
    public void setDescription(String lang, String value) {
        description.set(value, new Locale(lang, ""));
    }

    /**
     * Set size of the field.
     * @param value the value to set
     */
    public void setDBSize(int value) {
        maxLength = value;
    }

    /**
     * Set the basic MMBase type of the field.
     * @param value the id of the type
     */
    public void setDBType(int value) {
        typeInt = value;
        typeString = getDBTypeDescription(typeInt);
    }

    /**
     * Set the basic MMBase type of the field, using the type description
     * @param value the name of the type
     */
    public void setDBType(String value) {
        typeInt       = getDBTypeId(value);
        if (typeInt != TYPE_UNKNOWN) {
            typeString = value.toUpperCase();
        } else {
            typeString = null;
        }
    }

    /**
     * Set the position of the field in the database table.
     * @param value the value to set
     */
    public void setDBPos(int value) {
        pos = value;
    }


    /**
     * Set the parent builder for this field
     * @param parent the fielddefs parent
     */
    public void setParent(MMObjectBuilder parent) {
        this.parent = parent;
    }


    /**
     * Set the position of the field when listing.
     * A value of -1 indicates teh field is unavailable in a list.
     * @param value the value to set
     */
    public void setGUIList(int value) {
        guiList = value;
    }

    /**
     * Set the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     * @param value the value to set
     */
    public void setGUIPos(int value) {
        guiPos = value;
    }

    /**
     * Set the position of the field when searching.
     * A value of -1 indicates teh field is unavailable during search.
     * @param value the value to set
     */
    public void setGUISearch(int value) {
        guiSearch = value;
    }



    /**
     * Set the basic MMBase state of the field, using the state description
     * @param value the name of the state
     */
    public void setDBState(String value) {
        setDBState(getDBStateId(value));
    }
    public void setDBState(int i) {
        state = i;
    }


    public void setUnique(boolean u) {
        unique = u;
    }


    /**
     * Set whether the field is a key and thus need be unique.
     * @param value the value to set
     */
    public void setDBKey(boolean value) {
        setUnique(value);
    }

    public void setRequired(boolean r) {
        required = r;
    }

    /**
     * Set whether the field can be left blank.
     * @param value the value to set     
     */
    public void setDBNotNull(boolean value) {
        setRequired(value);
    }


    /**
     * Sorts a list with FieldDefs objects, using the default order (ORDER_CREATE)
     * @param fielddefs the list to sort
     */
    public static void sort(List fielddefs) {
        Collections.sort(fielddefs);
    }

    /**
     * Sorts a list with FieldDefs objects, using the specified order
     * @param fielddefs the list to sort
     * @param order one of ORDER_CREATE, ORDER_EDIT, ORDER_LIST,ORDER_SEARCH
     */
    public static void sort(List fielddefs, int order) {
        Collections.sort(fielddefs,new FieldDefsComparator(order));
    }

    /**
     * Comparator to sort Fielddefs by creation order, or by position
     * specified in one of the GUIPos fields.
     */
    private static class FieldDefsComparator implements Comparator {

        private int order = ORDER_CREATE;

        /**
         * Constrcuts a comparator to sort fields on teh specifie dorder
         * @param order one of ORDER_CREATE, ORDER_EDIT, ORDER_LIST,ORDER_SEARCH
         */
        FieldDefsComparator (int order) {
            this.order = order;
        }

        /**
         * retrieve the postion fo a FieldDefs object
         * according to the order to sort on
         */
        private int getPos(FieldDefs o) {
            switch (order) {
                case ORDER_EDIT: {
                    return o.getGUIPos();
                }
                case ORDER_LIST: {
                    return o.getGUIList();
                }
                case ORDER_SEARCH: {
                    return o.getGUISearch();
                }
                default : {
                    return o.getDBPos();
                }
            }
        }

        /**
         * Compare two objects (should be FieldDefs)
         */
        public int compare(Object o1, Object o2) {
            int pos1 = getPos((FieldDefs)o1);
            int pos2 = getPos((FieldDefs)o2);

            if (pos1 < pos2) {
                return -1;
            } else if (pos1 > pos2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
