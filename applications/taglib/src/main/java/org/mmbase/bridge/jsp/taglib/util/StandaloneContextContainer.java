/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This ContextContainer provides its own 'backing', it is used as 'subcontext' in other contextes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 **/
public class StandaloneContextContainer extends ContextContainer implements java.io.Serializable {
    private static final  long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(StandaloneContextContainer.class);
    /**
     * A simple map, which besides to itself also registers to page-context.
     */
    protected final BasicBacking backing;

    protected ContextContainer parent;

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */
    public StandaloneContextContainer(PageContext pc, String i, ContextContainer p) {
        super(i);
        assert p != null;
        parent = p;
        backing = createBacking(pc);
        // values must fall through to PageContext, otherwise you always must prefix by context, even in it.
    }

    /**
     * @since MMBase-1.9
     */
    public StandaloneContextContainer(String i, java.util.Map<String, Object> values, boolean ignoreEL) {
        super(i);
        parent = null;
        backing = new BasicBacking(values, ignoreEL);

    }

    @Override
    public void setParent(PageContext pc, ContextContainer p) {
        super.setParent(pc, p);
        parent = p;
    }

    @Override
    protected boolean simpleContainsKey(String key, boolean checkParent) {
        if (getBacking().containsKey(key)) {
            return true;
        } else if (checkParent && parent != null) {
            if (log.isDebugEnabled()) {
                log.debug("Checking " + parent + " for " + key);
            }
            return parent.simpleContainsKey(key, true);
        } else {
            return false;
        }
    }
    /**
     * Like get, but does not try to search dots, because you know already that there aren't.
     */
    @Override
    protected Object simpleGet(String key, boolean checkParent) { // already sure that there is no dot.
        Object result =  getBacking().getOriginal(key);
        if (result == null && checkParent && parent != null) {
            return parent.simpleGet(key, true);
        }
        return result;
    }
    /**
     * @since MMBase-1.7
     */
    @Override
    protected Set<String> keySet(boolean checkParent) {
        if (checkParent && parent != null) {
            HashSet<String> result = new HashSet<String>(getBacking().keySet());
            if (parent != null) {
                result.addAll(parent.keySet());
            }
            return result;
        } else {
            return getBacking().keySet();
        }
    }

    @Override
    public ContextContainer getParent() {
        return parent;
    }

    @Override
    public Set<Entry<String, Object>> entrySet(boolean checkParent) {
        if (! checkParent) {
            return getBacking().entrySet();
        } else {
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.putAll(parent);
            result.putAll(getBacking());
            return result.entrySet();
        }
    }


    protected BasicBacking createBacking(PageContext pc) {
        return new BasicBacking(pc, false);
    }


    @Override
    public  final BasicBacking getBacking() {
        return backing;
    }

    @Override
    public void release(PageContext pc, ContextContainer p) {
        super.release(pc, p);
        // restore also the parent.
        parent = p;
        //backing.release();
    }


}
