/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Tags that can be used inside a list tag. 
 *
 * @author Michiel Meeuwissen 
 *
 * @version $Id: ListReferrerTag.java,v 1.8 2003-08-27 21:33:34 michiel Exp $ 
 */

public abstract class ListReferrerTag extends ContextReferrerTag  {
    
    private static final Logger log = Logging.getLoggerInstance(ListReferrerTag.class); 

    protected Attribute  parentListId = Attribute.NULL;
    
    public void setList(String l) throws JspTagException {
        parentListId = getAttribute(l);
    }

    protected ListProvider getList() throws JspTagException {
        // find the parent list:
        return (ListProvider) findParentTag(ListProvider.class, (String) parentListId.getValue(this));
    }

}
