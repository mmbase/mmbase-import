/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.util.logging.*;

/**
 * The Function tag can be used as a child of a 'NodeProvider' tag (but not on clusternodes?). It
 * can call functions on the node.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: FunctionTag.java,v 1.7 2003-08-29 12:12:25 keesj Exp $
 */
public class FunctionTag extends AbstractFunctionTag implements Writer, FunctionContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(FunctionTag.class);


    public int doStartTag() throws JspTagException {     
        helper.setTag(this);
        helper.setValue(getFunctionValue());
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }
       
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }

}
