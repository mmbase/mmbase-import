/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * There's large simularity in the way data travels from and to a web server.
 * But unfortunately there is one difference: while the web server tells the browser what
 * character encoding the page it sends is in (via the Content-Type HTTP header),
 * the client does not send such information.
 *
 * Accordingly to the HTTP spec the HTTP request the browser send to the server
 * (that contains the submitted form) may well contain the Content-Type header too.
 * This would give the server the key to decript the form parameters.
 * Regretfully our present internet browsers do not send it
 *
 * The browser generally does the following: it takes user input in national characters
 * <ul>
 * <li>
 *   translates it to a byte sequence using the character encoding that the web page that contains
 *   the form is encoded with
 * </li>
 * <li>
 *   the resulting byte secuence is encoded into the query string according to the usual rules of
 *   encoding query strings. That is all bytes that correspond to legal ascii alpha-numeric chars
 *   are encoded as those chars, all the rest are converted to the %xy representation, where xy
 *   is the hexademical code of the corresponding byte (like %C1, for example)
 * </li>
 * </ul>
 * Then the encoded query (possibly containing %xy codes) is sent to the server. ascii characters,
 * according to the procedure described above are sent to the server as they are, provided that they
 * have the same codes both in ascii character encoding and in the national character encoding that is used.
 *
 *
 * This filter sets the character encoding before parameters are handled.
 * The filter sets the character encoding by the following information:
 * <ul>
 *   <li>HTTP content-type header</li>
 *   <li>Parameter of filter in the WEB-INF/web.xml</li>
 *   <li>MMBase encoding set in mmbase-config/modules/mmbaseroot.xml</li>
 *   <li>No encoding defined. (default UTF-8)</li>
 * </ul>
 *
 * Get it to work by incorporating the following piece of XML in your web.xml:
 <pre><code>
 *  &lt;filter&gt;
 *    &lt;filter-name&gt;Set Character Encoding&lt;/filter-name&gt;
 *    &lt;filter-class&gt;org.mmbase.servlet.CharacterEncodingFilter&lt;/filter-class&gt;
 *    &lt;!-- Overrides config/module/mmbaseroot.xml#encoding --&gt;
 *    &lt;!-- &lt;init-param&gt;
 *      &lt;param-name&gt;encoding&lt;/param-name&gt;
 *      &lt;param-value&gt;UTF-8&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 *    --&gt;
 *  &lt;/filter&gt;
 *
 *  &lt;filter-mapping&gt;
 *    &lt;filter-name&gt;Set Character Encoding&lt;/filter-name&gt;
 *    &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 </code></pre>
 *
 *
 * @author P.S.D.Reitsma (Finalist IT Group)
 * @author Nico Klasens (Finalist IT Group)
 *
 * @since  MMBase-1.6
 * @version $Id$
 */
public class CharacterEncodingFilter implements Filter {

    /** MMBase logging system */
    private static Logger log =
        Logging.getLoggerInstance(CharacterEncodingFilter.class.getName());

    private String encoding = null;
    private FilterConfig filterConfig = null;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        log.info("CharacterEncodingFilter init");
        filterConfig = config;
        encoding = config.getInitParameter("encoding");
        if (encoding == null) {
           MMBase mmbase = (MMBase) Module.getModule("MMBASEROOT");
           encoding = mmbase.getEncoding();
           if (encoding == null) {
              encoding = "UTF-8";
           }
        }
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        log.info("CharacterEncodingFilter destroy");
        filterConfig = null;
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            try {
                if (req.getCharacterEncoding() == null) {
                    // Parameters are usually in the encoding of the jsp with the form
                    // jsp pageEncoding directive (ContentType charset)
                    req.setCharacterEncoding(encoding);
               }
            }
            catch (Exception e) {
                log.warn("Error setting encoding : " + e.getMessage());
            }
        }

        // Perform any other filters that are chained after this one.
        // This includes calling the requested servlet!
        chain.doFilter(request, response);
    }
}
