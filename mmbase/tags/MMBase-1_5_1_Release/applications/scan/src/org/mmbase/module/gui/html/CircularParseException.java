/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import org.mmbase.module.ParseException;

/**
 * This exception gets thrown when a circular PART is detected.
 */
public class CircularParseException extends ParseException {

	/**
	 * Create the exception
 	 */
	public CircularParseException (String s) {
		super(s);
	}
}
