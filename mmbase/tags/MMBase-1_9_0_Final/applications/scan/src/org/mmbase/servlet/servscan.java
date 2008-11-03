/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * servscan is the 'extended html language' (parsing *.shtml) provided for MMbase its a system like
 * php3, asp or jsp but has its roots providing a simpler toolset for Interaction
 * designers and gfx designers. It is provided as an option but not demanded, you can
 * also use JSP for a more traditional parser system.
 *
 * @rename Servscan
 * @version $Id: servscan.java,v 1.48 2008-02-20 12:07:31 michiel Exp $
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Jan van Oosterom
 * @author Rob Vermeulen
 */
public class servscan extends JamesServlet {
    private static Logger log;

    // modules used in servscan
    private static sessionsInterface sessions=null;
    private static scanparser parser;

    public static final String SHTML_CONTENTTYPE = "text/html";
    public static final String DEFAULT_CHARSET = "UTF-8"; // can be overriden by parameter 'encoding' (in web.xml)

    protected String charSet = DEFAULT_CHARSET;

    /**
     * Initialize this servlet
     */
    public void init() throws ServletException {
        super.init();
        String encodingParameter = getInitParameter("encoding");
        if (encodingParameter != null) {
            charSet = encodingParameter;
        }
    }


    public void setMMBase(MMBase mmb) {
        super.setMMBase(mmb);

        if (log == null) { // use nullness of log to check whether these statics were inited /already.
            // bugfix #6648: scan.init() is not called, this will sometimes result in a crash of mmbase
            log = Logging.getLoggerInstance(servscan.class);
            try {
                MMBaseContext.initHtmlRoot();
            } catch (Exception e){
                log.error(e);
            }
            parser = (scanparser)getModule("SCANPARSER");
            log.info("Getting scan parser " + parser);
            if(parser == null) {
                throw new RuntimeException("Module with name 'scanparser' should be active");
            }
            sessions = (sessionsInterface)getModule("SESSION");
            if(sessions == null) {
                log.warn("module with name 'sessions' is not active");
            }
        }
    }

    /**
     * Adds charSet to mimetype given by SHTML_CONTENTTYPE for handling
     * of the charset used by the database
     */
    private String addCharSet(String mimetype) {
        if (mimetype.equals(SHTML_CONTENTTYPE)) {
            return mimetype + "; charset=\"" + charSet + "\"";
        } else {
            return mimetype;
        }
    }

    /**
     * handle_line is called by service to parse the SHTML in body.
     * It can be used by children to do their own parsing. The default
     * implementation calls parser.handle_line (from module scanparser)
     * to do the parsing.
     */
    protected String handle_line(String body, sessionInfo session, scanpage sp) throws ParseException {
        return parser.handle_line(body, session, sp);
    }

