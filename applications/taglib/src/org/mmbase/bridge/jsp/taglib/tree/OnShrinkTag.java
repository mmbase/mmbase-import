/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;
import java.util.*;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.bridge.jsp.taglib.util.*;

import org.mmbase.util.logging.*;
/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: OnShrinkTag.java,v 1.1 2003-12-24 00:31:38 michiel Exp $
 */
public class OnShrinkTag extends TreeReferrerTag implements DepthProvider { 
    private static final Logger log = Logging.getLoggerInstance(OnShrinkTag.class);
    private int depth;

    public int getDepth() {
        return depth;
    }

    public int doStartTag() throws JspTagException {
        DepthProvider dp =  (DepthProvider) findParentTag(DepthProvider.class, (String) parentTreeId.getValue(this));
        depth = dp.getDepth();
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        TreeProvider tp =  findTreeProvider();
        Stack stack = tp.getShrinkStack();
        String body = bodyContent != null ? bodyContent.getString() : "";
        stack.push(new ShrinkTag.Entry(depth, body));
        if (log.isDebugEnabled()) {
            log.debug("onshrink " + depth + " " + body);
        }
        return super.doAfterBody();
    }


}

