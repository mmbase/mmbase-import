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
 * This exception will occur if the getPostParameterByte method is used
 * While the arraylength is larger than the maximum size allowed
 */
public class PostValueToLargeException extends ServletException {

	/**
	 * Create the exception
 	 */
	public PostValueToLargeException (String s) {
		super(s);
	}
}
