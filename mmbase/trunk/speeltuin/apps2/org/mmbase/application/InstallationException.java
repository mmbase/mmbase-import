/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.application;

/**
 * This exception gets thrown when something goes wrong with installing an application component
 */
public class InstallationException extends Exception {
    
    /**
     * Constructs a <code>InstallationException</code> with the specified detail
     * message.
     *
     * @param message  a description of the error
     */
    public InstallationException(String message) {
        super(message);
    }
    
}
