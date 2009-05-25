/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Provides ErrorHandler methods
 *
 * @author Gerard van Enk
 * @version $Id$
 */

public class ErrorHandler implements org.xml.sax.ErrorHandler {
    public static final int WARNING =   1;
    public static final int ERROR =   2;
    public static final int FATAL_ERROR = 3;
    public static final int NEVER = 4;

    private static Logger log = Logging.getLoggerInstance(ErrorHandler.class);
    private int exceptionLevel;
    private boolean logMessages;
    private boolean warning = false;
    private boolean error = false;
    private boolean fatal = false;

    private StringBuilder messages = new StringBuilder();


    public ErrorHandler() {
        // default keep old behaviour
        logMessages = true;
        exceptionLevel = NEVER;
    }

    public ErrorHandler(boolean log, int exceptionLevel) {
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

    private boolean isJava5AndXInclude(Exception ex) {
        if ( ("" + System.getProperty("java.version")).startsWith("1.5")) {
            for (StackTraceElement el : ex.getStackTrace()) {
                if (el.getClassName().equals("com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler")) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public void error(SAXParseException ex) throws SAXException{
        String message = getLocationString(ex)+": "+ ex.getClass() + " " + ex.getMessage();
        if (isJava5AndXInclude(ex)) {
            // I get horrible validation exceptions in the log when doing xinclude in java 5.
            // It does not happen in java 6.
            // If you ask me, the xml parser of java 5 simply sucks. Going to ignore this as an 'error'.
            log.debug(message + " (this probably does not make sense)");
            return;
        }

        messages.append(message + "\n");
        error = true;
        if(logMessages) {
            if (log.isDebugEnabled()) {
                log.error(message, new Throwable());
            } else {
                log.error(message);
            }
        }
        if(exceptionLevel <= ERROR) {
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
        StringBuilder str = new StringBuilder();
        String systemId = ex.getSystemId();
        if (systemId != null) {
            str.append(systemId);
        } else {
            str.append("[NO SYSTEM ID]");
        }
        str.append(" line:");
        str.append(ex.getLineNumber());
        str.append(" column:");
        str.append(ex.getColumnNumber());
        return str.toString();
    }
}
