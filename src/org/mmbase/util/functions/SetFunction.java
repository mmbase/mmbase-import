/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.Arrays;
import java.lang.reflect.*;

import org.mmbase.util.logging.*;

/**
 * A SetFunction is a {@link Function} which wraps precisely one method of a class. It is used as one function of a 'set' of functions.
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: SetFunction.java,v 1.16 2006-10-26 11:42:19 michiel Exp $
 * @since MMBase-1.8
 * @see   FunctionSets
 */
class SetFunction extends AbstractFunction {
    private static final Logger log = Logging.getLoggerInstance(SetFunction.class);

    public static enum Type {
        /**
         * If type is 'class' the method must be static, or if it is not static, there will be instantiated <em>one</em> object.
         */
        CLASS,
        /**
         * If type is 'class' the method must not be static, and on every call to getFunctionValue, a new object is instantiated.
         */
        INSTANCE;
    }

    private final Method functionMethod;
    private final Object functionInstance ;
    private final Type type;

    SetFunction(String name, Parameter[] def, ReturnType returnType, String className, String methodName, Type type) {
        super(name, def, returnType);
        this.type = type;
        Class functionClass;
        try {
            functionClass = Class.forName(className);
        } catch(Exception e) {
            throw new RuntimeException("Can't create an application function class : " + className + " " + e.getMessage(), e);
        }
        try {
            functionMethod = functionClass.getMethod(methodName, createParameters().toClassArray());
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("Function method not found : " + className + "." + methodName + "(" +  Arrays.asList(getParameterDefinition()) +")", e);
        }

        if (Modifier.isStatic(functionMethod.getModifiers())) {
            functionInstance = null;
        } else {
            if (type != Type.INSTANCE) {
                try {
                    functionInstance = functionMethod.getDeclaringClass().newInstance();
                } catch(Exception e) {
                     throw new RuntimeException("Can't create an function instance : " + functionMethod.getDeclaringClass().getName(), e);
                }
            } else {
                functionInstance = null;
            }
        }
        if (returnType == null) setReturnType(new ReturnType(functionMethod.getReturnType(), functionMethod.getReturnType().getClass().getName()));

	String methodReturnType = functionMethod.getReturnType().getName();
	String xmlReturnType    = returnType.getDataType().getTypeAsClass().getName();

	if (methodReturnType.equals("boolean")) {  // ??
            methodReturnType = "java.lang.Boolean"; 
        }
        if (! methodReturnType.equals(xmlReturnType)) {
            log.warn("Return value of function " + className + "." + methodName + "(" + methodReturnType + ") does not match method return type as specified in XML: (" + xmlReturnType + ")");
        }
    }

    /**
     */
    public Object getFunctionValue(Parameters parameters) {
        parameters.checkRequiredParameters();
        try {
            return functionMethod.invoke(getInstance(), parameters.toArray());
        } catch (IllegalAccessException iae) {
            log.error("Function call failed (method not available) : " + name +", method: " + functionMethod +
                       ", instance: " + getInstance() +", parameters: " + parameters);
            return null;
        } catch (InvocationTargetException ite) {
            Throwable te = ite.getTargetException();
            if (te instanceof RuntimeException) {
                throw (RuntimeException) te;
            } else {
                throw new RuntimeException(te); // throw the actual exception that occurred
            }
        }
    }


    protected Object getInstance() {
        if (functionInstance != null || type == Type.CLASS) return functionInstance;
        try {
            return functionMethod.getDeclaringClass().newInstance();
        } catch(Exception e) {
            throw new RuntimeException("Can't create an function instance : " + functionMethod.getDeclaringClass().getName(), e);
        }
    }

    /**
     * Initializes the function by creating an instance of the function class, and
     * locating the method to call.
     */
    private void initialize(String className, String methodName) {

    }
}
