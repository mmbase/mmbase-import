/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

/** This excpetion is thrown when an external process failed.
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
public class ProcessException extends Exception {

    /**
     * Constructor for ProcessException.
     */
    public ProcessException() {
        super();
    }

    /**
     * Constructor for ProcessException.
     * @param message
     */
    public ProcessException(String message) {
        super(message);
    }


    /**
     * Constructor for ProcessException.
     * @since MMBase-1.9
     */
    public ProcessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for ProcessException.
     * @param message
     * @since MMBase-1.9
     */
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

}
