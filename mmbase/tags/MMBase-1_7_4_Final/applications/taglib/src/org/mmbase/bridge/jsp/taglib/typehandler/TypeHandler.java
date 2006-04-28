/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.storage.search.Constraint;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @since  MMBase-1.6
 * @version $Id: TypeHandler.java,v 1.8 2003-12-18 09:03:49 michiel Exp $
 */

public interface TypeHandler {

    public String htmlInput(Node node, Field field, boolean search) throws JspTagException;

    /**
     * returns true if setValue happened.
     */
    
    public boolean useHtmlInput(Node node, Field field) throws JspTagException;


    /**
     * @return Piece of SQL. null if no constraint.
     * 
     */
    public String whereHtmlInput(Field field) throws JspTagException;

    /**
     * @since MMBase-1.7
     */
    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException;

    /**
     * @since MMBase-1.7
     */
    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException;

}