    /**
     * Servlet request service.
     * This processes the the page and sends it back
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!checkInited(res)) {
            return;
        }
        if (parser == null) {
            throw new ServletException("No scan parser for request " + req.getRequestURI());
        }


        incRefCount(req);
        try {

            // pageLog.service("Parsing SCAN page: " + req.getRequestURI());

            scanpage sp = new scanpage(this, req, res, sessions);

            // POST
            if (req.getMethod().equals("POST")) {
                handlePost(sp, res);
            }

            // Generate page
            PrintWriter out  = null;

            do {
                sp.rstatus = 0;
                sp.body = parser.getfile(sp.req_line);

                if (log.isDebugEnabled()) {
                    log.trace("body :" + sp.body);
                }
                if (!doCrcCheck(sp,res)) {
                    throw new PageCRCException("invalid crc");
                }
                doSecure(sp, res); // name=doSecure(sp,res); but name not used here
                long stime = handleTime(sp);
                try {
                    if (handleCache(sp, res, out)) return;
                } catch (Exception e) {
                    log.error("servscan - something is wrong with scancache: " + e.getClass().getName() + " " + e.getMessage());
                    log.service(Logging.stackTrace(e));
                }

                if (log.isDebugEnabled()) {
                    log.trace("body " + sp.body);
                }

                if (sp.body != null && !sp.body.equals("")) {
                    // Process HTML
                    sp.body = handle_line(sp.body, sp.session, sp);
                    // Send data back
                    if (sp.body != null) {
                        if (sp.rstatus == 0) { // ok
                            sp.mimetype = addCharSet(sp.mimetype);
                            //res.reset();
                            res.setContentType(sp.mimetype);
                            if (out == null) {
                                out = res.getWriter();
                            }
                            out.print(sp.body);
                            handleCacheSave(sp, res);
                        } else if (sp.rstatus == 1) {
                            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);  // 302, "OK" ??
                            res.setContentType(addCharSet(sp.mimetype));
                            if (out == null) {
                                out = res.getWriter();
                            }
                            res.setHeader("Location", sp.body);
                        } else if (sp.rstatus == 2) { // 'new page'
                            sp.req_line = sp.body;
                            if (sp.req_line.indexOf('\n') !=- 1) {
                                sp.req_line = sp.req_line.substring(0 ,sp.req_line.indexOf('\n'));
                            }
                        } else if (sp.rstatus == 4) {
                            String tmp = req.getHeader("If-Modified-Since:");
                            if (tmp != null && sp.processor != null) {
                                res.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 304, "Not Modified"
                                res.reset();
                                res.setContentType(addCharSet(sp.mimetype));
                                if (out == null) {
                                    out = res.getWriter();
                                }
                            } else {
                            	// last-modification date and expire will be set to default
                                setHeaders(sp, res, sp.body,0,0);
                                if (out == null) {
                                    out = res.getWriter();
                                }
                                out.print(sp.body);
                            }
                        }
                    } else {
                        sp.body = "<TITLE>Servscan</TITLE>handle_line returned null<BR>";
                    	// last-modification date and expire will be set to default
                        setHeaders(sp, res, sp.body,0,0);
                        if (out == null) {
                            out = res.getWriter();
                        }
                        out.print(sp.body);
                    }
                } else {
                    break;
                }

                if (out == null) {
                    out = res.getWriter();
                }

                if (stime != -1) {
                    stime = System.currentTimeMillis()-stime;
                    if (log.isDebugEnabled()) {
                        log.debug("service(" + getRequestURL(req) + "): STIME " + stime + "ms " + (stime/1000) + "sec");
                    }
                }
            } while (sp.rstatus == 2);
            // End of page parser
            // pageLog.debug("END parsing SCAN page");
            out.flush();
            //out.close();
        } catch(NotLoggedInException e) {
            log.debug("service(): page(" + getRequestURL(req) + "): NotLoggedInException: ");
        } catch(PageCRCException e) {
            log.warn("service(): page(" + getRequestURL(req) + "): Invalid CRC");
        } catch(Exception a) {
            log.debug("service(): exception on page: " + getRequestURL(req));
            a.printStackTrace();
        } finally {
            decRefCount(req);
        }

    }

    /**
     * Set some generic headers for the response based on the scanpage
     * This method is 3 times as large as it could be, this should be cleaned!
     * @param len String which will be used to determin the length of the response
     */
    private final void setHeaders(scanpage sp, HttpServletResponse res, String len, long lastModDate, long expireDate) {
        res.reset();

        res.setContentType(addCharSet(sp.mimetype));


        // Guess this will be set by the app server if we don't set it.
        // mm: guessed wrong.
        if (sp.mimetype.equals(SHTML_CONTENTTYPE)) {
            try {
                res.setContentLength(len.getBytes(charSet).length);
            } catch (java.io.UnsupportedEncodingException uee) {
                // could not happen
            }
        } else {
            res.setContentLength(len.getBytes().length);
        }



    	Date lastmod = null;
        if (lastModDate > 0) {
            lastmod = new Date(lastModDate);
        } else {
            lastmod = new Date(); //current time
        }
    	Date expire = null;
        if (expireDate > 0) {
            expire = new Date(expireDate);
        } else {
            // 2 hours back in time. So it will expire at once?
            expire = new Date(System.currentTimeMillis() - 7200000);
        }

        //    	String dateStr = RFC1123.makeDate(new Date());
        String lastmodStr = RFC1123.makeDate(lastmod);
        String expireStr = RFC1123.makeDate(expire);

        if (log.isDebugEnabled()) {
            log.debug("Set headers for URL: " + sp.req_line + "\nExpires: " + expireStr + "\nLast-Modified: " + lastmodStr );
        }

        res.setHeader("Expires", expireStr);
        res.setHeader("Last-Modified", lastmodStr);
        //        res.setHeader("Date", dateStr);

        // You shouldn't set the no-cache headers
        // when you want the browser and proxies to cache the page until it is expired
        // otherwise it will go through the proxies to MMBase.

        //      res.setHeader("Cache-Control"," no-cache");
        //      res.setHeader("Pragma", "no-cache");
    }

