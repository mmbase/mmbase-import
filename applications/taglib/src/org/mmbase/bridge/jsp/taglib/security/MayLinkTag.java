/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import org.mmbase.bridge.jsp.taglib.ConditionTag;
import javax.servlet.jsp.JspTagException;


/**
* A very simple tag to check if node may be linked to.
* 
* @author Michiel Meeuwissen
*/
public class MayLinkTag extends MayWriteTag implements ConditionTag {
               
    public int doStartTag() throws JspTagException {
        if ((getNode().mayLink()) != inverse) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

}
