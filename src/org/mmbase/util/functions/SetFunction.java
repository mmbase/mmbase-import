/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.module.core.*;
import java.lang.reflect.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * A SetFunction is a {@link Function} which is identified solely by two Strings: the name of the
 * 'set' to which it belongs (see {@link FunctionSet}) and the name of the function.
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: SetFunction.java,v 1.4 2004-12-06 15:25:19 pierre Exp $
 * @since MMBase-1.8
 */
public class SetFunction extends AbstractFunction {
    private static final Logger log = Logging.getLoggerInstance(SetFunction.class);

    private String className  = "unknown";
    private String methodName = "unknown";

    private Class functionClass;
    private Method functionMethod;
    private Object functionInstance;

    public SetFunction(String name, Parameter[] def, ReturnType returnType) {
        super(name, def, returnType);
    }

    /**
     * {@inheritDoc}
     */
    public Object getFunctionValue(Parameters parameters) {
        try {
            return functionMethod.invoke(functionInstance, parameters.toArray());
        } catch (IllegalAccessException iae) {
            log.error("Function call failed (method not available) : " + name +", method: " + functionMethod +
                       ", instance: " + functionInstance +", parameters: " + parameters);
            return null;
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getTargetException()); // throw the actual exception that occurred
        }
    }

    public void setClassName(String className)   {
        this.className = className;
    }

    public void setMethodName(String methodName)   {
        this.methodName = methodName;
    }

    /**
     * Initializes the function by creating an instance of the function class, and
     * locating the method to call.
     * This method should be called after setting the class and method name, and before calling
     * the {@link #getFunctionValue} method.
     */
    public void initialize() {
        if (className != null) {
            try {
                functionClass = Class.forName(className);
            } catch(Exception e) {
                String errormessage = "Can't create an application function class : " + className + " " + e.getMessage();
                log.error(errormessage);
                throw new RunTimeException(errormessage,e);
            }
            try {
                functionInstance = functionClass.newInstance();
            } catch(Exception e) {
                String errormessage = "Can't create an function instance : " + className;
                log.error(errormessage);
                throw new RunTimeException(errormessage,e);
            }
            try {
                functionMethod = functionClass.getMethod(methodName, createParameters().toClassArray());
            } catch(NoSuchMethodException f) {
                String errormessage = "Function method  not found : " + className + "." + methodName + "(" + getParameterDefinition()+")";
                log.error(errormessage);
                throw new RunTimeException(errormessage,e);
            }
        }
    }
}
