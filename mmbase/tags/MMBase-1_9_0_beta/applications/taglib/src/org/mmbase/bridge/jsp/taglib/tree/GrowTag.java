/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.TaglibException;

import org.mmbase.util.logging.*;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: GrowTag.java,v 1.2 2006-11-14 22:53:49 michiel Exp $
 */
public class GrowTag extends AbstractTreeReferrerListTag {

    private static final Logger log = Logging.getLoggerInstance(GrowTag.class);

    protected int startDepth;
    protected int endDepth ;



    public int size() {
        return endDepth - startDepth;
    }

    public int doStartTag() throws JspException {
        doStartTagHelper();

        startDepth = tree.getPreviousDepth();
        endDepth   = tree.getDepth();
        depth      = startDepth;

        log.debug("while " + depth + " < " + endDepth);
        if (depth < endDepth) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }


    protected final int doAfterBodyHelper() throws JspTagException {
        collector.doAfterBody();
        depth++;
        if (depth < endDepth) {
            index++;
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        int result = doAfterBodyHelper();
        if (result == SKIP_BODY) {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
        }
        return result;

    }




}

