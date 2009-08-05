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
import org.mmbase.framework.*;
import org.mmbase.security.Action;
import org.mmbase.util.functions.Parameters;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if a certain action may be performed by the current user..
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id: MayTag.java,v 1.6 2008-08-06 12:18:54 michiel Exp $
 */

public class MayTag extends CloudReferrerTag implements Condition {

    protected Attribute inverse   = Attribute.NULL;
    protected Attribute action    = Attribute.NULL;
    protected Attribute namespace = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setAction(String a) throws JspTagException {
        action = getAttribute(a);
    }

    public void setNamespace(String a) throws JspTagException {
        namespace = getAttribute(a);
    }
    public void setComponent(String a) throws JspTagException {
        namespace = getAttribute(a);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        String ns = namespace.getString(this);
        if ("".equals(ns)) ns = null;
        if (ns == null) {
            Block b = getCurrentBlock();
            if (b != null) {
                ns = b.getComponent().getName();
            }
        }
        Action a = getCloudContext().getActionRepository().get(ns, action.getString(this));
        if (a == null) throw new JspTagException("No action " + ns + " " + action + " found");
        Parameters params = a.createParameters();
        fillStandardParameters(params);
        if (a == null) throw new JspTagException("No such action '" + action.getString(this) + "'");
        if ((getCloudVar().may(a, params)) != getInverse()) {
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
