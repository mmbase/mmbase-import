/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import org.mmbase.core.*;
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
        "UNKNOWN", "STRING", "INTEGER", "UNKNOWN", "BYTE", "FLOAT", "DOUBLE", "LONG", "XML", "NODE", "DATETIME", "BOOLEAN", "LIST"
    };


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
        if (state == null) return FieldType.STATE_UNKNOWN;
        state = state.toLowerCase();
        if (state.equals("persistent"))  return FieldType.STATE_PERSISTENT;
        if (state.equals("virtual"))     return FieldType.STATE_VIRTUAL;
        if (state.equals("system"))      return FieldType.STATE_SYSTEM;
        return FieldType.STATE_UNKNOWN;
    }

    /**
     * Provide an id for the specified mmbase type description
     * @param type the type description to get the id of
     * @return the id of the type.
     */
    public static int getType(String type) {
        if (type == null) return FieldType.TYPE_UNKNOWN;
        // XXX: deprecated VARCHAR
        type = type.toUpperCase();
        if (type.equals("VARCHAR")) return FieldType.TYPE_STRING;
        if (type.equals("STRING"))  return FieldType.TYPE_STRING;
        if (type.equals("XML"))     return FieldType.TYPE_XML;
        if (type.equals("INTEGER")) return FieldType.TYPE_INTEGER;
        if (type.equals("BYTE"))    return FieldType.TYPE_BYTE;
        if (type.equals("FLOAT"))   return FieldType.TYPE_FLOAT;
        if (type.equals("DOUBLE"))  return FieldType.TYPE_DOUBLE;
        if (type.equals("LONG"))    return FieldType.TYPE_LONG;
        if (type.equals("NODE"))    return FieldType.TYPE_NODE;
        if (type.equals("DATETIME"))return FieldType.TYPE_DATETIME;
        if (type.equals("BOOLEAN")) return FieldType.TYPE_BOOLEAN;
        if (type.startsWith("LIST"))    return FieldType.TYPE_LIST;
        return FieldType.TYPE_UNKNOWN;
    }

    public static void sort(List fields, int order) {
        Collections.sort(fields, new FieldComparator(order));
    }


    /**
     * Comparator to sort Fielddefs by creation order, or by position
     * specified in one of the GUIPos fields.
     */
    private static class FieldComparator implements Comparator {

        private int order = CoreField.ORDER_CREATE;

        /**
         * Constrcuts a comparator to sort fields on teh specifie dorder
         * @param order one of CoreField.ORDER_CREATE, CoreField.ORDER_EDIT, CoreField.ORDER_LIST,CoreField.ORDER_SEARCH
         */
        FieldComparator (int order) {
            this.order = order;
        }

        /**
         * retrieve the postion fo a FieldDefs object
         * according to the order to sort on
         */
        private int getPos(CoreField o) {
            switch (order) {
            case CoreField.ORDER_EDIT: {
                return o.getEditPosition();
            }
            case CoreField.ORDER_LIST: {
                return o.getListPosition();
            }
            case CoreField.ORDER_SEARCH: {
                return o.getSearchPosition();
            }
            default : {
                return o.getStoragePosition();
            }
            }
        }
        
        /**
         * Compare two objects (should be FieldDefs)
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
