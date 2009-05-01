/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

/**
 * This exception gets thrown when something goes wrong in the Framework,
 * such as when rendering or processing a component.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @version MMBase-1.9
 * @todo i18n?
 */
public class FrameworkException extends Exception {

    //javadoc is inherited
    public FrameworkException() {
        super();
    }

    //javadoc is inherited
    public FrameworkException(String message) {
        super(message);
    }

    //javadoc is inherited
    public FrameworkException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public FrameworkException(String message, Throwable cause) {
        super(message,cause);
    }

}
