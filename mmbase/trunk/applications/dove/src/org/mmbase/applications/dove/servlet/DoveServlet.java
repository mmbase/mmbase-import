/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.dove.servlet;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.apache.xerces.parsers.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.dove.*;
import org.mmbase.servlet.*;

/**
 * This servlet routes RPC requests (represented in xml) to the intended method of
 * the 'Dove' object.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.5
 * @version $Id: DoveServlet.java,v 1.3 2002-02-27 16:54:21 pierre Exp $
 */
public class DoveServlet extends JamesServlet {

    //logger
    private static Logger log = Logging.getLoggerInstance(DoveServlet.class.getName());

    /**
     * Handles a request using the GET method.
     * No communication is handled through GET - this method is for testing whether the servlet is online.
     * @param req the HHTP Request object
     * @param res the HHTP Response object
     */
    public void doGet (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        PrintWriter out = res.getWriter ();

        res.setContentType("text/html");
        out.println("<html><head><title>Dove</title></head>");
        out.println ("<body><h1>Dove RPC Router</h1>");
        out.println ("<p>The Dove servlet is active. use HTTP Post to send RPC commands.</p></body></html>");
    }

    /**
     * Handles a request using the POST method.
     * Retrieves the value of the 'xml' parameter, and parses the bodfu of that
     * parameter as an xml text. The resulting DOM tree is then passed to the Dove
     * class, which runs the RPCs described in that tree.
     * The result of Dove (also a DOM tree) is returned as xml to the client.
     * The mime type of the result is 'text/xml', unless the 'plain' parameter
     * is set to 'yes', in which case the mime type is 'text/plain'.
     * Specifying a 'pretty' parameter with value 'yes' results in pretty printed xml.
     * Both these parameters are ment for debugging.
     * <br />
     * XXX: Possibly we want to use xml directly in the body, instead of parameters.
     * <br />
     * XXX: Daniel suggested using CRC to validate calls. This is not implemented yet.
     * <br />
     * XXX: We have not yet established how we will use session-info and usercontext.
     *
     * @param req the HHTP Request object
     * @param res the HHTP Response object
     */
    public void doPost (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        String error="unknown error";
        String errortype="unknown";
        boolean pretty="yes".equals(req.getParameter("pretty"));
        boolean plain="yes".equals(req.getParameter("plain"));
        try {

            DOMParser parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            // are we going to enforce grammar?
            // My advise would be yes, but for debugging purposes I
            // have made it optional.
            parser.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            EntityResolver resolver = new XMLEntityResolver();
            parser.setEntityResolver(resolver);

            DoveErrorHandler errorhandler = new DoveErrorHandler();
            parser.setErrorHandler(errorhandler);


            // Right now we read conmtent from parameters
            // Maybe we want teh xml to eb directly in the body?
            // Depends on how the editors will post.
            String s = req.getParameter("xml");
            log.info("received : "+s);
            StringBufferInputStream sin= new StringBufferInputStream(s);
            InputSource in = new InputSource(sin);

// alternate code when not using parameters:
//            InputSource in = new InputSource(req.getInputStream());
            parser.parse(in);

            Document document = parser.getDocument();

            if (errorhandler.erroroccurred) {
                error=errorhandler.parsingerrors;
                errortype="parser";
            } else {
                Element rootin=document.getDocumentElement();
                if (rootin.getTagName().equals(Dove.REQUEST)) {
                    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = docBuilder.newDocument();
                    Element rootout =doc.createElement(Dove.RESPONSE);
                    doc.appendChild(rootout);
                    Dove birdy=new Dove(doc);
                    birdy.doRequest(rootin,rootout);
                    res.setStatus(200,"OK");
                    if (plain) {
                        res.setContentType("text/plain");
                    } else {
                        res.setContentType("text/xml");
                    }

                    BufferedOutputStream out=new BufferedOutputStream(res.getOutputStream());
                    OutputFormat format = new OutputFormat(doc);
                    format.setIndenting(pretty);
                    format.setPreserveSpace(!pretty);
//                    format.setOmitXMLDeclaration(true);
//                    format.setOmitDocumentType(true);
                    XMLSerializer prettyXML = new XMLSerializer(out,format);
                    prettyXML.serialize(doc);
                    out.flush();
                    return;
                } else {
                    error="No request found.";
                    errortype="parser";
                }
            }
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
            error="System error: "+e;
            errortype="server";
        }
        ServletOutputStream out = res.getOutputStream();
        res.setStatus(200,"OK");
        if (plain) {
            res.setContentType("text/plain");
        } else {
            res.setContentType("text/xml");
        }
        out.println("<response><error type=\""+errortype+"\">"+error+"</error></response>");
        out.flush();
    }

    /**
     * Dove Error handler for catching and storing parsing exceptions.
     * The Error handler catches all exceptions and stores their descriptions.
     * These can then be retrieved by the Dove servlet, so it can generate an
     * error response.
     */
    public class DoveErrorHandler implements ErrorHandler {

        /**
         * The errors that occurred during the parse.
         */
        public String parsingerrors="";

        /**
         * Indicates whether any errors occurred.
         */
        public boolean erroroccurred=false;

        /**
         * Logs an error and adds it to the list of parsing errors.
         * @param ex the parsing exception describing the error
         */
        public void error(SAXParseException ex) {
            erroroccurred=true;
            String s = getErrorString(ex);
            log.error(s);
            parsingerrors=parsingerrors+s+"\n";
        }

        /**
         * Logs a warning.
         * Warnings are not added to the list of parsing errors.
         * @param ex the parsing exception describig the error
         */
        public void warning(SAXParseException ex) {
            log.warn(getErrorString(ex));
        }

        /**
         * Logs a fatal error.
         * Fatal errors are not added to the list of parsing errors, they
         * throw an exception and abort parsing.
         * @param ex the parsing exception describing the error
         */
        public void fatalError(SAXParseException ex) throws SAXException {
            log.error("[Fatal Error] "+ getErrorString(ex));
            throw ex;
        }

        /**
         * Returns a string describing the error.
         * @param ex the parsing exception describing the error
         * @return the error string
         */
        private String getErrorString(SAXParseException ex) {
            String msg = "";
            String systemId = ex.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf('/');
                msg=systemId.substring(index + 1)+":";
            }
            return msg +ex.getLineNumber()+":"+
                        ex.getColumnNumber()+":"+
                        ex.getMessage();
        }
    }

}
