/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.ServletException;


/**
 * This exception gets thrown when a Cyclic Redundancy Check
 * fails.
 * @application SCAN
 * @move org.mmbase.servlet
 * @author vpro
 * @version $Id$
 */
public class PageCRCException extends ServletException {

    /**
     * Create the exception
     */
    public PageCRCException (String s) {
        super(s);
    }
}
