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
import org.mmbase.bridge.jsp.taglib.util.StringSplitter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Shortcut for List where the start node is the parent node.
 *
 * @author Michiel Meeuwissen
 * @author Jacco de Groot
 * @author Pierre van Rooden
 * @version $Id: RelatedTag.java,v 1.16.2.2 2003-04-09 14:27:00 pierre Exp $
 */
public class RelatedTag extends ListTag {
    private static Logger log = Logging.getLoggerInstance(RelatedTag.class.getName());

    protected Node getBaseNode() throws JspTagException {
        if (nodesString != null && !nodesString.equals("")) {
            return getCloud().getNode((String)StringSplitter.split(nodesString, ",").get(0));
        } else {
            return getNode();
        }
    }

    protected String getSearchNodes() throws JspTagException {
        return (nodesString == null || nodesString.equals("")) ? "" + getNode().getNumber() : nodesString;
    }
    protected String getPath() throws JspTagException {
        return getBaseNode().getNodeManager().getName() + "0," + super.getPath();
    }
}
