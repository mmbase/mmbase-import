/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Describing a function from a global set.
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @version $Id: SetFunction.java,v 1.2 2004-09-20 17:52:29 michiel Exp $
 * @since MMBase-1.8
 */
public class SetFunction extends Function {
    private static final Logger log = Logging.getLoggerInstance(SetFunction.class);

    private String type       = "unknown";
    private String className  = "unknown";
    private String methodName = "unknown";
    private String description = "unknown";
    // private String returntype="unknown";
    private Class functionClass;
    private Method functionMethod;
    private Object functionInstance; 
    private List params = new ArrayList();


    /**
     * {@link org.mmbase.util.function.FunctionFactory#getFunction(String, String)}
     */
    public static Function getFunction(String set, String name) {        
	return FunctionSets.getFunction(set, name);
    }


    SetFunction(String name, Parameter[] def, ReturnType returnType) {
        super(name, def, returnType);
    }


    /**
     * {@inheritDoc}
     */
    public Object getFunctionValue(Parameters arguments) {
	if (functionMethod == null) {
            if (!initFunction()) {
                return null;
            }
	}		

	try {
            return functionMethod.invoke(functionInstance, arguments.toArray());			
	} catch(Exception e) {
            log.error("function call : " + name);
            log.error("functionMethod="  + functionMethod);
            log.error("functionInstance=" + functionInstance);
            log.error("arglist=" + arguments);
	}

	return null;
    }

    void setType(String type)   { 
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description)   { 
        this.description = description;
    }

    void setName(String name)   { 
        this.name = name;
    }

    void setClassName(String className)   { 
        this.className = className;
    }

    void setMethodName(String methodName)   { 
        this.methodName = methodName;
    }

    /**
     * @javadoc
     */
    private  boolean initFunction() {
        if (className != null) {
            try {
                functionClass = Class.forName(className);
            } catch(Exception e) {
                log.error("Can't create an application function class : " + className + " " + e.getMessage());
            }

            try {
                functionInstance = functionClass.newInstance();
            } catch(Exception e) {
                log.error("Can't create an function instance : " + className);
            }

            try {
                functionMethod = functionClass.getMethod(methodName, getNewParameters().toClassArray());
            } catch(NoSuchMethodException f) {
                log.error("Function method  not found : " + className + "." + methodName + "(" + getParameterDefinition()+")");
            }
        }
        return true;
    }
}
