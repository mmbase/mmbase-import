/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * A way to instantiate function objects. A hand full a 'getFunction' methods are
 * implemented. Principally other implementations could be plugged in by replacing the right classes
 * ((putting them in WEB-INF/classes should do the trick).
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionFactory.java,v 1.1 2003-12-21 13:25:36 michiel Exp $
 * @since  MMBase-1.7
 */
public class FunctionFactory {

    private static final Logger log = Logging.getLoggerInstance(FunctionFactory.class);

    static String setFunctionImpl         = "org.mmbase.util.functions.SetFunction";
    static String nodeFunctionImpl        = "org.mmbase.util.functions.NodeFunction";
    static String nodeManagerFunctionImpl = "org.mmbase.util.functions.NodeManagerFunction";
    static String moduleFunctionImpl      = "org.mmbase.util.functions.ModuleFunction";
    static String methodFunctionImpl      = "org.mmbase.util.functions.MethodFunction";
    static String beanFunctionImpl        = "org.mmbase.util.functions.BeanFunction";


    private static Method getSetFunction;
    private static Method getNodeFunction;
    private static Method getNodeManagerFunction;
    private static Method getModuleFunction;
    private static Method getMethodFunction;
    private static Method getBeanFunction;

    static {
        getSetFunction         = findGetFunctionMethod(setFunctionImpl, new Class[] { String.class, String.class});
        getNodeFunction        = findGetFunctionMethod(nodeFunctionImpl, new Class[] { Node.class, String.class});
        getNodeManagerFunction = findGetFunctionMethod(nodeManagerFunctionImpl, new Class[] { NodeManager.class, String.class});
        getModuleFunction      = findGetFunctionMethod(moduleFunctionImpl, new Class[] { Module.class, String.class});
        getMethodFunction      = findGetFunctionMethod(methodFunctionImpl, new Class[] { Method.class, String.class});
        getBeanFunction        = findGetFunctionMethod(beanFunctionImpl, new Class[]   { Class.class, String.class});
    }

    private static Method findGetFunctionMethod(String impl, Class[] args) {
        try {
            Class clazz = Class.forName(impl);
            return clazz.getMethod("getFunction", args);
        } catch (ClassNotFoundException cnfe) {
            log.info("No implementation found for '" + impl + "', this functionality is not available");
        } catch (NoSuchMethodException nsme) {
            log.error("There is not method 'getFunction' in " + impl + " (with args" + Arrays.asList(args) + ")");
        }
        return null;
    }

    /**
     * Gets a function from given set, with given name
     */
    public static Function getFunction(String set, String name) {
        if (getSetFunction == null)  throw new UnsupportedOperationException("Not supported " + setFunctionImpl);
        try {
            return (Function) getSetFunction.invoke(null, new Object[] { set, name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
         
    /**
     * Gets a function object for a certain Node.
     */
    public static Function getFunction(Node node, String name) {
        if (getNodeFunction == null)  throw new UnsupportedOperationException("Not supported " + nodeFunctionImpl);
        try {
            return (Function) getNodeFunction.invoke(null, new Object[] { node, name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a function object for a certain NodeManager
     */
    public static Function getFunction(NodeManager nodeManager, String name) {
        if (getNodeManagerFunction == null)  throw new UnsupportedOperationException("Not supported " + nodeManagerFunctionImpl);
        try {
            return (Function) getNodeManagerFunction.invoke(null, new Object[] { nodeManager, name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets a function object for a certain Module
     */

    public static Function getFunction(Module module, String name) {
        if (getModuleFunction == null)  throw new UnsupportedOperationException("Not supported " + moduleFunctionImpl);
        try {
            return (Function) getModuleFunction.invoke(null, new Object[] { module, name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a function object for a certain Method
     */
    public static Function getFunction(Method method, String name) {
        if (getMethodFunction == null)  throw new UnsupportedOperationException("Not supported " + methodFunctionImpl);
        try {
            return (Function) getMethodFunction.invoke(null, new Object[] { method , name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a function object for a certain Bean
     */
    public static Function getFunction(Class claz, String name) {
        if (getBeanFunction == null)  throw new UnsupportedOperationException("Not supported " + beanFunctionImpl);
        try {
            return (Function) getBeanFunction.invoke(null, new Object[] { claz , name});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
