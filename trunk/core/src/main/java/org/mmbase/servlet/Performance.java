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

/**
 * Performance Servlet is used as a basic Servlet to test whether the installation of succeeded.
 * It also does a very basic test to measure how fast the JVM is.
 *
 * @author  vpro
 * @version $Id$
 */
public class Performance extends BridgeServlet {

    public static final long INT_LOOP = 20000000;
    public static final long STRING_LOOP = 2500000;
    public static final long METHOD_LOOP = 10000000;
    public static final String TEST_STRING = "test";

    /**
     * @javadoc
     */
    public void init() throws ServletException {
        super.init();
    }

    /**
     * Called by the server when a request is done.
     * @javadoc
     */
    public synchronized void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Open	a output stream so you can write to the client
        PrintStream out = new PrintStream(res.getOutputStream());

        // Set the content type of this request
        res.setContentType("text/html");

        // Write header to client
        //res.writeHeaders();

        // WRITE MESSAGE TO CLIENT
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\" >");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        out.println("<head><title>Performance</title></head>");
        out.println("<body>");
        out.println("<h1>Performance Servlet</h1>");
        out.println("<p>" + getServletInfo() + "</p>");
        long time = intloop();
        out.println("<p><strong>Performance Test (simple loop "+INT_LOOP+" times) </strong><br />");
        out.println("takes "+time+"ms, "+(((double)time)/INT_LOOP)+" ms/loop </p>");
        time = string();
        out.println("<p><strong>Performance Test (compare '"+TEST_STRING+"' "+STRING_LOOP+" times) </strong><br />");
        out.println("takes "+time+"ms, "+(((double)time)/STRING_LOOP)+" ms/loop </p>");

        out.println("<p><strong>Performance Test (loop through the provided stub method "+METHOD_LOOP+" times) </strong><br />");
        out.println("takes "+time+"ms, "+(((double)time)/METHOD_LOOP)+" ms/loop </p>");
        out.println("</body>");
        out.println("</html>");
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
        String test = TEST_STRING;
        for (int i = 0; i < 2500000; i++) {
            test.equals(TEST_STRING);
        }
        long end = new Date().getTime();
        return end - begin;
    }

    /**
     * @javadoc
     */
    protected void stub() {
    }

    /**
     * Info method, provides the user/server with some basic info on this Servlet
     * @return a descriptive text
     */
    public String getServletInfo() {
        return ("Performance tester, for JIT and other things");
    }
}

