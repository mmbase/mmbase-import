/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mmbase.bridge.Node;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;


/**
 * Base servlet for nodes with a 'handle' field. It serves as a basic implementation for more
 * specialized servlets. The mime-type is always application/x-binary, forcing the browser to
 * download.
 *
 * @version $Id: HandleServlet.java,v 1.15 2004-04-07 14:46:39 keesj Exp $
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
        log = Logging.getLoggerInstance(HandleServlet.class);

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
    protected boolean setContent(HttpServletRequest req, HttpServletResponse res, Node node, String mimeType) throws IOException {
        // Try to find a sensible filename to use in the content-disposition header.
        String fileName = node.getStringValue("filename");
        if (fileName == null || fileName.equals("")) {
            fileName = node.getStringValue("title");
            if (fileName == null || fileName.equals("")) { // give it up
                fileName = "mmbase-attachment";
            }
            // try to add an extension. 
            String format = node.getFunctionValue("format", null).toString();
            if (format != null && !format.equals("")) {
                fileName = fileName + "." + format;
            }
            // could also use the mime type to guess an extension!
        }
        StringObject fn = new StringObject(fileName);
        fn.replace(" ", "_");


        // Why we don't set Content-Disposition:
        // - IE can't handle that. (IE sucks!)

        res.setHeader("Content-Disposition", "attachment; filename=\""  + fn + "\"");
        //res.setHeader("X-MMBase-1", "Not sending Content-Disposition because this might confuse Microsoft Internet Explorer");
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
     * Sets cache-controlling headers. Only nodes which are to be served to 'anonymous' might be
     * (front proxy) cached. To other nodes there might be read restrictions, so they should not be
     * stored in front-proxy caches.
     * 
     * @return true if cacheing is disabled.
     * @since MMBase-1.7
     */
    protected boolean setCacheControl(HttpServletResponse res, Node node) {
        if (!node.getCloud().getUser().getRank().equals(org.mmbase.security.Rank.ANONYMOUS.toString())) {
            res.setHeader("Cache-Control", "private");
            // res.setHeader("Pragma", "no-cache"); // for http 1.0 : is frustrating IE when https
            // we really don't want this to remain in proxy caches, but the http 1.0 way is making IE not work.
            return true;
        } else {
            res.setHeader("Cache-Control", "public");
            return false;
        }
    }

    /**
     * Serves a node with a byte[] handle field as an attachment.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Node node = getNode(req, res);
        if (node == null) {
            log.debug("No node found, returning");
            return;
        }
        if (!node.getNodeManager().hasField("handle")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No handle found in node " + node.getNumber());
            return;
        }
        byte[] bytes = node.getByteValue("handle");

        // fill the headers
        res.setDateHeader("Date", System.currentTimeMillis());

        String mimeType = getMimeType(node);
        res.setContentType(mimeType);

        /*
         *  remove additional information left by PhotoShop 7 in jpegs
         * , this information may crash Internet Exploder. that's why you need to remove it.
         * With PS 7, Adobe decided by default to embed XML-encoded "preview" data into JPEG files, 
         * using a feature of the JPEG format that permits embedding of arbitrarily-named "profiles". 
         * In theory, these files are valid according to the JPEG specifications. 
         * However they break many applications, including Quark and, significantly, 
         * various versions of Internet Explorer on various platforms. 
         */
        if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg")) {
            bytes = IECompatibleJpegInputStream.process(bytes);
            // res.setHeader("X-MMBase-2", "This image was filtered, because Microsoft Internet Explorer might crash otherwise");
        }

        if (!setContent(req, res, node, mimeType)) {
            return;
        }
        setExpires(res, node);
        setCacheControl(res, node);
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
