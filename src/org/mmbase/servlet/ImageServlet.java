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

import java.util.List;
import java.util.Vector;
import java.util.Date;

import org.mmbase.bridge.Node;

import org.mmbase.util.RFC1123;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ImageServlet handles cached images. You have to put them in the
 * cache yourself. The cache() function of Images can be used for
 * this. An URL can be gotten with cachepath().
 *
 * @version $Id: ImageServlet.java,v 1.8 2002-06-28 21:10:10 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class ImageServlet extends BridgeServlet {
    private static Logger log;
    private long originalImageExpires;

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(ImageServlet.class.getName());
        String origExpires = getInitParameter("expire");
        if (origExpires == null) {
            // default: one hour
            originalImageExpires = 60 * 60 * 1000;
        } else {
            originalImageExpires = new Integer(origExpires).intValue() * 1000;
        }
        // make sure this servlet is known to process images
        associate("images", getServletName());
    }

    /**
     * Overrides parent function. The current time is returned now, but I wonder is this is ok.
     **/
    protected long getLastModified(HttpServletRequest req) {
        return System.currentTimeMillis();
    }

    /**
     * Serves images (and cached images).
     *
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Node node = getNode(req, res);

        if (node == null) return;

        byte[] bytes   = node.getByteValue("handle");
        if (bytes == null) {
            res.sendError(res.SC_NOT_FOUND, "Node with number " + node.getNumber() + " does contain a handle field.");
            return;

        }
        int    filesize = bytes.length;

        res.setContentType(node.getStringValue("mimetype()"));
        res.setContentLength(filesize);

        String now  = RFC1123.makeDate(new Date());
        res.setHeader("Date", now);

        String fileName;
        if (node.getNodeManager().getName().equals("icaches")) {
            fileName = getCloud().getNode(node.getIntValue("id")).getStringValue("title");
            // cached images never expire, they cannot change without receiving a new number, thus changing the URL.
            Date never = new Date(System.currentTimeMillis() + (long) (365.25 * 24 * 60 * 60 * 1000));
            // one year in future, this is considered to be sufficiently 'never'.
            res.setHeader("Expires", RFC1123.makeDate(never));
            
        } else { // 'images'
            fileName = node.getStringValue("title"); 
            // images themselves can expire,  the expiration time is set in init-param 'expire'.
            Date later =  new Date(System.currentTimeMillis() + originalImageExpires);
            res.setHeader("Expires", RFC1123.makeDate(later));
        }
        if (fileName == null || fileName.equals("")) fileName="image_from_mmbase";

        res.setHeader("Content-Disposition", "inline; filename=\"" + fileName  + "." + node.getStringValue("format()") + "\"");


        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(res.getOutputStream());
        } catch (java.io.IOException e) {
            log.error(Logging.stackTrace(e));
        }


        out.write(bytes, 0, filesize);
        out.flush();
        out.close();
    }



    public String getServletInfo()  {
        return "Serves cached MMBase images.";
    }
}
