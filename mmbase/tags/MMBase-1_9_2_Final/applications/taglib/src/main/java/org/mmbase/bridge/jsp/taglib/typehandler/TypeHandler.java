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
 * @version $Id$
 */

public interface TypeHandler {
    /**
     * Produces an form input field for the given Node, and Field.
     * @param search if true, then a search field is produced.
     */
    String htmlInput(Node node, Field field, boolean search) throws JspTagException;

    /**
     * @since MMBase-1.9.1
     */
    String htmlInputId(Node node, Field field) throws JspTagException;


    /**
     * Produces a div, containing the error message for the current value, or the empty string if everything ok.
     * @param errors Whether to output the error messages (otherwises only invalidates form)
     * @since MMBase-1.8
     */
    String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException;

    /**
     * returns true if setValue happened.
     */

    boolean useHtmlInput(Node node, Field field) throws JspTagException;


    /**
     * @return Piece of SQL. null if no constraint.
     *
     */
    String whereHtmlInput(Field field) throws JspTagException;

    /**
     * @since MMBase-1.7
     */
    void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException;

    /**
     * @since MMBase-1.7
     */
    Constraint whereHtmlInput(Field field, Query query) throws JspTagException;

    /**
     * @since MMBase-1.8
     */
    void init();

}