    public String getServletInfo() {
        return "extended html parser that adds extra html commands and a interface to modules.";
    }

    void handlePost(scanpage sp, HttpServletResponse res) throws Exception {
        String part;
        Hashtable proc_cmd = new Hashtable();
        Hashtable proc_var = new Hashtable();
        Object obj;
        HttpServletRequest req = sp.req;
        HttpPost poster = new HttpPost(req);
        sp.poster = poster;
        String name=null;

        // first check if it has a secure tag.
        String sec = poster.getPostParameter("SECURE");

        if (sec != null) {
            if (log.isDebugEnabled()) log.debug("handlePost(" + sp.getUrl() + "): Secure tag found, displaying username/password window at client-side.");
            name = getAuthorization(req, res);
            if (log.isDebugEnabled()) log.debug("handlePost(" + sp.getUrl() + "): getting cookie to check name");
            String sname = getCookie(req, res);
            if (name == null) {
                log.debug("handlePost(): Warning Username is null");
                return;
            }
        }

        // Process method=post information
        for (Enumeration<String> t = poster.getPostParameters().keys(); t.hasMoreElements(); ) {
            obj = t.nextElement();
            part = (String)obj;
            if (part.indexOf("SESSION-") == 0) {
                if (sp.session != null) {
                    if (poster.checkPostMultiParameter((String)obj)) {
                        // MULTIPLE SUPPORT
                        sessions.addSetValues(sp.session, part.substring(8), poster.getPostMultiParameter((String)obj));
                    } else {
                        sessions.setValue(sp.session, part.substring(8), poster.getPostParameter((String)obj));
                    }
                }
                // Personal objects
            } else if (part.indexOf("ID-") == 0) {
                //SESSION HACK getAuthorization(req.getAcceptor(),"Basic");
                //aaaa name=getRemoteUser();
                //name check
                if (name == null) {
                    log.debug("handlePost(): Warning Username is null");
                    return;
                }
                if (name != null && name.length() > 1) {
                    // setUserServletProperty(part.substring(3),poster.getPostParameter((String)obj),0);

                    if (poster.checkPostMultiParameter((String)obj)) {
                        // MULTIPLE SUPPORT
                        // id.setValue(name,part.substring(3),req.getPostMultiParameter((String)obj));
                    } else {
                        // id.setValue(name,part.substring(3),poster.getPostParameter((String)obj));
                    }
                }
                // PRC-CMD- commands
            } else if (part.indexOf("PRC-CMD-") == 0) {
                if (poster.checkPostMultiParameter((String)obj)) {
                    proc_cmd.put(part.substring(8), poster.getPostMultiParameter((String)obj));
                } else {
                    proc_cmd.put(part.substring(8), poster.getPostParameter((String)obj));
                }
                // PRC-VAR- vars
            } else if (part.indexOf("PRC-VAR-") == 0) {
                if (poster.checkPostMultiParameter((String)obj)) {
                    proc_var.put(part.substring(8), poster.getPostMultiParameter((String)obj));
                } else {
                    proc_var.put(part.substring(8), poster.getPostParameter((String)obj));
                }
            }
        }
        // If there are cmds process them
        if (!proc_cmd.isEmpty()) parser.do_proc_input(sp.req_line, poster,proc_var, proc_cmd,sp);
    }


    boolean handleCacheSave(scanpage sp, HttpServletResponse res) {
        if (sp.wantCache != null) {
            String req_line = sp.req_line;
            if (sp.querystring != null) req_line += "?" + sp.querystring;
            try {
                parser.scancache.newput(sp.wantCache, res, req_line, sp.body, sp.mimetype);
            } catch (Exception e) {
                log.error("servscan - something is wrong with scancache");
            }
        }
        return true;
    }

