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
 * This exception gets thrown if the user has an invalid password
 */
public class AuthorizationException extends ServletException {
	
	/**
	 * Create the exception
	 */
	public AuthorizationException (String s) {
		super(s);
	}
}
