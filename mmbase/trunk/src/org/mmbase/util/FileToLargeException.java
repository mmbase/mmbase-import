/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;
import java.lang.Exception;
import javax.servlet.*;

/**
 * This Exception will occur if the upload file exceeds a certain size, 
 * that's specified in WorkerPostHandler. 
 */
public class FileToLargeException extends ServletException {

	/**
	 * Create the exception
 	 */
	public FileToLargeException (String s) {
		super(s);
	}
}
