/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;

/**
 * @author Gerard van de Looi
 * @since  MMBase-1.6
 */
public interface TypeHandler {

    public String htmlInput(Node node, Field field, boolean search) throws JspTagException;
    
    public String useHtmlInput(Node node, Field field) throws JspTagException;

    public String whereHtmlInput(Field field) throws JspTagException;

}
