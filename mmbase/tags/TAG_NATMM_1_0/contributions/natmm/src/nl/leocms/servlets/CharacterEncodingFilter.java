/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Filter that converts the characters coming
 * from the browser to UTF-8 before they are handled
 * by the EditWizard application logic.
 * Get it to work by placing the class in the WEB-INF classes dir and incorporate the
 * following piece of XML in your web.xml:
 *  <filter>
 *	<filter-name>Set Character Encoding</filter-name>
 *	<filter-class>filters.CharacterEncodingFilter</filter-class>
 *  </filter>
 *
 *   <filter-mapping>
 *	<filter-name>Set Character Encoding</filter-name>
 *	<url-pattern>/*.jsp</url-pattern>
 *   </filter-mapping>
 *
 * author: P.S.D.Reitsma
 */
public class CharacterEncodingFilter implements Filter {

    private FilterConfig config = null;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    public void destroy() {
        config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpreq = (HttpServletRequest) request;
            try {
                //System.out.println("encoding="+request.getCharacterEncoding());
                request.setCharacterEncoding("UTF-8");
            }
            catch (Exception e) {
                config.getServletContext().
                log("Error setting UTF8 encoding : " + e.getMessage());
            }
        }

        // Perform any other filters that are chained after this one.
        // This includes calling the requested servlet!
        chain.doFilter(request, response);
    }
}



