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
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.util.logging.*;

/**
 * A very simple tag to check if node may be changed.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MayWriteTag extends NodeReferrerTag implements Condition {

    private static final Logger log = Logging.getLoggerInstance(MayWriteTag.class);

    protected Attribute inverse = Attribute.NULL;
    protected Attribute number = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setNumber(String n) throws JspTagException {
        number = getAttribute(n);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    protected Node getNodeToCheck() throws JspTagException {
        Node node;
        String n  = number.getString(this);
        if ("".equals(n)) {
            node = getNode();
        } else {
            node = getCloudVar().getNode(n);
        }
        return node;
    }

    public int doStartTag() throws JspException {
        initTag();
        try {
            if ((getNodeToCheck().mayWrite()) != getInverse()) {
                return EVAL_BODY;
            } else {
                return SKIP_BODY;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
