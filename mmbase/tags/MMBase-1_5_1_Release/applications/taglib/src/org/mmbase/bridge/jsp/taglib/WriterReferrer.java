/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

/**
 *
 * @author Michiel Meeuwissen 
 **/

public interface WriterReferrer {
    /**
     * Which writer to use
     */
    public void setWriter(String t) throws JspTagException;

}
