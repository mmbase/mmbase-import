/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.core.event.*;
import java.util.LinkedList;

import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A thread-local is used to store a stack of JSP PageContext.objects. MMBase Functions and other
 * objects can use this class to retrieve the current PageContext when it was not passed in as a parameter.
 *
 * @since MMBase-1.8.6
 */
public class PageContextThreadLocal {



    private static final Logger LOG = Logging.getLoggerInstance(PageContextThreadLocal.class);

    static {
        LOG.debug("Adding system event listener");
        EventManager.getInstance().addEventListener(new SystemEventListener() {
            @Override
            public void notify(SystemEvent se) {
                LOG.debug("Got " + se);
                if (se instanceof SystemEvent.Shutdown) {
                    LOG.info("Shutting down. Clearing " + threadPageContexts.get());
                    threadPageContexts.remove();
                }
            }
            @Override
            public int getWeight() {
                return 0;
            }
        });
    }


    private static ThreadLocal<LinkedList<PageContextInfo>> threadPageContexts
                        = new ThreadLocal<LinkedList<PageContextInfo>>() {
        @Override
        protected LinkedList<PageContextInfo> initialValue() {
                return new LinkedList<PageContextInfo>();
            }
    };

    private static String getPref(ContextReferrerTag contextReferrerTag) {
        return Thread.currentThread() + " " + contextReferrerTag + ":";
    }

    protected static void setThreadPageContext(final PageContext pc, ContextReferrerTag contextReferrerTag) {
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) {
            stack.add(0, new PageContextInfo(pc, contextReferrerTag));
            if (LOG.isTraceEnabled()) {
                LOG.trace(getPref(contextReferrerTag) + "added new stack size = " + stack.size());
            }
        } else {
            PageContextInfo first = stack.getFirst();
            if (first.pageContext != pc) {
                stack.add(0, new PageContextInfo(pc, contextReferrerTag));
                if (stack.size() < 50) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(getPref(contextReferrerTag) + "added new stack size = " + stack.size());
                    }
                } else {
                    LOG.warn(getPref(contextReferrerTag) + "added new stack size = " + stack.size());
                }
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(getPref(contextReferrerTag) + "PageContext is already on the stack");
                }
            }
        }
    }

    public static PageContext getThreadPageContext() {
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) {
            throw new RuntimeException("Used in thread which did not yet use mmbase tags");
        }
        PageContextInfo first = stack.getFirst();
        return first.pageContext;
    }

    protected static void cleanThreadPageContexts(ContextReferrerTag contextReferrerTag) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(getPref(contextReferrerTag) + "releasing pageContext info");
        }
        LinkedList<PageContextInfo> stack = threadPageContexts.get();
        if (stack.size() == 0) return;
        PageContextInfo first = stack.getFirst();
        if (first.firstTagWithPageContext == contextReferrerTag) {
            stack.removeFirst();
            if (LOG.isTraceEnabled()) {
                LOG.trace(getPref(contextReferrerTag) + "removed new stack size = " + stack.size());
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace(getPref(contextReferrerTag) + "Could not remove it is not first  " + stack.size() + " !");
            }
        }
    }

    private static class PageContextInfo {
        final PageContext pageContext;
        final ContextReferrerTag firstTagWithPageContext;

        PageContextInfo(PageContext pc, ContextReferrerTag contextReferrerTag) {
            this.pageContext = pc;
            this.firstTagWithPageContext = contextReferrerTag;
        }

    }

}
