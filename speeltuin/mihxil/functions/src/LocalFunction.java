/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import java.lang.reflect.*;

/**
 * Describing a function on a bridge Node, giving access to the underlying executeFunction of the MMObjectBuilder.
 *
 * @author Michiel Meeuwissen
 * @version $Id: LocalFunction.java,v 1.1 2003-12-11 21:56:21 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @since MMBase-1.7
 */
public class LocalFunction extends Function {


    Method method = null;
    public LocalFunction(String name, Class claz) {        
        super(name, null, null);
        Method[] methods = claz.getMethods();
        for (int j=0; j < methods.length; j++) {
            if (methods[j].getName().equals(name)) {
                if (method != null) {
                    throw new IllegalArgumentException("There is not excacly one method with name '" + name + "' in " + claz);
                }
                method = methods[j];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("There is no method with name '" + name + "' in " + claz);

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
