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
 * It can be configured by a file WEB-INF/config/charsetremover.list.
 *
 * @author Michiel Meeuwissen
 * @version $Id $
 * @since MMBase-1.7.4
 */

public class CharsetRemoverFilter implements Filter {
    private static final Logger log = Logging.getLoggerInstance(CharsetRemoverFilter.class);


    Set contentTypes = new HashSet();    
    FileWatcher watcher = new FileWatcher(true) {
            public void onChange(File file) {
                load(file);
            }
        };
    /**
     * Initializes the filter
     */
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        File file = new File(filterConfig.getServletContext().getRealPath("WEB-INF/config/charsetremover.list"));
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
                
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String ct = in.readLine();
                while (ct != null) {
                    ct.trim();
                    if (! ct.startsWith("#") && 
                        ! ct.equals("")) {
                        contentTypes.add(ct);                        
                    }
                    ct = in.readLine();
                }
            } catch (IOException ioe) {
                log.error(ioe);
            }
        } else {
            log.warn("This file does not exist");
            contentTypes.add("audio/x-pn-realaudio");
            contentTypes.add("text/vnd.rn-realtext");
            contentTypes.add("audio/x-pn-realaudio-plugin");
            contentTypes.add("image/vnd.rn-realpix");
            contentTypes.add("application/smil");
        }
    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
        throws java.io.IOException, ServletException {

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse) {
                private String contentType;
                public void setContentType(String ct) {
                    contentType = ct;
                    super.setContentType(ct);
                }

                /**
                 * This is the essence of this whole thing. The idea
                 * is to fake the use of getOutputStream(). Then you
                 * are in byte-writing mode.  and charset's become
                 * irrelevant,and tomcat will not add one any more.
                 */
                
                public PrintWriter getWriter() throws IOException {
                    if (contentTypes.contains(contentType)) {
                        log.debug("Wrapping outputstream to avoid charset");
                        try {
                            // ISO-8859-1 is default for HTTP
                            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(getOutputStream(), "ISO-8859-1")));
                        } catch (UnsupportedEncodingException uee) {
                            // could not happen
                            return super.getWriter();
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(" " + contentType + " is not contained by " + contentTypes);
                        }
                        return super.getWriter();
                    }
                }
        };
        filterChain.doFilter(servletRequest, wrapper);
        
    }
    /**
     * destroys the filter
     */
    public void destroy() {
    }


}
