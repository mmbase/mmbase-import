/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;


import org.mmbase.bridge.jsp.taglib.ContentTag;
import org.mmbase.bridge.jsp.taglib.CloudTag;
import java.util.Collection;
import java.util.Iterator;

import org.mmbase.bridge.*;

import org.mmbase.util.Casting;
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
 * @version $Id: Functions.java,v 1.15.2.1 2007-04-26 19:36:32 michiel Exp $
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
     * Provides the 'escape' functionality to the XSLT itself. (using taglib:escape('p', mytag))
     * 
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
     * @since MMBase-1.8.2
     */
    public static String url(String page, javax.servlet.jsp.PageContext pageContext) {
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
        StringBuffer show = new StringBuffer();
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
     * @since MMBase-1.8.4
     */
    public static String treefile(String page, javax.servlet.jsp.PageContext pageContext, Object objectList) throws javax.servlet.jsp.JspTagException {
        org.mmbase.bridge.jsp.taglib.pageflow.TreeHelper th =
            new org.mmbase.bridge.jsp.taglib.pageflow.TreeHelper();
        th.setCloud((Cloud) pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE));
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
        String t = th.findTreeFile(page, Casting.toString(objectList), pageContext.getSession());
        return req.getContextPath() + (t.charAt(0) == '/' ? "" : "/") + t;
    }



}
