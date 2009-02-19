/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import org.mmbase.bridge.jsp.taglib.ContentTag;
import org.mmbase.bridge.jsp.taglib.WriterHelper;
import org.mmbase.util.logging.*;


/**
 * A basic implementation for the backing of a ContextContainter. It uses a HashMap, but is also
 * writes every entry to the temporary to the page-context, to make them available to JSP2's
 * expression language, unless the 'ELIgnored' parameter of the MMBase taglib is true, or no
 * pageContext is given in the constructor.

 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: BasicBacking.java,v 1.7.2.3 2007-05-16 23:22:56 michiel Exp $
 */

public  class BasicBacking extends AbstractMap  implements Backing {
    private static final Logger log = Logging.getLoggerInstance(BasicBacking.class);

    private static final String PAGECONTEXT_KEY = "org.mmbase.taglib.basicbacking$";

    private static int uniqueNumbers = 0;
    private static final int SCOPE = PageContext.PAGE_SCOPE;

    private int uniqueNumber = ++uniqueNumbers;

    protected Map originalPageContextValues;
    private final Map b = new HashMap(); // the actual backing.

    private final boolean isELIgnored;
    private  PageContext pageContext;

    /**
     * @param pc The page-context to which variables must be reflected or <code>null</code> if this must not happen.
     */
    public BasicBacking(PageContext pc, boolean ignoreEL) {
        pageContext = pc;
        isELIgnored = ignoreEL || "true".equals(pageContext.getServletContext().getInitParameter(ContextTag.ISELIGNORED_PARAM));
        if (! isELIgnored) {
            originalPageContextValues = new HashMap();
            pageContext.setAttribute(PAGECONTEXT_KEY + uniqueNumber, originalPageContextValues);
        } else {
            originalPageContextValues = null;
        }
    }

    public void pushPageContext(PageContext pc) {
        PageContext origPageContext = pageContext;
        pageContext = pc;
        if (isELIgnored) return; // never mind
        log.debug("Pushing page-context for backing " + uniqueNumber + " for " + pc);
        originalPageContextValues = (Map) pageContext.getAttribute(PAGECONTEXT_KEY + uniqueNumber);
        if (originalPageContextValues == null) {
            originalPageContextValues = new HashMap();
            pageContext.setAttribute(PAGECONTEXT_KEY + uniqueNumber, originalPageContextValues);
        }
        if (! origPageContext.equals(pageContext)) {
            Iterator i = b.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                mirrorPut(entry.getKey(), entry.getValue());
            }
        }
    }
    public void pullPageContext(PageContext pc) {
        pageContext = pc;
        if (isELIgnored) return;
        originalPageContextValues = (Map) pageContext.getAttribute(PAGECONTEXT_KEY + uniqueNumber);
        if (originalPageContextValues == null) {
            log.warn("OIE " + Logging.stackTrace(10));
        }
    }
    public PageContext getPageContext() {
        return pageContext;
    }

    public void setJspVar(PageContext pc, String jspvar, int vartype, Object value) {
        if (jspvar == null) return;
        if (value == null) return;
        log.debug("Setting jspvar");
        // When it doesn't, it goes ok. (at least I think that this is the difference between orion and tomcat)
        if (vartype == WriterHelper.TYPE_STRING) {
            // string is final, the wrapped version cannot be string..
            Object v = Casting.unWrap(value);
            if (v == null) return;
            pc.setAttribute(jspvar, v);
        } else {
            pc.setAttribute(jspvar, value);
        }

    }

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
                                if (! isELIgnored) {
                                    String key = (String) last.getKey();
                                    if (! originalPageContextValues.containsKey(key)) {
                                        originalPageContextValues.put(key, pageContext.getAttribute(key, SCOPE));
                                    }
                                    pageContext.removeAttribute(key, SCOPE);
                                }
                            }
                        };
                }
            };
    }

    protected void mirrorPut(Object key, Object value) {
        if (isELIgnored) return;
        if (! originalPageContextValues.containsKey(key)) {
            // log.debug("Storing pageContext key " + key);
            originalPageContextValues.put((String) key, pageContext.getAttribute((String) key, SCOPE));
        }
        
        if (value != null) {
            pageContext.setAttribute((String) key, Casting.wrap(value, (CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
        } else {
            pageContext.removeAttribute((String) key, SCOPE);
        }
    }
    public Object put(Object key, Object value) {
        mirrorPut(key, value);
        return b.put(key, value);
    }

    // overriden for efficiency only (the implementation of AbstractMap does not seem very efficient)
    public Object get(Object key) {
        return b.get(key);
    }

    public Object getOriginal(Object key) {
        return b.get(key);
    }
    public boolean containsOwnKey(Object key) {
        return b.containsKey(key);
    }

        
    void release() {
        if (originalPageContextValues != null) {
            //log.debug("Restoring pageContext with " + originalPageContextValues);
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

    public String toString() {
        return "BASIC BACKING " + super.toString();
    }
        
        
} 
