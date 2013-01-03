/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.generator;

/**
 * @javadoc 
 * @author Kees Jongenburger <keesj@dds.nl>
 * @version $Id: NotInMMCIException.java,v 1.3 2003-08-29 09:40:19 pierre Exp $
 */
public class NotInMMCIException extends Exception {
    
    //javadoc is inherited
    public NotInMMCIException() {
        super();
    }

    //javadoc is inherited
    public NotInMMCIException(String message) {
        super(message);
    }

    //javadoc is inherited
    public NotInMMCIException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public NotInMMCIException(String message, Throwable cause) {
        super(message, cause);
    }

}
