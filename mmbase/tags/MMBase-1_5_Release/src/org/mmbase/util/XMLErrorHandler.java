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
 * @version $Revision: 1.4 $ $Date: 2001-07-10 11:04:08 $
 */

public class XMLErrorHandler implements ErrorHandler {
    private static Logger log = Logging.getLoggerInstance(XMLErrorHandler.class.getName());

    public void warning(SAXParseException ex) {
        log.warn(getLocationString(ex)+": "+ ex.getMessage());
    }

    public void error(SAXParseException ex) {
        log.error(getLocationString(ex)+": "+ ex.getMessage());
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        log.fatal(getLocationString(ex)+": "+ ex.getMessage());
	throw ex;
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

