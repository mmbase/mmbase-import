/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.*;
import java.lang.Exception;

/**
 * This exception gets thrown if the user has an invalid password
 */
public class AuthorizationException extends ServletException {

    /**
     * Create the exception
     */
    public AuthorizationException (String s) {
      super(s);
    }
}
