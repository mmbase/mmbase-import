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
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen

 * @version $Id: MMFunction.java
 */
public class ReturnType {

    private Class type;
    private String description;
    private Map   typeStruct = new HashMap(); // key -> ReturnType

    public  ReturnType(Class type, String description) {
        this.type = type;
        this.description = description;
    }  

    public Class getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }


    ReturnType addSubType(String name,  ReturnType type) {
        return (ReturnType) typeStruct.put(name, type); 
    }

    public Map getSubTypes() {
        return Collections.unmodifiableMap(typeStruct);
    }




}
