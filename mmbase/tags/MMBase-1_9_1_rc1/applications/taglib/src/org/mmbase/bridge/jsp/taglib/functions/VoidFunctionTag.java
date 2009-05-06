/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;

/**
 * If you want to completely ignore the result of a function (only interested in the side effect),
 * then you can use <mm:voidfunction
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: VoidFunctionTag.java,v 1.4 2008-08-14 13:58:49 michiel Exp $
 */
public class VoidFunctionTag extends AbstractFunctionTag implements FunctionContainerReferrer {
    public int doStartTag() throws JspTagException {
        initTag();
        getFunctionValue();
        return SKIP_BODY;
    }
}
