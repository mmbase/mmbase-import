package nl.didactor.security;


import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpUtils;


import nl.didactor.builders.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.util.FileWatcher;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


import nl.didactor.security.UserContext;
import nl.didactor.security.AuthenticationMode;

import org.mmbase.security.implementation.aselect.*;


import nl.didactor.utils.debug.LogController;

/**
 * Didactor authentication routines. This class authenticates users
 * against the cloud, and returns their rank based on the builder
 * they belong to.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class Authentication extends org.mmbase.security.Authentication
{
   private static Logger log = Logging.getLoggerInstance(Authentication.class.getName());
   private PeopleBuilder users;


   private static final String KEY_REQUEST   = "request";
   private static final String KEY_RESPONSE  = "response";


   /**
    * the address of the A-Select Agent
    */
   private static final String agentAddress = "127.0.0.1";

   /**
    * the communication port of the A-Select Agent
    */
   private int agentPort = 1495; // default


/*
   private Properties knownUsers;
   private Properties registeredRanks = null;
*/


   /**
    * ASelect server address
    */
   private String aselectServer = "https://ext1.alfa-ariss.com/aselectserver/server"; // default

   /**
    * A-Select Application
    */
   private String sASelectApplication = "mmbase";


   private FileWatcher fileWatcher = null;


   private boolean bDebugMode = LogController.showLogs("az");
   private String  sDebugIndo = "" + new Date() + "  A-Select Module: ";


   /**
    * This is a global login page
    * There is a manager inside the .jsp that serves defaul login method if there is any
    * By default users see menu with all possible login methods
    */
   private String sLoginPage = "/login.jsp";

   /**
    * Stores information about user login mode + node of people builder
    */
   private HashMap hmapSecurityModes = new HashMap();




   /**
    * Called once my MMBase core during security starting
    *
    */
   protected void load()
   {
      if(bDebugMode) System.out.println(sDebugIndo + "--------- security module loading ---------");

      if (fileWatcher == null)
      {
         fileWatcher = new ConfigurationWatcher();
         fileWatcher.add(configFile);
//         fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "accounts.properties"));
//         fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "ranks.properties"));

         fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "agent.conf"));
         fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "login.properties"));

         fileWatcher.setDelay(10 * 1000);
         fileWatcher.start();
      }
      configure();
   }









   static
   {
      Rank.createRank(200, "people");
   }

   /**
    * Login method: it tests the given credentials against MMBase.
    * @param application The application identifier
    * @param loginInfo A Map containing the login credentials
    * @param parameters A list of optional parameters
    */
   public org.mmbase.security.UserContext login(String application, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException
   {
      checkBuilder();


      if(bDebugMode) System.out.println();
      if(bDebugMode) System.out.println(sDebugIndo + "------------ LOGIN REQUEST ------------");
      if(bDebugMode) System.out.println(sDebugIndo + "application=" + application);


      HttpServletRequest request = null;
      HttpServletResponse response = null;

      if((loginInfo != null) && (loginInfo.get(KEY_REQUEST) != null) && (loginInfo.get(KEY_RESPONSE) != null)){
         request = (HttpServletRequest) loginInfo.get(KEY_REQUEST);
         response = (HttpServletResponse) loginInfo.get(KEY_RESPONSE);
      }

      if(bDebugMode){
         System.out.print(sDebugIndo + "authenticate=");
         if ( (request != null))
         {
            System.out.println(request.getParameter("authenticate"));
         }
         else
         {
            System.out.println("null");
         }
      }

      if("didactor-logout".equals(application)){
         if(hmapSecurityModes.get(request.getSession().getId()) != null){
            AuthenticationMode mode = (AuthenticationMode) hmapSecurityModes.get(request.getSession().getId());

            //System.out.println("LOGOUT-LOGOUT-LOGOUT-LOGOUT-LOGOUT");

            if("a-select".equals(mode.getName())){
               this.logout(request, response, sASelectApplication);
               //System.out.println("AGENT'S BASE FLUSHED");
            }

            hmapSecurityModes.remove(request.getSession().getId());
            //System.out.println("LOGOUT-FINISHED");
         }

         return null;
      }



      if((request != null) && ("plain".equals(request.getParameter("authenticate"))))
      {
         if(bDebugMode) System.out.println("");
         if(bDebugMode) System.out.println(sDebugIndo + "--- plain mode ---");

         return this.doPlainLogin(application, request, response, parameters);
      }





      if("a-select".equals(application))
      {
         if(bDebugMode) System.out.println("");
         if(bDebugMode) System.out.println(sDebugIndo + "--- a-select mode ---");

/*
         if ("anonymous".equals(application))
         {
            try
            {
               logout(request, response, "mmbase");
               // Hmm, I don't really understand why I cannot find the application back with the ticket or so.

               response.sendRedirect(aselectServer);
               return null;
               //return new ASelectUser("anonymous", Rank.ANONYMOUS);
            }
            catch (ASelectException ase)
            {
               throw new SecurityException(ase);
            }
            catch (Exception e)
            {
               return null;
            }
         }
*/

         ASelectUser newUser;
         // make connection  to A-Select agent, and create an ASelectUser object
         try
         {
            if (authentication(request, response, sASelectApplication))
            {
               String userName = getASelectUserId(request);
               Rank rank = Rank.BASICUSER;

               if (log.isDebugEnabled())
               {
                  log.debug("Logging in user: " + userName);
               }
               newUser = new ASelectUser(userName, rank);

            }
            else
            {
               log.debug("User needs authentication and has been redirected to A-Select");
               return null;
               //throw new SecurityException("user has been redirected to A-Select");
            }
         }
         catch (Exception e)
         {
            //System.out.println("A-SELECT connection error:" + e.toString());
            try{
               response.getWriter().println("<b>A-Select error:</b><br/>");
               response.getWriter().println(e.toString());
            }
            catch(Exception ex){

            }
            //throw new SecurityException(e);
            throw new SecurityException(e.getMessage(), e);
         }

/*
         List userNames = (List) loginInfo.get(KEY_USERNAMES);
         if (userNames != null && !userNames.contains(newUser.getIdentifier()))
         {
            throw new SecurityException("Logged on as wrong user ('" + newUser.getIdentifier() + "', but must be one of " + userNames + ")");
         }
*/


    //      MMObjectNode user = users.getUser(username, password);
         MMObjectNode user = users.getUser(newUser.getIdentifier());
         if (user == null)
         {
            //System.out.println("A-Select has been passed, but there is no such user in db");
            try{
               response.getWriter().println("<b>A-Select error:</b><br/>");
               response.getWriter().println("The authorization passed, but user with id=<b>" + newUser.getIdentifier() + "</b> is NOT present in Didactor's database<br/>");
               response.getWriter().println("Can't create security context for him.");
            }
            catch(Exception ex){
            }
            //throw new SecurityException(e);
            throw new SecurityException("no user (id=" + newUser.getIdentifier() + ") in db", null);
         }

         AuthenticationMode mode = new AuthenticationMode("a-select", user);
         this.hmapSecurityModes.put(request.getSession().getId(), mode);

         return new UserContext(user);
      }





      if((request == null) || (response == null))
      {//System mode
         if(bDebugMode) System.out.println("");
         if(bDebugMode) System.out.println(sDebugIndo + "--- system mode ---");
         return this.doSystemLogin();
      }
      else{
         //System.out.println("===========================it's time to use login!!!!!!!!");
         //System.out.println("CURRENT SECURITY MODE=" + hmapSecurityModes.get(request.getSession().getId()));

         if(hmapSecurityModes.get(request.getSession().getId()) != null){
            AuthenticationMode mode = (AuthenticationMode) hmapSecurityModes.get(request.getSession().getId());

            if (mode != null)
            {
               return new UserContext(mode.getUser());
            }
         }

         //The user hasn't login yet, redirecting him to Didactor's login page
         //System.out.println("==================================");
         //System.out.println(" Move to login page, damned user! ");
         //System.out.println("==================================");
         try{
            String sRedirect = response.encodeRedirectURL(this.sLoginPage + "?referrer=" + request.getRequestURI());
            if(bDebugMode) System.out.println(sRedirect);
            response.sendRedirect(sRedirect);
         }
         catch(Exception e){
            throw new SecurityException("Can't redirect to login page(" + sLoginPage + ")", e);
         }

         return null;
      }
   }





   public boolean isValid(org.mmbase.security.UserContext usercontext) throws org.mmbase.security.SecurityException
   {
      return true;
   }




   private void checkBuilder() throws org.mmbase.security.SecurityException
   {
      if (users == null)
      {
         org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
         users = (PeopleBuilder) mmb.getMMObject("people");
         if (users == null)
         {
            String msg = "builder people not found";
            log.error(msg);
            throw new org.mmbase.security.SecurityException(msg);
         }
      }
   }





   private class ConfigurationWatcher extends FileWatcher
   {
       ConfigurationWatcher()
       {
           super();
       }
       public void onChange(File file)
       {
           configure();
       }
   }





   protected void configure()
   {
       // get some parameters

       Properties config = new Properties();
       Properties propGlobalLogin = new Properties();

       String sASelectConfig = MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "agent.conf";
       String sGlobalConfig  = MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "login.properties";

       try
       {
           log.service("Reading file: " + sASelectConfig);
           if(bDebugMode) System.out.println(sDebugIndo + "Reading config file=" + sASelectConfig);

           config.load(new FileInputStream(sASelectConfig));
           agentPort    = new Integer(config.getProperty("serviceport")).intValue();
           if(bDebugMode) System.out.println(sDebugIndo + "port=" + agentPort);

           String firstServer = config.getProperty("aselect.server.1");
           aselectServer = config.getProperty("aselect.server." + firstServer);
           sASelectApplication = config.getProperty("application");
           if(bDebugMode) System.out.println(sDebugIndo + "server=" + aselectServer);
           if(bDebugMode) System.out.println(sDebugIndo + "application=" + sASelectApplication);
           if(bDebugMode) System.out.println(sDebugIndo + "---------");



           log.service("Reading file: " + sGlobalConfig);
           if(bDebugMode) System.out.println(sDebugIndo + "Reading config file=" + sGlobalConfig);
           propGlobalLogin.load(new FileInputStream(sGlobalConfig));

           this.sLoginPage = propGlobalLogin.getProperty("login_page");
           if(bDebugMode) System.out.println(sDebugIndo + "login_page=" + sLoginPage);

       }
       catch (Exception e)
       {
           log.error(e.getMessage());
       }

   }



   /**
    * Performs the work of authentication and session management.
    *
    * This function should be called for each request to the Servlet.<br>
    * If the user has a valid session, true will be returned and de Servlet can process the request.<br>
    * If not, this module has redirected the user to A-Select or has thrown an exception.<br>.
    * In the situation a user is not yet authenticated the Servlet wil get severel requests with authentication
    * parameters. That's why this function should be called before processing the request by the servlet.
    *
    * @param request the current HTTP request. Used to obtain the parameters
    * for authentication.
    * @param response the current HTTP response
    * @return true if the user was authenticated, false otherwise.<br>
    * If false is returned the Servlet should not write anything to the
    * client because the client was already redirected!
    * @throws ASelectException If the module could not perform the authenticate request
    */
   protected boolean authentication(HttpServletRequest request, HttpServletResponse response, String application) throws ASelectException
   {
       try
       {
           // look if there is a valid ticket
           // if not do the authentication process
           if (verify_ticket(request, response, application))
           {
               return true;
           }
           if (verify_credentials(request, response))
           {
               return false; //process continues
           }
           if (authenticate_user(request, response, application))
           {
               return false; //process continues
           }
           throw new Exception("The A-Select Authentication Module has received an unknown request. Please try again.");
       }
       catch (Exception e)
       {
           throw new ASelectException("Could not perform authentication: " + e.getMessage(),  e);
       }
   } //end of function : authentication



   /**
    * Retrieves the A-Select User Id from the cookies.
    *
    * @param request the current HTTP request. Used to obtain the cookie(s)
    * @return The A-Select user id or null if not set
    */
   protected String getASelectUserId(HttpServletRequest request) {
       String xUserId = getCookie(request, "aselectuid");
       return decodeCGI(xUserId);
   } //end of function : getASelectUserId




   /**
    * Retrieves the A-Select Organization Id from the cookies.
    *
    * @param request the current HTTP request. Used to obtain the cookie(s)
    * @return The A-Select organization id or null if not set
    */
   protected String getASelectOrganization(HttpServletRequest request) {
       String xOrganization = getCookie(request, "aselectorganization");
       return decodeCGI(xOrganization);

   } //end of function : getASelectOrganization






   /**
    * Retrieves the A-Select Session Id from the cookies.
    *
    * @param request the current HTTP request. Used to obtain the cookie(s)
    * @return The A-Select session id or null if not set
    */
   protected String getASelectSessionId(HttpServletRequest request) {
       String xSessionId = getCookie(request, "aselectticket");
       //   xSessionId = decodeCGI( xSessionId );
       return xSessionId;
   } //end of function : getASelectSessionId



   /**
    * Processes the logout of a user.
    *
    * @param request the current HTTP request
    * @param response the current HTTP response.
    */
   protected void logout(HttpServletRequest request, HttpServletResponse response, String application){
       String xTicket = null;
       String xUid = null;
       Hashtable xAgentResponse = null;

       String xResultCode = null;

       xUid    = getASelectUserId(request);
       xTicket = getASelectSessionId(request);

       if (log.isDebugEnabled()) {
           log.debug("Logging out uid '" + xUid + "' with ticket '" + xTicket + "'");
       }

       if (xTicket != null) {
           try {
               xAgentResponse = transferRequest("request=kill_ticket&ticket=" + xTicket + "&app_id=" + application);
           } catch (Exception e) {
               log.error(e);
           }
       }


       //added by AZ:
       request.getSession().removeAttribute("aselectticket");
       request.getSession().removeAttribute("aselectuid");
       request.getSession().removeAttribute("aselect_credentials");
       request.getSession().removeAttribute("aselectorgurlparams");
       request.getSession().removeAttribute("aselectorganization");
/*
       Cookie[] arrCookies = request.getCookies();
       for(int f = 0; f < arrCookies.length; f++){
          Cookie cookie = arrCookies[f];
          if(("aselectticket".equals(cookie.getName())) ||
             ("aselectuid".equals(cookie.getName())) ||
             ("aselect_credentials".equals(cookie.getName())) ||
             ("aselectorgurlparams".equals(cookie.getName())) ||
             ("aselectorganization".equals(cookie.getName()))){
             cookie.setValue("");
             cookie.setMaxAge(0);
             response.addCookie(cookie);
          }
          //System.out.println(cookie.getName());
       }
*/

   } //end of function : logout



   /**
    * This function will perform the verify ticket function.
    * @return 'true' if the user has a valid A-Select ticket (at the A-Select Agent!)
    *
    * @param request the current HTTP request
    * @param response the current HTTP response.
    */
   private boolean verify_ticket(HttpServletRequest request, HttpServletResponse response, String application) throws Exception
   {
       String xUid = null;
       String xOrganization = null;
       String xTicket = null;
       Hashtable xAgentResponse = null;

       String xResultCode = null;
       String xMethod = "ASelectServletFilter.verify_ticket -> ";

       log.debug("entering" + xMethod);


       xTicket = getASelectSessionId(request);
       if (xTicket == null) {
           log.debug("ticket is null returning ");
           return false;
       }


       xUid = getASelectUserId(request);
       if (xUid == null)
           return false;


       xOrganization = getASelectOrganization(request);
       if (xOrganization == null)
       {
          return false;
       }


       try
       {
          xAgentResponse = transferRequest("request=verify_ticket&ticket=" + xTicket + "&app_id=" + application + "&uid=" + xUid + "&organization=" + xOrganization);
       }
       catch (Exception e)
       {
          throw new Exception(xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br />  NOTE : The A-Select agent is not started or misconfigured <br />");
       }


       xResultCode = (String)xAgentResponse.get("result_code");
       if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR))
           return false;


       return true;
   } //end of function : verify_ticket




   /**
    * This function will perform the verify credentials function.
    * This function will return 'true' if the credentials provided in the parametrs are correct.
    * The credentials are being set by the A-Select server
    *
    * @param request the current HTTP request
    * @param response the current HTTP response.
    */
   private boolean verify_credentials(HttpServletRequest request, HttpServletResponse response) throws Exception {
       String xRID = null;
       String xCredentials = null;
       Hashtable xAgentResponse = null;

       String xResultCode = null;
       String xUid = null;
       String xOrganization = null;
       String xTicket = null;
       String xRedirectUrl = null;
       String xMethod = "ASelectServletFilter.verify_credentials -> ";

       xRID = request.getParameter("rid");
       if (xRID == null)
           return false;

       xCredentials = request.getParameter("aselect_credentials");
       if (xCredentials == null)
           return false;

       try {
           xAgentResponse = transferRequest("request=verify_credentials&rid=" + xRID + "&aselect_credentials=" + xCredentials);
           log.debug("Agent response " + xAgentResponse);
       } catch (Exception e) {
           throw new Exception(
               xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
       }

       xResultCode = (String)xAgentResponse.get("result_code");
       if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR))
           throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");

       // get the parameters to set as cookie
       xUid = (String)xAgentResponse.get("uid");
       if (xUid == null)
           throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'uid'.<br>");

       xOrganization = (String)xAgentResponse.get("organization");
       if (xOrganization == null)
           throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'organization'.<br>");

       xTicket = (String)xAgentResponse.get("ticket");
       if (xTicket == null)
           throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'ticket'.<br>");

       xUid = doCGIEncode(xUid);

