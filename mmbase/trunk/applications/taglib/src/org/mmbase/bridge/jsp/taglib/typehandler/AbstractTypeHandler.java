/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.typehandler;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.storage.search.*;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: AbstractTypeHandler.java,v 1.22 2003-12-09 21:18:19 michiel Exp $
 */

public abstract class AbstractTypeHandler implements TypeHandler {

    protected FieldInfoTag tag;

    /**
     * Constructor for AbstractTypeHandler.
     */
    public AbstractTypeHandler(FieldInfoTag tag) {
        super();
        this.tag = tag;
    }


    protected StringBuffer addExtraAttributes(StringBuffer buf) throws JspTagException {
        String options = tag.getOptions();
        if (options != null && options.startsWith("extra:")) {
            buf.append(" " + options.substring(6) + " ");
        }
        return buf;
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        // default implementation.
        StringBuffer show =  new StringBuffer("<input type =\"text\" class=\"small\" size=\"80\" ");
        addExtraAttributes(show);
        show.append("name=\"").append(prefix(field.getName())).append("\" ").append("value=\"");
        if (node != null) {
            show.append(node.getStringValue(field.getName()));
        } else if (search) {
            String searchParam = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()));
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
        String fieldValue = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
        if (fieldValue == null) {

        } else {
            if (! fieldValue.equals(node.getValue(fieldName))) {
                node.setValue(fieldName,  fieldValue);
            }
        }
        return "";
    }



    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String string = findString(field);
        if (string == null) return null;
        return "( [" + field.getName() + "] =" + getSearchValue(string) + ")";
    }

    /**
     * The operator to be used by whereHtmlInput(field, query)
     * @since MMBase-1.7
     */
    protected int getOperator() {
        return FieldCompareConstraint.EQUAL;
    }
    /**
     * Converts the value to the actual value to be searched. (mainly targeted at StringHandler).
     * @since MMBase-1.7
     */
    protected String getSearchValue(String string) {
        return string;
    }

    /**
     * @since MMBase-1.7
     */
    final protected String findString(Field field) throws JspTagException {
        String fieldName = field.getName();
        String search = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
        if (search == null || "".equals(search)) {
            return null;
        }
        return search;
    }


    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException  {
        handler.addParameter(prefix(field.getName()), findString(field));
    }


    /**
     * Adds search constraint to Query object.
     * @return null if nothing to be searched, the constraint if constraint added
     */

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String value = findString(field);
        if (value != null) {
            Constraint con = Queries.createConstraint(query, field.getName(), getOperator(), getSearchValue(findString(field)));
            return Queries.addConstraint(query, con);
        } else {
            return null;
        }
    }

    /**
     * Puts a prefix before a name. This is used in htmlInput and
     * useHtmlInput, they need it to get a reasonably unique value for
     * the name attribute of form elements.
     *
     */
    protected String prefix(String s) throws JspTagException {
        String id = tag.findFieldProvider().getId();
        if (id == null) id = "";
        if (id.equals("") ) {
            return "_" + s;
        } else {
            return id + "_" + s;
        }
    }


}
