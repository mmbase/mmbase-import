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
 * The implementation of one set of functions.
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: FunctionSet.java
 * @since MMBase-1.8
 */
public class FunctionSet {
    private static final Logger log = Logging.getLoggerInstance(FunctionSet.class);

    private String name, status, version, description;

    /** 
     * String -> Function
     */
    private Map functions = new HashMap();
    
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
    void addFunction(Function fun) {
        functions.put(fun.getName(), fun);    
    }

    /** 
     * @javadoc
     */
    Function getFunction(String name) {
        Object o = functions.get(name);
        if (o != null) {
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
        this.description = description;
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
     * @deprecated
     */
    public Enumeration getFunctions() {
        return Collections.enumeration(functions.keySet()); 
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
