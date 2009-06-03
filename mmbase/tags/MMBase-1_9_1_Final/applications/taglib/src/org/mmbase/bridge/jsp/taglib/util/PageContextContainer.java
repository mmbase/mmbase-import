/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The page context container stores variables directly in the page context, like JSTL does.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 **/

public class PageContextContainer extends ContextContainer {
    private static final Logger log = Logging.getLoggerInstance(PageContextContainer.class);

    private final PageContextBacking backing;

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */
    public PageContextContainer(final PageContext pc) {
        super("PAGECONTEXT", null);
        if (log.isDebugEnabled()) {
            log.debug("Creating pagecontext container for " + pc);
        }
        // this Maps pageContext.
        // This code simply makes pageContext look like a Map (which also can contain null-values).
        backing = new PageContextBacking(pc);
    }

    public void release(PageContext pc, ContextContainer p) {
        backing.release();
    }

    public PageContextBacking getBacking() {
        return backing;
    }

}
