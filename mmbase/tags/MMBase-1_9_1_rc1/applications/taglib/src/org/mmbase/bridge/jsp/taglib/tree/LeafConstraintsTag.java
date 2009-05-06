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
 * Sub-tag of tree container. Is itself a Query container. It makes it possible to set the 'search constraints on a tree'.
 * Search constraints do not change the actual tree, but only the 'leaves'. So, it is kind of constraint only valid on the leaves.
 * This makes it possible to easily search through a tree of objects.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: LeafConstraintsTag.java,v 1.7 2007-02-10 16:49:27 nklasens Exp $
 */
public class LeafConstraintsTag extends ContextReferrerTag implements NodeQueryContainer, QueryContainerReferrer {

    private static final int ON_TEMPLATE = 1;
    private static final int ON_TRUNK    = 2;
    private Attribute container   = Attribute.NULL;
    private Attribute onAttribute = Attribute.NULL;
    private int       on          = 1;
    private NodeQuery trunkClone;

    protected GrowingTreeList tree;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setOn(String c) throws JspTagException {
        onAttribute = getAttribute(c);
    }

    public Query getQuery() {
        return getNodeQuery();
    }
    public NodeQuery getNodeQuery() {
        switch(on) {
        case ON_TRUNK:
            if (trunkClone == null) {
                trunkClone = (NodeQuery) tree.getLeafQuery().clone();
                trunkClone.setConstraint(null);
            }
            return trunkClone;
        case ON_TEMPLATE:
        default:         return tree.getLeafTemplate();
        }
    }

    public Cloud getCloudVar() throws JspTagException {
        return getQuery().getCloud();
    }


    public int doStartTag() throws JspTagException {

        String o = onAttribute.getString(this);
        if ("".equals(o)) {
            on = ON_TEMPLATE;
        }  else if ("template".equals(o)) {
            on = ON_TEMPLATE;
        } else if ("trunk".equals(o)) {
            on = ON_TRUNK;
        } else {
            throw new JspTagException("Unknown value for 'on' attribute '" + o + "' (known are 'template' and 'trunk')");
        }
        tree = (findParentTag(TreeContainerTag.class, (String) container.getValue(this), true)).getTree();
        return EVAL_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if(trunkClone != null) {
            tree.setLeafConstraint(trunkClone.getConstraint());
        }
        // for garbage collection:
        tree = null;
        trunkClone = null;
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
