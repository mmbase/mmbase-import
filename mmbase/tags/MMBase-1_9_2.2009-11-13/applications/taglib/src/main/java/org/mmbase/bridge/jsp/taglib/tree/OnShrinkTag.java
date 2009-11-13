/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;
import java.util.Stack;

import javax.servlet.jsp.*;

import org.mmbase.util.logging.*;
/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public class OnShrinkTag extends TreeReferrerTag implements DepthProvider {
    private static final Logger log = Logging.getLoggerInstance(OnShrinkTag.class);
    private int depth;
    private Object prevDepthProvider;

    public int getDepth() {
        return depth;
    }

    public int doStartTag() throws JspTagException {
        initTag();
        DepthProvider dp = findDepthProvider();
        depth = dp.getDepth();
        prevDepthProvider = pageContext.getAttribute(DepthProvider.KEY, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute(DepthProvider.KEY, new BasicDepthProvider(getDepth()), PageContext.REQUEST_SCOPE);
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        TreeProvider tp =  findTreeProvider();
        Stack<ShrinkTag.Entry> stack = tp.getShrinkStack();
        String body = bodyContent != null ? bodyContent.getString() : "";
        stack.push(new ShrinkTag.Entry(depth, body));
        if (log.isDebugEnabled()) {
            log.debug("onshrink " + depth + " " + body);
        }
        return super.doAfterBody();
    }

    public int doEndTag() throws JspTagException  {
        pageContext.setAttribute(DepthProvider.KEY, prevDepthProvider, PageContext.REQUEST_SCOPE);
        prevDepthProvider = null;
        return super.doEndTag();
    }

}

