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
 * This exception will occur if the getPostParameterByte method is used
 * While the arraylength is larger than the maximum size allowed
 */
public class PostValueToLargeException extends ServletException {

    /**
     * Create the exception
      */
    public PostValueToLargeException (String s) {
        super(s);
    }
}
