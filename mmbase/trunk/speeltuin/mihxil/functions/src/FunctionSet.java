/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.io.*;
import java.util.*;
import org.mmbase.util.logging.*;


/**
 * MMBase FunctionSet
 *
 * Perhaps this can be done by dom objects (for persistance and parsing).
 *
 * It maintains a groups of ReflectionFunction instances.
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: FunctionSet.java,v 1.2 2003-11-21 22:01:50 michiel Exp $
 * @since MMBase-1.7

 */
public class FunctionSet {

    private static final Logger log = Logging.getLoggerInstance(FunctionSet.class);
    private String name, status, version, description;
    private Map    functions = new HashMap();
    private String filename;

    public FunctionSet(String name,String version,String status,String description) {
        this.name = name;
        this.version = version;
        this.status = status;
        this.description = description;
    }  

    public void addFunction(ReflectionFunction fun) {
        functions.put(fun.getName(),fun);	
    }

    public ReflectionFunction getFunction(String name) {
        Object o=functions.get(name);
        if (o!=null) {
            return (ReflectionFunction)o;
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

    public boolean save() {
        log.info("SAVE OF FUNCTIONSET");
        if (filename!=null) {
            saveFile(filename, createSetXML());
        }

        return true;
    }

    public Map getFunctions() {
        return functions;
    }


    private String createSetXML() {
	String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	body+="<!DOCTYPE functionset PUBLIC \"//MMBase - functionset //\" \"http://www.mmbase.org/dtd/functionset_1_0.dtd\">\n";
        
	body+="<functionset>\n";
        body+="<status>"+getStatus()+"</status>\n";
        body+="<version>"+getVersion()+"</version>\n";
        body+="<description>"+getDescription()+"</description>\n";
        Iterator e = functions.values().iterator();
        while (e.hasNext()) {
            ReflectionFunction fun = (ReflectionFunction) e.next();
            body+="\t<function name=\""+fun.getName()+"\">\n";
            body+="\t\t<description>"+fun.getDescription()+"</description>\n";
            // body+="\t\t<type>"+fun.getType()+"</type>\n";
            body+="\t\t<class>"+fun.getImplementor()+"</class>\n";
            body+="\t\t<method>"+fun.getMethodName()+"</method>\n";
            Parameter[] def =  fun.getParameterDefinition();
            for (int i = 0 ; i < def.length; i++) {
                Parameter p = def[i];
                Object o = p.getDefaultValue();
                if (o==null) {
                    body+="\t\t<param name=\""+p.getName()+"\" type=\""+p.getType()+"\" description=\""+p.getDescription()+"\" />\n";
                } else {
                    body+="\t\t<param name=\""+p.getName()+"\" type=\""+p.getType()+"\" description=\""+p.getDescription()+"\">"+(String)o+"</param>\n";
                }
            }
            body+="\t\t<return type=\"" + fun.getReturnType().getType().getName() + "\"> \n";
            Iterator e2 = fun.getReturnType().getSubTypes().entrySet().iterator();
            while (e2.hasNext()) {
                Map.Entry entry = (Map.Entry) e2.next();
                ReturnType rt = (ReturnType) entry.getValue();
                body+="\t\t\t<field name=\"" + entry.getKey()+"\" type=\"" + rt.getType().getName() + "\" description=\"" + rt.getDescription()+"\" />\n";
					
            }

            body+="\t</function>\n\n";
        }
	body+="</functionset>\n";
	return body;
    }

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

    public void setFileName(String filename) {
	this.filename=filename;
    }
}
