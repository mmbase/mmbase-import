/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.Exception;

/**
 * This exception gets thrown when a node contains invalid data.
 */
public class InvalidDataException extends Exception {

    /** Name of the field that caused the exception
    */
    private String invalidField=null;
	
	/**
	 * Create the exception.
 	 */
	public InvalidDataException() {
	}
	
	/**
	 * Create the exception.
	 * @param message a description of the exception
 	 */
	public InvalidDataException (String message) {
		super(message);
	}
	
	/**
	 * Create the exception.
	 * @param message a description of the exception
	 * @param fieldMame the name of the field that caused the exception
 	 */
	public InvalidDataException (String message, String fieldName) {
		super(message);
		invalidField=fieldName;
	}
	
	/**
	* Retrieved the name of the field that caused the exception
	* @return the field name as a String
	*/
	public String getInvalidFieldName() {
	    return invalidField;
	}
}
