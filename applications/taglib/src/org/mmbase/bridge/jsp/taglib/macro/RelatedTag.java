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
import org.mmbase.bridge.jsp.taglib.util.StringSplitter;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Shortcut for List where the start node is the parent node.
 */
public class RelatedTag extends ListTag {
    private static Logger log = Logging.getLoggerInstance(RelatedTag.class.getName());

    protected Node getBaseNode() throws JspTagException {
        if (nodes != Attribute.NULL && ! nodes.getString(this).equals("")) {
            return getCloud().getNode((String)StringSplitter.split(nodes.getString(this), ",").get(0));
        } else {
            return getNode();
        }
    }

    protected String getSearchNodes() throws JspTagException {
        return (nodes == Attribute.NULL || nodes.getString(this).equals("")) ? "" + getNode().getNumber() : nodes.getString(this);
    }

    /**
     * Obtain the list path. Adds the related basenode's type (extended with a '0' postfix to distinguis it from
     * other types in the path) to the front of the path.
     */
    protected String getPath() throws JspTagException {
        return getBaseNode().getNodeManager().getName() + "0," + path.getString(this);
    }

}
