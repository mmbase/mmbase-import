/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;
import javax.servlet.jsp.JspTagException;

/**
 * Function Container referrer can print function results
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainerReferrer.java,v 1.2 2003-08-11 15:26:36 michiel Exp $
 */
public interface FunctionContainerReferrer extends ContainerReferrer {

    /**
     * If it should not use parent container 
     */
    void setContainer(String c)  throws JspTagException;
}
