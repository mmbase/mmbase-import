/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


/**
 * Adds an extra parameter to the parent URL tag.
 * 
 * @author Michiel Meeuwissen
 */
public class ParamTag extends ContextReferrerTag {
    
    private String name = null;
    private String value = null;
    private UrlTag urlTag;
    private boolean handled;
           
    public void setName(String n) throws JspTagException {
        name = getAttributeValue(n);
    }
    public void setValue(String v) throws JspTagException {
        value = getAttributeValue(v);
    }

    public int doStartTag() throws JspException {
        urlTag = (UrlTag) findParentTag("org.mmbase.bridge.jsp.taglib.pageflow.UrlTag", null);
        handled = false;
        return super.doStartTag();
    }

    public int doAfterBody() throws JspException {
        if (value == null) {
            // the value is the body context.      
            urlTag.addParameter(name, bodyContent.getString());
            handled = true;
        }
        return super.doAfterBody();
    }

    public int doEndTag() throws JspException {
        if (! handled && value != null) {
            urlTag.addParameter(name, value);
        }
        return SKIP_BODY;
    }

}
