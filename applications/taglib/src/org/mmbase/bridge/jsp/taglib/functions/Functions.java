/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;


import org.mmbase.bridge.jsp.taglib.ContentTag;
import org.mmbase.bridge.jsp.taglib.LocaleTag;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Functions for EL variables, and XSL.
 * Like this:

<mm:import id="nodelist" vartype="list">1,2,123</mm:import>
<mm:cloud>
  <mm:node number="124" id="node" />
  <c:choose>
    <c:when test="${mm:contains(nodelist, node)}">
      YES!
    </c:when>
    <c:otherwise>
      NO!
    </c:otherwise>
  </c:choose>
</mm:cloud>
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.8
 * @version $Id: Functions.java,v 1.17 2006-11-11 12:57:54 michiel Exp $
 * @todo    EXPERIMENTAL
 */
public class Functions {
    private static final Logger log = Logging.getLoggerInstance(Functions.class);

    /**
     * MMBase specific 'contains' (for Collections). For strings use fn:contains.
     */
    public static boolean contains(Collection col, Object obj) {
        if (col == null) return false;
        if (obj instanceof Node) {
            if (col instanceof NodeList) {
                if (col.contains(obj)) return true;
            } else {
                obj = new Integer(((Node) obj).getNumber());
            }
        }
        if (col.contains(obj)) return true;
        return col.contains(Casting.toString(obj));
    }

    /**
     * MMBase specific 'remove' (for Collections).
     */
    public static void remove(Collection col, Object obj) {
        if (col == null) return;
        if (obj instanceof Collection) { // like removeAll
            Iterator i = ((Collection) obj).iterator();
            while (i.hasNext()) {
                remove(col, i.next());
            }
        } else {
            if (obj instanceof Node) {
                col.remove(new Integer(((Node) obj).getNumber()));
            }
            col.remove(Casting.toString(obj));
            col.remove(obj);
        }
    }


    /**
     * Provides the 'escape' functionality of taglib. Can be used in EL (using mm:escape('p', value)) and XSLT (using taglib:escape('p', mytag))
     */
    public static String escape(String escaper, String string) {
        try {
            CharTransformer ct = ContentTag.getCharTransformer(escaper, null);
            return ct == null ? "" + Casting.unWrap(string) : ct.transform("" + Casting.unWrap(string));
        } catch (Exception e) {
            String mes = "Could not escape " + string + " with escape " + escaper + " : " + e.getMessage();
            log.debug(mes, e);
            return mes;
        }
    }

    public static String directory(String file) {
        if (file.endsWith("/")) return file;
        return org.mmbase.util.ResourceLoader.getDirectory(file);
    }

    /**
     * MMBase url generation for EL
     * @since MMBase-1.8.2
     */
    public static String url(String page, javax.servlet.jsp.PageContext pageContext) {
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
        StringBuilder show = new StringBuilder();
        if (page.equals("")) { // means _this_ page
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                page = ".";
            } else {
                page = new java.io.File(requestURI).getName();
            }
        }
        if (page.charAt(0) == '/') { // absolute on servletcontex
            show.append(req.getContextPath());
        }
        show.append(page);
        return show.toString();

    }


    /**
     * @since MMBase-1.9
     */
    public static LocalizedString string(LocalizedString s, javax.servlet.jsp.PageContext pageContext) {
        WrappedLocalizedString result = new WrappedLocalizedString(s);
        Locale locale = (Locale) pageContext.getAttribute(LocaleTag.KEY, LocaleTag.SCOPE);
        if (locale == null) {
            locale = LocalizedString.getDefault();
        }
        result.setLocale(locale);
        return result;
    }


}
