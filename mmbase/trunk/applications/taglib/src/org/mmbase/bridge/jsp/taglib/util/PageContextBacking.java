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


/**
 * A basic implementation for the backing, using the PageContext itself. It can also store nulls, in
 * contradiction to PageContext.

 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: PageContextBacking.java,v 1.3 2005-05-18 08:08:09 michiel Exp $
 */

public  class PageContextBacking extends AbstractMap implements Backing {

    private static final int SCOPE = PageContext.PAGE_SCOPE;

    private final PageContext pageContext;

    // We also want to store null, pageContext cannot contain those.
    private Set nulls = new HashSet();

    private Map unwrapped = new HashMap();

    public PageContextBacking(PageContext pc) {
        pageContext = pc;
    }

    public Set entrySet() {
        return new AbstractSet() {                            
                List names = Collections.list(pageContext.getAttributeNamesInScope(SCOPE));
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
                                                return pageContext.getAttribute(name, SCOPE); 
                                            } else {
                                                return null;
                                            }
                                        }
                                        public Object setValue(Object value) {
                                            Object was = pageContext.getAttribute(name, SCOPE);
                                            if (value != null) {
                                                pageContext.setAttribute(name, Casting.wrap(value, (CharTransformer) pageContext.getAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
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
            Object v = Casting.wrap(value, (CharTransformer) pageContext.getAttribute(ContentTag.ESCAPER_KEY));
            pageContext.setAttribute(k, v, SCOPE);
        }
        return unwrapped.put(key, value);
    }
    public Object get(Object key) {
        return pageContext.getAttribute((String) key, SCOPE);
    }
    public Object getOriginal(Object key) {
        Object value = unwrapped.get(key);
        if (value != null) return value;
        return pageContext.getAttribute((String) key, SCOPE);
    }
    public boolean containsKey(Object key) {
        return pageContext.getAttribute((String) key, SCOPE) != null ||  nulls.contains(key);
    }

    public boolean containsOwnKey(Object key) {
        return unwrapped.containsKey(key);
    }

    void release() {
        nulls = null;
        unwrapped = null;
    }

    public String toString() {
        return "PAGECONTEXT BACKING " + super.toString();
    }
        
} 
