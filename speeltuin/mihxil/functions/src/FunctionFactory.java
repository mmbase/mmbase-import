/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Module;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionFactory.java,v 1.2 2003-11-21 20:29:52 michiel Exp $
 * @since  MMBase-1.7
 */
public class FunctionFactory {

    private static final Logger log = Logging.getLoggerInstance(FunctionFactory.class);

    public static Function getFunction(String set, String name) {
        /// get instasnce from XML
        throw new UnsupportedOperationException("");
    }


    private static Map getParameterConstants(Class clazz) {
        return getParameterConstants(clazz, new HashMap());
    }
    private static Map getParameterConstants(Class clazz, Map map) {

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
            throw new IllegalArgumentException("The function 'name' cannot be found on the builder '" + nodeManager + "'");
        }
        
        NodeFunction function = new NodeFunction(name, parameters, new ReturnType(Object.class, null), node);

        return function;
    }

    public static Function getFunction(NodeManager nodeManager, String name) {
        // defined in Builder XML?
        throw new UnsupportedOperationException("");
    }

    public static Function getFunction(Module module, String name) {
        // defined in Module XML?
        throw new UnsupportedOperationException("");
    }

    public static Function getFunction(String name) {
        return new Function(name, null, new ReturnType(Object.class, null));
    }

}
