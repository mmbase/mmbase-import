/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: TreeReferrerTag.java,v 1.3 2007-02-10 16:49:27 nklasens Exp $
 */
abstract public class TreeReferrerTag extends NodeReferrerTag {

    /**
     */
    protected Attribute parentTreeId = Attribute.NULL;

    public void setTree(String t) throws JspTagException {
        parentTreeId = getAttribute(t);
    }



    /**
     * This method tries to find an ancestor object of type NodeProvider
     * @return the NodeProvider if found else an exception.
     *
     */	
    public TreeProvider findTreeProvider() throws JspTagException {        
        return findParentTag(TreeProvider.class, (String) parentTreeId.getValue(this));
    }



    

}

