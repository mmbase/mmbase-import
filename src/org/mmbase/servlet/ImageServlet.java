/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.BufferedOutputStream;

import java.util.List;
import java.util.Vector;
import java.util.Date;

import org.mmbase.module.builders.AbstractImages;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.RFC1123;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ImageServlet handles cached images. You have to put them in the
 * cache yourself. The cache() function of Images can be used for
 * this.
 *
 * @version $Id: ImageServlet.java,v 1.4 2002-04-12 08:56:05 pierre Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class ImageServlet extends  MMBaseServlet {
    private static Logger log;
    private long originalImageExpires;

    /**
     */
    public ImageServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log = Logging.getLoggerInstance(ImageServlet.class.getName());
        String origExpires = getInitParameter("expire");
        if (origExpires == null) {
            // default: one hour
            originalImageExpires = 60*60*1000;
        } else {
            originalImageExpires = new Integer(origExpires).intValue() * 1000;
        }
        // make sure this servlet is known to process images
        associate("image-processing",config.getServletName());
        // clear the status of images
        // maybe this should be called elsewhere,
        // and servlet-data depending classes should register?
        AbstractImages.clear();
    }

    /**
     * Overrides parent function. The current time is returned now, but I wonder is this is ok.
     **/
    protected long getLastModified(HttpServletRequest req) {
        return System.currentTimeMillis();
    }

    /**
     * Serves (cached) images.
     *
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        AbstractImages icaches = (AbstractImages) mmbase.getMMObject("icaches");
        String query = req.getQueryString();
        if (query == null) { // also possible to use /img.db/<number>
            query = new java.io.File(req.getRequestURI()).getName();
        }
        log.info("Gonna do image "+query);

        Integer imageNumber = new Integer(query);
        Vector params = new Vector();
        params.add(imageNumber);


        byte[] bytes   = icaches.getImageBytes(params);
        if (bytes == null) {
            res.sendError(res.SC_NOT_FOUND, "Cached image with number " + imageNumber + " does not exist, did you cache your image?");
            return;
        }
        int    filesize = bytes.length;

        res.setContentType(icaches.getImageMimeType(params));
        res.setContentLength(filesize);

        String now  = RFC1123.makeDate(new Date());
        res.setHeader("Date", now);


        if (icaches.getNode(imageNumber.intValue()).parent.getTableName().equals("icaches")) {
            // cached images never expire, they cannot change without receiving a new number, thus changing the URL.
            Date never = new Date(System.currentTimeMillis() + (long) (365.25 * 24 * 60 * 60 * 1000));
            // one year in future, this is considered to be sufficiently 'never'.
            res.setHeader("Expires", RFC1123.makeDate(never));
        } else { // 'images'
            // images themselves can expire,  the expiration time is set in init-param 'expire'.
            Date later =  new Date(System.currentTimeMillis() + originalImageExpires);
            res.setHeader("Expires", RFC1123.makeDate(later));
        }
        BufferedOutputStream out=null;
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
