/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * Exceptions thrown by logging can be wrapped in this. Odd logging
 * implementation like 'ExceptionImpl' do this.

 *
 * @author Michiel Meeuwissen 
 * @since  MMBase-1.7
 * @see    ExceptionImpl
 */

public class LoggingException extends RuntimeException {
    public LoggingException(String message) {
        super(message);
    }
    public String toString() {
        return getMessage();
    }
}
