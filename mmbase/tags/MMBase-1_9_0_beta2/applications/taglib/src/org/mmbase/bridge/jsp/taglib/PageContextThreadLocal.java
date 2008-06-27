/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.LinkedList;

import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A thread-local is used to store a stack of JSP PageContext.objects. MMBase Functions and other
 * objects can use this class to retrieve the current PageContext while it was not passed in as a parameter
 *
 * @since MMBase-1.8.6
 */
public class PageContextThreadLocal {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(PageContextThreadLocal.class.getName());

    private static ThreadLocal<LinkedList<PageContextInfo>> threadPageContexts
                        = new ThreadLocal<LinkedList<PageContextInfo>>() {
        protected LinkedList<PageContextInfo> initialValue() {
                return new LinkedList<PageContextInfo>();
            }
    };

    protected static void setThreadPageContext(final PageContext pc, ContextReferrerTag contextReferrerTag) {
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) {
            stack.add(0, new PageContextInfo(pc, contextReferrerTag));
            log.trace("added new stack size = " + stack.size());
        }
        else {
            PageContextInfo first = stack.getFirst();
            if (first.pageContext != pc) {
                stack.add(0, new PageContextInfo(pc, contextReferrerTag));
                log.trace("added new stack size = " + stack.size());
            }
            else {
                log.trace("PageContext is already on the stack");
            }
        }
    }

    public static PageContext getThreadPageContext() {
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) throw new RuntimeException("Used in thread which did not yet use mmbase tags");
        PageContextInfo first = stack.getFirst();
        return first.pageContext;
    }

    protected static void cleanThreadPageContexts(ContextReferrerTag contextReferrerTag) {
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) return;
        PageContextInfo first = stack.getFirst();
        if (first.firstTagWithPageContext == contextReferrerTag) {
            stack.removeFirst();
            log.trace("removed new stack size = " + stack.size());
        }
    }

    private static class PageContextInfo {
        PageContext pageContext;
        ContextReferrerTag firstTagWithPageContext;

        PageContextInfo(PageContext pc, ContextReferrerTag contextReferrerTag) {
            this.pageContext = pc;
            this.firstTagWithPageContext = contextReferrerTag;
        }

    }

}
