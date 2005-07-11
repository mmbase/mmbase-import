/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Makes sure that there is no charset on the content type of certain
 * types. This is mainly because real-player does not understand
 * that. But it could be used for other content-types as well (There
 * are probably more lousy client programs out there).
 *
 * It can be configured by a file WEB-INF/config/charsetremover.properties with
 * <contenttype>=<supposed charset> properties.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CharsetRemoverFilter.java,v 1.5 2005-07-11 08:20:01 michiel Exp $
 * @since MMBase-1.7.4
 */

public class CharsetRemoverFilter implements Filter {
    private static final Logger log = Logging.getLoggerInstance(CharsetRemoverFilter.class);


    Properties contentTypes = new Properties();    
    FileWatcher watcher = new FileWatcher(true) {
            public void onChange(File file) {
                load(file);
            }
        };
    /**
     * Initializes the filter
     */
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        File file = new File(filterConfig.getServletContext().getRealPath("WEB-INF/config/charsetremover.properties"));
        log.info("Init of CharsetRemover Filter, using " + file);
        load(file);
        watcher.add(file);
        watcher.setDelay(10 * 1000); // check every 10 secs if config changed
        watcher.start();

    }

    public void load(File file) {
        log.info("Reading " + file);
        contentTypes.clear();
        if (file.canRead()) {
            try {
                contentTypes.load(new FileInputStream(file));
            } catch (IOException ioe) {
                log.error(ioe);
            }
        } else {
            log.warn("This file does not exist, using defaults");
            contentTypes.put("audio/x-pn-realaudio", "ISO-8859-1");
            contentTypes.put("text/vnd.rn-realtext","ISO-8859-1");
            contentTypes.put("audio/x-pn-realaudio-plugin", "ISO-8859-1");
            contentTypes.put("image/vnd.rn-realpix", "ISO-8859-1");
            contentTypes.put("application/smil", "ISO-8859-1");
        }
        log.info("The following content-types will have no charset on the content-type: " + contentTypes);
    }


    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, FilterChain filterChain) 
        throws java.io.IOException, ServletException {

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse) {
                private String contentType = null;
                private PrintWriter writer = null;

                
                public void setContentType(String ct) {
                    if (writer == null) {
                        contentType = ct;
                    } else {
                        // too late
                    }
                    if (log.isDebugEnabled()) {
                        log.trace("Setting contentType to " + ct + " " + Logging.stackTrace(new Exception()));
                    }
                    getResponse().setContentType(ct);                    
                }
                /**
                 * This is the essence of this whole thing. The idea
                 * is to fake the use of getOutputStream(). Then you
                 * are in byte-writing mode.  and charset's become
                 * irrelevant,and tomcat will not add one any more.
                 */
                
                public PrintWriter getWriter() throws IOException {
                    if (writer == null) {                        
                        String charSet = contentType == null ? null : (String) contentTypes.get(contentType);
                        if (charSet != null) {
                            if (charSet.equals("")) charSet = "ISO-8859-1"; // default for HTTP, IIRC.                             
                            if (log.isDebugEnabled()) {
                                log.debug("Wrapping outputstream to avoid charset " + charSet);
                            }
                            try {
                                // no need buffering, i supposed the wrapped outputstream is already buffered, using getBufferSize etc.
                                servletRequest.setAttribute("javax.servlet.include.servlet_path", "/bla"); //servletRequest.getServletPath());
                                writer = new PrintWriter(new OutputStreamWriter(servletResponse.getOutputStream(), charSet)) {
                                        public void write(String s, int off, int len) {
                                            log.info("Wrting1  " + s);
                                            super.write(s, off, len);
                                            flush();
                                        }
                                        public void write(char[] buf) {
                                            log.info("Wrting buf1 " + buf);
                                            super.write(buf);
                                        }
                                        public void write(char[] buf, int off, int len) {
                                            log.info("Wrting buf2 " + buf);
                                            super.write(buf, off, len);
                                        }

                                        public void print(String s)  {
                                            log.info("Printing " + s);
                                            super.write(s);
                                        }
                                        public void write(int c)  {
                                            log.info("Writch char" + c);
                                            super.write(c);
                                            if (c == 10) {
                                                flush();
                                            }
                                        }
                                        public void println() {
                                            log.info("prinln!");
                                            super.println();
                                                
                                        }


                                        public void flush() {
                                            log.info("Flushing for " + ((HttpServletRequest) servletRequest).getRequestURI());
                                            super.flush();
                                        }
                                    
                                    };
                            } catch (UnsupportedEncodingException uee) {
                                log.error(uee); 
                                writer = super.getWriter();
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(" " + contentType + " is not contained by " + contentTypes);
                            }
                            writer = super.getWriter();
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Returning " + writer.getClass());
                    }
                    return writer;                        
                }
                public void flushBuffer() throws IOException {                    
                    if (log.isDebugEnabled()) {
                        log.debug("Flushing for " + ((HttpServletRequest) servletRequest).getRequestURI());
                    }
                    log.info("Flushing wrapper for " + ((HttpServletRequest) servletRequest).getRequestURI());
                    if (writer != null) writer.flush();
                    super.flushBuffer();                    
                }
                
                    

        };
        filterChain.doFilter(servletRequest, wrapper);
        
    }

    /**
     * destroys the filter
     */
    public void destroy() {
        watcher.clear();
        watcher.exit();
        contentTypes.clear();
        contentTypes = null;
        
    }


}
