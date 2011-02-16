/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.*;


/**
 * A basic implementation for the backing of a ContextContainter. It uses a HashMap, but is also
 * writes every entry to the temporary to the page-context, to make them available to JSP2's
 * expression language, unless the 'ELIgnored' parameter of the MMBase taglib is true, or no
 * pageContext is given in the constructor.

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id: BasicBacking.java 39537 2009-11-04 15:23:01Z michiel $
 */

public  class CollectorBacking extends BasicBacking {
    private static final Logger log = Logging.getLoggerInstance(CollectorBacking.class);

    protected final Set<String> myKeys = new HashSet<String>();

    final ContextContainer parent;
    /**
     * @param pc The page-context to which variables must be reflected or <code>null</code> if this must not happen.
     */
    public CollectorBacking(PageContext pc, ContextContainer parent) {
        super(pc, true);
        this.parent = parent;
    }

    @Override
    public Object put(String key, Object value, boolean reset) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Putting in collector " + key + "=" + value + " " + parent);
        }
        assert parent != null;
        if (reset || myKeys.contains(key)) {
            parent.reregister(key, value);
        } else {
            if (! parent.containsKey(key)) {
                parent.register(key, value);
            } else {
                parent.register(key, value);
            }
        }
        myKeys.add(key);

        boolean r = reset || myKeys.contains(key);
        return super.put(key, value, r);
    }

    @Override
    public void setJspVar(PageContext pc, String jspvar, int vartype, Object value) {
        if (jspvar == null) return;
        if (value == null) return;
        myKeys.add(jspvar);
        super.setJspVar(pc, jspvar, vartype, value);
    }




}
