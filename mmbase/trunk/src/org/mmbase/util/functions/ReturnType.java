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

 * @version $Id: ReturnType.java,v 1.3 2004-11-02 18:35:32 michiel Exp $
 * @since MMBase-1.7
 */
public class ReturnType {

    /**
     * Describing the return type of function that does not return a thing.
     */
    public static final ReturnType VOID = new ReturnType(null, "Does not return anything");

    /**
     * Can be return by functions that don't want to return anything. (The function framework
     * requires you to return <em>something</em>).
     */
    public static final Object     VOID_VALUE = new Object();

    private Class type;
    private String description;
    private Map   typeStruct = new HashMap(); // key -> ReturnType

    public  ReturnType(Class type, String description) {
        this.type = type;
        this.description = description;
    }  

    /**
     * @return The 'Class' object which this object is wrapping.
     */
    public Class getType() {
        return type;
    }
    
    /**
     * @return A description of the return value. For documentation purposes.
     */
    public String getDescription() {
        return description;
    }

    /**
     * If the return type is like a map or struct (key-values pairs), then you might want to describe the 
     * types of the values seperately too.
     */

    ReturnType addSubType(String name,  ReturnType type) {
        return (ReturnType) typeStruct.put(name, type); 
    }

    /**
     * @return Unmodifiable Map containing the 'subtypes' in case the type is Map. An empty Map otherwise.
     */
    public Map getSubTypes() {
        return Collections.unmodifiableMap(typeStruct);
    }




}
