/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.containers.ListNodesContainerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like listnodes tag, but is is also a node-referrer, and substracts the related nodes of the referred node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UnRelatedNodesTag.java,v 1.6 2003-11-07 10:40:31 michiel Exp $
 * @since MMBase-1.7
 */

public class UnRelatedNodesTag extends ListNodesTag {
    private static final Logger log = Logging.getLoggerInstance(UnRelatedNodesTag.class);

    protected Attribute role        = Attribute.NULL;
    protected Attribute searchDir   = Attribute.NULL;
    protected Attribute excludeSelf = Attribute.NULL;


    public void setRole(String role) throws JspTagException {
        this.role = getAttribute(role);
    }

    public void setSearchdir(String search) throws JspTagException {
        searchDir = getAttribute(search);
    }

    public void setExcludeself(String e) throws JspTagException {
        excludeSelf = getAttribute(e);
    }

    protected NodeList getNodes() throws JspTagException {
        // obtain a reference to the node through a parent tag
        Node parentNode = getNode();
        if (parentNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }

        NodeList nodes = super.getNodes();

        NodeList relatedNodes = parentNode.getRelatedNodes(getNodeManager(), (String) role.getValue(this), (String) searchDir.getValue(this));

        if (excludeSelf.getBoolean(this, false)) {
            nodes.remove(parentNode);
        }

        nodes.removeAll(relatedNodes);

        return nodes;
    }

}

