/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

/**
 * Tags that can be used inside a list tag. 
 *
 * @author Michiel Meeuwissen 
 *
 * @version $Id: ListReferrerTag.java,v 1.9 2005-01-30 16:46:35 nico Exp $ 
 */

public abstract class ListReferrerTag extends ContextReferrerTag  {

    protected Attribute  parentListId = Attribute.NULL;
    
    public void setList(String l) throws JspTagException {
        parentListId = getAttribute(l);
    }

    protected ListProvider getList() throws JspTagException {
        // find the parent list:
        return (ListProvider) findParentTag(ListProvider.class, (String) parentListId.getValue(this));
    }

}
