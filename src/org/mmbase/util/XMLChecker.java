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
import org.xml.sax.helpers.DefaultHandler;

import org.mmbase.module.corebuilders.*;

/**
 * Check XML documents against their DTD's and be as pretty as possible about errors 
 *
 * cjr@dds.nl
 */
public class XMLChecker  {

    Document document;
    DOMParser parser;

    PrintStream out;
    
    public static String error_color = "#FF8000";
    public static String fatalerror_color = "#DD0000";
    public static String warning_color = "#007700";

    public class XMLCheckerHandler extends DefaultHandler {
	PrintStream out;
	
	int warningCount;
	int errorCount;
	int fatalErrorCount;
	public XMLCheckerHandler(PrintStream out) {
	    this.out = out;
	    warningCount = 0;
	    errorCount = 0;
	    fatalErrorCount = 0;
	}
	    
	public void error(SAXParseException e) {
	    errorCount++;
	    out.println("<font color=\""+error_color+"\">error:</font> "+e.getMessage()+"<br>\n");
	}

	public void fatalError(SAXParseException e) {
	    fatalErrorCount++;
	    out.println("<font color=\""+fatalerror_color+"\">fatal error:</font> "+e.getMessage()+"<br>\n");
	}

	public void warning(SAXParseException e) {
	    warningCount++;
	    out.println("<font color=\""+warning_color+"\">warning:</font> "+e.getMessage()+"<br>\n");
	}
	
	public void report() {
	    out.println("warning(s): "+warningCount+", error(s): "+errorCount+", fatal error(s): "+fatalErrorCount+"<p>\n");
	}
    }

    public XMLChecker(PrintStream out) {
	this.out = out;
    }

    public void validateAndReport(String filename) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
	    parser.setFeature("http://xml.org/sax/features/validation", true);
	    XMLCheckerHandler errorhandler = new XMLCheckerHandler(out);
            parser.setErrorHandler(errorhandler);
            parser.parse(filename);
	    errorhandler.report();

	} catch(Exception e) {
	    e.printStackTrace(out);
	}
    }
    
    public boolean validate(String filename) {
	return true;
    }
}
