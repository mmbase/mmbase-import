/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.gui.html;

import javax.servlet.*;
import java.lang.Exception;

/**
 * This exception gets thrown when the user hasn't logged in yet.
 */
public class CircularParseException extends ParseException {

	/**
	 * Create the exception
 	 */
	public CircularParseException (String s) {
		super(s);
	}
}
