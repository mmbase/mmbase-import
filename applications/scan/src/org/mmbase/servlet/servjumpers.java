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
 * servjumpers is a 'filter' servlet a concept found in james and orion and we are
 * trying to convince javasoft to add it to the Servlet API 2.x in this case 
 * servjumpers 'filters' all url's to see if it has a jumper it wants to redirect
 * (like www.vpro.nl/3voor12) its controlled by the jumpers builder.
 */
public class servjumpers extends JamesServlet {

    private static Logger log = Logging.getLoggerInstance(servjumpers.class.getName());
	static MMBase mmbase;

	public void init() {
		mmbase=(MMBase)getModule("MMBASEROOT");
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {	

		incRefCount(req);
		try {
			String url=null;
			String tmpr=req.getRequestURI().substring(1);
			if (tmpr.indexOf('.')==-1 && (!tmpr.endsWith("/"))) url=getUrl(tmpr);
			if (url!=null) {
				res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY,"OK");
				res.setContentType("text/html");
				res.setHeader("Location",url);
				return;
			}
		}
		finally { decRefCount(req); }
	}


	String getUrl(String key) {
		String url=null;
		Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
		if (bul!=null) {
			if (key.endsWith("/")) { 
				url=bul.getJump(key.substring(0,key.length()-1));
			} else {
				url=bul.getJump(key);
			}
			if (url!=null) return(url);
		} else {
			log.error("servjumpers -> can't access NodeManager jumpers (check jumpers.xml)");
		}
		return(null);
	}
}
