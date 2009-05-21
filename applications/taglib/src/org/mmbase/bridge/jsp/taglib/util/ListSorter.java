/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import javax.servlet.jsp.PageContext;
import java.util.*;
import java.text.Collator;

/**
 * A helper class for Lists, to implement an attribute 'comparator'
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class  ListSorter  {


    public static <E> List<E> sort(List<E> list, String comparator, ContextReferrerTag tag) throws JspTagException {
        if (comparator != null) {
            if (comparator.equals("SHUFFLE")) {
                Collections.shuffle(list);
            }  else if (comparator.equals("REVERSE")) {
                Collections.reverse(list);
            }  else if (comparator.equals("NATURAL")) {
                Collections.sort((List<? extends Comparable>) list);
            }  else if (comparator.equals("CASE_INSENSITIVE")) {
                Collator col = Collator.getInstance(tag.getLocale());
                col.setStrength(Collator.PRIMARY);
                Collections.sort(list, col);
            } else {
                try {
                    PageContext pageContext = tag.getPageContext();
                    Class<? super E> claz = null;
                    boolean pageClass = false;
                    if (comparator.indexOf(".") == -1) {
                        Class[] classes = pageContext.getPage().getClass().getDeclaredClasses();
                        for (Class element : classes) {
                            if (element.toString().endsWith(comparator)) {
                                claz = element;
                                pageClass = true;
                                break;
                            }
                        }
                    }
                    if (claz == null) {
                        claz = (Class<E>) Class.forName(comparator);
                    }

                    if (pageClass && ! java.lang.reflect.Modifier.isStatic(claz.getModifiers())) {
                        throw new TaglibException("Don't know how to instantiate non-static inner class: " + comparator + " (make it static please)");
                    }
                    Comparator<? super E> comp = (Comparator<? super E>) claz.newInstance();
                    init(comp, pageContext);
                    Collections.sort(list, comp);
                } catch (Exception e) {
                    throw new TaglibException(e);
                }
            }
        }
        return list;

    }

    private static final Class[] PAGE_CONTEXT_ARRAY = { PageContext.class };

    private static void init(Comparator comp, PageContext pageContext) {
        try {
            java.lang.reflect.Method initMethod = comp.getClass().getMethod("init" , PAGE_CONTEXT_ARRAY);
            initMethod.invoke(comp, pageContext);
        } catch(Exception e){
            // never mind
        }
    }


}
