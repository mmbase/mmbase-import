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

/**
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class IntegerHandler extends AbstractTypeHandler {

    /**
     * Constructor for IntegerHandler.
     * @param context
     */
    public IntegerHandler(FieldInfoTag context) {
        super(context);
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
            Calendar cal = Calendar.getInstance();
            if (node !=null) {
                if (node.getIntValue(field.getName()) != -1) {
                    cal.setTime(new Date(((long)node.getIntValue(field.getName()))*1000));
                }
            }
            buffer.append("<input type=\"hidden\" name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\" value=\"");
            buffer.append(cal.getTime().getTime()/1000);
            buffer.append("\" />");
            // give also present value, this makes it possible to see if user changed this field.

            String options = ((FieldInfoTag)context).getOptions();
            if (options == null || options.indexOf("date") > -1) {
                buffer.append("<select name=\"" + prefix(field.getName() + "_day") + "\">\n");
                for (int i = 1; i <= 31; i++) {
                    if (cal.get(Calendar.DAY_OF_MONTH) == i) {
                        buffer.append("  <option selected=\"selected\">");
                        buffer.append(i);
                        buffer.append("</option>\n");
                    } else {
                        buffer.append("  <option>");
                        buffer.append(i);
                        buffer.append("</option>\n");
                    }
                }
                buffer.append("</select>-");
                buffer.append("<select name=\"");
                buffer.append(prefix(field.getName() + "_month"));
                buffer.append("\">\n");
                for (int i = 1; i <= 12; i++) {
                    if (cal.get(Calendar.MONTH) == (i - 1)) {
                        buffer.append("  <option selected=\"selected\">" + i + "</option>\n");
                    } else {
                        buffer.append("  <option>" + i + "</option>\n");
                    }
                }
                buffer.append("</select>-");
                buffer.append("<input type =\"text\" size=\"5\" name=\"");
                buffer.append(prefix(field.getName() + "_year"));
                buffer.append("\" ");
                buffer.append("value=\"");
                buffer.append(cal.get(Calendar.YEAR));
                buffer.append("\" />");
            } else {
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_day") + "\" value=\"" + cal.get(Calendar.DAY_OF_MONTH) + "\" />");
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_month") + "\" value=\"" + cal.get(Calendar.MONTH) + "\" />");
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_year") + "\" value=\"" + cal.get(Calendar.YEAR) + "\" />");
            } 
            if (options == null || options.indexOf("time") > -1) {
                buffer.append("&nbsp;&nbsp;<select name=\"" + prefix(field.getName() + "_hour") + "\">\n");
                for (int i = 0; i <= 23; i++) {
                    if (cal.get(Calendar.HOUR_OF_DAY) == i) {
                        buffer.append("  <option selected=\"selected\">");
                    } else {
                        buffer.append("  <option>");
                    }
                    if (i<10) buffer.append("0");
                    buffer.append(i + "</option>\n");
                }
                buffer.append("</select> h :");
            
                buffer.append("<select name=\"" + prefix(field.getName() + "_minute") + "\">\n");
                for (int i = 0; i <= 59; i++) {
                    if (cal.get(Calendar.MINUTE) == i) {
                        buffer.append("  <option selected=\"selected\">");
                    } else {
                        buffer.append("  <option>");
                    }
                    if (i< 10) buffer.append("0");
                    buffer.append(i + "</option>\n");
                }
                buffer.append("</select> m :");
                buffer.append("<select name=\"" + prefix(field.getName() + "_second") + "\">\n");
                for (int i = 0; i <= 59; i++) {
                    if (cal.get(Calendar.SECOND) == i) {
                        buffer.append("  <option selected=\"selected\">");
                    } else {
                        buffer.append("  <option>");
                    }
                    if (i< 10) buffer.append("0");
                    buffer.append(i + "</option>\n");
                }
                buffer.append("</select> s");
            } else {
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_hour") + "\" value=\"" + cal.get(Calendar.HOUR_OF_DAY) + "\" />");
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_minute") + "\" value=\"" + cal.get(Calendar.MINUTE) + "\" />");
                buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_second") + "\" value=\"" + cal.get(Calendar.SECOND) + "\" />");
            }
            return buffer.toString();
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
            Calendar cal = Calendar.getInstance();
            try {
                Integer day    = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_day")));
                Integer month  = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_month")));
                Integer year   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_year")));
                Integer hour   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_hour")));
                Integer minute = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_minute")));
                Integer second = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_second")));
                int y = year.intValue();
                if (y < 1902 || y > 2037) {
                    throw new JspTagException("Year must be between 1901 and 2038 (now " + y + ")");
                }
                cal.set(y, month.intValue() - 1, day.intValue(),
                    hour.intValue(), minute.intValue(), second.intValue());
                node.setIntValue(fieldName, (int) (cal.getTime().getTime() / 1000));
            } catch (java.lang.NumberFormatException e) {
                throw new JspTagException("Not a valid number (" + e.toString() + ") in field " + fieldName);
            }
            return "";
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

        StringBuffer buffer = new StringBuffer();
        String guitype = field.getGUIType();
        String fieldName = field.getName();
        if (guitype.equals("eventtime")) {
            Calendar cal = Calendar.getInstance();
            try {
                Integer day    = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_day")));
                Integer month  = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_month")));
                Integer year   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_year")));
                Integer hour   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_hour")));
                Integer minute = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_minute")));
                Integer second = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_second")));
                int y = year.intValue();
                if (y < 1902 || y > 2037) {
                    throw new JspTagException("Year must be between 1901 and 2038 (now " + y + ")");
                }
                cal.set(y, month.intValue() - 1, day.intValue(),
                        hour.intValue(), minute.intValue(), second.intValue());
            } catch (java.lang.NumberFormatException e) {
                throw new JspTagException("Not a valid number (" + e.toString() + ")");
            }
            // check if changed:
            if (! context.getContextTag().findAndRegisterString(prefix(fieldName)).equals("" + cal.getTime().getTime() /1000)) {
                buffer.append("(" + fieldName + ">" + (cal.getTime().getTime() / 1000) + ")");
            } else {
                return null;
            }
        } else if ("types".equals(guitype) || "reldefs".equals(guitype)) {
            String id = prefix(fieldName + "_search");
            if (context.getContextTag().findAndRegister(id, id) == null) {
                return null;
            } else {
                return super.whereHtmlInput(field);
            }
        }  else if (guitype.equals("integer")) {
            return super.whereHtmlInput(field);
        } else {
            EnumHandler eh = new EnumHandler(context, field.getGUIType());
            if (eh.isAvailable()) {
                return eh.whereHtmlInput(field);
            }
        }

        return buffer.toString();
    }        

}
