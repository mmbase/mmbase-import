/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.Constraint;
import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.6
 * @version $Id$
 */

public class BooleanHandler extends AbstractTypeHandler {
    private static final Logger log = Logging.getLoggerInstance(BooleanHandler.class);

    public BooleanHandler(FieldInfoTag tag) throws JspTagException {
        super(tag);
    }


    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        EnumHandler eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.htmlInput(node, field, search);
        } else {
            StringBuilder buffer = new StringBuilder();
            buffer.append("<input type=\"checkbox\" class=\"");
            buffer.append(getClasses(node, field));
            buffer.append("\" name=\"").append(prefix(field.getName())).append("\" ");
            buffer.append("id=\"").append(prefixID(field.getName())).append("\" />");

            return buffer.toString();
        }

    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override public boolean useHtmlInput(Node node, Field field) throws JspTagException {
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
    @Override public String whereHtmlInput(Field field) throws JspTagException {
        EnumHandler eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field);
        }
        return super.whereHtmlInput(field);
    }

    @Override public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        EnumHandler eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field, query);
        }
        return super.whereHtmlInput(field, query);
    }

    @Override protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return Boolean.FALSE;
        return  super.cast(value, node, field);
    }

}
