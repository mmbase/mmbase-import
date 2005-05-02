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
 * @version $Id: ParamTag.java,v 1.5 2005-05-02 11:50:09 michiel Exp $
 */

public class ParamTag extends ContextReferrerTag {
    
    private Attribute name    = Attribute.NULL;
    private Attribute value   = Attribute.NULL;
    private Attribute referid = Attribute.NULL;
    private ParamHandler paramHandler;
    private boolean handled;
           
    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }
    /**
     * @since MMBase-1.8
     */
    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    public int doStartTag() throws JspException {
        paramHandler = (ParamHandler) findParentTag(ParamHandler.class, null);
        handled = false;
        return super.doStartTag();
    }

    public int doAfterBody() throws JspException {
        if (value == Attribute.NULL && referid == Attribute.NULL) {
            if (bodyContent != null) {
                // the value is the body context.      
                helper.setValue(bodyContent.getString()); // to deal with 'vartype' casting
                paramHandler.addParameter(name.getString(this), helper.getValue());
                helper.doAfterBody();
                handled = true;
            }
        }
        return super.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        if (! handled) {
            if (value != Attribute.NULL) {
                if (referid != Attribute.NULL) throw new JspTagException("Must specify either 'value' or 'referid', not both");
                helper.setValue(value.getString(this)); // to deal with 'vartype' casting
                paramHandler.addParameter(name.getString(this), helper.getValue());
                helper.doAfterBody();

            } else if (referid != Attribute.NULL) {
                paramHandler.addParameter(name.getString(this), getObject(referid.getString(this)));
            }
        }
        paramHandler = null;
        return super.doEndTag();
    }

}
