/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.typehandler;
import org.mmbase.bridge.jsp.taglib.containers.NodeListConstraintTag;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: AbstractTypeHandler.java,v 1.8 2003-08-01 14:13:23 michiel Exp $
 */

public abstract class AbstractTypeHandler implements TypeHandler {

    protected FieldInfoTag context;

    /**
     * Constructor for AbstractTypeHandler.
     */
    public AbstractTypeHandler(FieldInfoTag context) {
        super();
        this.context = context;
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        // default implementation.
        StringBuffer show =  new StringBuffer("<input type =\"text\" class=\"small\" size=\"80\" name=\"").append(prefix(field.getName())).append("\" ").append("value=\"");
        if (node != null) {
            show.append(node.getStringValue(field.getName()));
        } else if (search) {
            String searchParam = context.getContextProvider().getContainer().findAndRegisterString(context.getPageContext(), prefix(field.getName()), false);
            show.append((searchParam == null ? "" : searchParam));
        }
        show.append("\" />");
        return show.toString();
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public String useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName = field.getName();
        String fieldValue = context.getContextProvider().getContainer().findAndRegisterString(context.getPageContext(), prefix(fieldName), false);
        if (fieldValue == null) {

        } else {
            node.setValue(fieldName,  fieldValue);
        }
        return "";
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String fieldName = field.getName();
        String search = context.getContextProvider().getContainer().findAndRegisterString(context.getPageContext(), prefix(fieldName), false);
        if (search == null || "".equals(search)) {
            return null;
        }
        return "( [" + fieldName + "] =" + search + ")";
    }

    protected int getOperator() {
        return FieldCompareConstraint.EQUAL;
    }

    /**
     * Adds search constraint to Query object.
     * @return null if nothing to be searched, "" if constraint added
     */

    public String whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        String search = context.getContextProvider().getContainer().findAndRegisterString(context.getPageContext(), prefix(fieldName), false);
        if (search == null || "".equals(search)) {
            return null;
        }
        NodeListConstraintTag.addConstraint(query, fieldName, getOperator(), search);
        return "";
    }
    /**
     * Puts a prefix before a name. This is used in htmlInput and
     * useHtmlInput, they need it to get a reasonably unique value for
     * the name attribute of form elements.
     *
     */
    protected String prefix(String s) throws JspTagException {
        String id = context.findFieldProvider().getId();
        if (id == null) id = "";
        if (id.equals("") ) {
            return "__" + s;
        } else {
            return "__" + id + "_" + s;
        }
    }


}
