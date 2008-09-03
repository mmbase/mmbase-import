/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 *
 * @deprecatde Use org.mmbase.util.xml.ErrorHandler
 * @author Gerard van Enk
 * @version $Id: XMLErrorHandler.java,v 1.20 2008-09-03 23:41:47 michiel Exp $
 */

public class XMLErrorHandler extends org.mmbase.util.xml.ErrorHandler {
    public XMLErrorHandler() {
        super();
    }

    public XMLErrorHandler(boolean log, int exceptionLevel) {
        super(log, exceptionLevel);
    }


}
