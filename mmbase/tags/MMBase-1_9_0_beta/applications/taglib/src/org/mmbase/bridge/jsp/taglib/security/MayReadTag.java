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

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if node may be viewed
 *
 * @author Michiel Meeuwissen
 * @version $Id: MayReadTag.java,v 1.2 2007-07-18 07:50:47 michiel Exp $
 * @since MMBase-1.8.1
 */

public class MayReadTag extends CloudReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;
    protected Attribute referid = Attribute.NULL;
    protected Attribute number = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setNumber(String n) throws JspTagException {
        number = getAttribute(n);
    }
    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        Cloud cloud = getCloudVar();
        String n   = number.getString(this);
        String rid = referid.getString(this);
        String nodeNumber;
        if ("".equals(n)) {
            nodeNumber  = getString(rid);
        } else {
            nodeNumber = n;
            if (rid.length() != 0) {
                throw new JspTagException("Cannot specify both 'number' and 'referid");
            }
        }
        if (cloud.mayRead(nodeNumber) != getInverse()) {
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
