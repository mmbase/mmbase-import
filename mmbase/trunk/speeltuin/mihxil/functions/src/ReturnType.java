/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;

/**
 * Description of the return type of certain function. This wraps a Class object but it has some extra 
 * members.
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen

 * @version $Id: ReturnType.java,v 1.2 2003-11-21 22:01:51 michiel Exp $
 * @since MMBase-1.7
 */
public class ReturnType {

    public static final ReturnType VOID = new ReturnType(null, "Does not return anything");
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

    public Map getSubTypes() {
        return Collections.unmodifiableMap(typeStruct);
    }




}
