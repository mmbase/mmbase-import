/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.util.Attribute;


/**
 * Whether current node has relations.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class HasRelationsTag extends NodeReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected final boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspException {
        super.doStartTag();
        if ((getNode().hasRelations()) != getInverse()) {
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
            return SKIP_BODY;
        } catch(java.io.IOException e){
            throw new JspTagException("IO Error: " + e.getMessage());
        }
    }


}
