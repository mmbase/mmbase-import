/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import java.util.Calendar;
import java.util.Date;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class IntegerHandler extends AbstractTypeHandler {

    private static Logger log = Logging.getLoggerInstance(IntegerHandler.class.getName());


    private DateHandler dateHandler;

    /**
     * Constructor for IntegerHandler.
     * @param context
     */
    public IntegerHandler(FieldInfoTag context) {
        super(context);
        dateHandler = new IntegerDateHandler(context);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {

        StringBuffer buffer = new StringBuffer();
        if (field.getGUIType().equals("boolean")) {
            boolean value = false;
            if (node != null) {
                value = node.getBooleanValue(field.getName());
            }
            buffer.append("<input type=\"checkbox\" name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\"");
            if (value) {
                buffer.append(" checked");
            }
            buffer.append(" />\n");
            return buffer.toString();
        } else if (field.getGUIType().equals("types")) {
            log.warn("Guitype 'types' is deprecated. Use 'typedef' instead.");
            buffer.append("<select name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\">\n");
            int value = 0;
            if (node != null) {
                value = node.getIntValue(field.getName());
            }
            // list all node managers.
            org.mmbase.bridge.Cloud cloud = context.getCloud();
            org.mmbase.bridge.NodeManager typedef = cloud.getNodeManager("typedef");
            org.mmbase.bridge.NodeIterator i = typedef.getList(null, "name", null).nodeIterator();
            //java.util.Collections.sort(l);
            while (i.hasNext()) {
                Node nmNode = i.nextNode();
                try {
                    org.mmbase.bridge.NodeManager nm = cloud.getNodeManager(nmNode.getStringValue("name"));
                    int listvalue = nmNode.getNumber();
                    buffer.append("<option value=\"");
                    buffer.append(listvalue);
                    buffer.append("\"");
                    if (node != null) {
                        if (listvalue == value) {
                            buffer.append(" selected=\"selected\"");
                        }
                    }
                    buffer.append(">");
                    buffer.append(nm.getGUIName());
                    buffer.append("</option>\n");
                } catch (org.mmbase.bridge.BridgeException e) {
                    // ignore possible errors.
                }
            }
            buffer.append("</select>");
            if (search) {
                buffer.append("<input type=\"checkbox\" name=\"");
                buffer.append(prefix(field.getName() + "_search"));
                buffer.append("\" />\n");
            }
            return buffer.toString();
        } else if (field.getGUIType().equals("reldefs")) {
            log.warn("Guitype 'reldefs' is deprecated. Use 'reldef' instead.");
            buffer.append("<select name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\">\n");
            int value = 0;
            if (node != null) {
                value = node.getIntValue(field.getName());
            }
            // list all roles
            org.mmbase.bridge.Cloud cloud = context.getCloud();
            org.mmbase.bridge.NodeManager typedef = cloud.getNodeManager("reldef");
            org.mmbase.bridge.NodeIterator i = typedef.getList(null, "sguiname,dguiname", null).nodeIterator();

            //java.util.Collections.sort(l);
            while (i.hasNext()) {
                Node reldef = i.nextNode();
                int listvalue = reldef.getNumber();
                buffer.append("<option value=\"");
                buffer.append(reldef.getNumber());
                buffer.append("\"");
                if (node != null) {
                    if (listvalue == value) {
                        buffer.append(" selected=\"selected\"");
                    }
                }
                buffer.append(">");
                buffer.append(reldef.getStringValue("sguiname"));
                buffer.append("/");
                buffer.append(reldef.getStringValue("dguiname"));
                buffer.append("</option>\n");
            }
            buffer.append("</select>");
            if (search) {
                buffer.append("<input type=\"checkbox\" name=\"");
                buffer.append(prefix(field.getName() + "_search"));
                buffer.append("\" />\n");
            }
            return buffer.toString();
        } else if (field.getGUIType().equals("eventtime")) {
            return dateHandler.htmlInput(node, field, search);
        } else if (field.getGUIType().equals("integer")) {
            return super.htmlInput(node, field, search);
        } else {
            EnumHandler eh = new EnumHandler(context, field.getGUIType());
            if (eh.isAvailable()) {
                return eh.htmlInput(node, field, search);
            }
        }

        return super.htmlInput(node, field, search);
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public String useHtmlInput(Node node, Field field) throws JspTagException {

        String fieldName = field.getName();
        if (field.getGUIType().equals("boolean")) {
            String fieldValue = context.getContextTag().findAndRegisterString(prefix(fieldName));
            fieldValue = context.encode(fieldValue, field);
            if (fieldValue == null) {
                node.setIntValue(fieldName, 0);
            } else {
                node.setIntValue(fieldName, 1);
            }
            return "";
        } else  if (field.getGUIType().equals("eventtime")) {
            return dateHandler.useHtmlInput(node, field);
        } else if (field.getGUIType().equals("integer")) {
            return super.useHtmlInput(node, field);
        } else {
            EnumHandler eh = new EnumHandler(context, field.getGUIType());
            if (eh.isAvailable()) {
                return eh.useHtmlInput(node, field);
            }
        }

        return super.useHtmlInput(node, field);
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String guitype = field.getGUIType();
        String fieldName = field.getName();
        if (guitype.equals("eventtime")) {
            return dateHandler.whereHtmlInput(field);
        } else if ("types".equals(guitype) || "reldefs".equals(guitype)) {
            String id = prefix(fieldName + "_search");
            if (context.getContextTag().findAndRegister(id, id) == null) {
                return null;
            } else {
                return super.whereHtmlInput(field);
            }
        } else if (guitype.equals("integer")) {
            return super.whereHtmlInput(field);
        } else {
            EnumHandler eh = new EnumHandler(context, field.getGUIType());
            if (eh.isAvailable()) {
                return eh.whereHtmlInput(field);
            }
        }
        return null;
    }

    private class IntegerDateHandler extends DateHandler {
            public IntegerDateHandler(FieldInfoTag context) {
                super(context);
            }

            protected int checkYear(Integer year, String fieldName) throws JspTagException {
                int y = super.checkYear(year, fieldName);
                if (y < 1902 || y > 2037) {
                    throw new JspTagException("Year of field '" + fieldName + "' must be between 1901 and 2038 (now " + y + ")");
                }
                return y;
            }
    }

}
