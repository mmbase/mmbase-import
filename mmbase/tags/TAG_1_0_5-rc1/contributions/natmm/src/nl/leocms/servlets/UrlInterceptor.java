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
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.mmbase.module.core.MMBaseContext;


/**
 * Requestfilter that filters out all virtual URL's that have a corresponding
 * page within Leeuwnet. The filtering and conversion to a URL pointing to an
 * existing JSP template is done by the class UrlConverter.
 * If a match is made, a forward to the ROOT_TEMPLATE is made.
 * Criterium used: all URL's ** not ** containing a dot are tried to be converted.
 * <br>
 * Initialisation of the MMBaseContext object is done from here as well. Since
 * RequestFilters are the first classes to be instantiated in the boot sequence and
 * we want MMBase logging initialised for this class as well.
 *
 * Date: Oct 22, 2003
 * Time: 5:30:28 PM
 * @author Finalist IT Group / peter
 * @version $Id: UrlInterceptor.java,v 1.1 2006-03-05 21:43:59 henk Exp $
 */
public class UrlInterceptor  implements Filter  {
   private FilterConfig config = null;

   private static Logger log;


   /**
    * The initialisation method of the filter, called on startup.
    *
    * @param config object containing init parameters specified
    * @throws ServletException thrown when an exception occurs
    * in the web.xml
    */
   public void init(FilterConfig config) throws ServletException {
      //initialize MMBase here.
      this.config = config;
      if (!MMBaseContext.isInitialized()) {
         ServletContext servletContext = config.getServletContext();
         MMBaseContext.init(servletContext);
         MMBaseContext.initHtmlRoot();
      }
      log = Logging.getLoggerInstance(UrlInterceptor.class.getName());
      log.debug("UrlInterceptor initialized");
   }


   /**
    * Destroy method
    */
   public void destroy() {
      config = null;
   }


   /**
    * Does the filtering. URL conversion is only tried when a dot is found in the URI.
    * The conversion work is delegated to the UrlConverter class.
    *
    * @param request incoming request
    * @param response outgoing response
    * @param chain a chain object, provided for by the servlet container
    * @throws ServletException thrown when an exception occurs
    * @throws IOException thrown when an exception occurs
    * @todo deal with param string 
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

      if (request instanceof HttpServletRequest) {
         HttpServletRequest req = (HttpServletRequest) request;
         log.debug("Incoming request, uri = " + req.getRequestURL());
         
         String checkUrl = req.getRequestURL().toString();
         if (UrlConverter.ignoreUrl(checkUrl)) {
            log.debug("Url should be ignored, therefore no url forwarding");
            chain.doFilter(request, response);
         } else {
            String forwardUrl = UrlConverter.convertUrl(req);
            if (forwardUrl != null) {
               
               // HttpServletResponse res = (HttpServletResponse) response;
               // res.sendRedirect(forwardUrl);
               
               String contextPath = req.getContextPath();
               if(!contextPath.equals("")) {
                  forwardUrl = StringUtils.substringAfterLast(forwardUrl,contextPath);
               } else {
                  int sPos = forwardUrl.substring(7).indexOf("/")+7;
                  forwardUrl = forwardUrl.substring(sPos);
               }
               log.debug("Forwarding to " + forwardUrl);               
               
               RequestDispatcher requestDispatcher = request.getRequestDispatcher(forwardUrl);
               requestDispatcher.forward(request, response);
            } else {
               log.debug("No forwardUrl found, therefore no url forwarding");
               chain.doFilter(request, response);
            }
         }
      } else {
         log.debug("Request is not an instance of HttpServletRequest, therefore no url forwarding");
         chain.doFilter(request, response);
      }
   }

}


/*
$Log: not supported by cvs2svn $
Revision 1.3  2003/11/03 13:14:52  Gerard van de Weerd
url converter / interceptor takes context path into account

Revision 1.2  2003/10/30 10:21:02  Gerard van de Weerd
url interceptor changed to work with page templates

Revision 1.1  2003/10/24 14:40:25  peter
*** empty log message ***

*/
