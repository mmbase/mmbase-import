/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.config;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;

/**
 * @author Case Roole, cjr@dds.nl
 * @version $Id: BuilderReport.java,v 1.1 2000-09-11 20:26:53 case Exp $
 *
 * $Log: not supported by cvs2svn $
 */
public class BuilderReport extends AbstractReport {
    
    // --- public methods ---------------------------------------
    public String label() {
	return "Builders";
    }

    /**
     * @return String with database configuration
     */
    public String report() {
	String eol = (String)specialChars.get("eol");
	String res = "";
	Vector builderList;
	try {
	    builderList = listDirectory(configpath+File.separator+"builders");
	} catch (IOException e) {
	    debug("Error reading builder directory: "+e.getMessage());
	    builderList = new Vector();
	}
	String buildername, path;
	for (int i=0;i<builderList.size();i++) {
	    buildername = (String)builderList.elementAt(i);
	    path = configpath+File.separator+"builders"+File.separator+buildername+".xml";
	    // Aiai, parsing twice...
	    res = res + "- " + buildername +":";
 	    XMLParseResult pr = new XMLParseResult(path);
	    boolean foundXMLError = false;
	    if (!pr.hasDTD()) {
		res = res + "(no dtd)";
	    } else {
		int n = pr.getResultList().size();
		if (n!=0) {
		    foundXMLError = true;
		    res = res + "xml error(s):" + eol;
		    res = res + xmlErrorMessage(path,pr) + eol;
		}
	    }
	    if (!foundXMLError) {
		XMLBuilderReader reader = new XMLBuilderReader(path);
		res = res + "status = " + reader.getStatus();
		
		String classfile = reader.getClassFile();
		String classpath;
		if (classfile == null) {
		    res = res + "*** no class file set!" +eol;
		} else {
		    if (!(classfile.indexOf(".") > 0)) {
			classpath = "org.mmbase.module.builders."+classfile;
		    } else {
			classpath = classfile;
		    }
		    try {
			Class c = Class.forName(classpath);
			if (!classfile.equals("Dummy")) {
			    res = res + " (Java class = "+classpath+")" + eol;
			} else {
			    res = res + eol;
			}
		    } catch (Exception e) {
			res = res + "*** Error loading associated class " + classpath + eol;
		    }
		}
	    }
        }
	return res;
    }

    // -- private methods ---------------------------------------
    /**
     * @param path Full path to builder configuration file
     * @return boolean telling whether builder is active
     * Oops, parsing the darned thing again!!
     */
    private boolean builderIsActive(String path) {
        XMLBuilderReader reader = new XMLBuilderReader(path);
        return reader.getStatus().equalsIgnoreCase("active");
    }
}
