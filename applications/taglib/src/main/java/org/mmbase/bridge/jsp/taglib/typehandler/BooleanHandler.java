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
    @Override
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        EnumHandler enumh = getEnumHandler(node, field);
        if (enumh != null) {
            return enumh.htmlInput(node, field, search);
        } else {
            StringBuilder buffer = new StringBuilder();
            buffer.append("<input type=\"checkbox\" class=\"");
            buffer.append(getClasses(node, field, search));
            buffer.append("\" name=\"").append(prefix(field.getName())).append("\" ");
            buffer.append("id=\"").append(prefixID(field.getName())).append("\" ");
            Object value = getFieldValue(node, field, ! search);
            if (value == null) {
                log.debug("Found null");
            } else {
                log.debug("Found " + value.getClass() + " " + value);
            }
            if (Boolean.TRUE.equals(org.mmbase.util.Casting.toBoolean(value))) {
              buffer.append("checked=\"checked\"");
            }
            buffer.append(" />");

            // The following is needed because for a checkbox itself there is no distinction between 'not posted at all', and 'false'.
            String hidden = prefix(field.getName()) + "_check";
            buffer.append("<input type=\"hidden\" name=\"").append(hidden).append("\" value=\"yes\" />");
            return buffer.toString();
        }

    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        log.debug("using html-input");
        EnumHandler enumh = getEnumHandler(node, field);
        if (enumh != null) {
            return enumh.useHtmlInput(node, field);
        }
        return super.useHtmlInput(node, field);
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    @Override
    public String whereHtmlInput(Field field) throws JspTagException {
        EnumHandler enumh = getEnumHandler(null, field);
        if (enumh != null) {
            return enumh.whereHtmlInput(field);
        }
        return super.whereHtmlInput(field);
    }

    @Override
    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        EnumHandler enumh = getEnumHandler(null, field);
        if (enumh != null) {
            return enumh.whereHtmlInput(field, query);
        }
        return super.whereHtmlInput(field, query);
    }

    @Override
    protected Object cast(Object value, Node node, Field field) {
         if (value == null || "".equals(value)) return Boolean.FALSE;
         return  super.cast(value, node, field);
    }


    @Override
    public Object getFieldValue(Node node, Field field) throws JspTagException {
        Object v = super.getFieldValue(node, field);
        EnumHandler enumh = getEnumHandler(null, field);
        if (enumh == null) {
            // check-boxes.
          boolean posted = "yes".equals(tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()) + "_check"));
          if (posted) {
            v = "on".equals(v);
          }

        }
        return v;
    }

}
