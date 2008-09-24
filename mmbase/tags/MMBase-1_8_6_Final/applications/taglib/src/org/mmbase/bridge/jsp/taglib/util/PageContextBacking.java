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
import org.mmbase.bridge.jsp.taglib.ContentTag;
import org.mmbase.bridge.jsp.taglib.WriterHelper;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A basic implementation for the backing, using the PageContext itself. It can also store nulls, in
 * contradiction to PageContext.

 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: PageContextBacking.java,v 1.11.2.1 2006-11-21 20:39:56 michiel Exp $
 */

public  class PageContextBacking extends AbstractMap implements Backing {

    private static final Logger log = Logging.getLoggerInstance(PageContextBacking.class);

    private static final int SCOPE = PageContext.PAGE_SCOPE;

    private final PageContext pageContext;

    // We also want to store null, pageContext cannot contain those.
    private final Set nulls = new HashSet();

    private final Set jspvars = new HashSet();

    private final Map unwrapped = new HashMap();

    public PageContextBacking(PageContext pc) {
        pageContext = pc;
    }

    public void pushPageContext(PageContext pc) {

    }

    public void pullPageContext(PageContext pc) {

    }
    public PageContext getPageContext() {
        return pageContext;
    }


    public void setJspVar(PageContext pc, String jspvar, int vartype, Object value) {
        if (jspvar == null) return;
        if (value == null) return;
        jspvars.add(jspvar);
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
                Collection names = unwrapped.keySet();
                public Iterator iterator() {
                    return new Iterator() {
                            Iterator back = names.iterator();
                            Iterator nul  = null;
                            String name;
                            public Object next() {
                                if (nul == null) {
                                    name = (String) back.next();
                                } else {
                                    name = (String) nul.next();
                                }
                                return new Map.Entry() {
                                        public Object getKey() {
                                            return name;
                                        }
                                        public Object getValue() {
                                            if (nul == null) {
                                                return pageContext.findAttribute(name);
                                            } else {
                                                return null;
                                            }
                                        }
                                        public Object setValue(Object value) {
                                            Object was = pageContext.findAttribute(name);
                                            if (value != null) {
                                                pageContext.setAttribute(name, jspvars.contains(name) ? value : Casting.wrap(value, (CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
                                            } else {
                                                pageContext.removeAttribute(name, SCOPE);
                                                nulls.add(name);
                                            }
                                            return was;
                                        }
                                    };
                            }
                            public boolean hasNext() {
                                if (back.hasNext()) return true;
                                if (nul == null) nul = nulls.iterator();
                                return nul.hasNext();
                            }
                            public void remove() {
                                pageContext.removeAttribute(name, SCOPE);
                                nulls.remove(name);
                                unwrapped.remove(name);
                            }
                        };
                }
                public int size() {
                    return names.size();
                }
            };
    }


    public Object put(Object key, Object value) {
        if (value == null) {
            nulls.add(key);
        } else {
            String k = (String) key;
            Object v = jspvars.contains(key) ? value : Casting.wrap(value, (CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY));
            pageContext.setAttribute(k, v, SCOPE);
        }
        return unwrapped.put(key, value);
    }
    public Object get(Object key) {
        return pageContext.findAttribute((String) key);
    }
    public Object getOriginal(Object key) {
        Object value = unwrapped.get(key);
        if (value != null) return value;
        return pageContext.findAttribute((String) key);
    }
    public boolean containsKey(Object key) {
        return pageContext.findAttribute((String) key) != null ||  nulls.contains(key);
    }

    public boolean containsOwnKey(Object key) {
        return unwrapped.containsKey(key);
    }

    void release() {
        nulls.clear();
        unwrapped.clear();
        jspvars.clear();
    }

    public String toString() {
        return "PAGECONTEXT BACKING " + super.toString();
    }

}
