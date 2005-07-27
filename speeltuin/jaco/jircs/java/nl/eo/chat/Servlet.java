/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The server class is responsible for initializing and starting the needed
 * threads like incoming and outgoing translators and the chat engine
 * according to the server configuration file.
 *
 * @author Jaco de Groot
 */
public class Servlet extends HttpServlet {
    private Server server;
    private Thread serverThread;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String configFileName = getInitParameter("configfile");
        if (configFileName == null) {
            throw new ServletException("Parameter 'configfile' not specified.");
        }
        try {
            String root = config.getServletContext().getRealPath("/").toString();
            configFileName = root + "WEB-INF/" + configFileName;
            server = ServerConstructor.createServer(configFileName);
            serverThread = new Thread(server);
            serverThread.start();
            System.err.println("Chat server started");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        server.shutdown();
    }

}


