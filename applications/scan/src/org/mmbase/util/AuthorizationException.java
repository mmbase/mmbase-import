/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.ServletException;

/**
 * This exception gets thrown if the user has an invalid password
 * @deprecated only used by HttpAuth.
 * @application SCAN
 * @move org.mmbase.servlet
 * @version $Id$
 */
public class AuthorizationException extends ServletException {

    //javadoc is inherited
    public AuthorizationException() {
        super();
    }

    //javadoc is inherited
    public AuthorizationException(String message) {
        super(message);
    }

    //javadoc is inherited
    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public AuthorizationException(String message, Throwable cause) {
        super(message,cause);
    }

}
