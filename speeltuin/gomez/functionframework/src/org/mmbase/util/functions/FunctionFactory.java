/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * The FunctionFactory instanciates {@link Function} objects. There are 6 static getFunctions
 * objects in this class, which correspond to 6 static getFunction methods in the following 6
 * classes:
 <ol>
   <li>{@link org.mmbase.util.functions.SetFunction}</li>
   <li>{@link org.mmbase.util.functions.NodeFunction}</li>
   <li>{@link org.mmbase.util.functions.NodeManagerFunction}</li>
   <li>{@link org.mmbase.util.functions.ModuleFunction}</li>
   <li>{@link org.mmbase.util.functions.MethodFunction}</li>
   <li>{@link org.mmbase.util.functions.BeanFunction}</li>
 </ol>

 <p>
   Those static function thus further factor the Function objects. Possibly, but nut necessarily,
   those static methods make instances of their own class. That was the original idea - hence the
   name - but of course it is no absolute rule
 </p>
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionFactory.java,v 1.1 2004-11-24 13:23:03 pierre Exp $
 * @since  MMBase-1.7
 */
public class FunctionFactory {

    private static final Logger log = Logging.getLoggerInstance(FunctionFactory.class);

    /**
     * Gets a function from a function set
     */
    public static Function getFunction(String setName, String functionName) {
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
    public static Function getFunction(Method method, String functionName) {
        return new MethodFunction(method, functionName);
    }

    /**
     * Gets a function object for a Bean
     */
    public static Function getFunction(Class claz, String functionName) throws java.lang.IllegalAccessException, InstantiationException, InvocationTargetException {
        return BeanFunction.getFunction(claz,functionName);
    }

}
