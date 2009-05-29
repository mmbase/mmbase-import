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
public class BeanFunction extends AbstractFunction<Object> {

    private static final long serialVersionUID  = 0L;
    private static int producerSeq = 0;
    /**
     * @since MMBase-1.8.5
     */
    public static abstract class Producer {
        public abstract Object getInstance();
        @Override
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
        for (Class c : classes) {
            if (c.getName().endsWith("$" + name)) {
                return c;
            }
        }
        throw new IllegalArgumentException("There is no inner class with name '" + name + "' in " + claz);
    }

    /**
     * A cache for bean classes. Used to avoid some reflection.
     */
    private static Cache<String, BeanFunction> beanFunctionCache = new Cache<String, BeanFunction>(50) {
        @Override
        public String getName() {
            return "BeanFunctionCache";
        }
        @Override
        public String getDescription() {
            return "ClassName.FunctionName -> BeanFunction object";
        }
    };
    static {
        beanFunctionCache.putCache();
    }


    /**
     * Gives back a Function object based on the 'bean' concept.
     * @param claz The class which must be considered a 'bean' function
     * @param name The name of the function (the name of a Method in the given class)
     * @param producer An object that can produce in instance of the class
     * <code>claz</code>. Defaults to a producer that simply calls <code>claz.newInstance()</code>
     * @since MMBase-1.8.5
     */
    public static BeanFunction getFunction(final Class claz, String name, Producer producer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String key = claz.getName() + '.' + name + '.' + producer;
        BeanFunction result = beanFunctionCache.get(key);
        if (result == null) {
            result = new BeanFunction(claz, name, producer);
            beanFunctionCache.put(key, result);
        }
        return result;
    }
    /**
     * Called from {@link FunctionFactory}
     */
    public static BeanFunction getFunction(final Class claz, String name) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return getFunction(claz, name, new Producer() {
                public Object getInstance()  {
                    try {
                        return  claz.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            @Override
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
    public static <C> C getInstance(final Class<C> claz, Object constructorArgument) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class c = constructorArgument.getClass();
        while (c != null) {
            try {
                Constructor<C> con = claz.getConstructor(c);
                return con.newInstance(constructorArgument);
            } catch (NoSuchMethodException e) {
                c = c.getSuperclass();
            }
        }
        Class[] interfaces = constructorArgument.getClass().getInterfaces();
        for (Class element : interfaces) {
            try {
                Constructor<C> con = claz.getConstructor(element);
                return con.newInstance(constructorArgument);
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
     * The method corresponding to the function called in getFunctionValue.
     */
    private final Method method;

    /**
     * A list of all found setter methods. This list 1-1 corresponds with getParameterDefinition. Every Parameter belongs to a setter method.
     */
    private List<Method> setMethods = new ArrayList<Method>();

    private final Producer producer;

    /**
     * The constructor! Performs reflection to fill 'method' and 'setMethods' members.
     */
    private  BeanFunction(Class<?> claz, String name, Producer producer) throws IllegalAccessException, InstantiationException,  InvocationTargetException {
        super(name, null, null);
        this.producer = producer;

        Method candMethod = null;
        // Finding the  methods to be used.
        for (Method m : claz.getMethods()) {
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

        List<Parameter> parameters = new ArrayList<Parameter>();
        Method nodeParameter = null;
        for (Method m : claz.getMethods()) {
            String methodName = m.getName();
            Class[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length == 1 && methodName.startsWith("set")) {
                String parameterName = methodName.substring(3);
                boolean required = false;
                Required requiredAnnotation = m.getAnnotation(Required.class);
                required = requiredAnnotation != null;

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
                    nodeParameter = m;
                } else {
                    if(defaultValue != null) {
                        if (required) {
                            log.warn("Required annotation ignored, because a default value is present");
                        }
                        parameters.add(new Parameter<Object>(parameterName, parameterTypes[0], defaultValue));
                    } else {
                        parameters.add(new Parameter(parameterName, parameterTypes[0], required));
                    }
                    setMethods.add(m);
                }

            }
        }
        if (nodeParameter != null) {
            parameters.add(Parameter.NODE);
            setMethods.add(nodeParameter);
        }
        setParameterDefinition(parameters.toArray(Parameter.emptyArray()));
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
        Object b = getProducer().getInstance();
        int count = 0;
        Iterator<?> i = parameters.iterator();
        Iterator<Method> j = setMethods.iterator();
        while(i.hasNext() && j.hasNext()) {
            Object value  = i.next();
            Method setter = j.next();
            try {
                if (value == null) {
                    if (setter.getParameterTypes()[0].isPrimitive()) {
                        log.debug("Tried to sed null in in primitive setter method");
                        //primitive types cannot be null, never mind.
                        continue;
                    }
                }
                Object defaultValue = parameters.getDefinition()[count].getDefaultValue();
                if ((defaultValue == null && value != null) ||
                    (defaultValue != null && (! defaultValue.equals(value)))) {
                    setter.invoke(b, value);
                }
                count++;
            } catch (Exception e) {
                throw new RuntimeException("" + setter + " value: " + value + " " + e.getMessage(), e);
            }

        }
        try {
            Object ret =  method.invoke(b);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("" + this + " " + e.getMessage(), e);
        }
    }


    public static void main(String[] argv) throws Exception {
        Function fun = getFunction(Class.forName(argv[0]), argv[1]);
        System.out.println("" + fun);
        System.out.println("" + fun.createParameters());
    }
}
