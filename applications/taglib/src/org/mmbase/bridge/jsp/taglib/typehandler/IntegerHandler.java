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

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: IntegerHandler.java,v 1.21 2004-01-19 17:22:09 michiel Exp $
 */

public class IntegerHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(IntegerHandler.class);


    private DateHandler dateHandler;

    /**
     * Constructor for IntegerHandler.
     * @param tag
     */
    public IntegerHandler(FieldInfoTag tag) {
        super(tag);
        dateHandler = new IntegerDateHandler(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {

        String guiType = field.getGUIType();
        StringBuffer buffer = new StringBuffer();
        if (guiType.equals("boolean")) {
            boolean value = false;
            if (node != null) {
                value = node.getBooleanValue(field.getName());
            }
            buffer.append("<input type=\"checkbox\" name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\"");
            if (value) {
                buffer.append(" checked=\"checked\" ");
            }
            addExtraAttributes(buffer);
            buffer.append(" />\n");
            return buffer.toString();
        } else if (guiType.equals("types")) {
            log.warn("Guitype 'types' is deprecated. Use 'typedef' instead.");
            buffer.append("<select name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\"");
            addExtraAttributes(buffer);
            buffer.append(">\n");
            int value = 0;
            if (node != null) {
                value = node.getIntValue(field.getName());
            }
            // list all node managers.
            org.mmbase.bridge.Cloud cloud = tag.getCloud();
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
        } else if (guiType.equals("reldefs")) {
            log.warn("Guitype 'reldefs' is deprecated. Use 'reldef' instead.");
            buffer.append("<select name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\"");
            addExtraAttributes(buffer);
            buffer.append(">\n");
            int value = 0;
            if (node != null) {
                value = node.getIntValue(field.getName());
            }
            // list all roles
            org.mmbase.bridge.Cloud cloud = tag.getCloud();
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
        } else if (guiType.equals("eventtime")) {
            return dateHandler.htmlInput(node, field, search);
        } else if (guiType.equals("integer") || guiType.equals("")) {
            return super.htmlInput(node, field, search);
        } else {
            EnumHandler eh = new EnumHandler(tag, guiType);
            if (eh.isAvailable()) {
                return eh.htmlInput(node, field, search);
            }
        }

        return super.htmlInput(node, field, search);
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        String guiType = field.getGUIType();
        String fieldName = field.getName();
        long currentValue = node.getLongValue(fieldName);
        if (guiType.equals("boolean")) {
            String fieldValue =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
            fieldValue = tag.encode(fieldValue, field);
            if (fieldValue == null && currentValue != 0) {
                node.setIntValue(fieldName, 0);
                return true;
            } else if (currentValue != 1) {
                node.setIntValue(fieldName, 1);
                return true;
            }
            return false;
        } else  if (guiType.equals("eventtime")) {
            return dateHandler.useHtmlInput(node, field);
        } else if (guiType.equals("integer") || guiType.equals("")) {
            return super.useHtmlInput(node, field);
        } else {
            EnumHandler eh = new EnumHandler(tag, guiType);
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
        String guiType = field.getGUIType();
        String fieldName = field.getName();
        if (guiType.equals("eventtime")) {
            return dateHandler.whereHtmlInput(field);
        } else if ("types".equals(guiType) || "reldefs".equals(guiType)) {
            String id = prefix(fieldName + "_search");
            if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
                return null;
            } else {
                return super.whereHtmlInput(field);
            }
        } else if (guiType.equals("integer") || guiType.equals("")) {
            return super.whereHtmlInput(field);
        } else {
            EnumHandler eh = new EnumHandler(tag, guiType);
            if (eh.isAvailable()) {
                return eh.whereHtmlInput(field);
            }
        }
        return super.whereHtmlInput(field);
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String guiType = field.getGUIType();
        String fieldName = field.getName();
        if (guiType.equals("eventtime")) {
            return dateHandler.whereHtmlInput(field, query);
        } else if ("types".equals(guiType) || "reldefs".equals(guiType)) {
            String id = prefix(fieldName + "_search");
            if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
                return null;
            } else {
                return super.whereHtmlInput(field, query);
            }
        } else if (guiType.equals("integer") || guiType.equals("")) {
            return super.whereHtmlInput(field, query);
        } else {
            EnumHandler eh = new EnumHandler(tag, guiType);
            if (eh.isAvailable()) {
                return eh.whereHtmlInput(field, query);
            }
            return null;
        }

    }

    private class IntegerDateHandler extends DateHandler {
        public IntegerDateHandler(FieldInfoTag tag) {
            super(tag);
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
