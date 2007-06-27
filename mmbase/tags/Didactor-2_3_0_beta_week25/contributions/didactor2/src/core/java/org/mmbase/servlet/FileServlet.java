/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import org.mmbase.module.core.*;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.io.*;

import org.mmbase.util.logging.*;


/**
 *
 * @version $Id: FileServlet.java,v 1.1 2007-04-30 16:51:42 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9
 * @see    AttachmentServlet
 */
public class FileServlet extends BridgeServlet {
    private static Logger log;

    private static File files = null;
    //private static final Properties properties= new Properties();

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(FileServlet.class);
    }
    public static void init(HttpServletResponse res) {
        if (files == null) {
            if (log == null) {
                log = Logging.getLoggerInstance(FileServlet.class);
            }
            if (mmbase == null) throw new RuntimeException("MMBase is null");
            String dataDir = mmbase.getInitParameter("datadir");
            if (dataDir != null && ! "".equals(dataDir)) {
                File data;
                if (dataDir.startsWith(".")) {
                    data = new File(new File(MMBaseContext.getServletContext().getRealPath("WEB-INF/data/")), dataDir);
                } else if (dataDir.startsWith("/")) {
                    data = new File(dataDir);
                } else {
                    data = new File(MMBaseContext.getServletContext().getRealPath(dataDir));
                }
                files = new File(data, "files");
            } else {
                files = new File(MMBaseContext.getServletContext().getRealPath("WEB-INF/data/files"));
            }

            
            if (! files.exists()) {
                if (files.mkdirs()) {
                    log.info("Directory " + files + " was created");
                } else {
                    log.warn("Directory " + files + " could not be created");
                }
            }
            log.info("Using datadir " + files);

        }
        
    }
    protected boolean checkInited(HttpServletResponse res) throws ServletException, IOException  {
        boolean inited = super.checkInited(res);
        if (inited) {
            init(res);
            /*
            try {
                properties.load(new FileInputStream(new File(files, ".mmbaseowners")));
            } catch (Exception e) {
                log.warn(e);
            }
            */
        }
        return inited;
    }
    public String getServletInfo()  {
        return "Serves files from <MMBase data directory>/files";
    }

    protected Map<String, Integer> getAssociations() {
        Map<String, Integer> a = super.getAssociations();
        a.put("files",      50);  
        return a;
    }
    

    public static File getFile(String pathInfo, ServletResponse res) {
        if (res != null) init((HttpServletResponse) res);
        if (pathInfo == null) {
            return files;
        } else {
            return new File(files, pathInfo);
        }
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = getFile(req.getPathInfo(), resp);
        if (! file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The file '" + req.getPathInfo() + "' does not exist");
            log.debug("" + file + " does not exist");
            return;
        }
        if (! file.canRead()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + req.getPathInfo() + "' may not be read");
            log.debug("" + file + " may not be read");
            return;
        }
        resp.setContentType(getServletContext().getMimeType(file.getName()));
        resp.setContentLength((int) file.length());
        BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[1024];
        int b = 0;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        out.flush();
        in.close();
        out.close();
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = getFile(req.getPathInfo(), resp);
        if (file.exists()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + req.getPathInfo() + "' already exists");
            return;
        }
        /*
        Cloud cloud = getCloud(readQuery(req.getQueryString()));
        if (cloud.getUser().getRank() == Rank.ANONYMOUS) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + req.getPathInfo() + "' already exists");
            return;
        }
        */
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        BufferedInputStream in = new BufferedInputStream(req.getInputStream());
        byte[] buf = new byte[1024];
        int b = 0;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        out.flush();
        in.close();
        out.close();
    }

    protected long getLastModified(HttpServletRequest req) {
        return getFile(req.getPathInfo(), null).lastModified();
    }
}