    boolean handleCache(scanpage sp,HttpServletResponse res, PrintWriter out) {
        String req_line = sp.req_line;
        if (sp.querystring != null) req_line += "?" + sp.querystring;

        // new new new scancache setup, needs to be moved
        // ----------------------------------------------

        // This depends on the PRAGMA: no-cache header
        // Which Internet Explorer does not send.

        if (sp.body != null) {

            int start = sp.body.indexOf("<CACHE HENK");
            if (start >= 0) {
                start += 11;
                int end = sp.body.indexOf(">", start);
                sp.wantCache  ="HENK";

                String rst = parser.scancache.get(sp.wantCache, req_line, sp.body.substring(start, end + 1), sp);
                if (log.isDebugEnabled()) {
                    log.debug("handleCache: sp.reload: " + sp.reload);
                }

                if (rst != null && !sp.reload) {
                    long lastModDate = parser.scancache.getLastModDate(sp.wantCache, req_line);
                    long expireDate = parser.scancache.getExpireDate(sp.wantCache, req_line, sp.body.substring(start, end).trim());

                    setHeaders(sp, res, rst, lastModDate, expireDate);
                    // org.mmbase res.writeHeaders();
                    try {
                        if (out == null) {
                            out = res.getWriter();
                        }
                        out.print(rst);
                        out.flush();
                        out.close();
                    } catch (IOException io) {
                        log.error(io);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("handleCache(): cache.hit(" + req_line + ")");
                    }
                    return(true);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("handleCache(): cache.miss(" + req_line + ")");
                    }
                }
            }

            if (sp.body.indexOf("<CACHE PAGE>") !=- 1) {

                sp.wantCache="PAGE";
                String rst=parser.scancache.get(sp.wantCache, req_line, sp);

                if (log.isDebugEnabled()) {
                    log.debug("handleCache: sp.reload: " + sp.reload);
                }
                if (rst != null && !sp.reload) {
                    long lastModDate = parser.scancache.getLastModDate(sp.wantCache, req_line);

                    setHeaders(sp, res,rst,lastModDate,0);
                    // org.mmbase res.writeHeaders();
                    try {

                        if (out == null) {
                            out = res.getWriter();
                        }
                        out.print(rst);
                        out.flush();
                        out.close();
                    } catch (IOException io) {
                        log.error(io);
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("handleCache(): cache.hit(" + req_line + ")");
                    }
                    return true;
                } else {
                    log.debug("handleCache(): cache.miss(" + req_line + ")");
                }
            }
        }

        return  false;
    }


    private long handleTime(scanpage sp) {
        if (sp.body != null && sp.body.indexOf("<TIME>") != -1) {
            return System.currentTimeMillis();
        }
        return -1;
    }


    private boolean doCrcCheck(scanpage sp, HttpServletResponse res) {
        if (sp.body != null && sp.body.indexOf("<CRC>") != -1) {
            Vector<String> p = sp.getParamsVector();
            String value = null;
            String checker = null;
            for (String part : p) {
                if (!p.lastElement().equals(part)) {
                    if (value == null) {
                        value = part;
                    } else {
                        value += "+" + part;
                    }
                } else {
                    checker = part;
                }
            }
            value = sp.req.getRequestURI() + "?" + value;
            int crc = scanparser.calccrc32(value);
            String thiscrc = "CRC" + crc;
            System.out.println("CRC = " + crc);
            if (checker != null && checker.equals(thiscrc)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private String doSecure(scanpage sp, HttpServletResponse res) throws Exception {
        String name = null;
        if (sp.body != null && sp.body.indexOf("<SECURE>") != -1) {
            if (log.isDebugEnabled()) {
                log.debug("doSecure(" + sp.getUrl() + "): Secure tag found, calling getAuthorization()...");
            }
            name = getAuthorization(sp.req, res);
            if (log.isDebugEnabled()) {
                log.debug("doSecure(" + sp.getUrl() + "): getting cookie from user...");
            }
            String sname = getCookie(sp.req, res);

            // check name
            if (name == null) {
                log.warn("doSecure(" + sp.getUrl() + "): WARNING: no username found!");
                return null;
            }
        }
        return name;
    }
}
