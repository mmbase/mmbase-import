/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;
/**
 * This exception gets thrown when something goes wrong in the MMBase-taglib.
 * 
 * MMBase specific. JDK 1.4.
 *
 * @author Michiel Meeuwissen
 * @version $Id: TaglibException.java,v 1.2 2004-02-06 12:13:31 michiel Exp $
 * @since MMBase-1.7
 */
public class TaglibException extends JspTagException {

    //javadoc is inherited
    public TaglibException() {
        super();
    }

    //javadoc is inherited
    public TaglibException(String message) {
        super(message);
    }

    //javadoc is inherited
    public TaglibException(Throwable cause) {
        super(cause.getMessage());
        initCause(cause);
    }

    //javadoc is inherited
    public TaglibException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

}
