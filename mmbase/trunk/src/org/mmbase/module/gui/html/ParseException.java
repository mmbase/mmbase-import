package org.mmbase.module.gui.html;

import javax.servlet.*;
import java.lang.Exception;

/**
 * This exception gets thrown when the user hasn't logged in yet.
 */
public class ParseException extends ServletException {

	/**
	 * Create the exception
 	 */
	public ParseException (String s) {
		super(s);
	}
}
