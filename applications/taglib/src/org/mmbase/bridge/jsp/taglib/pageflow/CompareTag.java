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


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A very simple tag to check if the value of a certain context
 * variable equals a certain String value. 
 * 
 * @author Michiel Meeuwissen 
 */

public class CompareTag extends PresentTag implements ConditionTag {

    private static Logger log = Logging.getLoggerInstance(CompareTag.class.getName());

    private String value;
    public void setValue(String v) throws JspTagException {
        value =  getAttributeValue(v);
    }

    protected boolean doCompare(String compare) {
        if (log.isDebugEnabled()) {
            log.debug("comparing '" + value + "' to '" + compare + "'");
        }
        return value.equals(compare);
    }
               
    public int doStartTag() throws JspTagException {
        String compare;
        if (getReferid() == null) {
            Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", null);
            Object o = w.getValue();
            if (o == null) {
                compare = "";
            } else {
                compare = o.toString();
            }
        } else {
            compare = getString(getReferid());
        }
        if (doCompare(compare) != inverse ) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }
}
