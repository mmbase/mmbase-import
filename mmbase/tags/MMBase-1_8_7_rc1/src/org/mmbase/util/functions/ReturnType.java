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
public class ReturnType extends Parameter implements java.io.Serializable {

    /**
     * The return type of a function that does not return a thing.
     */
    public static final ReturnType VOID = new ReturnType(void.class, "Does not return anything");

    /**
     * The return type of a function that returns a String.
     */
    public static final ReturnType STRING = new ReturnType(String.class, "String");

    public static final ReturnType CHARSEQUENCE = new ReturnType(CharSequence.class, "CharSequence");

    /**
     * The return type of a function that returns a Integer.
     */
    public static final ReturnType INTEGER = new ReturnType(Integer.class, "Integer");

    /**
     * The return type of a function that returns a Long.
     */
    public static final ReturnType LONG = new ReturnType(Long.class, "Long");

    /**
     * The return type of a function that returns a Double.
     */
    public static final ReturnType DOUBLE = new ReturnType(Double.class, "Double");

    /**
     * The return type of a function that returns a Boolean.
     */
    public static final ReturnType BOOLEAN = new ReturnType(Boolean.class, "Boolean");

    /**
     * The return type of a function that returns a List.
     */
    public static final ReturnType LIST = new ReturnType(List.class, "List");


    /**
     * The return type of a function that returns a NodeList.
     */
    public static final ReturnType NODELIST = new ReturnType(org.mmbase.bridge.NodeList.class, "NodeList");

    /**
     * The return type of a function that returns a Node.
     */
    public static final ReturnType NODE = new ReturnType(org.mmbase.bridge.Node.class, "Node");

    /**
     * The return type of a function that returns a Set.
     */
    public static final ReturnType SET = new ReturnType(Set.class, "Set");
    /**
     * The return type of a function that returns a Set.
     */
    public static final ReturnType COLLECTION = new ReturnType(Collection.class, "Collection");

    /**
     * The return type of a function that returns a Map.
     */
    public static final ReturnType MAP = new ReturnType(Map.class, "Map");

    /**
     * The return type of a function is unknown.
     */
    public static final ReturnType UNKNOWN = new ReturnType(Object.class, "unknown");

    /**
     * The return type of a function is None
     */
    public static final ReturnType NONE = new ReturnType(Object.class, "none");

    /**
     * Can be return by functions that don't want to return anything. (The function framework
     * requires you to return <em>something</em>).
     */
    public static final Object VOID_VALUE = new Object();

    private Map typeStruct = new HashMap(); // key -> ReturnType

    public  ReturnType(Class type, String description) {
        super("RETURN_VALUE", type);
        setDescription(description, null);
    }

    public boolean isRequired() {
        return false;
    }

    /**
     * If the return type is like a map or struct (key-values pairs), then you might want to describe the
     * types of the values seperately too.
     */
    public ReturnType addSubType(String name,  ReturnType type) {
        return (ReturnType) typeStruct.put(name, type);
    }

    /**
     * @return Unmodifiable Map containing the 'subtypes' in case the type is Map. An empty Map otherwise.
     */
    public Map getSubTypes() {
        return Collections.unmodifiableMap(typeStruct);
    }


    public String toString() {
        return getDataType().getTypeAsClass().getName();
    }

}
