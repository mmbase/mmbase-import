/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;


/**
 * This exception will occur if the getPostParameterByte method is used
 * While the arraylength is larger than the maximum size allowed
 *
 * @author vpro
 * @version $Id: PostValueToLargeException.java,v 1.7 2003-12-03 11:29:20 keesj Exp $
 */
public class PostValueToLargeException extends RuntimeException {

    //javadoc is inherited
    public PostValueToLargeException() {
        super();
    }

    //javadoc is inherited
    public PostValueToLargeException(String message) {
        super(message);
    }

    //javadoc is inherited
    public PostValueToLargeException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public PostValueToLargeException(String message, Throwable cause) {
        super(message, cause);
    }

}
