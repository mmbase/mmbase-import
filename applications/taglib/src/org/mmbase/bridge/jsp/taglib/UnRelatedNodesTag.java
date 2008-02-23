/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * Like listnodes tag, but is is also a node-referrer, and substracts the related nodes of the referred node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UnRelatedNodesTag.java,v 1.11 2008-02-23 16:00:44 michiel Exp $
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



    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult = doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        Node parentNode = getNode();

        if (parentNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }

        NodeQuery query = getQuery();

        NodeList relatedNodes = parentNode.getRelatedNodes(query.getNodeManager(), (String) role.getValue(this), (String) searchDir.getValue(this));

        NodesAndTrim result = getNodesAndTrim(query, relatedNodes.size() + 1); // query a bit more in case relatedNodes are subtracted


        if (excludeSelf.getBoolean(this, false)) {
            result.nodeList.remove(parentNode);
        }
        result.nodeList.removeAll(relatedNodes);

        return setReturnValues(result.nodeList, result.needsTrim);

    }


}

