/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Circular reference exception.
 * This exception is thrown when circularity is detected between two builders,
 * i.e. when the extend each other.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: CircularReferenceException.java,v 1.1 2002-03-21 10:06:02 pierre Exp $
 */
public class CircularReferenceException extends RuntimeException {

    public CircularReferenceException(String s) {
        super(s);
    }
}

