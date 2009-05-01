/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.mmbase.bridge.*;

/**
 * The FunctionFactory instanciates {@link Function} objects. There are 6 static getFunctions
 * objects in this class, which correspond to 6 different kind of Function objects.
 *
 * The function factory was more important in the 1.7 version of MMBase. Since MMBase 1.8 there are
 * {@link FunctionProvider}s, so often, it is just as easy and straight forward to simply call the
 * getFunction method on the relevant FunctionProvider (like {@link FunctionSet}, {@link Cloud},
 * {@link Module}, or {@link Node}) or to simply instantiate the Function object (e.g. {@link BeanFunction})
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.7
 */
public class FunctionFactory {

    /**
     * Gets a function from a function set
     */
    public static Function<?> getFunction(String setName, String functionName) {
        return FunctionSets.getFunction(setName, functionName);
    }

    /**
     * Gets a function from a function set on a certain cloud
     */
    public static Function getFunction(Cloud cloud, String setName, String functionName) {
        return cloud.getFunction(setName, functionName);
    }

    /**
     * Gets a function object for a Node.
     */
    public static Function getFunction(Node node, String functionName) {
        return node.getFunction(functionName);
//        return NodeFunction.getFunction(node, functionName);
    }

    /**
     * Gets a function object for a NodeManager
     */
    public static Function getFunction(NodeManager nodeManager, String functionName) {
        return nodeManager.getFunction(functionName);
    }


    /**
     * Gets a function object for a Module
     */
    public static Function getFunction(Module module, String functionName) {
       return module.getFunction(functionName);
    }

    /**
     * Gets a function object for a certain Method
     */
    public static Function<Object> getFunction(Method method, String functionName) {
        return new MethodFunction(method, functionName);
    }

    /**
     * Gets a function object for a Bean
     */
    public static Function<Object> getFunction(Class<?> claz, String functionName) throws java.lang.IllegalAccessException, InstantiationException, InvocationTargetException {
        return BeanFunction.getFunction(claz, functionName);
    }

}