/*
       // set the cookies
       Cookie xTicketCookie = new Cookie("aselectticket", xTicket);
       xTicketCookie.setPath(request.getContextPath());
       Cookie xUidCookie = new Cookie("aselectuid", xUid);
       xUidCookie.setPath(request.getContextPath());
       Cookie xOrgCookie = new Cookie("aselectorganization", xOrganization);
       xOrgCookie.setPath(request.getContextPath());

       response.addCookie(xTicketCookie);
       response.addCookie(xUidCookie);
       response.addCookie(xOrgCookie);
*/
       request.getSession().setAttribute("aselectticket", xTicket);
       request.getSession().setAttribute("aselectuid", xUid);
       request.getSession().setAttribute("aselectorganization", xOrganization);



       xRedirectUrl = new String(HttpUtils.getRequestURL(request));
       xRedirectUrl += "?" + request.getQueryString();

       try
       {
           response.sendRedirect(xRedirectUrl);
       }
       catch (Exception e)
       {
           throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
       }
       log.debug("success " + xUid);
       return true;
   } //end of function : verify_credentials




   /**
    * This function will perform the authenticate function.
    * This function will call the agent and the redirect the user to the A-Select server.
    *
    * @param request the current HTTP request
    * @param response the current HTTP response.
    */
   private boolean authenticate_user(HttpServletRequest request, HttpServletResponse response, String application) throws Exception {
       String xAppUrl = null;
       String xParams = null;
       Hashtable xAgentResponse = null;
       String xResultCode = null;
       String xRedirectUrl = null;
       String xAsUrl = null;
       String xASelectServer = null;
       String xRid = null;
       String xMethod = "ASelectServletFilter.verify_credentials -> ";

       xParams = request.getQueryString();
       if (xParams != null)
       {
          /*
           Cookie xOrgParamsCookie = new Cookie("aselectorgurlparams", xParams);
           response.addCookie(xOrgParamsCookie);
          */
          request.getSession().setAttribute("aselectorgurlparams", xParams);
       }
       xAppUrl = request.getRequestURL().toString();
       //System.out.println("++++++++++" + xAppUrl);


       try
       {
           xAgentResponse = transferRequest("request=authenticate&app_url=" + xAppUrl + "&app_id=" + application);
       }
       catch (Exception e)
       {
           throw new Exception("The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
       }



       xResultCode = (String)xAgentResponse.get("result_code");
       if(bDebugMode) System.out.println(sDebugIndo + "the client has returned code:" + xResultCode);


       if (xResultCode == null)
       {
          throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");
       }
       else if (!xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR))
       {
           if (xResultCode.equals(ASelectErrors.ASELECT_UNKNOWN_USER) || xResultCode.equals(ASelectErrors.ASELECT_COULD_NOT_AUTHENTICATE_USER))
           {
               xRedirectUrl = new String(HttpUtils.getRequestURL(request));
               try
               {
                   response.sendRedirect(xRedirectUrl);
               }
               catch (Exception e)
               {
                   throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
               }
           }
           else throw new Exception(xMethod + "The A-Select Agent did return an error : " + xResultCode + "<br>");
       }

       // get the parameters to set as cookie
       xRid = (String)xAgentResponse.get("rid");
       if (xRid == null)
       {
          throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'rid'.<br>");
       }


       xAsUrl = (String)xAgentResponse.get("as_url");
       if (xAsUrl == null)
       {
          throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'as_url'.<br>");
       }


       xASelectServer = (String)xAgentResponse.get("a-select-server");
       if (xASelectServer == null)
       {
          throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'a-select-server'.<br>");
       }

       xRedirectUrl = xAsUrl + "&rid=" + xRid + "&a-select-server=" + xASelectServer;
       try
       {
           response.sendRedirect(URLDecoder.decode(xRedirectUrl, "ISO-8859-1"));
       }
       catch (Exception e)
       {
           throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
       }
       return true;
   } //end of function : authenticate_user




   /**
    * This function will transfer the request to the A-Select Agent.
    *
    * @param request the request which has to be sent to the A-Select Agent.
    * @returns Hashtable This hashtable contains the response from the Agent.
    */
   private Hashtable transferRequest(String request) throws Exception {
//       System.out.println("111111111111111111111111111111111" + request);
       Socket xSocket = null;
       PrintStream xOut = null;
       BufferedReader xIn = null;
       String xAgentResponse = null;

       xSocket = new Socket(agentAddress, agentPort);
       xOut = new PrintStream(xSocket.getOutputStream());
       xIn = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));

       xOut.println(request);
       xAgentResponse = xIn.readLine();
