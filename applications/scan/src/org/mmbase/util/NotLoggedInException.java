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
 * This exception gets thrown when the user hasn't logged in yet.
 */
public class NotLoggedInException extends ServletException {

    /**
     * Create the exception
      */
    public NotLoggedInException (String s) {
        super(s);
    }
}
