/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.logging.*;

/**
 * Performance Servlet is used as a basic Servlet to test whether the installation of succeeded.
 * It also does a very basic test to measure how fast the JVM is.
 *
 * @rename     Performance
 * @author     vpro
 * @version    $Id: performance.java,v 1.9 2002-06-28 07:49:24 pierre Exp $
 */
public class performance extends JamesServlet {
    // logging
    private static Logger log;

    /**
     * @javadoc
     */
    public void init() throws ServletException {
        super.init();
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(performance.class.getName());
        log.info("Init of servlet " + getServletConfig().getServletName() + ".");
    }

    /**
     * Called by the server when a request is done.
     * @javadoc
     */
    public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Open	a output stream so you can write to the client
        PrintStream out = new PrintStream(res.getOutputStream());

        // Set the content type of this request
        res.setContentType("text/html");

        // Write header to client
        //res.writeHeaders();

        // WRITE MESSAGE TO CLIENT
        out.println("<HTML>");
        out.println("<HEAD><TITLE>NWO</TITLE></HEAD>");
        out.println("<BODY BACKGROUND=\"/jamesdoc/images/" + getInitParameter("bg") + "\" BGCOLOR=\"#000000\"" +
                " TEXT=\"#FFFFFF\" LINK=\"#00FFFF\" VLINK=\"#00FFFF\">");
        out.println("me (servlet load test) = " + this + "<BR>");
        long time = intloop();
        out.println("intloop = " + time + "<BR>");
        time = string();
        out.println("stringloop = " + time + "<BR>");
        out.println("methodloop = " + method() + "<BR>");
        out.println("</BODY>");
        out.println("</HTML>");
        log.debug("performance servlet - Perftime = " + time);
    }

    /**
     * @javadoc
     */
    private long intloop() {
        long begin = new Date().getTime();
        for (int i = 0; i < 20000000; i++) {
            ;
        }
        long end = new Date().getTime();
        return end - begin;
    }

    /**
     * @javadoc
     */
    private long string() {
        long begin = new Date().getTime();
        String test = "test";
        for (int i = 0; i < 2500000; i++) {
            test.equals("test");
        }
        long end = new Date().getTime();
        return end - begin;
    }

    /**
     * @javadoc
     */
    private long method() {
        long begin = new Date().getTime();
        for (int i = 0; i < 10000000; i++) {
            stub();
        }
        long end = new Date().getTime();
        return end - begin;
    }


    /**
     * @javadoc
     */
    private void stub() {
    }

    /**
     * Info method, provides the user/server with some basic info on this Servlet
     * @return a descriptive text
     */
    public String getServletInfo() {
        return ("Performance tester, for jit and other things - Rob Vermeulen");
    }
}

