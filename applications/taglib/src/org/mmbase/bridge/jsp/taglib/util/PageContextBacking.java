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
 * @version $Id: PageContextBacking.java,v 1.1 2005-03-22 15:02:05 michiel Exp $
 */

public  class PageContextBacking extends AbstractMap {

    private static final int SCOPE = PageContext.PAGE_SCOPE;

    private final PageContext pageContext;

    // We also want to store null, pageContext cannot contain those.

    private Set nulls = new HashSet();

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
                                                return Casting.unWrap(pageContext.getAttribute(name, SCOPE));                                                            
                                            } else {
                                                return null;
                                            }
                                        }
                                        public Object setValue(Object value) {
                                            Object was = Casting.unWrap(pageContext.getAttribute(name, SCOPE));
                                            pageContext.setAttribute(name, Casting.wrap(value, (CharTransformer) pageContext.getAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
                                            if (value == null) {
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
                            }
                        };
                }
                public int size() {
                    return names.size();
                }
            };
    }
    
    public Object put(Object key, Object value) {
        
        Object was = Casting.unWrap(pageContext.getAttribute((String) key, SCOPE));
        if (value == null) {
            nulls.add(key);
        } else {
            String k = (String) key;
            Object v = Casting.wrap(value, (CharTransformer) pageContext.getAttribute(ContentTag.ESCAPER_KEY));
            pageContext.setAttribute(k, v, SCOPE);
        }
        return was;
    }
        
} 
