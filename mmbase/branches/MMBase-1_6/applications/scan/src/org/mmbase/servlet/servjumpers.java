/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import org.mmbase.module.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

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
 * @version 24 Apr 2001
 */
public class servjumpers extends JamesServlet {
    private final static int EXPIRE = 120;
    private static Logger log;
    // reference to the MMBase cloud
    static MMBase mmbase;

    /**
     * tells if the user is using reload.
     *
     * @param the HttpServletRequest of the user.
     * @return <code>true</code> if the user is using reloading, <code>false</code> otherwise.
     */
    private boolean reloadInfo(HttpServletRequest request) {
        
	    sessionsInterface sessions=null;
	    Cookie[] cookies=request.getCookies();

        if(cookies==null) {
            return false;
        }
        Cookie cookie = cookies[0];
        
        // If user is not having a cookie he cannot be reloading.
        if(cookie==null) {
            return false;
        }
        
        if (sessions==null) {
            sessions = (sessionsInterface)mmbase.getModule("SESSION");
            if(sessions==null) {
                log.error("Need SESSION module to be able to retrieve the reload variable");
                return false;
            }
        }
        
        // Converting cookie to SCAN cookie
        sessionInfo info = sessions.getSession(new scanpage(),"MMBase_Ident/"+cookie.getValue());
        
        // if the reload variable is not set to R, the user is not reloading.
        String reload = sessions.getValue(info, "RELOAD");
        if(reload==null || !reload.equals("R")) {
            log.debug("reloading for user ("+cookie.getValue()+") is off");
            return false;
        }
        
        // Reloading is on, but check if the reload time is expired.
        String reloadtime = sessions.getValue(info, "RELOADTIME");
        if (reloadtime!=null) {
            int then=Integer.parseInt(reloadtime);
            int now= (int)(DateSupport.currentTimeMillis()/1000);
            if ((now-then)<EXPIRE) {
                log.debug("reloading for user ("+cookie.getValue()+") is on");
                return true;
            } else {
                log.debug("reloading for user ("+cookie.getValue()+") is expired");
                sessions.setValue(info, "RELOAD","N");
            }
        }
        return false;
    }

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
     * specified file.<br>
     * So '/mypath/' or '/myfile.html' will not be filtered, but '/mypath' will.
     * @param req The HTTP Servlet request
     * @param res The HTTP Servlet response
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        incRefCount(req);  // increase reference count
        try {
            String url=null;
            String tmpr=req.getRequestURI().substring(1);

	    boolean reloadStatus=reloadInfo(req);
	    log.debug("reloadStatus: "+reloadStatus);

            // jump.db was handled by the serdb servlet,
            // but it is nicer when it is handled in one place.
            // and the servdb had a little issue setting the mimetype for jumpers
            // which prevented some search bots to index the full site   
        	if (tmpr.indexOf("jump.db") != -1 ) {
        		String jumpKey = req.getQueryString();
				if (jumpKey != null || "".equals(jumpKey.trim())) {        		
        			url=getUrl(jumpKey,reloadStatus);
				}
        	} else {
            	if (tmpr.indexOf('.')==-1 && (!tmpr.endsWith("/"))) {
            	 	url=getUrl(tmpr,reloadStatus);
            	}
        	}
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
    String getUrl(String key, boolean reloadStatus) {
        String url=null;
        Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
        if (bul!=null) {
            if (key.endsWith("/")) {
                url=bul.getJump(key.substring(0,key.length()-1),reloadStatus);
            } else {
                url=bul.getJump(key,reloadStatus);
            }
            if (url!=null) return url;
        } else {
            log.error("servjumpers -> can't access NodeManager jumpers (check jumpers.xml)");
        }
        return null;
    }
}
