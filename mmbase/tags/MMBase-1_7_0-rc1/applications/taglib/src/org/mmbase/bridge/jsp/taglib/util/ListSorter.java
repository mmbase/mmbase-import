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
 * @version $Id: ListSorter.java,v 1.3 2003-12-05 15:19:10 michiel Exp $
 * @since MMBase-1.7
 */
public class  ListSorter  {


    public static List sort(List list, String comparator, PageContext pageContext) throws JspTagException {
        if (comparator != null) {
            if (comparator.equals("SHUFFLE")) {
                Collections.shuffle(list);
            }  else if (comparator.equals("REVERSE")) {
                Collections.reverse(list);
            } else {
                try {
                    Class claz = null;
                    if (comparator.indexOf(".") == -1) {                
                        Class[] classes = pageContext.getPage().getClass().getDeclaredClasses();
                        for (int i = 0; i < classes.length; i++) {
                            if (classes[i].toString().endsWith(comparator)) { 
                                claz = classes[i];
                                break;
                            }
                        }                        
                    } 
                    if (claz == null) {
                        claz = Class.forName(comparator);
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
