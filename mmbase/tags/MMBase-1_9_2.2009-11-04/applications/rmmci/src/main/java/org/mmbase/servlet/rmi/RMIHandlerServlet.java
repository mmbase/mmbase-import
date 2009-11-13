package org.mmbase.servlet.rmi;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.util.logging.*;

/**
 * The default RMI socket factory contains several "fallback"
 * mechanisms which enable an RMI client to communicate with a remote
 * server.  When an RMI client initiates contact with a remote server,
 * it attempts to establish a connection using each of the following
 * protocols in turn, until one succeeds:
 *
 * 1. Direct TCP connection.
 * 2. Direct HTTP connection.
 * 3. Proxy connection (SOCKS or HTTP).
 * 4. Connection on port 80 over HTTP to a CGI script.
 * 5. Proxy HTTP connection to CGI script on port 80.
 *
 * The RMI ServletHandler can be used as replacement for the
 * java-rmi.cgi script that comes with the Java Development Kit (and
 * is invoked in protocols 4 and 5 above).  The java-rmi.cgi script
 * and the ServletHandler both function as proxy applications that
 * forward remote calls embedded in HTTP to local RMI servers which
 * service these calls.  The RMI ServletHandler enables RMI to tunnel
 * remote method calls over HTTP more efficiently than the existing
 * java-rmi.cgi script.  The ServletHandler is only loaded once from
 * the servlet administration utility.  The script, java-rmi.cgi, is
 * executed once every remote call.
 *
 * The ServletHandler class contains methods for executing as a Java
 * servlet extension.  Because the RMI protocol only makes use of the
 * HTTP post command, the ServletHandler only supports the
 * <code>doPost</code> <code>HttpServlet</code> method.  The
 * <code>doPost</code> method of this class interprets a servlet
 * request's query string as a command of the form
 * "<command>=<parameters>".  These commands are represented by the
 * abstract interface, <code>RMICommandHandler</code>.  Once the
 * <code>doPost</code> method has parsed the requested command, it
 * calls the execute method on one of several command handlers in the
 * <code>commands</code> array.
 *
 * The command that actually proxies remote calls is the
 * <code>ServletForwardCommand</code>.  When the execute method is
 * invoked on the ServletForwardCommand, the command will open a
 * connection on a local port specified by its <code>param</code>
 * parameter and will proceed to write the body of the relevant post
 * request into this connection.  It is assumed that an RMI server
 * (e.g. SampleRMIServer) is listening on the local port, "param."
 * The "forward" command will then read the RMI server's response and
 * send this information back to the RMI client as the body of the
 * response to the HTTP post method.
 *
 * Because the ServletHandler uses a local socket to proxy remote
 * calls, the servlet has the ability to forward remote calls to local
 * RMI objects that reside in the ServletVM or outside of it.
 *
 * Servlet documentation may be found at the following location:
 *
 * http://jserv.javasoft.com/products/java-server/documentation/
 *        webserver1.0.2/apidoc/Package-javax.servlet.http.html
 */
