/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.Node;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


/**
 * Straight-forward wrapper arround {@link org.mmbase.bridge.Node#isNull}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: IsNullTag.java,v 1.1 2009-03-03 20:23:09 michiel Exp $
 * @since MMBase-1.9.1
 */

public class IsNullTag extends FieldReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;
    protected Attribute name    = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b, true);
    }
    public void setName(String n) throws JspTagException {
        name = getAttribute(n, true);
    }

    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspException {
        Node n = getNode();
        String fieldName;
        if (name != Attribute.NULL) {
            fieldName = name.getString(this);
        } else {
            fieldName = getField().getName();
        }
        if (n.isNull(fieldName) != getInverse()) {
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
