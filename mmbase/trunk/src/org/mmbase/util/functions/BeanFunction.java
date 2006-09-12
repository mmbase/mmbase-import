/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.cache.Cache;

import java.lang.reflect.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * One or more functions based on a Java-bean. Every setter method of the bean corresponds with one
 * parameter.  The default value of the parameter can be defined with the getter method (which will
 * be called immediately after instantiation of such a Class).
 *
 * All other methods (with no arguments) of the class correspond to the functions. So, you can
 * implement more bean-functions in the same class, as long as they have the same parameters.
 *
 * A BeanFunction can be aquired via {@link FunctionFactory#getFunction(Class, String)} (which
 * delegates to a static method in this class).
 *
 * @author Michiel Meeuwissen
 * @version $Id: BeanFunction.java,v 1.9 2006-09-12 18:38:51 michiel Exp $
 * @see org.mmbase.util.functions.MethodFunction
 * @see org.mmbase.util.functions.FunctionFactory
 * @since MMBase-1.8
 */
public class BeanFunction extends AbstractFunction {
    private static final Logger log = Logging.getLoggerInstance(BeanFunction.class);
    /**
     * Utility function, searches an inner class of a given class. This inner class can perhaps be used as a
     * bean. Used in JSP/taglib.
     * @param claz The class to be considered
     * @param name The name of the inner class
     * @throws IllegalArgumentException if claz has no inner class with that name
     */
    public static Class getClass(Class claz, String name) {
        Class[] classes = claz.getDeclaredClasses();
        for (int j=0; j < classes.length; j++) {
            Class c = classes[j];
            if (c.getName().endsWith("$" + name)) {
                return c;
            }
        }
        throw new IllegalArgumentException("There is no inner class with name '" + name + "' in " + claz);
    }

    /**
     * A cache for bean classes. Used to avoid some reflection.
     */
    private static Cache beanFunctionCache = new Cache(50) {
        public String getName() {
            return "BeanFunctionCache";
        }
        public String getDescription() {
            return "ClassName.FunctionName -> BeanFunction object";
        }
    };

    /**
     * Gives back a Function object based on the 'bean' concept.
     * Called from {@link FunctionFactory}
     */
    public static Function getFunction(Class claz, String name) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String key = claz.getName() + '.' + name;
        BeanFunction result = (BeanFunction) beanFunctionCache.get(key);
        if (result == null) {
            result = new BeanFunction(claz, name);
            beanFunctionCache.put(key, result);
        }
        return result;
    }

    /* ================================================================================
       Instance methods
       ================================================================================
    */

    /**
     * This class of the bean
     */
    private Class  claz   = null;

    /**
     * The method corresponding to the function called in getFunctionValue.
     */
    private Method method = null;

    /**
     * A list of all found setter methods. This list 1-1 corresponds with getParameterDefinition. Every Parameter belongs to a setter method.
     */
    private List   setMethods = new ArrayList();



    /**
     * The constructor! Performs reflection to fill 'method' and 'setMethods' members.
     */
    private  BeanFunction(Class claz, String name) throws IllegalAccessException, InstantiationException,  InvocationTargetException {
        super(name, null, null);
        this.claz = claz;

        // Finding the  methods to be used.
        for (Method m : claz.getMethods()) {
            String methodName = m.getName();
            if (methodName.equals(name) && m.getParameterTypes().length == 0) {
                method = m;
                break;
            }
        }

        if (method == null) {
            throw new IllegalArgumentException("The class " + claz + " does not have method " + name + " (with no argument)");
        }

        // Now finding the parameters.


        // need a sample instance to get the default values from.
        Object sampleInstance = claz.newInstance();

        List<Parameter> parameters = new ArrayList();
        for (Method m : claz.getMethods()) {
            String methodName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && methodName.startsWith("set")) {
                String parameterName = methodName.substring(3);
                // find a corresponding getter method, which can be used for a default value;
                Object defaultValue;
                try {
                    Method getter = claz.getMethod("get" + parameterName);
                    defaultValue = getter.invoke(sampleInstance);
                } catch (NoSuchMethodException nsme) {
                    defaultValue = null;
                }
                if (Character.isUpperCase(parameterName.charAt(0))) {
                    if (parameterName.length() > 1) {
                        if (! Character.isUpperCase(parameterName.charAt(1))) {
                            parameterName = "" + Character.toLowerCase(parameterName.charAt(0)) + parameterName.substring(1);
                        }
                    } else {
                        parameterName = parameterName.toLowerCase();
                    }
                }
                if (parameterName.equals("node") && org.mmbase.bridge.Node.class.isAssignableFrom(parameterTypes[0])) {
                    parameters.add(Parameter.NODE);
                } else {
                    parameters.add(new Parameter(parameterName, parameterTypes[0], defaultValue));
                }
                setMethods.add(method);
            }
        }
        setParameterDefinition(parameters.toArray(Parameter.EMPTY));
        ReturnType returnType = new ReturnType(method.getReturnType(), "");
        setReturnType(returnType);

    }


    /**
     * {@inheritDoc}
     * Instantiates the bean, calls all setters using the parameters, and executes the method associated with this function.
     */
    public Object getFunctionValue(Parameters parameters) {
        try {
            Object bean = claz.newInstance();
            Iterator i = parameters.iterator();
            Iterator j = setMethods.iterator();
            while(i.hasNext() && j.hasNext()) {
                Object value = i.next();
                Method method = (Method) j.next();
                method.invoke(bean, new Object[] {value});
            }
            Object ret =  method.invoke(bean, new Object[] {});
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
