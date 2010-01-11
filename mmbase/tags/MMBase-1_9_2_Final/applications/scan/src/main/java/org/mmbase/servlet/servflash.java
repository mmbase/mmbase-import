/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.sessionsInterface;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.gui.flash.*;
import org.mmbase.util.scanpage;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
  * The Servflas servlet responds on certain file extensions to dynamically generate Shockwave Flash
  * based on a template and information from within MMBase
  * @rename Servflash
 */
public class servflash extends JamesServlet {
    private static Logger log;

    private MMFlash gen;
    private static sessionsInterface sessions = null;

    public void init() throws ServletException {
        super.init();
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(servflash.class);
    }
    
    public void setMMBase(org.mmbase.module.core.MMBase mmb) {
        super.setMMBase(mmb);        
        try {            
            MMBaseContext.initHtmlRoot();
        } catch (ServletException se) {
            log.error(se);            
        }        
        gen = (MMFlash)getModule("mmflash");
        sessions = (sessionsInterface)getModule("SESSION");
    }

    /**
     * reload
     */
    public void reload() {
    }

    /**
     * service call will be called by the server when a request is done
     * by a user.
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!checkInited(res)) {
            return;            
        }

        incRefCount(req);
        try {
            pageLog.service("Parsing FLASH page: " + req.getRequestURI());
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(res.getOutputStream());
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }
            if (gen != null) {
                scanpage sp = new scanpage(this, req, res, sessions);
                if (req.getRequestURI().endsWith(".swt")) {
                    res.setContentType("text/plain");
                    byte[] bytes = gen.getDebugSwt(sp);
                    if (bytes != null) {
                        out.write(bytes, 0, bytes.length);
                    } else {
                        res.sendError(404);
                    }
                } else {
                    res.setContentType(sp.mimetype); // application/x-shockwave-flash
                    byte[] bytes = gen.getScanParsedFlash(sp);
                    if (bytes != null) {
                        out.write(bytes, 0, bytes.length);
                    } else {
                        res.sendError(404);
                    }
                }
            }
            pageLog.debug("END Parsing FLASH page");
        }
        finally {
            decRefCount(req);
        }
    }
}
