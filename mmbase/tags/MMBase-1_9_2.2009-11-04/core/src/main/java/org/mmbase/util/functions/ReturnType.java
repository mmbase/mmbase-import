/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;

/**
 * Description of the return type of certain function. This wraps a Class object but it has some
 * extra members. Can be used as a constructor argument of {@link Function} objects or as an
 * argument of {@link Function#setReturnType}.
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen

 * @version $Id$
 * @since MMBase-1.7
 */
public class ReturnType<C> extends Parameter<C> implements java.io.Serializable {
    private static final long serialVersionUID = 0L;
    /**
     * The return type of a function that does not return a thing.
     */
    public static final ReturnType<Void> VOID = new ReturnType<Void>(void.class, "Does not return anything");

    /**
     * The return type of a function that returns a String.
     */
    public static final ReturnType<String> STRING = new ReturnType<String>(String.class, "String");

    public static final ReturnType<CharSequence> CHARSEQUENCE = new ReturnType<CharSequence>(CharSequence.class, "CharSequence");

    /**
     * The return type of a function that returns a Integer.
     */
    public static final ReturnType<Integer> INTEGER = new ReturnType<Integer>(Integer.class, "Integer");

    /**
     * The return type of a function that returns a Long.
     */
    public static final ReturnType<Long> LONG = new ReturnType<Long>(Long.class, "Long");

    /**
     * The return type of a function that returns a Double.
     */
    public static final ReturnType<Double> DOUBLE = new ReturnType<Double>(Double.class, "Double");

    /**
     * The return type of a function that returns a Boolean.
     */
    public static final ReturnType<Boolean> BOOLEAN = new ReturnType<Boolean>(Boolean.class, "Boolean");

    /**
     * The return type of a function that returns a List.
     */
    public static final ReturnType<List> LIST = new ReturnType<List>(List.class, "List");


    /**
     * The return type of a function that returns a NodeList.
     */
    public static final ReturnType<org.mmbase.bridge.NodeList> NODELIST = new ReturnType<org.mmbase.bridge.NodeList>(org.mmbase.bridge.NodeList.class, "NodeList");

    /**
     * The return type of a function that returns a Node.
     */
    public static final ReturnType<org.mmbase.bridge.Node> NODE = new ReturnType<org.mmbase.bridge.Node>(org.mmbase.bridge.Node.class, "Node");

    /**
     * The return type of a function that returns a Set.
     */
    public static final ReturnType<Set<?>> SET = new ReturnType<Set<?>>(Set.class, "Set");
    /**
     * The return type of a function that returns a Set.
     */
    public static final ReturnType<Collection<?>> COLLECTION = new ReturnType<Collection<?>>(Collection.class, "Collection");

    /**
     * The return type of a function that returns a Map.
     */
    public static final ReturnType<Map<?, ?>> MAP = new ReturnType<Map<?, ?>>(Map.class, "Map");

    /**
     * The return type of a function is unknown.
     */
    public static final ReturnType<Object> UNKNOWN = new ReturnType<Object>(Object.class, "unknown");

    /**
     * The return type of a function is None
     */
    public static final ReturnType<Object> NONE = new ReturnType<Object>(Object.class, "none");

    /**
     * Can be return by functions that don't want to return anything. (The function framework
     * requires you to return <em>something</em>).
     */
    public static final Object VOID_VALUE = new Object();

    /**
     * @since MMBase-1.9
     */
    public static final ReturnType<?> getReturnType(Class<?> type) {
        if (type.equals(void.class)) {
            return VOID;
        } else if (type.equals(String.class)) {
            return STRING;
        } else if (type.equals(CharSequence.class)) {
            return CHARSEQUENCE;
        } else if (type.equals(Integer.class)) {
            return INTEGER;
        } else if (type.equals(Long.class)) {
            return LONG;
        } else if (type.equals(Double.class)) {
            return DOUBLE;
        } else if (type.equals(Boolean.class)) {
            return BOOLEAN;
        } else if (type.equals(List.class)) {
            return LIST;
        } else if (type.equals(org.mmbase.bridge.NodeList.class)) {
            return NODELIST;
        } else if (type.equals(org.mmbase.bridge.Node.class)) {
            return NODE;
        } else if (type.equals(Set.class)) {
            return SET;
        } else if (type.equals(Collection.class)) {
            return COLLECTION;
        } else if (type.equals(Map.class)) {
            return MAP;
        } else if (type.equals(Void.class)) {
            return VOID;
        } else {
            return new ReturnType<Object>(type, type.getName());
        }
    }

    private Map<String, ReturnType> typeStruct = new HashMap<String, ReturnType>();

    public  ReturnType(Class type, String description) {
        super("RETURN_VALUE", type);
        setDescription(description, null);
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    /**
     * If the return type is like a map or struct (key-values pairs), then you might want to describe the
     * types of the values seperately too.
     */
    public ReturnType addSubType(String name,  ReturnType type) {
        return typeStruct.put(name, type);
    }

    /**
     * @return Unmodifiable Map containing the 'subtypes' in case the type is Map. An empty Map otherwise.
     */
    public Map<String, ReturnType> getSubTypes() {
        return Collections.unmodifiableMap(typeStruct);
    }


    public String toString() {
        return getDataType().getTypeAsClass().getName();
    }

}
