/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;

import java.util.*;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This ContextContainer provides its own 'backing', it is used as 'subcontext' in other contextes.
 *
 * @author Michiel Meeuwissen
 * @version $Id: StandaloneContextContainer.java,v 1.3 2005-01-05 20:49:36 michiel Exp $
 * @since MMBase-1.8
 **/

public class StandaloneContextContainer extends ContextContainer {
    
    private static final int SCOPE = PageContext.PAGE_SCOPE;


    /**
     * A simple map, which besides to itself also registers to page-context.
     */
    protected BasicBacking backing;
        
    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */
    public StandaloneContextContainer(PageContext pc, String i, ContextContainer p) {
        super(pc, i, p);
        backing = new BasicBacking();
        // values must fall through to PageContext, otherwise you always must prefix by context, even in it.
    }


    protected  Map getBacking() {
        return backing;        
    }

    public void release() {
        backing.release();
    }


    /**
     * A basic implementation for the backing, using a HashMap, but which also writes every entry
     * (except <code>null</code>) to the pageContext.
     */

    class BasicBacking extends AbstractMap {
        // contains the values originally in the pageContext, so that they can be restored.
        protected Map originalPageContextValues = new HashMap();        
        private final Map b = new HashMap();
        public Set entrySet() {
            return new AbstractSet() {
                    public int size() {
                        return b.size();
                    }
                    public Iterator iterator() {
                        return new Iterator() {
                                Iterator i = b.entrySet().iterator();
                                Map.Entry last = null;
                                public boolean hasNext() {
                                    return i.hasNext();
                                }
                                public Object next() {
                                    last = (Map.Entry) i.next();
                                    return last;
                                }
                                public void remove() {
                                    i.remove();
                                    pageContext.removeAttribute((String) last.getKey());
                                }
                            };
                    }
                };
        }
        public Object put(Object key, Object value) {
            if (! b.containsKey(key)) {
                originalPageContextValues.put((String) key, pageContext.getAttribute((String) key, SCOPE));
            } 
            if (value != null) {
                pageContext.setAttribute((String) key, Casting.wrapToString(value), SCOPE);
            } else {
                pageContext.removeAttribute((String) key, SCOPE);
            }
            return b.put(key, value);
        }
        
        public void release() {
            if (originalPageContextValues != null) {
                // restore the pageContext
                Iterator i = originalPageContextValues.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    if (e.getValue() == null) {
                        pageContext.removeAttribute((String) e.getKey(), SCOPE);
                    } else {
                        pageContext.setAttribute((String) e.getKey(), e.getValue(), SCOPE);
                    }
                }
                originalPageContextValues.clear();
            }
        }
        
        
    } 
    
}
