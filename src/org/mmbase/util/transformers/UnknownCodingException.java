/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

/**
 * Class UnknownCodingException
 * 
 * @javadoc
 *
 * @author Michiel Meeuwissen
 * @version $Id: UnknownCodingException.java,v 1.6 2004-05-06 12:34:39 keesj Exp $
 */
public class UnknownCodingException extends RuntimeException {

    //javadoc is inherited
    public UnknownCodingException() {
        super();
    }

    //javadoc is inherited
    public UnknownCodingException(String message) {
        super(message);
    }

    //javadoc is inherited
    public UnknownCodingException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public UnknownCodingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * @javadoc
     * @since MMBase-1.7
     */
    public UnknownCodingException (Class c, String reason, int i) {
        super(c.getName() + ": " + reason + " '" + i + "'");        
    }
    
    /**
     * @javadoc
     * @since MMBase-1.7
     */
    public UnknownCodingException (Class c, int i) {
        super(c.getName() + ": '" + i + "'");        
    }
}
