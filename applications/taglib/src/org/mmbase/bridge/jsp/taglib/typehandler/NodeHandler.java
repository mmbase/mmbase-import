/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.util.Encode;
import java.util.*;

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

    protected class IgnoreCaseComparator implements Comparator {
        public int  compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.toUpperCase().compareTo(s2.toUpperCase());
        }                                    
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
            String value = "0";
            if (node != null) value = node.getStringValue(field.getName());


            // args for gui function
            List args = new Vector();
            args.add("");
            args.add(context.getCloud().getLocale().getLanguage());
            // should actually be added
            //args.add(sessionName);
            //args.add(context.pageContext.getResponse());
            

            NodeIterator nodes = context.getCloud().getNodeManager(field.getGUIType()).getList(null, null, null).nodeIterator();
            SortedMap sortedGUIs = new TreeMap(new IgnoreCaseComparator());
            while(nodes.hasNext()) {
                Node n = nodes.nextNode();
                sortedGUIs.put(n.getFunctionValue("gui", args).toString(), "" + n.getNumber());
            }
            Iterator i = sortedGUIs.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry gui = (Map.Entry) i.next();

                // we have a match on the number!
                buffer.append("  <option ");
                if(gui.getValue().equals(value)) {
                    // this is the selected one!
                    buffer.append("selected=\"selected\"");
                }
                buffer.append("value=\""+gui.getValue()+"\">");
                buffer.append(Encode.encode("ESCAPE_XML", (String)  gui.getKey()));
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
