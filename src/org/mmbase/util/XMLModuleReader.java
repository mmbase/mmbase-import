/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.corebuilders.*;

/**
 */
public class XMLModuleReader extends XMLBasicReader {

    Document document;
    DOMParser parser;


    public XMLModuleReader(String filename) {
        super(filename);
    }

    /**
    * get the status of this builder
    */
    public String getStatus() {
        Element e = getElementByPath("module.status");
        return getElementValue(e);
    }


    /**
    * get the version of this application
    */
    public int getModuleVersion() {
        Element e = getElementByPath("module");
        String version = getElementAttributeValue(e,"version");
        int n = 0;
        if (version == null) {
            return n;
        } else {
            try {
                n = Integer.parseInt(version);
            } catch (Exception f) {
                n = 0;
            }
            return n;
        }
    }


    /**
    * get the version of this application
    */
    public String getModuleMaintainer() {
        Element e = getElementByPath("module");

        String tmp=getElementAttributeValue(e,"maintainer");
	if (tmp!=null && !tmp.equals("")) {
		return(tmp);	
	} else {
		return("mmbase.org");
	}
    }


    /**
    * get the classfile of this builder
    */
    public String getClassFile() {
        Element e = getElementByPath("module.classfile");
        return getElementValue(e);
    }


    /**
    * Get the properties of this builder
    */
    public Hashtable getProperties() {
        Hashtable hash=new Hashtable();
        Element e = getElementByPath("module.properties");
	if (e!=null) {
	    Enumeration enum = getChildElements(e,"property");
	    Element p;
	    String name, value;
	    while (enum.hasMoreElements()) {
		p = (Element)enum.nextElement();
		name = getElementAttributeValue(p,"name");
		value = getElementValue(p);
		hash.put(name,value);
	    }
	}
        return(hash);
    }


}

