/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * This exception gets thrown when something goes wronmg on the MMCI.
 */
public class BridgeException extends RuntimeException {

    /**
     * Constructs a <code>BridgeException</code> with the specified detail
     * message.
     *
     * @param message  a description of the error
     */
    public BridgeException(String message) {
        super(message);
    }

}
