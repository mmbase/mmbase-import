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

import java.util.Vector;

import org.mmbase.module.builders.AbstractImages;

import org.mmbase.module.core.MMObjectNode;

//import org.mmbase.util.RFC1123;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ImageServlet handles cached images. You have to put them in the
 * cache yourself. The cache() function of Images can be used for
 * this.
 *
 * @version $Id: ImageServlet.java,v 1.1 2002-03-13 12:07:55 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class ImageServlet extends  MMBaseServlet {
    private static Logger log;

    /**
     */
    public ImageServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log = Logging.getLoggerInstance(ImageServlet.class.getName());
    }

    /**
     * Serves (cached) images.
     * 
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        AbstractImages icaches = (AbstractImages) mmbase.getMMObject("icaches");
        Integer imageNumber = new Integer(req.getQueryString());
        Vector params = new Vector();
        params.add(imageNumber);
 
        byte[] bytes   = icaches.getImageBytes(params);
        if (bytes == null) {
            throw new ServletException("Cached image with number " + imageNumber + " does not exist, did you cache your image?");
        }
        int    filesize = bytes.length;

        res.setContentType(icaches.getImageMimeType(params));
        res.setContentLength(filesize);
        
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
