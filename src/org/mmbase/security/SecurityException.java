/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 *  Thrown by the security classes to indicate a security violation/malfunction.
 */
public class SecurityException extends java.lang.SecurityException {

    /**
     *	Constructs a SecurityException with the specified detail message.
     *	@parm message The detail message.
     */
    public SecurityException(String message) {
    	super(message);
    }
}
