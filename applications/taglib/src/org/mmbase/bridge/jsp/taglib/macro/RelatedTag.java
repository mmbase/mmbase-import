/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.macro;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;

import org.mmbase.bridge.jsp.taglib.ListTag;
import org.mmbase.bridge.jsp.taglib.NodeProvider;

/**
 * Shortcut for List where the start node is the parent node.
 */
public class RelatedTag extends ListTag {

    private String parentNodeId = null;
    private String relatedNodesString=null;
    private String relatedPathString=null;

    /**
     * Override original ListTag method.
     */
    public void setNodes(String nodes) throws JspTagException {
        relatedNodesString=parseNodes(nodes);
    }

    /**
     * Override original ListTag method.
     */
    public void setNode(String node) {
        parentNodeId=node;
    }

    /**
     * Override original ListTag method.
     * @param type a comma separated list of nodeManagers
     */
    public void setPath(String path) throws JspTagException {
        this.relatedPathString = getAttributeValue(path);
    }

    public int doStartTag() throws JspTagException {
        NodeProvider nodeProvider;
        nodeProvider = (NodeProvider)findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider",
                                                   parentNodeId);
        Node node=nodeProvider.getNodeVar();
        nodesString=node.getStringValue("number");
        if (relatedNodesString!=null) {
           nodesString+=","+relatedNodesString;
        }
        String nodeType=node.getNodeManager().getName();
        // adapt the path to include the (needed) starting nodemanager name
        // The nodemanager will be referrable as nodemanager0
        pathString= nodeType+"0,"+relatedPathString;
        return super.doStartTag();
    }
}
