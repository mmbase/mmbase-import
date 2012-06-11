/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.jsp.taglib.ContextProvider;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A helper class for Lists, to implement ContextProvider. This ContextContainer writes every key to
 * it's parent too, so it is 'transparent'.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class  ContextCollector extends StandaloneContextContainer {
    private static final Logger log = Logging.getLoggerInstance(ContextCollector.class);

    private Map<String, Object> unregister = new HashMap<String, Object>();

    public ContextCollector(ContextProvider p) throws JspTagException {
        super(p.getPageContext(), "CONTEXT-COLLECTOR" + (p.getId() == null ? "" : "-" + p.getId()), p.getContextContainer());
        if (log.isDebugEnabled()) {
            log.debug("Using collector with pagecontext " + p.getPageContext());
        }
    }

    @Override
    protected BasicBacking createBacking(PageContext pc) {
        //        System.out.println("IGNORE " + (parent instanceof PageContextContainer || parent instanceof ContextCollector));
        return new CollectorBacking(pc, parent);
    }

    @Override
    public void unRegister(String key) throws JspTagException {
        super.unRegister(key);
        parent.unRegister(key);

    }
    @Override
    protected void register(String newid, Object n, boolean check, boolean checkParent) throws JspTagException {
        if (! check || unregister.containsKey(newid)) {
            parent.unRegister(newid);
        }
        //log.debug("" + unregister + " check " + check);
        boolean c = check;
        if (unregister.containsKey(newid)) c = false;
        super.register(newid, n, c , checkParent);

    }


    /**
     * For a context-collector it also interesting to have a 'doAFterBody', because it can be iterated again.
     * It calls {@link #clear}.
     */
    public final void doAfterBody(boolean iteratesAgain) throws JspTagException {
        if (iteratesAgain) {
            synchronized(backing.getOriginalMap()) {
                for (Map.Entry<String, Object> e : backing.entrySet()) {
                    if (((CollectorBacking) backing).myKeys.contains(e.getKey())) {
                        //parent.unRegister(e.getKey());
                        unregister.put(e.getKey(), e.getValue());
                    }
                }
            }
            //
        } else {
            for (Map.Entry<String, Object> e : unregister.entrySet()) {
                if (! parent.containsKey(e.getKey())) {
                    parent.register(e.getKey(), e.getValue());
                }
            }
        }
        clear();

    }

    @Override
    public void release(PageContext pc, ContextContainer p) {
        super.release(pc, p);
    }

}
