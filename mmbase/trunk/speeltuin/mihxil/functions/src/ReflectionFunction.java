/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.util.logging.*;

import java.lang.reflect.*;
/**
 * MMBase Function

 * A reflection funcion is defined in XML, but executed with reflection (using class-name,
 * method-name, and arguments defined in that XML).
 *
 * It serves a kind of 'static' function since it is not directly associated with any object. 
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: ReflectionFunction.java,v 1.3 2003-11-21 22:01:50 michiel Exp $
 * @since MMBase-1.7
 * @todo should be fixed, because I commented most out...
 */
public class ReflectionFunction extends Function {

    private static final Logger log = Logging.getLoggerInstance(ReflectionFunction.class);

    private Method method;



    public ReflectionFunction(String name, Parameter[] def, ReturnType returnType, Method method) {
        super(name, def, returnType);
        this.method  = method;
    }

    public Class getImplementor() {
        return method.getDeclaringClass();
    }

    public String getMethodName() {
        return method.getName();
    }

    public Object getFunctionValue(Parameters arguments) {
        return null;
        /*
        if (functionMethod==null) {
            if (!initFunction()) {
                return null;
            }
        }
		

        Object arglist[] = new Object[arguments.size()];
        // test call
        try {
            
            Enumeration e=params.elements();
            int i=0;
            while (e.hasMoreElements()) {
                MMFunctionParam p=(MMFunctionParam)e.nextElement();
                String key=p.getName();
                String type=p.getType();
                String value=(String)atr.get(key);
                if (value==null) {
                    value=(String)p.getDefaultValue();
                }
                if (type.equals("String")) {
                    arglist[i]=value;
                }
                if (type.equals("int")) {
                    try {
                        arglist[i]=new Integer(Integer.parseInt(value));
                    } catch(Exception f) {}
                }
                i++;
            }

            Object retobj=functionMethod.invoke(functionInstance,arglist);
            return(retobj);

			
        } catch(Exception e) {
            log.error("function call : "+name);
            log.error("functionMethod="+functionMethod);
            log.error("functionInstance="+functionInstance);
            log.error("arglist="+arglist.toString());
            e.printStackTrace();
        }

        return(null);
    }



    private  boolean initFunction() {
        if (classname!=null) {
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
	
            Class paramtypes[]= new Class[params.size()];
            Enumeration e=params.elements();
            int i=0;
            while (e.hasMoreElements()) {
                MMFunctionParam p=(MMFunctionParam)e.nextElement();
                String type=p.getType();
                if (type.equals("String")) {
                    paramtypes[i]=String.class;
                }
                if (type.equals("int")) {
                    paramtypes[i]=int.class;
                }
                i++;
            }
            try {
                functionMethod=functionClass.getMethod(methodname,paramtypes);
            } catch(NoSuchMethodException f) {
                String paramstring="";
                e=params.elements();
                while (e.hasMoreElements()) {
                    if (!paramstring.equals("")) paramstring+=",";
                    MMFunctionParam p=(MMFunctionParam)e.nextElement();
                    paramstring+=p.getType();
                    paramstring+=" "+p.getName();
					
                }
                log.error("MMFunction method  not found : "+classname+"."+methodname+"("+paramstring+")");
                //f.printStackTrace();
            }
        }
        return(true);
        */
	}

}
