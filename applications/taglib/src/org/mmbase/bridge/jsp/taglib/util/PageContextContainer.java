/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.ContentTag;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.Casting;
import org.mmbase.util.transformers.CharTransformer;

import java.util.*;

/**
 * The page context container stores variables directly in the page context, like JSTL does.
 *
 * @author Michiel Meeuwissen
 * @version $Id: PageContextContainer.java,v 1.9 2005-03-16 12:11:56 michiel Exp $
 * @since MMBase-1.8
 **/

public class PageContextContainer extends ContextContainer {

    private static int SCOPE = PageContext.PAGE_SCOPE;

    private Map backing;
    private Set nulls = new HashSet();
    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */

    public PageContextContainer(final PageContext pc) {
        super(pc, "PAGECONTEXT", null);

        // this Maps pageContext.
        // This code simply makes pageContext look like a Map (which also can contain null-values).
        backing = new AbstractMap() {
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
                                                        pageContext.setAttribute(name, Casting.wrap(value, (CharTransformer) pc.getAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
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
                        pageContext.setAttribute((String) key, Casting.wrap(value, (CharTransformer) pc.getAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
                    }
                    return was;
                }
            };
    }

    public void release() {
        // no  need to release anything
    }
        
    protected Map getBacking() {
        return backing;
    }

    protected boolean checkJspVar(String jspvar, String id) {
        return ! id.equals(jspvar);
    }

}
