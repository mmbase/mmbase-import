/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.*;

/**
 * Tags that can be used inside a list tag. 
 *
 * @author Michiel Meeuwissen 
 *
 * @version $Id: ListReferrerTag.java,v 1.11 2006-09-29 10:04:08 michiel Exp $ 
 */

public abstract class ListReferrerTag extends ContextReferrerTag  {

    protected Attribute  parentListId = Attribute.NULL;
    
    public void setList(String l) throws JspTagException {
        parentListId = getAttribute(l);
    }

    protected ListProvider getList() throws JspTagException {
        // find the parent list:
        return findParentTag(ListProvider.class, (String) parentListId.getValue(this));
    }

    /**
     * @since MMBase-1.8
     */
    protected LoopTag getLoopTag() throws JspTagException {
        // find the parent list:
        return findParentTag(LoopTag.class, (String) parentListId.getValue(this));
    }
 

}
