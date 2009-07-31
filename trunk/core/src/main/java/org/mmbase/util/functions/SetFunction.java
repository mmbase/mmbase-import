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
 * @version $Id$
 * @since MMBase-1.8
 * @see   FunctionSets
 */
public class SetFunction extends AbstractFunction<Object> {
    private static final Logger log = Logging.getLoggerInstance(SetFunction.class);

    public static enum Type {
        /**
         * If type is 'class' the method must be static, or if it is not static, there will be instantiated <em>one</em> object.
         */
        CLASS,
        /**
         * If type is 'instance' the method must not be static, and on every call to getFunctionValue, a new object is instantiated.
         */
        INSTANCE,
        /**
         * If type is 'singleton', then the static method 'getInstance' will be called to get the one instance, unless the method is static.
         */
        SINGLETON
    }

    private final Method functionMethod;
    private final Object functionInstance ;
    private final Type type;
    private final int defLength;

    /**
     * Simple utility method to convert primitive classes to their 'sophisticated' counterparts.
     *
     * @since MMBase-1.8.5
     */
    protected static Class sophisticate(Class primitive) {

        if (primitive.isPrimitive()) {
            if (primitive.equals(Boolean.TYPE)) {
                return  Boolean.class;
            } else if (primitive.equals(Character.TYPE)) {
                return  Character.class;
            } else if (primitive.equals(Byte.TYPE)) {
                return Byte.class;
            } else if (primitive.equals(Character.TYPE)) {
                return  Short.class;
            } else if (primitive.equals(Integer.TYPE)) {
                return Integer.class;
            } else if (primitive.equals(Long.TYPE)) {
                return Long.class;
            } else if (primitive.equals(Float.TYPE)) {
                return Float.class;
            } else if (primitive.equals(Double.TYPE)) {
                return Double.class;
            } else if (primitive.equals(Void.TYPE)) {
                return Void.class;
            }
        }
        // already sophisticated
        return primitive;
    }


    SetFunction(String name, Parameter[] def, ReturnType<Object> returnType, Class functionClass, String methodName, Type type) {
        super(name, def, returnType);
        this.type = type;

        try {
            functionMethod = functionClass.getMethod(methodName, createParameters().toClassArray());
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("Function method not found : " + functionClass + "." + methodName + "(" +  Arrays.asList(getParameterDefinition()) +")", e);
        }

        if (Modifier.isStatic(functionMethod.getModifiers())) {
            functionInstance = null;
        } else {
            switch (type) {
            case CLASS:
                try {
                    functionInstance = functionMethod.getDeclaringClass().newInstance();
                } catch(Exception e) {
                     throw new RuntimeException("Can't create an function instance : " + functionMethod.getDeclaringClass().getName(), e);
                }
                break;
            case SINGLETON:
                try {
                    Method singleton = functionClass.getMethod("getInstance");
                    functionInstance = singleton.invoke(null);
                } catch(Exception e) {
                    throw new RuntimeException("Can't create an function instance : " + functionMethod.getDeclaringClass().getName(), e);
                }
                break;
            case INSTANCE:
                functionInstance = null;
                // one will be made on every calle
                break;
            default:
                functionInstance = null;
            }

        }
        if (returnType == null) {
            setReturnType(new ReturnType<Object>(functionMethod.getReturnType(), functionMethod.getReturnType().getClass().getName()));
            returnType = getReturnType();
        }

	Class methodReturnType = sophisticate(functionMethod.getReturnType());
	Class xmlReturnType    = sophisticate(returnType.getDataType().getTypeAsClass());

        if (! xmlReturnType.isAssignableFrom(methodReturnType)) {
            log.warn("Return value of function " + functionClass + "." + methodName + "(" + methodReturnType + ") does not match method return type as specified in XML: (" + xmlReturnType + ")");
        }
        defLength = def.length;
    }

    /**
     * @since MMBase-1.8.5
     */
    public SetFunction(String name, Parameter[] def, Class clazz) {
        this(name, def, null, clazz, name, Type.CLASS);
    }

    /**
     */
    public Object getFunctionValue(Parameters parameters) {
        parameters.checkRequiredParameters();
        try {
            if (defLength < parameters.size()) {
                // when wrapping this fucntion, it can happen that the number of parameters increases.
                return functionMethod.invoke(getInstance(), parameters.subList(0, defLength).toArray());
            } else {
                return functionMethod.invoke(getInstance(), parameters.toArray());
            }
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

}
