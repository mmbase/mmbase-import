/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;
import java.util.Collection;
import org.mmbase.bridge.jsp.taglib.functions.Functions;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

/**
 * Remove an object from the Context.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: RemoveTag.java,v 1.10 2005-01-03 22:10:45 michiel Exp $ 
 */

public class RemoveTag extends ContextReferrerTag {

    private Attribute value = Attribute.NULL;

    /**
     * @since MMBase-1.8
     */
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }
    
    public int doEndTag() throws JspTagException {
        if (value.getString(this).equals("")) {
            getContextProvider().getContextContainer().unRegister(getReferid());
        } else {
            Collection col = (Collection) getContextProvider().getContextContainer().get(getReferid());
            Functions.remove(col, value.getValue(this));
        }
        return EVAL_PAGE;
    }
    
}
