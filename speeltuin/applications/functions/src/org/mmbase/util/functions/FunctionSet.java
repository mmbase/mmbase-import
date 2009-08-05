/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;
import java.lang.reflect.*;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;


/**
 * MMBase FunctionSet
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: FunctionSet.java
 * @since MMBase-1.8
 */
public class FunctionSet {
    private static final Logger log = Logging.getLoggerInstance(FunctionSet.class);

    private String name,status,version,description;

    private Map functions = new Hashtable();
    
    private String fileName;

    public FunctionSet(String name, String version, String status, String description) {
        this.name        = name;
        this.version     = version;
        this.status      = status;
        this.description = description;
    }  
    
    /** 
     * @javadoc
     */
    public void addFunction(Function fun) {
        functions.put(fun.getName(), fun);    
    }

    /** 
     * @javadoc
     */
    public Function getFunction(String name) {
        Object o = functions.get(name);
        if (o!=null) {
            return (Function)o;
        }    
        return null;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public void setVersion(String version) {
        this.version=version;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    /** 
     * @javadoc
     */
    public boolean save() {
        // 1.7temp
        /*
          if (filename!=null) {
          saveFile(filename,createSetXML());
        }
        */

        return true;
    }

    /** 
     * Enumeration??!! Using deprecated stuff to start with is not nice
     * @javadoc
     */
    public Enumeration getFunctions() {
        return Collections.enumeration(functions.keySet()); 
    }



    /*
    private String createSetXML() {
    String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    body+="<!DOCTYPE functionset PUBLIC \"//MMBase - functionset //\" \"http://www.mmbase.org/dtd/functionset_1_0.dtd\">\n";

    body+="<functionset>\n";
            body+="<status>"+getStatus()+"</status>\n";
            body+="<version>"+getVersion()+"</version>\n";
            body+="<description>"+getDescription()+"</description>\n";
            Enumeration e=functions.elements();
            while (e.hasMoreElements()) {
                Function fun=(Function)e.nextElement();
                body+="\t<function name=\""+fun.getName()+"\">\n";
                body+="\t\t<description>"+fun.getDescription()+"</description>\n";
                body+="\t\t<type>"+fun.getType()+"</type>\n";
                body+="\t\t<class>"+fun.getClassName()+"</class>\n";
                body+="\t\t<method>"+fun.getMethodName()+"</method>\n";
                Enumeration e2=fun.getParams().elements();
                while (e2.hasMoreElements()) {
                    FunctionParam p=(FunctionParam)e2.nextElement();
                    Object o=p.getDefaultValue();
                    if (o==null) {
                        body+="\t\t<param name=\""+p.getName()+"\" type=\""+p.getType()+"\" description=\""+p.getDescription()+"\" />\n";
                    } else {
                        body+="\t\t<param name=\""+p.getName()+"\" type=\""+p.getType()+"\" description=\""+p.getDescription()+"\">"+(String)o+"</param>\n";
                    }
                }
                body+="\t\t<return type=\""+fun.getReturnType()+"\"> \n";
                e2=fun.getReturnValues().elements();
                while (e2.hasMoreElements()) {
                    FunctionReturnValue r=(FunctionReturnValue)e2.nextElement();
                    body+="\t\t\t<field name=\""+r.getName()+"\" type=\""+r.getType()+"\" description=\""+r.getDescription()+"\" />\n";
                    
                }
                body+="\t\t</return> \n";

                body+="\t</function>\n\n";
            }
    body+="</functionset>\n";
    return body;
    }
    */

    /*
    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }
    */

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