public class RMIHandlerServlet extends MMBaseServlet {
    private static Logger log = Logging.getLoggerInstance(RMIHandlerServlet.class.getName());
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.service("HTTP RMI bridge loaded");
    }
    
    @SuppressWarnings("unused")
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            // Command and parameter for this POST request.
            String queryString = req.getQueryString();
            String command, param;
            int delim = queryString.indexOf("=");
            if (delim == -1) {
                command = queryString;
                param = "";
            } else {
                command = queryString.substring(0, delim);
                param = queryString.substring(delim + 1);
            }
            
            log.debug("command/param: " + command +"/"  + param);
            try {
                if (command.equals("forward")){
                    forward(req,res,param);
                } else if (command.equals("gethostname")){
                    getHostname(req,res,param);
                } else if (command.equals("ping")){
                    ping(req,res,param);
                } else if (command.equals("hostname")){
                    hostname(req,res,param);
                } else {
                    returnClientError(res, "invalid command: " + command);
                }
            } catch (ServletClientException e) {
                log.debug(e.getMessage(), e);
                returnClientError(res, "client error: " + e.getMessage());
            } catch (ServletServerException e) {
                log.debug(e.getMessage(), e);
                returnServerError(res, "internal server error: " + e.getMessage());
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            returnServerError(res, "internal error: " +  e.getMessage() + " " + Logging.stackTrace(e));
        }
    }
    
    /**
     * Provide more intelligible errors for methods that are likely to
     * be called.  Let unsupported HTTP "do*" methods result in an
     * error generated by the super class.
     *
     * @param req  http Servlet request, contains incoming command and
     *             arguments
     *
     * @param res  http Servlet response
     *
     * @exception  ServletException and IOException when invoking
     *             methods of <code>req<code> or <code>res<code>.
     */
    @SuppressWarnings("unused")
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        returnClientError(res, "GET Operation not supported: Can only forward POST requests.");
    }
    
    @SuppressWarnings("unused")
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        returnClientError(res,
        "PUT Operation not supported: " +
        "Can only forward POST requests.");
    }
    
    @Override
    public String getServletInfo() {
        return "RMI Call Forwarding Servlet";
    }
    
    /**
     * Return an HTML error message indicating there was error in
     * the client's request.
     *
     * @param res Servlet response object through which <code>message</code>
     *            will be written to the client which invoked one of
     *            this servlet's methods.
     * @param message Error message to be written to client.
     */
    private static void returnClientError(HttpServletResponse res, String message)
    throws IOException {
        
        res.sendError(HttpServletResponse.SC_BAD_REQUEST,
        "<HTML><HEAD>" +
        "<TITLE>Java RMI Client Error</TITLE>" +
        "</HEAD>" +
        "<BODY>" +
        "<H1>Java RMI Client Error</H1>" +
        message +
        "</BODY></HTML>");
        
        log.warn(HttpServletResponse.SC_BAD_REQUEST + "Java RMI Client Error" + message);
    }
    
    /**
     * Return an HTML error message indicating an internal error
     * occurred here on the server.
     *
     * @param res Servlet response object through which <code>message</code>
     *            will be written to the servlet client.
     * @param message Error message to be written to servlet client.
     */
    private static void returnServerError(HttpServletResponse res,
    String message)
    throws IOException {
        
        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        "<HTML><HEAD>" +
        "<TITLE>Java RMI Server Error</TITLE>" +
        "</HEAD>" +
        "<BODY>" +
        "<H1>Java RMI Server Error</H1>" +
        message + "</BODY></HTML>");
        
        log.warn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR +
        "Java RMI Server Error: " +
        message);
    }
    
    
    
    /**
     * Execute the forward command.  Forwards data from incoming servlet
     * request to a port on the local machine.  Presumably, an RMI server
     * will be reading the data that this method sends.
     *
     * @param req   The servlet request.
     * @param res   The servlet response.
     * @param param Port to which data will be sent.
     */
    public void forward(HttpServletRequest req, HttpServletResponse res,  String param)
    throws ServletClientException, ServletServerException, IOException {
        
        int port;
        try {
            port = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new ServletClientException("invalid port number: " +
            param);
        }
        if (port <= 0 || port > 0xFFFF)
            throw new ServletClientException("invalid port: " + port);
        if (port < 1024)
            throw new ServletClientException("permission denied for port: "
            + port);
        
        byte buffer[];
        Socket socket;
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
        } catch (IOException e) {
            throw new ServletServerException("could not connect to " +
            "local port");
        }
        
        // read client's request body
        DataInputStream clientIn =
        new DataInputStream(req.getInputStream());
        buffer = new byte[req.getContentLength()];
        try {
            clientIn.readFully(buffer);
        } catch (EOFException e) {
            throw new ServletClientException("unexpected EOF " +
            "reading request body");
        } catch (IOException e) {
            throw new ServletClientException("error reading request" +
            " body");
        }
        
        DataOutputStream socketOut = null;
        // send to local server in HTTP
        try {
            socketOut =
            new DataOutputStream(socket.getOutputStream());
            socketOut.writeBytes("POST / HTTP/1.0\r\n");
            socketOut.writeBytes("Content-length: " +
            req.getContentLength() + "\r\n\r\n");
            socketOut.write(buffer);
            socketOut.flush();
        } catch (IOException e) {
            throw new ServletServerException("error writing to server");
        }
        
        // read response from local server
        DataInputStream socketIn;
        try {
            socketIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new ServletServerException("error reading from " +
            "server");
        }
        String key = "Content-length:".toLowerCase();
        boolean contentLengthFound = false;
        String line;
        int responseContentLength = -1;
        do {
            try {
                line = socketIn.readLine();
            } catch (IOException e) {
                throw new ServletServerException("error reading from server");
            }
            if (line == null)
                throw new ServletServerException(
                "unexpected EOF reading server response");
            
            if (line.toLowerCase().startsWith(key)) {
                if (contentLengthFound)
                    ; // what would we want to do in this case??
                responseContentLength =
                Integer.parseInt(line.substring(key.length()).trim());
                contentLengthFound = true;
            }
        } while ((line.length() != 0) &&
        (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));
        
        if (!contentLengthFound || responseContentLength < 0)
            throw new ServletServerException(
            "missing or invalid content length in server response");
        buffer = new byte[responseContentLength];
        try {
            socketIn.readFully(buffer);
        } catch (EOFException e) {
            throw new ServletServerException(
            "unexpected EOF reading server response");
        } catch (IOException e) {
            throw new ServletServerException("error reading from server");
        }
        
        // send local server response back to servlet client
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/octet-stream");
        res.setContentLength(buffer.length);
        
        try {
            OutputStream out = res.getOutputStream();
            out.write(buffer);
            out.flush();
        } catch (IOException e) {
            throw new ServletServerException("error writing response");
        } finally {
            socketOut.close();
            socketIn.close();
        }
    }
    public void getHostname(HttpServletRequest req, HttpServletResponse res, String param)
    throws IOException {
        
        byte[] getHostStringBytes = req.getServerName().getBytes();
        
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/octet-stream");
        res.setContentLength(getHostStringBytes.length);
        
        OutputStream out = res.getOutputStream();
        out.write(getHostStringBytes);
        out.flush();
    }
    
    
    public void ping(HttpServletRequest req, HttpServletResponse res, String param) {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/octet-stream");
        res.setContentLength(0);
    }
    
    public void hostname(HttpServletRequest req, HttpServletResponse res, String param)
    throws IOException {
        
        PrintWriter pw = res.getWriter();
        
        pw.println("");
        pw.println("<HTML>" +
        "<HEAD><TITLE>Java RMI Server Hostname Info" +
        "</TITLE></HEAD>" +
        "<BODY>");
        pw.println("<H1>Java RMI Server Hostname Info</H1>");
        pw.println("<H2>Local host name available to Java VM:</H2>");
        pw.print("<P>InetAddress.getLocalHost().getHostName()");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            
            pw.println(" = " + localHostName);
        } catch (UnknownHostException e) {
            pw.println(" threw java.net.UnknownHostException");
        }
        
        pw.println("<H2>Server host information obtained through Servlet " +
        "interface from HTTP server:</H2>");
        pw.println("<P>SERVER_NAME = " + req.getServerName());
        pw.println("<P>SERVER_PORT = " + req.getServerPort());
        pw.println("</BODY></HTML>");
    }
    
    
    /**
     * ServletClientException is thrown when an error is detected
     * in a client's request.
     */
    protected static class ServletClientException extends Exception {
        public ServletClientException(String s) {
            super(s);
        }
    }
    
    /**
     * ServletServerException is thrown when an error occurs here on the server.
     */
    protected static class ServletServerException extends Exception {
        public ServletServerException(String s) {
            super(s);
        }
    }
}
