package com.finalist.cmsc.knownvisitor.ntlm;

import jcifs.http.NtlmSsp;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import jcifs.smb.SmbAuthException;
import jcifs.UniAddress;
import jcifs.Config;
import jcifs.util.Base64;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.naming.NamingException;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.mmbase.util.ApplicationContextReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.knownvisitor.KnownVisitorModule;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.util.http.HttpUtil;

/**
 * @author Freek Punt, Finalist IT Group
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class NtlmVisitorFilter implements Filter {
   private static final String realm = "jCIFS";


   private static Logger log = Logging.getLoggerInstance(NtlmVisitorFilter.class);


   public void init(FilterConfig filterConfig) throws ServletException {


      /* Set jcifs properties we know we want; soTimeout and cachePolicy to 10min.
      */
      Config.setProperty("jcifs.smb.client.soTimeout", "300000");
      Config.setProperty("jcifs.netbios.cachePolicy", "1200");
   }

   public void destroy() {
   }

   /**
    * This method simply calls <tt>negotiate( req, resp, false )</tt> and then <tt>chain.doFilter</tt>. You can override
    * and call negotiate manually to achive a variety of different behavior.
    */
   public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {
      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpServletResponse resp = (HttpServletResponse) response;
      
      if (isEnabled() && !negotiate(req, resp, false)) {
         return;
      }
      
      chain.doFilter(req, resp);
   }

   /**
    * Negotiate password hashes with MSIE clients using NTLM SSP
    *
    * @param req The servlet request
    * @param resp The servlet response
    * @param skipAuthentication If true the negotiation is only done if it is initiated by the client (MSIE post
    * requests after successful NTLM SSP authentication). If false and the user has not been authenticated yet the
    * client will be forced to send an authentication (server sends HttpServletResponse.SC_UNAUTHORIZED).
    * @return True if the negotiation is complete, otherwise false
    */
   protected boolean negotiate(HttpServletRequest req, HttpServletResponse resp, boolean skipAuthentication) throws IOException, ServletException {
      String msg = req.getHeader("Authorization");
      
      log.debug("Message: "+msg);
      if (msg != null && (msg.startsWith("NTLM "))) {
         boolean loginResult = tryNtlmLogin(req, resp, msg );
         
         if(loginResult) {
            return true;
         }
         
         // login failed, restart login
         msg = null;
      } 

      
      if(alreadyLoggedIn(req) || skipAuthentication) {
         return true;
      }
      else {
         
         String cookie = getCookieValue(req.getCookies(), "NtlmHttpAuthUsername");
         if(cookie != null) {
            req.getSession().setAttribute("NtlmHttpAuth", cookie);
            ((NtlmKnownVisitorModule)KnownVisitorModule.getInstance()).justLoggedIn(req, cookie);
            return true;
         }
         else {
// de volgende 4 regels zijn tijdelijk uitgezet!
//            if (msg == null) {
//               tryToStartAuthentication(resp);
//            }
//            else { 
               String alternativeLogin = req.getParameter("ntlm_alternative_login");
               if(alternativeLogin != null) {
                  if(tryAlternativeLogin(req, resp)) {
                     return true;
                  }
               }
   
               tryToStartAlternativeLogin(req, resp);
// de volgende regel is tijdelijk uitgezet!
//            }
         }
      }

      return false;
   }

   private String getCookieValue(Cookie[] cookies, String cookieName) {
      if(cookies != null) {
         for(int i=0; i<cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equals(cookie.getName())) {
               return(cookie.getValue());
            }
         }
      }
      return null;
   }
   
   
   private void tryToStartAlternativeLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnsupportedEncodingException, ServletException {
      req.setAttribute("old_uri", req.getRequestURI());
      RequestDispatcher rd = req.getRequestDispatcher ("/ntlm/alternative_login.jsp");
      rd.forward(req, resp);
   }


   private boolean tryAlternativeLogin(HttpServletRequest req, HttpServletResponse resp) throws UnknownHostException, SmbException {
      log.debug("Handling alternative login!");
      
      String username = req.getParameter("username");
      String password = req.getParameter("password");
      
      NtlmPasswordAuthentication ntlm = new NtlmPasswordAuthentication("", username, password);
      UniAddress dc = UniAddress.getByName(getDomainController(), true);
      
      try {
   
         SmbSession.logon(dc, ntlm);
   
         if (log.isDebugEnabled()) {
            log.debug("NtlmHttpFilter: " + ntlm + " successfully authenticated against " + dc);
         }
         
         req.getSession().setAttribute("NtlmHttpAuth", ntlm);
         ((NtlmKnownVisitorModule)KnownVisitorModule.getInstance()).justLoggedIn(req,ntlm.getUsername());
         
         Cookie cookie = new Cookie("NtlmHttpAuthUsername", ntlm.getUsername());
         cookie.setMaxAge (Integer.MAX_VALUE);
         resp.addCookie(cookie);
         
         return true;
      } catch (SmbAuthException sae) {
         log.debug("Login failed: "+sae);
         req.setAttribute("failed", sae.getMessage());
      }
      return false;
   }

   private void tryToStartAuthentication(HttpServletResponse resp) throws IOException {
      log.debug("Not NTLM authenticated, starting authentication.");
      resp.setHeader("WWW-Authenticate", "NTLM");
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      resp.setContentLength(0);
      resp.flushBuffer();
   }

   private boolean alreadyLoggedIn(HttpServletRequest req) {
      HttpSession ssn = req.getSession(false);
      return (ssn != null && ssn.getAttribute("NtlmHttpAuth") != null);
   }

   private boolean tryNtlmLogin(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException, ServletException {
      log.debug("Message starts with NTLM.");
      HttpSession ssn = req.getSession();
      byte[] challenge;

      UniAddress dc = UniAddress.getByName(getDomainController(), true);
      challenge = SmbSession.getChallenge(dc);
      NtlmPasswordAuthentication ntlm = NtlmSsp.authenticate(req, resp, challenge);
      if (ntlm == null) {
         return false;
      }
      
      /* negotiation complete, remove the challenge object */
      log.debug("negotiation complete, remove the challenge object.");
      ssn.removeAttribute("NtlmHttpChal");
      try {

         SmbSession.logon(dc, ntlm);

         if (log.isDebugEnabled()) {
            log.debug("NtlmHttpFilter: " + ntlm +
                    " successfully authenticated against " + dc);
         }

         req.getSession().setAttribute("NtlmHttpAuth", ntlm);
         ((NtlmKnownVisitorModule)KnownVisitorModule.getInstance()).justLoggedIn(req,ntlm.getUsername());
         return true;
         
      } catch (SmbAuthException sae) {
         if (log.isServiceEnabled()) {
            log.service("NtlmHttpFilter: " + ntlm.getName() +
                    ": 0x" + jcifs.util.Hexdump.toHexString(sae.getNtStatus(), 8) +
                    ": " + sae);
         }
         if (sae.getNtStatus() == SmbAuthException.NT_STATUS_ACCESS_VIOLATION) {
            /* Server challenge no longer valid for
            * externally supplied password hashes.
            */
            if (ssn != null) {
               ssn.removeAttribute("NtlmHttpAuth");
            }
         }
         return false;
      }
   }

   private boolean isEnabled() {
      return PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_ENABLED).equals("true");
   }
   
   private String getDomainController() {
      return PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_DOMAIN_CONTROLLER);
   }

}
