/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

/**
 * Remove an object from the Context.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: RemoveTag.java,v 1.8 2003-06-06 10:03:09 pierre Exp $ 
 */

public class RemoveTag extends ContextReferrerTag {
    
    public int doEndTag() throws JspTagException {
        getContextProvider().getContainer().unRegister(getReferid());
        return EVAL_PAGE;
    }
    
}
