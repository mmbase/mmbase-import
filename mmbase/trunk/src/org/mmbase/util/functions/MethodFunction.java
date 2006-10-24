/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import java.lang.reflect.*;
import java.lang.annotation.*;

/**
 * A function based on an abritrary method. Since the name of the parameters cannot be found by
 * reflection, this is only of limited use. Normally you would probably better use BeanFunction. A
 * method-function can come in handy on JSP's. With the advent of java 1.5 we can use annotations to
 * annotate acutal parameter names.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MethodFunction.java,v 1.10 2006-10-24 09:39:36 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.7
 */
public class MethodFunction extends AbstractFunction<Object> {


    public static Function getFunction(Method method, String name) {
        return new MethodFunction(method, name); // could be cached...
    }
    
    /**
     * @since MMBase-1.9
     */
    public static Function getFunction(Method method, String name, Object instance) {
        return new MethodFunction(method, name, instance); // could be cached...
    }

    /**
     * Returns the MethodFunction representing the method 'name' in class 'clazz'. If there are more
     * methods whith that name, the one with the largest number of by name annotated parameters is taken.
     * @since MMBase-1.9
     */
    public static Function getFunction(Class clazz, String name) {
        // Finding method to use
        Method method = getMethod(clazz, name);
        return getFunction(method, method.getName());
    }
    public static Method getMethod(Class clazz, String name) {
        // Finding method to use
        Method method = null;
        float score = -1.0f;
        for (Method m : clazz.getMethods()) {
            String methodName = m.getName();
            if (methodName.equals(name)) {
                Annotation[][] annots = m.getParameterAnnotations();
                int found = 0; 
                int total = 1; // avoids division by zero and ensures that methods with more parameters are better.
                for (Annotation[] anot : annots) {
                    for (Annotation a : anot) {
                        if (a.annotationType().equals(Name.class)) {
                            found ++;
                        }
                    }
                    total++;
                }
                if ((float) found / total > score) {
                    method = m;
                    score = (float) found / total;
                }
            }
        }
        return method;
    }

    private final Method method;
    private final Object instance;
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method) {
        this(method, method.getName(), null);
    }
    public MethodFunction(Method method, String name) {
        this(method, name, null);
    }
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method, Object instance) {
        this(method, method.getName(), instance);
    }
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method, String name, Object instance) {
        super(name, null, null);
        this.method = method;
        this.instance = instance;
        if (instance == null) {
            if (! Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("The method " + method + " is not static"); // otherwise NPE in getFunctionValue
            }
        } else {
            if (! method.getDeclaringClass().isInstance(instance)) {
                throw new IllegalArgumentException("The object " + instance + " is not an instance of the class of  " + method);
            }
        }

        Annotation[][] annots = method.getParameterAnnotations();
        Class[] parameters = method.getParameterTypes();
        Parameter[] def = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            String paramName = null;
            for (Annotation annot : annots[i]) {
                if (annot.annotationType().equals(Name.class)) {
                    paramName = ((Name) annot).value();
                }
            }
            if (paramName == null) paramName = "parameter" + (i + 1);

            def[i] = new Parameter<String>(paramName, parameters[i]); // no way to find the name of the parameter
        }

        setParameterDefinition(def);

        ReturnType returnType = ReturnType.getReturnType(method.getReturnType());
        setReturnType(returnType);

    }

    public Object getFunctionValue(Parameters parameters) {
        try {
            return method.invoke(instance, parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
