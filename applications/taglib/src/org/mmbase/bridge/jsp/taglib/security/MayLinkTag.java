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
 * A very simple tag to check if node may be linked to.
 *
 * @deprecated  As of 20020123, replaced by {@link MayCreateRelationTag}
 * @author Michiel Meeuwissen
 * @version $Id: MayLinkTag.java,v 1.6 2003-06-06 10:03:33 pierre Exp $
 */

public class MayLinkTag extends MayWriteTag implements Condition {

    public int doStartTag() throws JspTagException {
        if ((getNode().mayLink()) != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

}
