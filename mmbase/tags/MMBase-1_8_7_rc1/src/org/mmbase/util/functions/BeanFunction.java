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
 * @version $Id$
 * @see org.mmbase.util.functions.MethodFunction
 * @see org.mmbase.util.functions.FunctionFactory
 * @since MMBase-1.8
 */
public class BeanFunction extends AbstractFunction {

    private static int producerSeq = 0;
    /**
     * @since MMBase-1.8.5
     */
    public static abstract class Producer {
        public abstract Object getInstance();
        public String toString() {
            return getClass().getName() + "." + (producerSeq++);
        }

    }
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
     * @since MMBase-1.8.5
     */
    public static BeanFunction getFunction(final Class claz, String name, Producer producer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String key = claz.getName() + '.' + name + '.' + producer;
        BeanFunction result = (BeanFunction) beanFunctionCache.get(key);
        if (result == null) {
            result = new BeanFunction(claz, name, producer);
            log.debug("Created new function " + result);
            beanFunctionCache.put(key, result);
        } else {
            log.debug("Found in cache " + result);
        }
        return result;
    }
    /**
     * Called from {@link FunctionFactory}
     */
    public static Function getFunction(final Class claz, String name) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return getFunction(claz, name, new Producer() {
                public Object getInstance()  {
                    try {
                        return  claz.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                public String toString() {
                    return "";
                }
            });
    }

    /**
     * Utitily function to create an instance of a certain class. Two constructors are tried, a one
     * argument one, and if that fails, simply newInstance is used.
     * @since MMBase-1.8.5
     */
    public static Object getInstance(final Class claz, Object constructorArgument) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class c = constructorArgument.getClass();
        while (c != null) {
            try {
                Constructor con = claz.getConstructor(new Class[] {c});
                return con.newInstance(new Object[] {constructorArgument});
            } catch (NoSuchMethodException e) {
                c = c.getSuperclass();
            }
        }
        Class[] interfaces = constructorArgument.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            try {
                Constructor con = claz.getConstructor(new Class[] {interfaces[i]});
                return con.newInstance(new Object[] {constructorArgument});
            } catch (NoSuchMethodException e) {
            }

        }
        return claz.newInstance();
    }

    /* ================================================================================
       Instance methods
       ================================================================================
    */

    /**
     * This class of the bean
     */
    private final Class  claz;

    /**
     * The method corresponding to the function called in getFunctionValue.
     */
    private final Method method;


    /**
     * A list of all found setter methods. This list 1-1 corresponds with getParameterDefinition. Every Parameter belongs to a setter method.
     */
    private final List   setMethods = new ArrayList();


    private final Producer producer;



    /**
     * The constructor! Performs reflection to fill 'method' and 'setMethods' members.
     */
    private  BeanFunction(Class claz, String name, Producer producer) throws IllegalAccessException, InstantiationException,  InvocationTargetException {
        super(name, null, null);
        this.claz = claz;
        this.producer = producer;


        Method candMethod = null;
        // Finding the  methods to be used.
        Method[] methods = claz.getMethods();
        for (int i = 0 ; i < methods.length; i++) {
            Method m = methods[i];
            String methodName = m.getName();
            if (methodName.equals(name) && m.getParameterTypes().length == 0) {
                candMethod = m;
                break;
            }
        }

        if (candMethod == null) {
            throw new IllegalArgumentException("The class " + claz + " does not have method " + name + " (with no argument)");
        }
        method = candMethod;

        // Now finding the parameters.


        // need a sample instance to get the default values from.
        Object sampleInstance = producer.getInstance();
        if (sampleInstance == null) throw new RuntimeException("Producer " + producer + " did not produce an instance");

        List parameters = new ArrayList();
        Method nodeParameter = null;
        for (int i = 0 ; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && methodName.startsWith("set")) {
                String parameterName = methodName.substring(3);
                // find a corresponding getter method, which can be used for a default value;
                Object defaultValue;
                try {
                    Method getter = claz.getMethod("get" + parameterName, new Class[] {});
                    defaultValue = getter.invoke(sampleInstance, new Object[] {});
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
                    nodeParameter = method;
                } else {
                    parameters.add(new Parameter(parameterName, parameterTypes[0], defaultValue));
                    setMethods.add(method);
                }

            }
        }
        if (nodeParameter != null) {
            parameters.add(Parameter.NODE);
            setMethods.add(nodeParameter);
        }
        setParameterDefinition((Parameter[]) parameters.toArray(new Parameter[0]));
        ReturnType returnType = new ReturnType(method.getReturnType(), "");
        setReturnType(returnType);

    }

    /**
     * @since MMBase-1.8.5
     */
    public BeanFunction(final Object bean, String name) throws IllegalAccessException, InstantiationException,  InvocationTargetException {
        this(bean.getClass(), name, new Producer() { public Object getInstance() { return bean; }});
    }

    /**
     * @since MMBase-1.8.5
     */
    public Producer getProducer() {
        return producer;
    }

    /**
     * {@inheritDoc}
     * Instantiates the bean, calls all setters using the parameters, and executes the method associated with this function.
     */
    public Object getFunctionValue(Parameters parameters) {
        try {
            Object bean = getProducer().getInstance();
            int count = 0;
            Iterator i = parameters.iterator();
            Iterator j = setMethods.iterator();
            while(i.hasNext() && j.hasNext()) {
                Object value = i.next();
                Method method = (Method) j.next();
                if (value == null) {
                    if (method.getParameterTypes()[0].isPrimitive()) {
                        log.debug("Tried to sed null in in primitive setter method");
                        //primitive types cannot be null, never mind.
                        continue;
                    }
                }
                Object defaultValue = parameters.getDefinition()[count].getDefaultValue();
                if ((defaultValue == null && value != null) ||
                    (defaultValue != null && (! defaultValue.equals(value)))) {
                    method.invoke(bean, new Object[] {value});
                }
                count++;
            }
            Object ret =  method.invoke(bean, new Object[] {});
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
