/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * This exception gets thrown when an object is not found in the bridge.
 * @author Michiel Meeuwissen
 * @version $Id: NotFoundException.java,v 1.4 2003-08-29 09:36:50 pierre Exp $
 */
public class NotFoundException extends BridgeException {

    //javadoc is inherited
    public NotFoundException() {
        super();
    }

    //javadoc is inherited
    public NotFoundException(String message) {
        super(message);
    }

    //javadoc is inherited
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public NotFoundException(String message, Throwable cause) {
        super(message,cause);
    }

}
