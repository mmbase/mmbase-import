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
 * @author Pierre van Rooden
 * @since  MMBase-1.6
 * @version $Id: BooleanHandler.java,v 1.2 2005-05-14 14:05:39 nico Exp $
 */

public class BooleanHandler extends AbstractTypeHandler {

    /**
     * Constructor for BooleanTypeHandler.
     */
    public BooleanHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        StringBuffer show =  new StringBuffer();
        if (search) {
            show.append("<select ");
            addExtraAttributes(show);
            show.append("name=\"").append(prefix(field.getName()+"_value")).append("\">");
            String searchParam = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()+"_value"));
            show.append("<option id=\"true\" ");
            if ("true".equals(searchParam)) {
                show.append("selected = \"selected\"");
            }
            show.append(">true</option"); // internationalize
            show.append("<option id=\"false\" ");
            if ("false".equals(searchParam)) {
                show.append("selected = \"selected\"");
            }
            show.append(">false</option></select>"); // internationalize
        }
        show.append("<input type =\"checkbox\" ");
        addExtraAttributes(show);
        show.append("name=\"").append(prefix(field.getName())).append("\" ").append("value=\"true\" ");
        if (node != null && node.getBooleanValue(field.getName())) {
            show.append("checked = \"checked\"");
        } else if (search) {
            String searchParam = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()));
            if (searchParam != null) {
                show.append("checked = \"checked\"");
            }
        }
        show.append(" />");
        return show.toString();
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName = field.getName();
        String fieldValue = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
        node.setBooleanValue(fieldName,  "true".equals(fieldValue));
        return true;
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String string = findString(field);
        if (string == null) return null;
        String value = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()+"_value"));
        if (value.equals("true")) {
            return "( [" + field.getName() + "] = 1 )";
        } else {
            return "( [" + field.getName() + "] = 0 )";
        }
    }

    /**
     * Adds search constraint to Query object.
     * @return null if nothing to be searched, the constraint if constraint added
     */

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String value = findString(field);
        if (value != null) {
            value = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()+"_value"));
            String fieldName = field.getName();
            if (query.getSteps().size() > 1) {
                fieldName = field.getNodeManager().getName()+"."+fieldName;
            }
            Constraint con = Queries.createConstraint(query, fieldName, getOperator(), Boolean.valueOf(value));
            Queries.addConstraint(query, con);
            return con;
        } else {
            return null;
        }
    }

}
