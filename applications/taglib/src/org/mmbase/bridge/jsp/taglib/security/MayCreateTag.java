/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if node may be created
 *
 * @author Michiel Meeuwissen
 * @version $Id: MayCreateTag.java,v 1.6 2003-06-06 10:03:32 pierre Exp $
 */

public class MayCreateTag extends CloudReferrerTag implements Condition {

    protected Attribute  type = Attribute.NULL;
    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }

    public int doStartTag() throws JspTagException {
        if ((getCloud().getNodeManager(type.getString(this)).mayCreateNode()) != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        try{
            if(bodyContent != null) {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch(java.io.IOException e){
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

    public int doEndTag() throws JspTagException {
        return EVAL_PAGE;
    }
}
