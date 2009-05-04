/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import java.lang.reflect.*;

/**
 * A function based on an abritrary method. Since the name of the parameters cannot be found by
 * reflection, this is only of limited use. Normally you would probably better use BeanFunction. A
 * method-function can come in handy on JSP's.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.7
 */
public class MethodFunction extends AbstractFunction {

    public static Function getFunction(Method method, String name) {
        return new MethodFunction(method, name); // could be cached...
    }

    private Method method = null;
    public MethodFunction(Method method, String name) {
        super(name, null, null);
        this.method = method;
        if (! Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("The method " + method + " is not static"); // otherwise NPE in getFunctionValue
        }

        Class[] parameters = method.getParameterTypes();
        Parameter[] def = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            def[i] = new Parameter("parameter" + (i + 1), parameters[i]); // no way to find the name of the parameter
        }

        setParameterDefinition(def);

        ReturnType returnType = new ReturnType(method.getReturnType(), "");
        setReturnType(returnType);

    }

    public Object getFunctionValue(Parameters parameters) {
        try {
            return method.invoke(null, parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
