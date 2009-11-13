/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import org.mmbase.bridge.jsp.taglib.Condition;
import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if node may be deleted.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MayDeleteTag extends MayWriteTag implements Condition {

    public int doStartTag() throws JspTagException {
        initTag();
        if ((getNode().mayDelete()) != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }

}
