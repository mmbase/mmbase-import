/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Tags that can be used inside a list tag. 
 *
 * @author Michiel Meeuwissen 
 *
 */

public abstract class ListReferrerTag extends ContextReferrerTag  {
    
    private static Logger log = Logging.getLoggerInstance(ListReferrerTag.class.getName()); 
    private String  parentListId;
    
    public void setList(String l) throws JspTagException {
        parentListId = getAttributeValue(l);
    }

    protected ListProvider getList() throws JspTagException {
        // find the parent list:
        return (ListProvider)findParentTag("org.mmbase.bridge.jsp.taglib.ListProvider", parentListId);
    }

}
