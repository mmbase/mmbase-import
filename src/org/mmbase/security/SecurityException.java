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
