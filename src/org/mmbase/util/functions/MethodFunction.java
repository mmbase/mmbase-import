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
 * method-function can come in handy on JSP's.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MethodFunction.java,v 1.6 2006-09-15 14:55:51 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.7
 */
public class MethodFunction extends AbstractFunction {


    public static Function getFunction(Method method, String name) {
        return new MethodFunction(method, name); // could be cached...
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

            def[i] = new Parameter(paramName, parameters[i]); // no way to find the name of the parameter
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
