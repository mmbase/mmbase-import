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
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;
/**
 * Provides ErrorHandler methods for checking xml files by Config module
 *
 * @author cjr@dds.nl
 * @version $Id: XMLCheckErrorHandler.java,v 1.3 2002-12-03 21:32:21 michiel Exp $
 */

public class XMLCheckErrorHandler implements ErrorHandler {

    private static Logger log = Logging.getLoggerInstance(XMLCheckErrorHandler.class.getName());

    private List warninglist,errorlist,fatallist,resultlist;

    public XMLCheckErrorHandler() {
        log.debug("New xmlcheckerrorhandler");
        warninglist = new Vector();
        errorlist   = new Vector();
        fatallist   = new Vector();
        resultlist  = new Vector();
    }
    
    public void warning(SAXParseException ex) throws SAXException {
        log.debug("warn");
        ErrorStruct err = new ErrorStruct("warning", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        warninglist.add(err);
        resultlist.add(err);
        
    }
    
    public void error(SAXParseException ex) throws SAXException{
        log.debug("error");
        ErrorStruct err = new ErrorStruct("error", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        errorlist.add(err);
        resultlist.add(err);
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        log.debug("fatalError");
        ErrorStruct err = new ErrorStruct("fatal error", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        fatallist.add(err);
        resultlist.add(err);
    }

    public List getWarningList() {
        return warninglist;
    }
    
    public List getErrorList() {
        return errorlist;
    }
    
    public List getFatalList() {
        return fatallist;
    }
    
    public List getResultList() {
        return resultlist;
    }
}

