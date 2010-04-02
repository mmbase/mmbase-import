/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

import org.mmbase.bridge.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.datatypes.*;
import java.util.*;

/**

 * @since MMBase-1.8
 * @version $Id: Fields.java 37864 2009-08-15 11:32:01Z michiel $
 */
public class Fields {
    private static final boolean allowNonQueriedFields = true; // not yet configurable


    public final static int STATE_MINVALUE    = 0;
    public final static int STATE_MAXVALUE    = 3;
    private final static String[] STATES = {
        "unknown", "virtual", "unknown", "persistent", "system", "systemvirtual"
    };

    public final static int TYPE_MINVALUE    = 1;
    private final static String[] TYPES = {
        "UNKNOWN", "STRING", "INTEGER", "UNKNOWN", "BINARY" /* BYTE */, "FLOAT", "DOUBLE", "LONG", "XML", "NODE", "DATETIME", "BOOLEAN", "LIST", "DECIMAL"
    };
    public final static int TYPE_MAXVALUE    = TYPES.length - 1;


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
     * @param state the state description to get the id of
     * @return the id of the state.
     */
    public static int getState(String state) {
        if (state == null) return Field.STATE_UNKNOWN;
        state = state.toLowerCase();
        if (state.equals("persistent"))    return Field.STATE_PERSISTENT;
        if (state.equals("virtual"))       return Field.STATE_VIRTUAL;
        if (state.equals("systemvirtual")) return Field.STATE_SYSTEM_VIRTUAL;
        if (state.equals("system"))        return Field.STATE_SYSTEM;
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
        if (type.equals("DECIMAL")) return Field.TYPE_DECIMAL;
        if (type.startsWith("LIST"))    return Field.TYPE_LIST;
        return Field.TYPE_UNKNOWN;
    }

    /**
     * Determines the MMBase type of a specified class. The MMBase base type is sue by the storage layer to
     * determine how to store a field.
     * If the base type cannot be determined from the class, the value returned is {@link Field#TYPE_UNKNOWN}.
     * @param classType
     * @return an MMBase base type constant
     */
    public static int classToType(Class classType) {
        if (classType == null) {
            return Field.TYPE_UNKNOWN;
        } else if (classType.isArray() && classType.getComponentType() == Byte.TYPE) {
            return Field.TYPE_BINARY;
        } else if (java.io.InputStream.class.isAssignableFrom(classType)) {
            return Field.TYPE_BINARY;
        } else if (classType == Integer.class || classType == Integer.TYPE) {
            return Field.TYPE_INTEGER;
        } else if (classType == Long.class || classType == Long.TYPE) {
            return Field.TYPE_LONG;
        } else if (classType == Double.class || classType == Double.TYPE) {
            return Field.TYPE_DOUBLE;
        } else if (classType == Float.class || classType == Float.TYPE) {
            return Field.TYPE_FLOAT;
        } else if (classType == Boolean.class || classType == Boolean.TYPE) {
            return Field.TYPE_BOOLEAN;
        } else if (classType == String.class) {
            return Field.TYPE_STRING;
        } else if (org.w3c.dom.Node.class.isAssignableFrom(classType)) {
            return Field.TYPE_XML;
        } else if (Node.class.isAssignableFrom(classType)) {
            return Field.TYPE_NODE;
        } else if (Date.class.isAssignableFrom(classType)) {
            return Field.TYPE_DATETIME;
        } else if (List.class.isAssignableFrom(classType)) {
            return Field.TYPE_LIST;
        } else if (java.math.BigDecimal.class.isAssignableFrom(classType)) {
            return Field.TYPE_DECIMAL;
        } else {
            return Field.TYPE_UNKNOWN;
        }
    }

    /**
     * Determines the class for a specified MMBase base type.
     * If the value is {@link Field#TYPE_UNKNOWN}), the method returns <code>null</code>.
     * @param type
     * @return an MMBase base type constant
     */
    public static Class typeToClass(int type) {
        switch (type) {
        case Field.TYPE_STRING : return String.class;
        case Field.TYPE_INTEGER : return Integer.class;
        case Field.TYPE_BINARY: return byte[].class;
        case Field.TYPE_FLOAT: return Float.class;
        case Field.TYPE_DOUBLE: return Double.class;
        case Field.TYPE_LONG: return Long.class;
        case Field.TYPE_XML: return org.w3c.dom.Document.class;
        case Field.TYPE_NODE: return Node.class;
        case Field.TYPE_DATETIME: return java.util.Date.class;
        case Field.TYPE_BOOLEAN: return Boolean.class;
        case Field.TYPE_DECIMAL: return java.math.BigDecimal.class;
        case Field.TYPE_LIST: return List.class;
        default: return null;
        }
    }


    public static void sort(List<? extends Field> fields, int order) {
        Collections.sort(fields, new FieldComparator(order));
    }


    /**
     * Comparator to sort CoreFields by creation order, or by position
     * specified in one of the GUIPos fields.
     */
    private static class FieldComparator implements Comparator<Field> {

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
        private int getPos(Field o) {
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
         * Compare two objects.
         */
        public int compare(Field o1, Field o2) {
            int pos1 = getPos(o1);
            int pos2 = getPos(o2);

            if (pos1 < pos2) {
                return -1;
            } else if (pos1 > pos2) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    public static Map<String, Field> getFieldTypes(SearchQuery query,  NodeManager nm) {
        Cloud cloud = nm.getCloud();
        Map<String, Field> fieldTypes = new HashMap<String, Field>();
        // code to solve the fields.
        for (Step step : query.getSteps()) {
            String name = step.getAlias();
            if (name == null) {
                name = step.getTableName();
            }
            Field ft = new VirtualNodeManagerField(nm, createSystemField(cloud, name, Field.TYPE_NODE),  name);
            fieldTypes.put(name, ft);

            if (allowNonQueriedFields && ! query.isAggregating()) {
                /// if hasField returns true also for unqueried fields
                for (Field f : cloud.getNodeManager(step.getTableName()).getFields()) {
                    final String fieldName = name + "." + f.getName();
                    fieldTypes.put(fieldName, new VirtualNodeManagerField(nm, f, fieldName));
                }
            }
        }
        if (! allowNonQueriedFields || query.isAggregating()) {
            //hasField only returns true for queried fields
            for (StepField field : query.getFields()) {
                Step step = field.getStep();
                Field f = cloud.getNodeManager(step.getTableName()).getField(field.getFieldName());
                String name = field.getAlias();
                if (name == null) {
                    name = step.getAlias();
                    if (name == null) {
                        name = step.getTableName();
                    }
                    name += "." + field.getFieldName();
                }
                final String fieldName = name;
                fieldTypes.put(name, new VirtualNodeManagerField(nm, f, fieldName));

            }
        }
        return fieldTypes;
    }


    public static Field createSystemField(Cloud cloud, String name, int type) {
        return createField(cloud, name, type, Field.TYPE_UNKNOWN, Field.STATE_SYSTEM, null);
    }
    /**
     * Defaulting version of {@link #createField(String, int int, int, DataType)} (no list item type,
     * because it is nearly always irrelevant).
      */
    public static Field createField(Cloud cloud, String name, int type, int state, DataType dataType) {
        return createField(cloud, name, type, Field.TYPE_UNKNOWN, state, dataType);
    }


    public static Field createField(Cloud cloud, String name, int type, int listItemType, int state, DataType dataType) {
        throw new UnsupportedOperationException("TODO");
    }





}
