/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.util.Encode;

/**
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class NodeHandler extends IntegerHandler {

    /**
     * Constructor for NodeHandler.
     * @param context
     */
    public NodeHandler(FieldInfoTag context) {
        super(context);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {

        // if the gui was a builder(maybe query in future) then show a drop down for this thing, listing the nodes..
        if(context.getCloud().getNodeManagers().contains(field.getGUIType())) {
            StringBuffer buffer = new StringBuffer();
            // yippee! the gui was the same a an builder!
            buffer.append("<select name=\"" + prefix(field.getName()) + "\">\n");
            // list all our nodes of the specified builder here...
            int value = 0;
            if (node != null) value = node.getIntValue(field.getName());
            org.mmbase.bridge.NodeIterator nodes = context.getCloud().getNodeManager(field.getGUIType()).getList(null, null, null).nodeIterator();
            while(nodes.hasNext()) {
                org.mmbase.bridge.Node tmp = nodes.nextNode();
                // we have a match on the number!
                buffer.append("  <option ");
                if(tmp.getNumber() == value) {
                    // this is the selected one!
                    buffer.append("selected=\"selected\"");
                }
                buffer.append("value=\""+tmp.getNumber()+"\">");

                java.util.List args = new java.util.Vector();
                args.add("");
                args.add(context.getCloud().getLocale().getLanguage());

                // should actually be added
                //args.add(sessionName);
                //args.add(context.pageContext.getResponse());

                buffer.append(Encode.encode("ESCAPE_XML", tmp.getFunctionValue("gui", args).toString()));
                buffer.append("</option>\n");
            }
            buffer.append("</select>");
            if (search) {
                buffer.append("<input type=\"checkbox\" name=\"");
                buffer.append(prefix(field.getName() + "_search"));
                buffer.append("\" />\n");
            }
            return buffer.toString();
        }
        return super.htmlInput(node, field, search);
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String fieldName = field.getName();
        if (context.getCloud().getNodeManagers().contains(field.getGUIType())) {
            String id = prefix(fieldName + "_search");
            if (context.getContextTag().findAndRegister(id, id) == null) {
                return null;
            } else {
                String search = context.getContextTag().findAndRegisterString(prefix(fieldName));
                if (search == null || "".equals(search)) {
                    return null;
                }
                return "(" + fieldName + "=" + search + ")";
            }
        }
        return super.whereHtmlInput(field);
    }

}
