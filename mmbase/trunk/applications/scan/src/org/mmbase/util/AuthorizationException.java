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
