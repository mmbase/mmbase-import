/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.mmbase.util.logging.*;

/**
 * Servjumpers filters all url's to see if it has a jumper (specified in the
 * jumpers builder).
 * If a jumper is found, it will redirect the jumper to the designation url.
 *
 * @rename Servjumpers
  * @author Daniel Ockeloen
 * @version $Id: servjumpers.java,v 1.17 2003-03-10 11:50:42 pierre Exp $
 */
public class servjumpers extends JamesServlet {
    private static Logger log;
    // reference to the MMBase cloud
    static MMBase mmbase;

    public void init() throws ServletException {
        super.init();
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(servjumpers.class.getName());
        log.info("Init of servlet " + getServletConfig().getServletName() + ".");
        mmbase=(MMBase)getModule("MMBASEROOT");
    }

    /**
     * Service call for filtering.
     * Will be called by the server when a request is done
     * by a user.
     * Determines an url based on a jumper key, provided the original
     * url (reclaimed from the request) is NOT a directory root and NOT a fully
     * specified file.<br />
     * So '/mypath/' or '/myfile.html' will not be filtered, but '/mypath' will.
     * @param req The HTTP Servlet request
     * @param res The HTTP Servlet response
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        incRefCount(req);  // increase reference count
        try {
            String url=null;
            String tmpr=req.getRequestURI().substring(1);
            if (tmpr.indexOf('.')==-1 && (!tmpr.endsWith("/"))) url=getUrl(tmpr);
            if (url!=null) {
                res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
                res.setContentType("text/html");
                res.setHeader("Location",url);
                return; // ??
            }
        }
        finally { decRefCount(req); } // decrease reference count
    }

    /**
     * Retrieve an alternate url based on a jumper key.
     * @param key the jumper key (original url specified)
     * @return the alternate yurl, or <code>null</code> if no url was found.
     */
    String getUrl(String key) {
        String url=null;
        Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
        if (bul!=null) {
            if (key.endsWith("/")) {
                url=bul.getJump(key.substring(0,key.length()-1));
            } else {
                url=bul.getJump(key);
            }
            if (url!=null) return url;
        } else {
            log.error("servjumpers -> can't access NodeManager jumpers (check jumpers.xml)");
        }
        return null;
    }
}
