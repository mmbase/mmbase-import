/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.BufferedOutputStream;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;


/**
 * As AttachmentServlet, but the mime-type is always application/x-binary, forcing the browser to
 * download.
 *
 * @version $Id: HandleServlet.java,v 1.3 2002-09-06 21:33:38 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @see ImageServlet
 * @see AttachmentServlet
 */
public class HandleServlet extends BridgeServlet {
    private static Logger log;

    private long expires; // expires so many milliseconds after serving


    protected Map getAssociations() {
        Map a = super.getAssociations();
        // Can do the following:
        a.put("attachments", new Integer(0));
        a.put("downloads",   new Integer(20)); // good at this (because it does not determin the mime-type)
        a.put("images",      new Integer(-10)); // bad in images (no mime-type, no awareness of icaches)
        return a;
    }

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(AttachmentServlet.class.getName());

        String expiresParameter = getInitParameter("expire");
        if (expiresParameter == null) {
            // default: one hour
            expires = 60 * 60 * 1000;
        } else {
            expires = new Integer(expiresParameter).intValue() * 1000;
        }
    }


    protected String getMimeType(Node node) {
        return "application/x-binary";
    }

    /**
     * Sets the content disposition header.
     * @return true on success
     */
    protected boolean setContent(HttpServletResponse res, Node node, String mimeType) throws IOException {
        // Try to find a sensible filename to use in the content-disposition header.
        String fileName = node.getStringValue("filename");
        if (fileName == null || fileName.equals("")) {
            fileName = node.getStringValue("title");
            if (fileName == null || fileName.equals("")) {  // give it up
                fileName = "mmbase-attachment";
            } 
            // try to add an extension. 
            String format = node.getStringValue("format()");
            if (format != null && !format.equals("")) {
                fileName = fileName + "." + format;
            }
            // could also use the mime type to guess an extension!
        }
        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        return true;
    }

    /**
     * Sets the exires header.
     * @return true on sucess
     */
    protected boolean setExpires(HttpServletResponse res, Node node) {
        if (node.getNodeManager().getName().equals("icaches")) {
            // cached images never expire, they cannot change without receiving a new number, thus changing the URL.
            long never = System.currentTimeMillis() + (long) (365.25 * 24 * 60 * 60 * 1000);
            // one year in future, this is considered to be sufficiently 'never'.
            res.setDateHeader("Expires", never);           
        } else {
            long later = System.currentTimeMillis() + expires;
            res.setDateHeader("Expires", later);
        }
        return true;
    }

    /**
     * Serves a node with a byte[] handle field as an attachment.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {    
        Node node = getNode(req, res);
        if (node == null) return;
        byte[] bytes = node.getByteValue("handle"); 
        if (bytes == null) {
            res.sendError(res.SC_NOT_FOUND, "No handle found in node " + node.getNumber());
            return;
        }
        // fill the headers
        res.setDateHeader("Date", System.currentTimeMillis());                    

        String mimeType = getMimeType(node);
        res.setContentType(mimeType);

        if (!setContent(res, node, mimeType)) return;
        setExpires(res, node);                           

        sendBytes(res, bytes);
    }


    /**
     * Utility function to send bytes at the end of doGet implementation.
     */
    final protected void sendBytes(HttpServletResponse res, byte[] bytes) throws IOException {
        int fileSize = bytes.length;
        res.setContentLength(fileSize);

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(res.getOutputStream());
        } catch (java.io.IOException e) {
            log.error(Logging.stackTrace(e));
        }
        out.write(bytes, 0, fileSize);
        out.flush();
        out.close();
    }


}
