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
 * Provides ErrorHandler methods
 *
 * @author Gerard van Enk
 * @version $Revision: 1.1 $ $Date: 2000-07-03 22:27:53 $
 */

public class XMLErrorHandler implements ErrorHandler {

    private String classname  = getClass().getName();

    private void debug(String msg) {
		System.out.println( classname +":"+ msg );
	}
	public void warning(SAXParseException ex) {
		debug("[Warning] "+ getLocationString(ex)+": "+ ex.getMessage());
	}

	public void error(SAXParseException ex) {
		debug("[Error] "+ getLocationString(ex)+": "+ ex.getMessage());
	}

	public void fatalError(SAXParseException ex) throws SAXException {
		debug("[Fatal Error] "+ getLocationString(ex)+": "+ ex.getMessage());
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
		str.append(':');
		str.append(ex.getLineNumber());
		str.append(':');
		str.append(ex.getColumnNumber());

		return str.toString();
	}
}

