/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.Exception;

/**
 * This exception gets thrown when a node contains invalid data
 */
public class InValidDataException extends Exception {

    public String invalidField="";
	/**
	 * Create the exception
 	 */
	public InValidDataException (String s) {
		super(s);
	}
	
	public InValidDataException (String s, String fieldName) {
		super(s);
		invalidField=fieldName;
	}
}
