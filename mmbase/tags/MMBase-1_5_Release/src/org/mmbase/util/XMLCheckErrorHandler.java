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
 * Provides ErrorHandler methods for checking xml files by Config module
 *
 * @author cjr@dds.nl
 * @version $id$

 * $log$
 */

public class XMLCheckErrorHandler implements ErrorHandler {

    private String classname  = getClass().getName();

    private Vector warninglist,errorlist,fatallist,resultlist;

    public XMLCheckErrorHandler() {
	warninglist = new Vector();
	errorlist = new Vector();
	fatallist = new Vector();
	resultlist = new Vector();
    }
    
    public void warning(SAXParseException ex) {
	ErrorStruct err = new ErrorStruct("warning",ex.getLineNumber(),ex.getColumnNumber(),ex.getMessage());
	warninglist.addElement( err );
	resultlist.addElement( err );
	
    }

    public void error(SAXParseException ex) {
	ErrorStruct err = new ErrorStruct("error",ex.getLineNumber(),ex.getColumnNumber(),ex.getMessage());
	errorlist.addElement( err );
	resultlist.addElement( err );
    }

    public void fatalError(SAXParseException ex) throws SAXException {
	ErrorStruct err = new ErrorStruct("fatal error",ex.getLineNumber(),ex.getColumnNumber(),ex.getMessage());
	fatallist.addElement( err );
;	resultlist.addElement( err );
    }

    public Vector getWarningList() {
	return warninglist;
    }

    public Vector getErrorList() {
	return errorlist;
    }

    public Vector getFatalList() {
	return fatallist;
    }
    
    public Vector getResultList() {
	return resultlist;
    }
}

