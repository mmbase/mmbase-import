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
   
   private List allowedIPRanges;  
   
   private static Logger log;

   /**
    * The initialisation method of the filter, called on startup.
    * 
    * @param config object containing init parameters specified
    * @throws ServletException thrown when an exception occurs in the web.xml
    */
   public void init(FilterConfig filterConfig) throws ServletException {
      this.config = filterConfig;
      this.allowedIPRanges = new ArrayList();
      
      String allowedIPRangesProperty = NatMMConfig.getAllowedIPRanges();
      StringTokenizer token = new StringTokenizer(allowedIPRangesProperty, ",");

      while (token.hasMoreTokens()) {
         allowedIPRanges.add(token.nextToken());
      }      

      log = Logging.getLoggerInstance(UrlInterceptor.class.getName());
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
      String ip = request.getRemoteAddr();

      log.debug("Incoming ip, ip = " + ip);
      
      HttpServletResponse httpResp = null;

      if (response instanceof HttpServletResponse) {
         httpResp = (HttpServletResponse) response;         
      }

      StringTokenizer toke = new StringTokenizer(ip, ".");
      int dots = 0;
      String byte1 = "";
      String byte2 = "";
      String client = "";

      while (toke.hasMoreTokens()) {
         ++dots;

         // if we've reached the second dot, break and check out the index value
         if (dots == 1)  {
            byte1 = toke.nextToken();
         } else {
            byte2 = toke.nextToken();
            break;
         }
      }
      
      // Piece together half of the client IP address so it can be compared
      // with the forbidden range represented by IPFilter.IP_RANGE
      client = byte1 + "." + byte2;

      if (allowedIPRanges.contains(client)) {
         log.debug("Ip " + ip + " allowed.");
         chain.doFilter(request, response);
      } else {
         log.debug("Ip " + ip + " not allowed.");
         httpResp.sendError(HttpServletResponse.SC_FORBIDDEN, "That means goodbye forever!");
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