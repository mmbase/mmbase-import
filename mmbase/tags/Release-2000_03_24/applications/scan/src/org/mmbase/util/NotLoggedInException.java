/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import javax.servlet.*;
import java.lang.Exception;

/**
 * This exception gets thrown when the user hasn't logged in yet.
 */
public class NotLoggedInException extends ServletException {

	/**
	 * Create the exception
 	 */
	public NotLoggedInException (String s) {
		super(s);
	}
}
