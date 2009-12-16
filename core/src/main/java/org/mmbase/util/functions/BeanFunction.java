/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.cache.Cache;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.datatypes.util.xml.*;
import org.xml.sax.InputSource;
import java.util.regex.*;
import java.lang.reflect.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * <p>
 * One or more functions based on a Java-bean. Every setter method of the bean corresponds with one
 * parameter.  The default value of the parameter can be defined with the getter method (which will
 * be called immediately after instantiation of such a Class). So the setters/getters define  {@link
 * Parameter}s. More specific {@link org.mmbase.datatypes.DataType}s can be attributes to such
 * parameters using the {@link Type} annotation.
 * </p><p>
 * All other methods (with no arguments) of the class correspond to the functions. So, you can
 * implement more bean-functions in the same class, as long as they have the same parameters.
 * </p><p>
 * A BeanFunction can be aquired via {@link FunctionFactory#getFunction(Class, String)} (which
 * delegates to a static method in this class).
 * </p>
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
     * A producer can instantiate beans
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
     * <code>claz</code>. Defaults to a producer that simply calls {@link Class#newInstance()}.
     * @since MMBase-1.8.5
     */
    public static BeanFunction getFunction(final Class claz, String name, Producer producer) throws IllegalAccessException, InstantiationException, InvocationTargetException, DependencyException  {
        String key = claz.getName() + '.' + name + '.' + producer;
        BeanFunction result = beanFunctionCache.get(key);
        if (result == null) {
            result = new BeanFunction(claz, name, producer);
            beanFunctionCache.put(key, result);
        }
        return result;
    }
    /**
     * This defaulting version of {@link #getFunction(Class, String, Producer)} uses a producer that uses {@link Class#newInstance()}.
     * Called from {@link FunctionFactory}
     */
    public static BeanFunction getFunction(final Class claz, String name) throws IllegalAccessException, InstantiationException, InvocationTargetException, DependencyException  {
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

    /**
     * @since MMBase-1.9.2
     */
    public static Parameter<?>[] getParameterDefinition(Object sampleInstance, List<Method> setMethods) throws IllegalAccessException, InvocationTargetException, DependencyException {
        Class<?> claz = sampleInstance.getClass();

        boolean mustBeAnnotated = false;
        List<String> setters;
        {
            FunctionParameters fp = claz.getAnnotation(FunctionParameters.class);
            if (fp != null) {
                mustBeAnnotated = fp.annotated();
                setters =  Arrays.asList(fp.value());
                if (setters.size() == 1 && setters.get(0).length() == 0) {
                    setters = null;
                }
            } else {
                setters = null;
            }
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        Method nodeParameter = null;
        for (Method m : claz.getMethods()) {
            String methodName = m.getName();
            Class[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length == 1 && methodName.startsWith("set")) {
                String parameterName = methodName.substring(3);
                if (setters != null && ! setters.contains(parameterName)) continue;

                org.mmbase.datatypes.DataType dataType;
                Type annotatedDataType =  m.getAnnotation(Type.class);
                if (annotatedDataType != null) {
                    dataType = getDataType(annotatedDataType.value(), org.mmbase.datatypes.DataTypes.createDataType(parameterName, parameterTypes[0]));
                } else {
                    if (mustBeAnnotated) continue;
                    dataType = org.mmbase.datatypes.DataTypes.createDataType(parameterName, parameterTypes[0]);
                }

                {
                    boolean required = false;
                    Required requiredAnnotation = m.getAnnotation(Required.class);
                    if (requiredAnnotation != null) {
                        dataType = dataType.clone();
                        dataType.setRequired(true);
                    }
                }

                if (annotatedDataType == null) {
                    // If no datatype annotated (which would define a default),
                    // find a corresponding getter method, which can be used for a default value;
                    try {
                        Object defaultValue;
                        Method getter = claz.getMethod("get" + parameterName);
                        defaultValue = getter.invoke(sampleInstance);
                        dataType.setDefaultValue(defaultValue);
                    } catch (NoSuchMethodException nsme) {
                        //defaultValue = null;
                    }
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
                    if (m.getAnnotation(Name.class) != null) {
                        parameterName = m.getAnnotation(Name.class).value();
                    }
                    parameters.add(new Parameter<Object>(parameterName, dataType));
                    if (setMethods != null) {
                        setMethods.add(m);
                    }
                }

            }
        }
        if (nodeParameter != null) {
            parameters.add(Parameter.NODE);
            if (setMethods != null) {
                setMethods.add(nodeParameter);
            }
        }

        return parameters.toArray(Parameter.emptyArray());
    }

    /**
     * @param  b The 'bean' on which the setter methods must be invoked
     * @param  parameters The object containing the parameter values
     * @param  setMethods  The setter methods
     * @since MMBase-1.9.2
     */
    public static void setParameters(Object b, Parameters parameters, List<Method> setMethods) throws IllegalAccessException, InvocationTargetException {
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
                Type annotatedDataType = setter.getAnnotation(Type.class);
                boolean resetDefaults = annotatedDataType != null;
                if (resetDefaults) {
                    if (log.isDebugEnabled()) {
                        log.debug("Invoking " + b + " " + setter + "(" + value + ")");
                    }
                    setter.invoke(b, value);
                } else {
                    Object defaultValue = parameters.getDefinition()[count].getDefaultValue();
                    if ((defaultValue == null && value != null) ||
                        (defaultValue != null && (! defaultValue.equals(value)))) {
                        if (log.isDebugEnabled()) {
                            log.debug("Invoking " + b + " " + setter + "(" + value + ")");
                        }
                        setter.invoke(b, value);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Not Invoking " + b + " " + setter + "(" + value + "), it already has the default value");
                        }
                    }
                }
                count++;
            } catch (Exception e) {
                throw new RuntimeException("" + setter + " value: " + value + " " + e.getMessage(), e);
            }

        }
    }


    /**
     * @since MMBase-1.9.2
     */
    public static final Pattern NCName = Pattern.compile("[\\p{L}_][\\p{L}_\\-\\.0-9]*");

    /**
     * Given a string and a 'base' datatype, produces a new {@link org.mmbase.datatypes.DataType}. If the string matches
     * {@link #NCName} then the datatype is looked up in the MMBase DataType repository at {@link
     * org.mmbase.datatypes.DataTypes#getDataType}. Otherwise the String is interpreted as a piece
     * of XML.
     *
     * @since MMBase-1.9.2
     */
    public static org.mmbase.datatypes.DataType getDataType(String value, org.mmbase.datatypes.BasicDataType base) throws org.mmbase.datatypes.util.xml.DependencyException {
        org.mmbase.datatypes.DataType dt;
        if (NCName.matcher(value).matches()) {
             dt = org.mmbase.datatypes.DataTypes.getDataType(value);
             if (dt == null) {
                 log.warn("No such datatype '" + value + "' taking " + base, new Exception());
                 dt = base;
             }
        } else {
            DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(value)), true, org.mmbase.datatypes.util.xml.DataTypeReader.class);
            dt = org.mmbase.datatypes.util.xml.DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), base, null).dataType;
            if (dt == null) {
                log.warn("Could not parse '" + value + "' taking " + base, new Exception());
                dt = base;
            }
        }
        return dt;
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
    private  BeanFunction(Class<?> claz, String name, Producer producer) throws IllegalAccessException, InstantiationException,  InvocationTargetException, DependencyException  {
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

        Parameter<?>[] definition = getParameterDefinition(sampleInstance, setMethods);
        setParameterDefinition(definition);
        ReturnType returnType = new ReturnType(method.getReturnType(), "");
        setReturnType(returnType);

    }

    /**
     * @since MMBase-1.8.5
     */
    public BeanFunction(final Object bean, String name) throws IllegalAccessException, InstantiationException,  InvocationTargetException, DependencyException {
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
        try {
            setParameters(b, parameters, setMethods);

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
