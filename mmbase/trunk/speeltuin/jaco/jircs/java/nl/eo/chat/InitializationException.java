/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

/**
 * Thrown when initialization of a class fails.
 *
 * @author Jaco de Groot
 */
public class InitializationException extends Exception {
    
    public InitializationException(String message) {
        super(message);
    }
    
}

