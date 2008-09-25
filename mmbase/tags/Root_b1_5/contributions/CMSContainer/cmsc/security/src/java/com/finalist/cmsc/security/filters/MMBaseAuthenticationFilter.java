package com.finalist.cmsc.security.filters;

import com.finalist.cmsc.security.ThreadLocalAuthenticate;
import jcifs.smb.NtlmPasswordAuthentication;
import net.sf.mmapps.commons.bridge.CloudUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class MMBaseAuthenticationFilter implements Filter {

   public void init(FilterConfig filterConfig) throws ServletException {
   }


   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
         throws IOException, ServletException {

      if (servletRequest instanceof HttpServletRequest) {
         HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

         String remoteUser = httpServletRequest.getRemoteUser();
         Principal principal = httpServletRequest.getUserPrincipal();
         if (principal instanceof NtlmPasswordAuthentication) {
            NtlmPasswordAuthentication ntlmPasswordAuthentication = (NtlmPasswordAuthentication) principal;
            remoteUser = ntlmPasswordAuthentication.getUsername();
         }
         HttpSession session = httpServletRequest.getSession(false);
         boolean logout = session != null && session.getAttribute("logout") != null
               && (Boolean) session.getAttribute("logout");
         if (remoteUser != null && !logout) {
            if (!CloudUtil.hasCloud(httpServletRequest)) {
               ThreadLocalAuthenticate.bind(remoteUser);
               try {
                  if (CloudUtil.createCloud(httpServletRequest, "threadlocal", null) == null) {
                     // could not log in
                  }
               }
               catch (SecurityException se) {
                  // could not log in
               }
               ThreadLocalAuthenticate.unbind();
            }
         }
      }

      filterChain.doFilter(servletRequest, servletResponse);
   }


   public void destroy() {

   }
}
