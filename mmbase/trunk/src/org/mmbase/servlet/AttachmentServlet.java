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

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.RFC1123;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.Date;

/**
 *
 * @version $Id: AttachmentServlet.java,v 1.1 2002-06-27 12:57:44 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class AttachmentServlet extends  MMBaseServlet {
    private static Logger log;


    private long expires;
    /**
     */
    public AttachmentServlet() {
        super();
    }

    public void init() throws ServletException {
        log = Logging.getLoggerInstance(AttachmentServlet.class.getName());

        String expiresParameter = getInitParameter("expire");
        if (expiresParameter == null) {
            // default: one hour
            expires = 60 * 60 * 1000;
        } else {
            expires = new Integer(expiresParameter).intValue() * 1000;
        }

        associate("attachments", getServletName());
    }

    /**
     * Overrides parent function. The current time is returned now, but I wonder is this is ok.
     **/
    protected long getLastModified(HttpServletRequest req) {
        return System.currentTimeMillis();
    }

    /**
     * Serves (cached) attachements.
     *
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {        
        String query = req.getQueryString();

        if (query == null) { // also possible to use /attachments/<number>
            query = new java.io.File(req.getRequestURI()).getName();
        }
        MMObjectBuilder bul=mmbase.getTypeDef(); // just to have some builder.        
        MMObjectNode node = bul.getNode(query);

        if (node == null) { // can this happen?
            res.sendError(res.SC_NOT_FOUND, "Cannot find node " + query);
            return;
        }

        byte[] bytes = node.getByteValue("handle");
        if (bytes == null) {
            res.sendError(res.SC_NOT_FOUND, "No handle found in node " + query);
            return;
        }
        int    fileSize = bytes.length;

        String fileName = node.getStringValue("filename");
        if (fileName == null || fileName.equals("")) {
            fileName = "MMBase-attachment";
        }
        String mimeType = node.getStringValue("mimetype");
        if (mimeType == null || mimeType.equals("")) {
            mimeType = "application/x-binary";
        }

        res.setContentType(mimeType);
        res.setContentLength(fileSize);
        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        String now  = RFC1123.makeDate(new Date());
        res.setHeader("Date", now);
                

        // attachments could principaly expire,  the expiration time is set in init-param 'expire'.
        Date later =  new Date(System.currentTimeMillis() + expires);
        res.setHeader("Expires", RFC1123.makeDate(later));
        BufferedOutputStream out=null;
        try {
            out = new BufferedOutputStream(res.getOutputStream());
        } catch (java.io.IOException e) {
            log.error(Logging.stackTrace(e));
        }


        out.write(bytes, 0, fileSize);
        out.flush();
        out.close();
    }



    public String getServletInfo()  {
        return "Serves MMBase attachments.";
    }

}
