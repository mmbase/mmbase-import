package nl.leocms.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import nl.leocms.applications.NatMMConfig;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This filter rejects or allows a request based on the IP address it comes
 * from.
 * 
 * @author Jurn de Ruijter
 */
public class IPFilter implements Filter {

   private FilterConfig config;
   
   private List allowedIPList; 
   private boolean IPFilterEnabled;
   
   private static Logger log;

   /**
    * The initialisation method of the filter, called on startup.
    * 
    * @param config object containing init parameters specified
    * @throws ServletException thrown when an exception occurs in the web.xml
    */
   public void init(FilterConfig filterConfig) throws ServletException {
      this.config = filterConfig;
      this.allowedIPList = new ArrayList();
      this.IPFilterEnabled = NatMMConfig.isIPFilterEnabled();
      
      String allowedIPProperty = NatMMConfig.getAllowedIP();
      StringTokenizer token = new StringTokenizer(allowedIPProperty, ",");

      while (token.hasMoreTokens()) {
         allowedIPList.add(token.nextToken());
      }      

      log = Logging.getLoggerInstance(IPFilter.class.getName());
      log.debug("IPFilter initialized");
   }

   /**
    * Does the filtering. URL conversion is only tried when a dot is found in
    * the URI. The conversion work is delegated to the UrlConverter class.
    * 
    * @param request incoming request
    * @param response outgoing response
    * @param chain a chain object, provided for by the servlet container
    * @throws ServletException thrown when an exception occurs
    * @throws IOException thrown when an exception occurs
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      
      // skip IPFiltering if disabled
      if (!IPFilterEnabled) {
         log.debug("Ip filtering disabled.");
         chain.doFilter(request, response);
         
      } else {     
         String ip = request.getRemoteAddr();
         log.debug("Incoming ip, ip = " + ip);
         
         HttpServletResponse httpResp = null;
         
         if (response instanceof HttpServletResponse) {
            httpResp = (HttpServletResponse) response;         
         }
   
         if (allowedIPList.contains(ip)) {
            log.debug("Ip " + ip + " allowed.");
            chain.doFilter(request, response);
         } else {
            log.debug("Ip " + ip + " not allowed.");
            httpResp.sendError(HttpServletResponse.SC_FORBIDDEN, "That means goodbye forever!");
         }
      }
   }

   /**
    * Destroy method
    */
   public void destroy() {
      /*
       * called before the Filter instance is removed from service by the web
       * container
       */
      config = null;
   } 
}