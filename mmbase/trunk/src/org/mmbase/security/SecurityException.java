/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * Thrown by the security classes to indicate a security violation/malfunction.
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: SecurityException.java,v 1.4 2003-03-07 09:31:09 pierre Exp $
 */
public class SecurityException extends java.lang.SecurityException {

    /**
     *	Constructs a SecurityException with the specified detail message.
     *	@param message The detail message.
     */
    public SecurityException(String message) {
        super(message);
    }
}
