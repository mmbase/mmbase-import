/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.NodeManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;


/**
 * Straight-forward wrapper arround {@link org.mmbase.bridge.NodeManager#hasField}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class HasFieldTag extends NodeReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;
    protected Attribute name    = Attribute.NULL;
    private Attribute nodeManagerAtt = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }


    public void setNodetype(String t) throws JspTagException {
        nodeManagerAtt = getAttribute(t);
    }



    public int doStartTag() throws JspException {
        super.doStartTag();
        String nm = nodeManagerAtt.getString(this);
        NodeManager nodeManager = "".equals(nm) ? getNode().getNodeManager() : getCloudVar().getNodeManager(nm);
        if (nodeManager.hasField(name.getString(this)) != getInverse()) {
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
