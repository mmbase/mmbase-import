/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
abstract public class TreeReferrerTag extends NodeReferrerTag {

    /**
     */
    protected Attribute parentTreeId = Attribute.NULL;

    public void setTree(String t) throws JspTagException {
        parentTreeId = getAttribute(t);
    }


    /**
     * @since MMBase-1.8.6
     */
    protected DepthProvider findDepthProvider() throws JspTagException {
        DepthProvider dp =  findParentTag(DepthProvider.class, (String) parentTreeId.getValue(this), false);
        if (dp != null) return dp;
        dp = (DepthProvider) pageContext.getAttribute(DepthProvider.KEY, PageContext.REQUEST_SCOPE);
        if (dp != null) return dp;
        throw new JspTagException("Could not find parent depth provider");
    }

    /**
     * This method tries to find an ancestor object of type NodeProvider
     * @return the NodeProvider if found else an exception.
     *
     */
    public TreeProvider findTreeProvider() throws JspTagException {
        TreeProvider dp =  findParentTag(TreeProvider.class, (String) parentTreeId.getValue(this), false);
        if (dp != null) return dp;
        dp = (TreeProvider) pageContext.getAttribute(TreeProvider.KEY, PageContext.REQUEST_SCOPE);
        if (dp != null) return dp;
        throw new JspTagException("Could not find parent depth provider");
    }


}
