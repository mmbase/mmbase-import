/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class CastException extends Exception {


    //javadoc is inherited
    public CastException() {
        super();
    }

    //javadoc is inherited
    public CastException(String message) {
        super(message);
    }

    //javadoc is inherited
    public CastException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public CastException(String message, Throwable cause) {
        super(message,cause);
    }



}
