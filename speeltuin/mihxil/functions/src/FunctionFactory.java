/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This I use now to instantiate function objects, but perhaps it is silly. (Can perhaps add static methods to the impelentation themselves or so).
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionFactory.java,v 1.5 2003-12-11 21:56:21 michiel Exp $
 * @since  MMBase-1.7
 */
public class FunctionFactory {

    private static final Logger log = Logging.getLoggerInstance(FunctionFactory.class);


    /**
     * Gets a function from given set, with given name
     */
    public static Function getFunction(String set, String name) {
        /// get instance from MMFunctions?
        throw new UnsupportedOperationException("don't know yet how to do this..");
    }

    


    /**
     * Trying to determin Parameter[] constants using a Class. It considers all static fields of
     * type Parameter[]. The name of the field is lowercased and everything after and including the
     * first underscore is removed. This gives a key (which is supposed to equal to the 'execute' function name).
     *
     * @return a map, with function name -> Parameter[] pairs.
     * 
     */
    private static Map getParameterConstants(Class clazz) {
        log.service("Finding function parameters of class " + clazz);
        return getParameterConstants(clazz, new HashMap());
    }
    private static Map getParameterConstants(Class clazz, Map map) {

        log.debug("Searching " + clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0 ; i < fields.length; i++) {
            Field field = fields[i];
            try {
                
                int mod = field.getModifiers(); 
                if (! Modifier.isStatic(mod)) continue;
                
                if (! field.getType().equals(Parameter[].class)) continue;
                
                
                
                // get name (using convention)
                
                String name = field.getName().toLowerCase();
                int underscore = name.indexOf("_");
                if (underscore > 0) {
                    name = name.substring(0, underscore);
                }
                if (! map.containsKey(name)) { // overriding works, but don't do backwards :-)
                    log.service("Found a function definition '" + name + "' in " + clazz);
                    map.put(name, (Parameter[])field.get(null));
                }
            } catch (IllegalAccessException iae) { // never mind
                log.warn("Found inaccessible parameter[] constant: " + field.getName());

            }
        }
        Class sup = clazz.getSuperclass();
        if (sup != null) {
            getParameterConstants(sup, map);
        }

        return map;
        
    }
    /**
     * A cache, to avoid doing refliection on every function call on a Node.
     * buildername -> Map, functioname-> Parameter[]
     */
    private static Map parameterConstants = new HashMap(); 

         
    /**
     * Gets a function object for a certain Node.
     */
    public static Function getFunction(Node node, String name) {
        // find the MMObjectBuilder belong to this node.
        String nodeManager = node.getNodeManager().getName();

        Map functions = (Map) parameterConstants.get(nodeManager);
        if (functions == null) {
            MMBase mmbase = MMBase.getMMBase();
            Class implementation = mmbase.getBuilder(nodeManager).getClass();
            functions = getParameterConstants(implementation);
            parameterConstants.put(nodeManager, functions);
        }

        Parameter[] parameters = (Parameter[]) functions.get(name);
        if (parameters == null) {
            // can set later.
            log.warn("Trying to use unknown function '" + name + "' on builder '" + nodeManager + "'");            
            //throw new IllegalArgumentException("The function 'name' cannot be found on the builder '" + nodeManager + "'");
        }
        
        NodeFunction function = new NodeFunction(name, parameters, new ReturnType(Object.class, null), node);

        return function;
    }

    /**
     * Gets a function object for a certain NodeManager
     */
    public static Function getFunction(NodeManager nodeManager, String name) {
        // defined in Builder XML?
        throw new UnsupportedOperationException("");
    }


    /**
     * Gets a function object for a certain Module
     */

    public static Function getFunction(Module module, String name) {
        // defined in Module XML?
        throw new UnsupportedOperationException("");
    }


    public static Function getFunction(Class claz, String name) {
        return new LocalFunction(name, claz);
    }


}
