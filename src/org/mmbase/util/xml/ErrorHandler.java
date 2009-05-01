/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

/**
 * Provides ErrorHandler methods
 *
 * @author Gerard van Enk
 * @version $Id: ErrorHandler.java,v 1.1 2008-09-03 23:41:47 michiel Exp $
 */

public class ErrorHandler extends org.mmbase.util.XMLErrorHandler {
    public ErrorHandler() {
        super();
    }

    public ErrorHandler(boolean log, int exceptionLevel) {
        super(log, exceptionLevel);
    }
}
