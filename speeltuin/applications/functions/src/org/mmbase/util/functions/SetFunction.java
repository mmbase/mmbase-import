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
 * Describing a function on a global set, giving access to the underlying executeFunction of the FunctionSet
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @version $Id: SetFunction.java,v 1.2 2004-09-20 17:13:40 michiel Exp $
 * @since MMBase-1.8
 */
public class SetFunction extends Function {
    private static final Logger log = Logging.getLoggerInstance(SetFunction.class);

    private String type       = "unknown";
    private String classname  = "unknown";
    private String methodname = "unknown";
    private String description = "unknown";
    // private String returntype="unknown";
    private Class functionClass;
    private Method functionMethod;
    private Object functionInstance; 
    private List params = new Vector();


    /**
     * {@link org.mmbase.util.function.Function#getFunction(String, String)}
     */
    public static Function getFunction(String set, String name) {        
	return FunctionSets.getFunction(set,name);
    }

    /**
     * {@inheritDoc}
     */
    public Object getFunctionValue(Parameters arguments) {
	if (functionMethod==null) {
            if (!initFunction()) {
                return null;
            }
	}
		

	Object arglist[] = new Object[arguments.size()];
	try {

            for (int i = 0; i<arguments.size(); i++) {
                //log.info(arguments.get(i));
                arglist[i] = arguments.get(i);
            }	
            
            Object retobj = functionMethod.invoke(functionInstance,arglist);

            return retobj;

			
	} catch(Exception e) {
            log.error("function call : "+name);
            log.error("functionMethod="+functionMethod);
            log.error("functionInstance="+functionInstance);
            log.error("arglist="+arglist.toString());
            e.printStackTrace();
	}

	return null;
    }

    SetFunction(String name, Parameter[] def, ReturnType returnType) {
        super(name, def, returnType);
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

    void setClassName(String classname)   { 
        this.classname = classname;
    }

    void setMethodName(String methodname)   { 
        this.methodname = methodname;
    }

    /**
     * @javadoc
     */
    private  boolean initFunction() {
        if (classname != null) {
            try {
                functionClass = Class.forName(classname);
            } catch(Exception e) {
                log.error("can't create an application function class : "+classname);
                e.printStackTrace();
            }

            try {
                functionInstance = functionClass.newInstance();
            } catch(Exception e) {
                log.error("can't create an function instance : "+classname);
                e.printStackTrace();
            }

            Parameter[] pmd= getParameterDefinition();	
            Class paramtypes[]= new Class[pmd.length];
            for (int i=0;i<pmd.length;i++) {
                paramtypes[i]=pmd[i].getType();
            }
            try {
                functionMethod = functionClass.getMethod(methodname,paramtypes);
            } catch(NoSuchMethodException f) {
               f.printStackTrace();
                String paramstring="";
                /*
                  e=params.elements();
                  while (e.hasMoreElements()) {
                  if (!paramstring.equals("")) paramstring+=",";
                  FunctionParam p=(FunctionParam)e.nextElement();
                  paramstring+=p.getType();
                  paramstring+=" "+p.getName();
					
                  }
                */
                log.error("Function method  not found : "+classname+"."+methodname+"("+paramstring+")");
                //f.printStackTrace();
            }
        }
        return true;
    }
}
