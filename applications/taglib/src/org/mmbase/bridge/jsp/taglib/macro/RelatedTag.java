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
     * Override original ListTag method.
     */
    public void setNode(String node) throws JspTagException {
        throw new JspTagException("Node is not a supported attribute for the related tag");
    }

    public int doStartTag() throws JspTagException {
        NodeProvider nodeProvider;
        nodeProvider = (NodeProvider)findParentTag("org.mmbase.bridge.jsp"
                                                   + ".taglib.NodeProvider",
                                                   parentNodeId);
        Node node=nodeProvider.getNodeVar();
        nodesString=node.getStringValue("number");
        return super.doStartTag();
    }
}
