/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: IntegerHandler.java,v 1.36 2006-07-05 15:20:45 pierre Exp $
 */

public class IntegerHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(IntegerHandler.class);

    /**
     * Constructor for IntegerHandler.
     * @param tag
     */
    public IntegerHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        EnumHandler eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.htmlInput(node, field, search);
        }
        return super.htmlInput(node, field, search);
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        log.debug("using html-input");
        EnumHandler eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.useHtmlInput(node, field);
        }
        return super.useHtmlInput(node, field);
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        EnumHandler eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field);
        }
        return super.whereHtmlInput(field);
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        EnumHandler eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field, query);
        }
        return super.whereHtmlInput(field, query);
    }

    protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return "";
        return  super.cast(value, node, field);
    }

}
