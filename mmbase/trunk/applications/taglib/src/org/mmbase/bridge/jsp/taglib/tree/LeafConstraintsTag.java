/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.util.*;

/**
 * Sub-tag of tree container. Is itself a Query container. It makes it possible to set the 'search constraints on a tree'.
 * Search constraints do not change the actual tree, but only the 'leaves'. So, it is kind of constraint only valid on the leaves.
 * This makes it possible to easily search through a tree of objects.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: LeafConstraintsTag.java,v 1.3 2005-11-23 10:24:13 michiel Exp $
 */
public class LeafConstraintsTag extends ContextReferrerTag implements NodeQueryContainer, QueryContainerReferrer { 

    private Attribute container = Attribute.NULL;

    protected GrowingTreeList tree;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public Query getQuery() {
        return tree.getLeafTemplate();
    }
    public NodeQuery getNodeQuery() {
        return (NodeQuery) getQuery();
    }
    public Cloud getCloudVar() throws JspTagException {
        return getQuery().getCloud();
    }

    public int doStartTag() throws JspTagException {
        TreeContainerTag c = (TreeContainerTag) findParentTag(TreeContainerTag.class, (String) container.getValue(this), false);
        tree = c.getTree();
        return EVAL_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (java.io.IOException ioe){
                throw new JspTagException(ioe.toString());
            } 
        }
        return SKIP_BODY;        
    }

}
