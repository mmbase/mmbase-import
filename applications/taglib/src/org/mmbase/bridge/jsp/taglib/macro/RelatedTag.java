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

    /**
     * Override original ListTag method.
     */
    public void setNodes(String nodes) throws JspTagException {
        throw new JspTagException("Nodes is not a supported attribute for the related tag");
    }

    /**
     * Node makes it possible to refer to another node than the direct
     * ancestor.
     */
    public void setNode(String node){
        parentNodeId = node;
    }

    public int doStartTag() throws JspTagException {
        NodeProvider nodeProvider =
          (NodeProvider)findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider", parentNodeId);
        Node node=nodeProvider.getNodeVar();
        nodesString=node.getStringValue("number");
        String nodeType=node.getNodeManager().getName();
        // adapt the path to include the (needed) starting ndoemanager name
        pathString= nodeType+","+pathString;
        return super.doStartTag();
    }
}
