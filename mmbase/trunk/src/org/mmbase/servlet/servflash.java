/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;
 
// import the needed packages
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.gui.flash.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Performance Servlet is used for 2 reasons as a basic Servlet test to see if
 * the install went oke (same as SimpleServlet) and to see how fast the JVM is
 * we are running on (very basic test).
 */
public class servflash extends JamesServlet {
    private static Logger log;

    private MMFlash gen;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(servflash.class.getName());
        log.info("Init of servlet " + config.getServletName() + ".");
        MMBaseContext.initHtmlRoot();
        gen=(MMFlash)getModule("mmflash");
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
    public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {    
        BufferedOutputStream out=null;
        try {
            out=new BufferedOutputStream(res.getOutputStream());
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        if (gen!=null) {

            String url=req.getRequestURI();
            String query=req.getQueryString();
	    if (url.endsWith(".swt")) {
        	res.setContentType("text/plain");
            	byte[] bytes=gen.getDebugSwt(url,query,req);
		if (bytes!=null) {
	            	out.write(bytes,0,bytes.length);
		} else {
			res.sendError(404);
		}
	    } else {
        	res.setContentType("application/x-shockwave-flash");
           	 byte[] bytes=gen.getScanParsedFlash(url,query,req);
		if (bytes!=null) {
	            	out.write(bytes,0,bytes.length);
		} else {
			res.sendError(404);
		}
	    }	
        }
    }

}
