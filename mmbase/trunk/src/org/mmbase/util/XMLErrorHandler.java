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
import org.mmbase.util.logging.*;

/**
 * Provides ErrorHandler methods
 *
 * @author Gerard van Enk
 * @version $Revision: 1.7 $ $Date: 2002-10-07 17:20:58 $
 */

public class XMLErrorHandler implements ErrorHandler {
    public static int WARNING =   1;
    public static int ERROR =   2;
    public static int FATAL_ERROR = 3;
    public static int NEVER = 4;

    private static Logger log = Logging.getLoggerInstance(XMLErrorHandler.class.getName());
    private int exceptionLevel;
    private boolean logMessages;
    private boolean warning = false;
    private boolean error = false;
    private boolean fatal = false;

    private StringBuffer messages = new StringBuffer();

    /**
     * This class is used by init of logging system.
     * After configuration of logging, logging must be reinitialized.
     */
    static void reinitLogger() {
        log = Logging.getLoggerInstance(XMLErrorHandler.class.getName());
    }


    public XMLErrorHandler() {
        // default keep old behaviour
        logMessages = true;
        exceptionLevel = NEVER;
    }

    public XMLErrorHandler(boolean log, int exceptionLevel) {
        this.logMessages = log;
        this.exceptionLevel = exceptionLevel;
    }

    public void warning(SAXParseException ex) throws SAXException {
        String message = getLocationString(ex)+": "+ ex.getMessage();
        messages.append(message + "\n");
        warning = true;
        if(logMessages) {
            log.warn(message);
        }
        if(exceptionLevel<=WARNING) {
            throw ex;
        }
    }

    public void error(SAXParseException ex) throws SAXException{
        String message = getLocationString(ex)+": "+ ex.getMessage();
        messages.append(message + "\n");
        error = true;
        if(logMessages) {
            log.error(message);
        }
        if(exceptionLevel<=ERROR) {
            throw ex;
        }
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        String message = getLocationString(ex)+": "+ ex.getMessage();
        messages.append(message + "\n");
        fatal = true;
        if(logMessages) {
            log.fatal(message);
        }
        if(exceptionLevel<=FATAL_ERROR) {
            throw ex;
        }
    }

    public boolean foundWarning() {
        return warning;
    }

    public boolean foundError() {
        return error;
    }

    public boolean foundFatalError() {
        return fatal;
    }

    public boolean foundNothing() {
        return !(warning || error || fatal);
    }

    public String getMessageBuffer() {
        return messages.toString();
    }

    /**
     * Returns a string of the location.
     */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
                str.append(systemId);
        }
        str.append(" line:");
        str.append(ex.getLineNumber());
        str.append(" column:");
        str.append(ex.getColumnNumber());
        return str.toString();
    }
}
