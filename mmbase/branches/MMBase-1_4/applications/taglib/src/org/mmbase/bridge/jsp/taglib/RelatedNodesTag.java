/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * RelatedNodesTag, provides functionality for listing single related nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 */
public class RelatedNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class.getName());
    private String parentNodeId = null;
    private String number    = null;
    protected String typeString = null;

    public void setNode(String node) throws JspTagException {
        parentNodeId=node;
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        typeString = getAttributeValue(type);
    }

    public void setNumber(String number) throws JspTagException {
        this.number = getAttributeValue(number);
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {

        // obtain a reference to the node through a parent tag
        Node node = null;
        if ((number!=null) && !number.equals("")) {
            node=getCloudProviderVar().getNode(number);
        } else {
            String classname = "org.mmbase.bridge.jsp.taglib.NodeProvider";
            NodeProvider nodeProvider = (NodeProvider)findParentTag(classname,parentNodeId);
            node=nodeProvider.getNodeVar();
        }

        NodeList nodes;
        if ((whereString != null && !whereString.equals(""))
                || (sortedString != null && !sortedString.equals(""))) {
            NodeManager manager=getCloudProviderVar().getNodeManager(typeString);
            NodeList initialnodes = node.getRelatedNodes(typeString);
            String where=null;
            for (NodeIterator i = initialnodes.nodeIterator(); i.hasNext(); ) {
                Node n=i.nextNode();
                if (where==null) {
                    where=""+n.getNumber();
                } else {
                    where+=","+n.getNumber();
                }
            }
            if (where==null) { // empty list, so use that one.
                nodes=initialnodes;
            } else {
                where="number in ("+where+")";
                if (whereString!=null) where="("+whereString+") AND "+where;
                nodes = manager.getList(where,sortedString,directionString);
            }
        } else {
            nodes = node.getRelatedNodes(typeString);
        }
        return setReturnValues(nodes,true);
    }

}

