/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.*;


/**
 * Adds an extra parameter to the parent URL tag.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: ParamTag.java,v 1.2.2.1 2004-07-05 17:19:59 michiel Exp $
 */

public class ParamTag extends ContextReferrerTag {
    
    private Attribute name  = Attribute.NULL;
    private Attribute value = Attribute.NULL;
    private ParamHandler paramHandler;
    private boolean handled;
           
    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }

    public int doStartTag() throws JspException {
        paramHandler = (ParamHandler) findParentTag(ParamHandler.class, null);
        handled = false;
        return super.doStartTag();
    }

    public int doAfterBody() throws JspException {
        if (value == Attribute.NULL) {
            if (bodyContent != null) {
                // the value is the body context.      
                helper.setValue(bodyContent.getString()); // to deal with 'vartype' casting
                paramHandler.addParameter(name.getString(this), helper.getValue());
                handled = true;
            }
        }
        return super.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        if (! handled && value != Attribute.NULL) {
            helper.setValue(value.getString(this)); // to deal with 'vartype' casting
            paramHandler.addParameter(name.getString(this), helper.getValue());
        }
        paramHandler = null;
        return super.doEndTag();
    }

}
