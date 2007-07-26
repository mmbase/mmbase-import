/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.security.Action;

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if node may be changed.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MayTag.java,v 1.1 2007-07-26 22:22:22 michiel Exp $
 */

public class MayTag extends CloudReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;
    protected Attribute action = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setAction(String a) throws JspTagException {
        action = getAttribute(a);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        Action a = getCloudContext().getActionRepository().get(action.getString(this));
        if (a == null) throw new JspTagException("No such action '" + action.getString(this) + "'");
        if ((getCloudVar().may(a)) != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return SKIP_BODY;
    }
}
