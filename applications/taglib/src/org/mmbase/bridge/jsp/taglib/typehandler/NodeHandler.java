/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.storage.search.Constraint;
import org.mmbase.util.Encode;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Taglibs handler for Node typed fields.
 *
 * Currently this recognized node manager names for the guitype (produces dropdowns). If gui-type is not another builder,
 * this falls back to 'AbstractTypeHandler'.
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: NodeHandler.java,v 1.24 2003-12-21 13:27:51 michiel Exp $
 */

public class NodeHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(NodeHandler.class);

    /**
     * Constructor for NodeHandler.
     * @param context
     */
    public NodeHandler(FieldInfoTag tag) {
        super(tag);
    }

    protected class IgnoreCaseComparator implements Comparator {
        public int  compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.toUpperCase().compareTo(s2.toUpperCase());
        }
    }

    // checks whether a node passed refers to a relation builder
    // (a nodemanager used for creating relations)
    private boolean isRelationBuilder(Node n) throws JspTagException {
        try {
            NodeManager nm = tag.getCloud().getNodeManager(n.getStringValue("name"));
            // not a really good way to check, but it wil work for now
            // better is to use some property like
            //    NodeManager.getNodeClass()
            // which might then return Node, Relation, NodeManager, or RelationManager.
            return nm.hasField("snumber") && !nm.getName().equals("typerel");
        } catch (BridgeException e) {
            return false;
        }
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {

        log.info("hoi");
        // if the gui was a builder(maybe query in future) then show a drop down for this thing, listing the nodes..
        if(tag.getCloud().hasNodeManager(field.getGUIType())) {
            log.info("Generating list for " + field.getGUIType());
            StringBuffer buffer = new StringBuffer();
            // yippee! the gui was the same a an builder!
            buffer.append("<select name=\"" + prefix(field.getName()) + "\"");
            addExtraAttributes(buffer);
            buffer.append(">\n");
            // list all our nodes of the specified builder here...
            String value = "0";
            if (node != null) value = node.getStringValue(field.getName());

            log.info("node : " + node);

            // args for gui function
            List args = new ArrayList();
            args.add("");
            args.add(tag.getCloud().getLocale().getLanguage());
            // should actually be added
            //args.add(sessionName);
            //args.add(tag.pageContext.getResponse());

            NodeIterator nodes = tag.getCloud().getNodeManager(field.getGUIType()).getList(null, null, null).nodeIterator();

            log.info(" nodes " + tag.getCloud().getNodeManager(field.getGUIType()).getList(null, null, null));
            SortedMap sortedGUIs = new TreeMap(new IgnoreCaseComparator());

            // If this is the 'builder' field of the reldef builder, we need to filter
            // as we are only interested in insrel-derived builders.
            // Since there is no facility to filter this, we need to 'hard code' this.
            // Not so nice, but since it involves a core builder, this may work for now.
            // possibly in the future we need to define a new typehandler.
            boolean reldefFilter = field.getName().equals("builder") && field.getNodeManager().getName().equals("reldef");

            while(nodes.hasNext()) {
                Node n = nodes.nextNode();
                // if this is reldef, filter the 'builder' node
                if (!reldefFilter || isRelationBuilder(n)) {
                  sortedGUIs.put(n.getFunctionValue("gui", args).toString(), "" + n.getNumber());
                }
            }
            Iterator i = sortedGUIs.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry gui = (Map.Entry) i.next();

                // we have a match on the number!
                buffer.append("  <option ");
                if(gui.getValue().equals(value)) {
                    // this is the selected one!
                    buffer.append("selected=\"selected\"");
                } else if (search) {
                    String searchi =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()));
                    if (gui.getValue().equals(searchi)) {
                        buffer.append(" selected=\"selected\"");
                    }
                }
                buffer.append("value=\"" + gui.getValue() + "\">");
                buffer.append(Encode.encode("ESCAPE_XML", (String)  gui.getKey()));
                buffer.append("</option>\n");
            }
            buffer.append("</select>");
            if (search) {
                buffer.append("<input type=\"checkbox\" name=\"");
                String name = prefix(field.getName() + "_search");
                buffer.append(name);
                String searchi =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), name);
                buffer.append("\" ");
                if (searchi != null) {
                    buffer.append(" checked=\"checked\"");
                }
                buffer.append(" />\n");
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
        if (tag.getCloud().hasNodeManager(field.getGUIType())) {
            String id = prefix(fieldName + "_search");
            if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
                return null;
            } 
        }
        return super.whereHtmlInput(field);
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        if (tag.getCloud().hasNodeManager(field.getGUIType())) {
            String id = prefix(fieldName + "_search");
            if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
                return null;
            } 
        }                
        return super.whereHtmlInput(field, query);
    }

}
