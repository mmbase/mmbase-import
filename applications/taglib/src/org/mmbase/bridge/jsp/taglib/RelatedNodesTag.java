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
 */
public class RelatedNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class.getName());

    protected String typeString=null;

    private String parentNodeId = null;

    /**
     * Node makes it possible to refer to another node than the direct
     * ancestor.
     */
    public void setNode(String node){
        parentNodeId = node;
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        typeString = getAttributeValue(type);
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        NodeProvider nodeProvider =
          (NodeProvider)findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider", parentNodeId);
        Node node=nodeProvider.getNodeVar();
        NodeList nodes;
        if ((whereString!=null)|| (sortedString!=null)) {
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

