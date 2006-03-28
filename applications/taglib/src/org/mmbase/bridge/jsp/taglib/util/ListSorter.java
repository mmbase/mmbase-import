/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import javax.servlet.jsp.PageContext;
import java.util.*;

/**
 * A helper class for Lists, to implement an attribute 'comparator'
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListSorter.java,v 1.6 2006-03-28 20:32:40 michiel Exp $
 * @since MMBase-1.7
 */
public class  ListSorter  {


    public static List sort(List list, String comparator, PageContext pageContext) throws JspTagException {
        if (comparator != null) {
            if (comparator.equals("SHUFFLE")) {
                Collections.shuffle(list);
            }  else if (comparator.equals("REVERSE")) {
                Collections.reverse(list);
            }  else if (comparator.equals("NATURAL")) {
                Collections.sort(list);
            }  else if (comparator.equals("CASE_INSENSITIVE")) {
                Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
            } else {
                try {
                    Class claz = null;
                    boolean pageClass = false;
                    if (comparator.indexOf(".") == -1) {
                        Class[] classes = pageContext.getPage().getClass().getDeclaredClasses();
                        for (int i = 0; i < classes.length; i++) {
                            if (classes[i].toString().endsWith(comparator)) {
                                claz = classes[i];
                                pageClass = true;
                                break;
                            }
                        }
                    }
                    if (claz == null) {
                        claz = Class.forName(comparator);
                    }

                    if (pageClass && ! java.lang.reflect.Modifier.isStatic(claz.getModifiers())) {
                        throw new TaglibException("Don't know how to instantiate non-static inner class: " + comparator + " (make it static please)");
                    }
                    Comparator comp = (Comparator) claz.newInstance();
                    Collections.sort(list, comp);
                } catch (Exception e) {
                    throw new TaglibException(e);
                }
            }
        }
        return list;

    }
}