//       System.out.println("222222222222222222222222222222222" + xAgentResponse);
       return convertCGIMessage(xAgentResponse);
   } //end of function : transferRequest




   /**
    * This function will get the cookie form the (servlet)request
    *
    * @param request the servlet request
    * @param xCookieName the name of the cookie to obtain from the request.
    * @returns String The value of the cookie.
    */
   private String getCookie(HttpServletRequest request, String xCookieName) {

/*
       Cookie[] xCookies = null;

       xCookies = request.getCookies();
       if (xCookies == null) {
           log.debug("no cookies here");
           return null;
       }

       for (int i = 0; i < xCookies.length; i++) {
           log.debug("looking at cookie " + xCookies[i].getName() + " with value " + xCookies[i].getValue());
           if (xCookies[i].getName().equals(xCookieName)) {
               log.debug("found cookie " + xCookieName + " with value " + xCookies[i].getValue());

               if("".equals(xCookies[i].getValue())){
                  //the cookie was forcedly reseted by Didactor
                  return null;
               }
               return xCookies[i].getValue();
           }
       }
       log.debug("failed to find cookie " + xCookieName);
       return null;
*/
      if(request.getSession().getAttribute(xCookieName) != null){
         return (String) request.getSession().getAttribute(xCookieName);
      }
      else{
         return null;
      }
   } //end of function : getCookie



   /**
    * This function will do a little CGI encoding
    * For now it is not complete implemented
    *
    *@param xValue The value to encode
    *@return the encoded value
    */
   private String doCGIEncode(String xValue) {
       String xEncoded = null;

       if (xValue != null) {
           xEncoded = xValue.replace(' ', '+');
       }

       return xEncoded;
   } //end of function : doCGIEncode




   /**
    * This function will do a little CGI decoding
    * For now it is not complete implemented
    *
    *@param xValue The value to decode
    *@return the decoded value
    * hmm
    */
   private String decodeCGI(String xValue) {

       int iPos;
       String xDecoded = null;
       if (xValue != null) {
           xDecoded = xValue.replace('+', ' ');
           iPos = xDecoded.indexOf("%2B");
           while (iPos != -1) {
               xDecoded = xDecoded.substring(0, iPos) + xDecoded.substring(iPos + 3);
               iPos = xDecoded.indexOf("%2B");
           }
       }
       return xDecoded;
   } //end of function : decodeCGI




   /**
    * This method will convert a string of <code>key=value&key=value</code>
    * etc. tuples (aka a CGI request string) into a hashtable for much easier
    * processing.<br />
    * <b>Note:</b> The key names are all converted to lowercase.
    * @todo can we not simply use request.getParameter?
    */
   public static Hashtable convertCGIMessage(String xMessage) {
       String xToken, xKey, xValue;
       StringTokenizer xST = null;
       int iPos;
       Hashtable response = new Hashtable();

       if (xMessage != null) {
           xST = new StringTokenizer(xMessage, "&");

           while (xST.hasMoreElements()) {
               xToken = (String)xST.nextElement();
               if (!xToken.trim().equals("")) {
                   iPos = xToken.indexOf('=');
                   if (iPos != -1) {
                       xKey = xToken.substring(0, iPos);

                       try {
                           xValue = xToken.substring(iPos + 1);
                       } catch (Exception e) {
                           xValue = "";
                       }

                       if (xKey != null && xValue != null) {
                           response.put(xKey.toLowerCase(), xValue);
                       }
                   }
               }
           }
       }
       return response;
   }




   /**
    * Old Dicator's style login
    *
    *
    * @param application String
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * @param parameters Object[]
    * @return UserContext
    */
   org.mmbase.security.UserContext doPlainLogin(String application, HttpServletRequest request, HttpServletResponse response, Object[] parameters)
   {
      if ("anonymous".equals(application))
      {
         return new UserContext("anonymous", "anonymous", Rank.ANONYMOUS);
      }


      String sLogin = request.getParameter("username");
      String sPassword = request.getParameter("password");



      if(bDebugMode) System.out.println();
      if(bDebugMode) System.out.println(sDebugIndo + "plain: username=" + sLogin);
      if(bDebugMode) System.out.println(sDebugIndo + "plain: password=" + sPassword);


      if (sLogin == null || sPassword == null)
      {
         return null;
      }

      MMObjectNode user = users.getUser(sLogin, sPassword);
      if (user == null)
      {
         return null;
      }

      AuthenticationMode mode = new AuthenticationMode("plain", user);
      this.hmapSecurityModes.put(request.getSession().getId(), mode);

      return new UserContext(user);
   }




   /**
    * Admin's backdoor for Cron & etc
    * Doesn't require password for login
    *
    * @return UserContext
    */
   org.mmbase.security.UserContext doSystemLogin()
   {
      MMObjectNode user = users.getUser("admin");
      if (user == null)
      {
         return null;
      }

      if(bDebugMode) System.out.println(sDebugIndo + "system: username=admin");

      return (org.mmbase.security.UserContext)(new UserContext(user));
   }

}
