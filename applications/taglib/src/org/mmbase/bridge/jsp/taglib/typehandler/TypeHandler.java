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
import org.mmbase.bridge.Query;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @since  MMBase-1.6
 * @version $Id: TypeHandler.java,v 1.3 2003-07-28 20:01:19 michiel Exp $
 */

public interface TypeHandler {

    public String htmlInput(Node node, Field field, boolean search) throws JspTagException;
    
    public String useHtmlInput(Node node, Field field) throws JspTagException;

    public String whereHtmlInput(Field field) throws JspTagException;

    /**
     * @since MMBase-1.7
     */
    public String whereHtmlInput(Field field, Query query) throws JspTagException;

}
