/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.DataTypes;
import org.mmbase.core.CoreField;
import java.util.*;

/**

 * @since MMBase-1.8
 */
public class Fields {
    public final static int STATE_MINVALUE    = 0;
    public final static int STATE_MAXVALUE    = 3;
    private final static String[] STATES = {
        "unknown", "virtual", "unknown", "persistent", "system"
    };

    public final static int TYPE_MINVALUE    = 1;
    public final static int TYPE_MAXVALUE    = 12;
    private final static String[] TYPES = {
        "UNKNOWN", "STRING", "INTEGER", "UNKNOWN", "BINARY" /* BYTE */, "FLOAT", "DOUBLE", "LONG", "XML", "NODE", "DATETIME", "BOOLEAN", "LIST"
    };

    /**
     * Returns an instance of a CoreField based on the type, with state 'SYSTEM', and a basic datatype assigned.
     * @param name The name of the field
     * @param type the MMBase basic field type, one of the {@link Field} TYPE constants. Specifying {@link Field#TYPE_LIST},
     *             may give unpredictable results.
     */
    public static CoreField createSystemField(String name, int type) {
        return createField(name, type, Field.TYPE_UNKNOWN, Field.STATE_SYSTEM, null);
    }

    /**
     * Returns an instance of a CoreField based on the type and state.
     * @param name The name of the field
     * @param type the MMBase basic field type, one of the {@link Field} TYPE constants.
     * @param listItemType the MMBase type for items of a list (if type is {@link Field#TYPE_LIST}).
     * @param state the MMBase field state, one of the {@link Field} STATE constants.
     * @param dataType the dataType to use for validating the field data. If <code>null</code>, a default datatype is assigned
     */
    public static CoreField createField(String name, int type, int listItemType, int state, DataType dataType) {
        if (dataType == null) {
            if (type == Field.TYPE_LIST) {
                dataType = (DataType)DataTypes.getListDataType(listItemType).clone();
            } else {
                dataType = (DataType)DataTypes.getDataType(type).clone();
            }
        }
        return new org.mmbase.module.corebuilders.FieldDefs(name, type, listItemType, state, dataType);
    }

    /**
     * Provide a description for the specified type.
     * Useful for debugging, errors or presenting GUI info.
     * @param type the type to get the description of
     * @return the description of the type.
     */
    public static String getTypeDescription(int type) {
       if (type < TYPE_MINVALUE || type > TYPE_MAXVALUE) {
            return TYPES[0];
       }

       return TYPES[type - TYPE_MINVALUE + 1];
    }

    /**
     * Provide a description for the specified state.
     * Useful for debugging, errors or presenting GUI info.
     * @param state the state to get the description of
     * @return the description of the state.
     */
    public static String getStateDescription(int state) {
       if (state < STATE_MINVALUE || state > STATE_MAXVALUE) {
            return STATES[0];
       }
       return STATES[state - STATE_MINVALUE + 1];
    }



    /**
     * Provide an id for the specified mmbase state description.
     * @param type the state description to get the id of
     * @return the id of the state.
     */
    public static int getState(String state) {
        if (state == null) return Field.STATE_UNKNOWN;
        state = state.toLowerCase();
        if (state.equals("persistent"))  return Field.STATE_PERSISTENT;
        if (state.equals("virtual"))     return Field.STATE_VIRTUAL;
        if (state.equals("system"))      return Field.STATE_SYSTEM;
        return Field.STATE_UNKNOWN;
    }

    /**
     * Provide an id for the specified mmbase type description
     * @param type the type description to get the id of
     * @return the id of the type.
     */
    public static int getType(String type) {
        if (type == null) return Field.TYPE_UNKNOWN;
        // XXX: deprecated VARCHAR
        type = type.toUpperCase();
        if (type.equals("VARCHAR")) return Field.TYPE_STRING;
        if (type.equals("STRING"))  return Field.TYPE_STRING;
        if (type.equals("XML"))     return Field.TYPE_XML;
        if (type.equals("INTEGER")) return Field.TYPE_INTEGER;
        if (type.equals("BYTE"))    return Field.TYPE_BINARY;
        if (type.equals("BINARY"))    return Field.TYPE_BINARY;
        if (type.equals("FLOAT"))   return Field.TYPE_FLOAT;
        if (type.equals("DOUBLE"))  return Field.TYPE_DOUBLE;
        if (type.equals("LONG"))    return Field.TYPE_LONG;
        if (type.equals("NODE"))    return Field.TYPE_NODE;
        if (type.equals("DATETIME"))return Field.TYPE_DATETIME;
        if (type.equals("BOOLEAN")) return Field.TYPE_BOOLEAN;
        if (type.startsWith("LIST"))    return Field.TYPE_LIST;
        return Field.TYPE_UNKNOWN;
    }

    public static void sort(List fields, int order) {
        Collections.sort(fields, new FieldComparator(order));
    }


    /**
     * Comparator to sort CoreFields by creation order, or by position
     * specified in one of the GUIPos fields.
     */
    private static class FieldComparator implements Comparator {

        private int order = NodeManager.ORDER_CREATE;

        /**
         * Constrcuts a comparator to sort fields on teh specifie dorder
         * @param order one of NodeManager.ORDER_CREATE, NodeManager.ORDER_EDIT, NodeManager.ORDER_LIST, NodeManager.ORDER_SEARCH
         */
        FieldComparator (int order) {
            this.order = order;
        }

        /**
         * Retrieve the postion of a CoreField object according to the order to sort on
         */
        private int getPos(CoreField o) {
            switch (order) {
            case NodeManager.ORDER_EDIT: {
                return o.getEditPosition();
            }
            case NodeManager.ORDER_LIST: {
                return o.getListPosition();
            }
            case NodeManager.ORDER_SEARCH: {
                return o.getSearchPosition();
            }
            default : {
                return o.getStoragePosition();
            }
            }
        }

        /**
         * Compare two objects (should be CoreFields)
         */
        public int compare(Object o1, Object o2) {
            int pos1 = getPos((CoreField)o1);
            int pos2 = getPos((CoreField)o2);

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
