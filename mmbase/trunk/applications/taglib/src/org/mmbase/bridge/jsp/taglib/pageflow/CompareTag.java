/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.ConditionTag;
import org.mmbase.bridge.jsp.taglib.Writer;
import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if the value of a certain context
 * variable equals a certain String value. 
 * 
 * @author Michiel Meeuwissen 
 */

public class CompareTag extends PresentTag implements ConditionTag {

    private String value;

    public void setValue(String v) throws JspTagException {
        value =  getAttributeValue(v);
    }
               
    public int doStartTag() throws JspTagException {
        String compare;
        if (getReferid() == null) {
            Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", null);
            compare =  (String) w.getValue();
        } else {
            compare = getString(getReferid());            
        }
        if (value.equals(compare) != inverse ) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }
}
