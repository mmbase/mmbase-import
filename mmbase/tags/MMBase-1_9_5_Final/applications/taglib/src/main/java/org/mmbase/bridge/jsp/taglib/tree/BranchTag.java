/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.util.*;

/**
 * Sub-tag of tree container. Is itself a Query container.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id$
 * @todo   EXPERIMENTAL
 */
public class BranchTag extends ContextReferrerTag implements QueryContainerReferrer {

    private Attribute container   = Attribute.NULL;
    private Attribute nodeManager = Attribute.NULL;
    private Attribute role        = Attribute.NULL;
    private Attribute searchDir   = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setType(String n) throws JspTagException {
        nodeManager = getAttribute(n);
    }

    public void setRole(String r) throws JspTagException {
        role = getAttribute(r);
    }
    public void setSearchdir(String sd) throws JspTagException {
        searchDir = getAttribute(sd);
    }


    public int doStartTag() throws JspTagException {
        GrowingTreeList tree = (findParentTag(TreeContainerTag.class, (String) container.getValue(this), true)).getTree();
        NodeManager nm = tree.getCloud().getNodeManager(nodeManager == Attribute.NULL ? "object" : nodeManager.getString(this));
        tree.grow(nm, role.getString(this), searchDir.getString(this));
        return EVAL_BODY;
    }



}
