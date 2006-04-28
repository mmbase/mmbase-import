/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Describing a function on a bridge Node, giving access to the underlying executeFunction of the MMObjectBuilder.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeFunction.java,v 1.1 2003-12-21 13:25:36 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @since MMBase-1.7
 */
public class NodeFunction extends Function {

    private static final Logger log = Logging.getLoggerInstance(NodeFunction.class);

    /**
     * A cache, to avoid doing refliection on every function call on a Node.
     * nodemanager name -> Map, functioname-> Parameter[]
     */
    private static Map parameterConstants = new HashMap(); 
    
    /**
     * Utility function, which can be called from MMObjectBuilder implementations to implement their
     * getParameters easily
     */

    public static Parameter[] getParametersByReflection(Class claz, String name) {
        Map constants = (Map) parameterConstants.get(claz.getName());
        if (constants == null) {
            constants = getParameterConstants(claz);
            parameterConstants.put(claz.getName(), constants);
        }
        return (Parameter[]) constants.get(name);

    }

    public static Function getFunction(Node node, String name) {
        // find the MMObjectBuilder belong to this node.
        // XXX the method should perhaps be on bridge's Node!

        String nodeManager = node.getNodeManager().getName();
        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder builder = mmbase.getBuilder(nodeManager);

        Parameter[] parameters = builder.getParameterDefinition(name);
        if (parameters == null) {
            // can set later.
            log.warn("Trying to use unknown function '" + name + "' on builder '" + nodeManager + "'");            
            //throw new IllegalArgumentException("The function 'name' cannot be found on the builder '" + nodeManager + "'");
        }
        
        NodeFunction function = new NodeFunction(name, parameters, new ReturnType(Object.class, null), node);

        return function;
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
                    log.debug("Found a function definition '" + name + "' in " + clazz);
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



    private Node node;
    public NodeFunction(String name, Parameter[] def, ReturnType returnType, Node node) {
        super(name, def, returnType);
        this.node = node;
    }

    public Object getFunctionValue(Parameters arguments) {
        return node.getFunctionValue(name, arguments).get();
    }

}
