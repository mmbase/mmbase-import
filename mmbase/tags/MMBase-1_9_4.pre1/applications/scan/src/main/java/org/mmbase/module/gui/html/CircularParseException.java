/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import org.mmbase.module.ParseException;

/**
 * This exception gets thrown when a circular PART is detected.
 * @application SCAN
 * @author Rico Jansen
 * @version $Id$
 */
public class CircularParseException extends ParseException {

    //javadoc is inherited
    public CircularParseException() {
        super();
    }

    //javadoc is inherited
    public CircularParseException(String message) {
        super(message);
    }

    //javadoc is inherited
    public CircularParseException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public CircularParseException(String message, Throwable cause) {
        super(message,cause);
    }
}
