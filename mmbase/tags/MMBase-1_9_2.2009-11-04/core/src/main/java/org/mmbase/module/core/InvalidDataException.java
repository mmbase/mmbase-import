/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;


/**
 * This exception gets thrown when a node contains invalid data.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class InvalidDataException extends Exception {

    // Name of the field that caused the exception
    private String invalidField = null;

    //javadoc is inherited
    public InvalidDataException () {
        super();
    }

    //javadoc is inherited
    public InvalidDataException(String message) {
        super(message);
    }

    //javadoc is inherited
    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create the exception.
     * @param message a description of the exception
     * @param fieldName the name of the field that caused the exception
     */
    public InvalidDataException (String message, String fieldName) {
        super(message);
        invalidField = fieldName;
    }

    /**
     * Create the exception.
     * The cause can be retrieved with getCause().
     *
     * @param cause Throwable the cause of the exception
     * @param fieldName the name of the field that caused the exception
     * @since MMBase-1.7
     */
    public InvalidDataException (Throwable cause, String fieldName) {
        super(cause);
        invalidField = fieldName;
    }

    /**
     * Retrieved the name of the field that caused the exception
     * @return the field name as a String
     */
    public String getInvalidFieldName() {
        return invalidField;
    }
}
