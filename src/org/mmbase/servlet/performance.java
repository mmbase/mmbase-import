/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;
 
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.logging.*;

/**
 * Performance Servlet is used for 2 reasons as a basic Servlet test to see if
 * the install went oke (same as SimpleServlet) and to see how fast the JVM is
 * we are running on (very basic test).
 */
public class performance extends JamesServlet {
    static Logger log;
	
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(performance.class.getName());
        log.info("Init of servlet " + config.getServletName() + ".");
    }

	public void reload() { }

	/**
 	 * service call will be called by the server when a request is done
	 * by a user.
 	 */
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {	
		// Open	a output stream so you can write to the client
		PrintStream out = new PrintStream(res.getOutputStream());

		// Set the content type of this request
		res.setContentType("text/html");

		// Write header to client
		//res.writeHeaders();

		// WRITE MESSAGE TO CLIENT 
		out.println("<HTML>");
		out.println("<HEAD><TITLE>NWO</TITLE></HEAD>");
		out.println("<BODY BACKGROUND=\"/jamesdoc/images/"+getInitParameter("bg")+"\" BGCOLOR=\"#000000\""+
					" TEXT=\"#FFFFFF\" LINK=\"#00FFFF\" VLINK=\"#00FFFF\">");
		out.println("me (servlet load test) = "+this+"<BR>");
		long time=intloop();
		out.println("intloop = "+time+"<BR>");
		time=string();
		out.println("stringloop = "+time+"<BR>");
		out.println("methodloop = "+method()+"<BR>");
		out.println("</BODY>");
		out.println("</HTML>");
		log.debug("performance servlet - Perftime = "+time);
	}

	 private long intloop() {
        long begin = new Date().getTime();
        for(int i=0; i<20000000;i++);
        long end = new Date().getTime();
        return end-begin;
    }

	private long string() {
        long begin = new Date().getTime();
		String test="test";
		for(int i=0;i<2500000;i++) test.equals("test");
        long end = new Date().getTime();
        return end-begin;
	}
	
	private long method() {
        long begin = new Date().getTime();
		for(int i=0;i<10000000;i++) stub(); 
        long end = new Date().getTime();
        return end-begin;
	}
	
	private void stub() {
	}

	/**
	 * Info method, provides the user/server with some basic info on
	 * this Servlet
 	 */
	public String getServletInfo() {
		return ("Performance tester, for jit and other things - Rob Vermeulen");
	}
}
