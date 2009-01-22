package com.finalist.cmsc.knownvisitor.ntlm;

import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcifs.Config;
import jcifs.UniAddress;
import jcifs.http.NtlmSsp;
import jcifs.smb.*;
import jcifs.util.Base64;

import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.knownvisitor.KnownVisitorModule;
import com.finalist.cmsc.mmbase.PropertiesUtil;

/**
 * @author Freek Punt, Finalist IT Group
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class NtlmVisitorFilter implements Filter {

   private static final String realm = "jCIFS";

   private static final Logger log = Logging.getLoggerInstance(NtlmVisitorFilter.class);


   public void init(FilterConfig filterConfig) {

      /*
       * Set jcifs properties we know we want; soTimeout and cachePolicy to
       * 10min.
       */
      Config.setProperty("jcifs.smb.client.soTimeout", "300000");
      Config.setProperty("jcifs.netbios.cachePolicy", "1200");
   }


   public void destroy() {
      // nothing
   }


   /**
    * This method simply calls <tt>negotiate( req, resp, false )</tt> and then
    * <tt>chain.doFilter</tt>. You can override and call negotiate manually
    * to achive a variety of different behavior.
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {
      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpServletResponse resp = (HttpServletResponse) response;

      if (isEnabled()) {
         List<String> exceptions = getIpExceptions();
         if (!exceptions.isEmpty()) {
            String clientIp = req.getHeader("X-Forwarded-For");
            if (StringUtils.isBlank(clientIp)) {
               // not behind a proxy or mod_proxy
               clientIp = request.getRemoteAddr();
            }
            if (exceptions.contains(clientIp)) {
               chain.doFilter(req, resp);
               return;
            }
         }

         if (!negotiate(req, resp, false)) {
            return;
         }
      }

      chain.doFilter(req, resp);
   }


   /**
    * Negotiate password hashes with MSIE clients using NTLM SSP
    * 
    * @param req
    *           The servlet request
    * @param resp
    *           The servlet response
    * @param skipAuthentication
    *           If true the negotiation is only done if it is initiated by the
    *           client (MSIE post requests after successful NTLM SSP
    *           authentication). If false and the user has not been
    *           authenticated yet the client will be forced to send an
    *           authentication (server sends
    *           HttpServletResponse.SC_UNAUTHORIZED).
    * @return True if the negotiation is complete, otherwise false
    */
   protected boolean negotiate(HttpServletRequest req, HttpServletResponse resp, boolean skipAuthentication)
         throws IOException, ServletException {
      UniAddress dc;
      String msg;
      NtlmPasswordAuthentication ntlm = null;
      msg = req.getHeader("Authorization");
      boolean offerBasic = offerBasic(req);

      log.debug("Message: " + msg);
      if (msg != null && (msg.startsWith("NTLM ") || (offerBasic && msg.startsWith("Basic ")))) {
         if (msg.startsWith("NTLM ")) {
            log.debug("Message starts with NTLM.");
            HttpSession ssn = req.getSession();
            byte[] challenge;

            dc = UniAddress.getByName(getDomainController(), true);
            challenge = SmbSession.getChallenge(dc);

            if ((ntlm = NtlmSsp.authenticate(req, resp, challenge)) == null) {
               return false;
            }
            /* negotiation complete, remove the challenge object */
            log.debug("negotiation complete, remove the challenge object.");
            ssn.removeAttribute("NtlmHttpChal");
         }
         else {
            log.debug("Message starts with Basic.");
            String auth = new String(Base64.decode(msg.substring(6)), "US-ASCII");
            int index = auth.indexOf(':');
            String user = (index != -1) ? auth.substring(0, index) : auth;
            String password = (index != -1) ? auth.substring(index + 1) : "";
            index = user.indexOf('\\');
            if (index == -1)
               index = user.indexOf('/');
            
            String domain;
            if (index == -1) {
                domain = PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_DOMAIN);
            } else {
                domain = user.substring(0, index);
            }

            user = (index != -1) ? user.substring(index + 1) : user;
            ntlm = new NtlmPasswordAuthentication(domain, user, password);
            dc = UniAddress.getByName(getDomainController(), true);
         }
         try {

            SmbSession.logon(dc, ntlm);

            if (log.isDebugEnabled()) {
               log.debug("NtlmHttpFilter: " + ntlm + " successfully authenticated against " + dc);
            }
         }
         catch (SmbAuthException sae) {
            if (log.isServiceEnabled()) {
               log.service("NtlmHttpFilter: " + ntlm.getName() + ": 0x"
                     + jcifs.util.Hexdump.toHexString(sae.getNtStatus(), 8) + ": " + sae);
            }
            if (sae.getNtStatus() == NtStatus.NT_STATUS_ACCESS_VIOLATION) {
               /*
                * Server challenge no longer valid for externally supplied
                * password hashes.
                */
               HttpSession ssn = req.getSession(false);
               if (ssn != null) {
                  ssn.removeAttribute("NtlmHttpAuth");
               }
            }
            // lets try mmbase authentication now
            return true;
         }
         req.getSession().setAttribute("NtlmHttpAuth", ntlm);
         ((NtlmKnownVisitorModule) KnownVisitorModule.getInstance()).justLoggedIn(req, ntlm);
      }
      else {
         if (!skipAuthentication) {
            HttpSession ssn = req.getSession(false);
            if (ssn == null || (ntlm = (NtlmPasswordAuthentication) ssn.getAttribute("NtlmHttpAuth")) == null) {
               log.debug("Not NTLM authenticated, starting authentication.");
               resp.setHeader("WWW-Authenticate", "NTLM");
               if (offerBasic) {
                  resp.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
               }
               resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               resp.setContentLength(0);
               resp.flushBuffer();
               return false;
            }
         }
      }

      return ntlm != null;
   }


   private boolean offerBasic(HttpServletRequest req) {
      boolean offerBasic = req.isSecure();
      if (!offerBasic) {
         String basic = PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_BASIC_AUTH);
         if (StringUtils.isBlank(basic) || "secure".equalsIgnoreCase(basic)) {
            return false;
         }
         else {
            // basic authentication is not forced to be on secured urls and the current request is not secured
            // This does not mean that the url arrived at the webserver was not secured. The webserver could
            // proxy the request without using the secured flag.
            return true;
         }
      }
      return offerBasic;
   }


   private boolean isEnabled() {
      return Boolean.parseBoolean(PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_ENABLED));
   }

   private String getDomainController() {
      return PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_DOMAIN_CONTROLLER);
   }

   private List<String> getIpExceptions() {
       String prop = PropertiesUtil.getProperty(NtlmKnownVisitorModule.PROPERTY_IPEXCEPTIONS);
       return convertToList(prop);
   }

   private static List<String> convertToList(String prop) {
      List<String> list = new ArrayList<String>();
      StringTokenizer tokenizer = new StringTokenizer(prop, ", \t\n\r\f");
      while (tokenizer.hasMoreTokens()) {
         String str = tokenizer.nextToken();
         list.add(str);
      }
      return list;
   }
}
