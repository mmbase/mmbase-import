/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import java.io.IOException;
import java.util.Stack;

import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public class ShrinkTag extends AbstractTreeReferrerListTag implements Writer {

    private static final Logger log = Logging.getLoggerInstance(ShrinkTag.class);
    private int startDepth;
    private int endDepth;
    private int size;
    private boolean foundSize;
    private boolean foundBody;
    private boolean write;

    @Override
    public int size() {
        if (! foundSize) { // avoid determining size if it is never requested
            size = index;
            while (tree.getShrinkStack().peek().depth < endDepth) {
                size ++;
            }
            foundSize = true;
        }
        return size;
    }


    @Override
    public int doStartTag() throws JspTagException {
        log.debug("starttag");
        doStartTagHelper();

        Stack<Entry> stack  = tree.getShrinkStack();


        if (log.isDebugEnabled()) {
            log.debug("Shrinking " + stack + " to " + tree.getNextDepth());
        }
        if (stack.size() == 0) return SKIP_BODY;

        Entry entry = stack.peek();

        startDepth = entry.depth;
        depth      = startDepth;
        endDepth   = tree.getNextDepth();
        foundSize = false;
        foundBody = false;
        write     = false;

        helper.setValue("");
        helper.useEscaper(false);

        if (depth >= endDepth) {
            return EVAL_BODY_BUFFERED;
        } else {
            log.debug("nothing to shrink");
            return SKIP_BODY;
        }
    }
    @Override
    public void doInitBody() throws JspException {
        // called once, if there is a body.
        log.debug("initbody");
        Stack<Entry> stack  = tree.getShrinkStack();
        if (stack.size() > 0) {
            Entry entry = stack.peek();
            log.debug("making available now " + entry.string);
            helper.setValue(entry.string);
            helper.doAfterBody();
            write = helper.getWrite().getBoolean(this, false);
            if (write) {
                try {
                    pageContext.getOut().write(Casting.toString(helper.getValue()));
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
        }
        super.doInitBody();
    }


    @Override
    public int doAfterBody() throws JspTagException {
        log.debug("afterbody");
        if (index == 0) {
            foundBody = true;
        }

        Stack<Entry> stack  = tree.getShrinkStack();

        stack.pop();

        if (stack.size() > 0) {
            Entry entry = stack.peek();
            depth = entry.depth;
            if (depth >= endDepth) {
                log.debug("making available now " + entry.string);
                helper.setValue(entry.string);
                if (write) {
                    try {
                        pageContext.getOut().write(Casting.toString(helper.getValue()));
                    } catch (IOException ioe){
                        throw new TaglibException(ioe);
                    }
                }

                index++;
                collector.doAfterBody(true);
                return EVAL_BODY_AGAIN;
            }
        }

        collector.doAfterBody(false);
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new TaglibException(ioe);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("stack now " + stack);
        }
        return SKIP_BODY;



    }

    @Override
    public int doEndTag() throws JspTagException {
        log.debug("endtag");
        if (! foundBody) { // write everything to page!
            Stack<Entry> stack  = tree.getShrinkStack();
            if (stack.size() > 0) {
                log.debug("no body, doing work");
                Entry entry = stack.peek();
                StringBuilder value = new StringBuilder();
                while (depth >= endDepth) {
                    log.debug("writing now " + entry.string);
                    value.append(entry.string);
                    stack.pop();
                    if (stack.size() == 0) break;
                    entry = stack.peek();
                    depth = entry.depth;
                }
                helper.setValue(value.toString());
                helper.doEndTag();
                return  super.doEndTag();
            } else {
                log.debug("Empty stack, nothing to do");
                return super.doEndTag();
            }
        } else {
            log.debug("handled by doAfterBodies");
            return super.doEndTag();
        }

    }


    public static class Entry {
        Entry(int d, String s) {
            depth = d; string = s;
        }
        public int depth;
        public String string;
        @Override
        public String toString() {
            return "" + depth + ":" + string;
        }
    }



}

