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
