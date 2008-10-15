package org.jahia.portlet.jforum;

import java.io.IOException;
import java.util.Date;

import javax.portlet.*;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.entities.User;
import net.jforum.util.MD5;
import org.jahia.portlet.util.StringUtil;

import java.security.Principal;

import net.jforum.entities.UserSession;
import net.jforum.SessionFacade;
import org.apache.log4j.Logger;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;

/**
 * Manage Auto portal connect user to JForum
 *
 * @author Khaled TLILI
 */
public class PortalAutoConnectUserManager {

   private final String GUEST = "guest";
   private RenderRequest request;
   private RenderResponse response;
   private final String MD5_KEY = "org.jahia.portlet.jforum";
   private final String AUTO_LOGIN_ATT = "autologin";
   private final String AUTO_REGISTER_ATT = "autoRegister";
   private final String JAHIA_PORTAL_ENGINE_PARAM = "engine_params";
   private boolean useJavacscriptRedirection = false;
   private final String ADMINISTRATOR_ROLE = "administrator";
   private boolean redirect = false;
   private static Logger logger = Logger.getLogger(PortalAutoConnectUserManager.class);


   /**
    * Constructor for the PortalAutoConnectUserManager object
    *
    * @param request  Description of Parameter
    * @param response Description of Parameter
    */
   public PortalAutoConnectUserManager(RenderRequest request, RenderResponse response) {
      this.request = request;
      this.response = response;
      logger.debug("Portal user: " + request.getRemoteUser());
   }


   /**
    * Gets the Registred attribute of the PortalAutoConnectUserManager class
    *
    * @return The Registred value
    */
   public boolean isRegistred() {
      try {
         UserDAO um = DataAccessDriver.getInstance().newUserDAO();
         logger.debug("Is registred: " + um.isUsernameRegistered(request.getRemoteUser()));
         return um.isUsernameRegistered(request.getRemoteUser());
      }
      catch (Exception ex) {
         ex.printStackTrace();
         return true;
      }
   }


   /**
    * Gets the Administrator attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The Administrator value
    */
   public boolean isAdministrator() {
      return request.isUserInRole(ADMINISTRATOR_ROLE);
   }


   /**
    * Gets the Redirect attribute of the PortalAutoConnectUserManager object
    *
    * @return The Redirect value
    */
   public boolean isRedirect() {
      return redirect;
   }


   /**
    * Gets the ValidConnection attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The ValidConnection value
    */
   public boolean isValidConnection() {
      Principal user = request.getUserPrincipal();

      // a user is using the portal
      if (isPortlatUser()) {
         String connectPortalUser = user.getName();
         String connectJForumUser = getJForumUsername();
         System.out.println("CurrentPortalUSer : " + connectPortalUser);
         // a user is using the jforum
         if (connectJForumUser != null) {
            System.out.println("CurrentJForumUSer : " + connectJForumUser);
            //DisConnect the user if he isn't the user of the portal
            if (!connectPortalUser.equalsIgnoreCase(connectJForumUser)) {
               return false;
            }
         } else {
            System.out.println("user session is null");
         }

      }
      return true;
   }


   /**
    * True if it's a not allowed user action. eg.g: logout for a portal user
    *
    * @return Description of the Returned Value
    */
   public boolean isNotAllowedAction() {
      return isPortlatUser() && isLogoutUserAction();
   }


   /**
    * Connect and register user thanks to jforum sso impl.
    *
    * @return Description of the Returned Value
    */
   public boolean processSSO() {
      // set pass to be used by sso
      String password = getPortalUserPassword();
      request.getPortletSession().setAttribute(SystemGlobals.getValue(ConfigKeys.SSO_PASSWORD_ATTRIBUTE), password, PortletSession.APPLICATION_SCOPE);
      //set it's group
      if (isAdministrator()) {
         request.getPortletSession().setAttribute(SystemGlobals.getValue("ROLE_ADMIN"), "ROLE_ADMIN", PortletSession.APPLICATION_SCOPE);

      }

      return true;
   }


   /**
    * process auto actions (register, connect)
    *
    * @return True if an auto action was performed
    */
   public boolean process() {
      // validate lout action
      /*
         *  if (isLogoutUserAction()) {
         *  validateLogout();
         *  }
         */
      // check logout action
      if (hasToMakeLogout()) {
         doAutoLogoutUser();
         logger.debug("do auto logout");
         return true;
      }

      // check other automatics action
      if (isPortlatUser()) {
         logger.debug("User is a portal user");
         if (!isRegistred()) {
            logger.debug("Process: Not already register");
            boolean hasToMakeRegister = hasToMakeRegister();
            // register
            if (hasToMakeRegister) {
               logger.debug("Process: do register");
               doAutoRegisteUser();
               doAutoConnectUser();
               return true;
            } else {
               logger.debug("Process: do not register");
               return false;
            }
         } else {

            // is already registred
            if (hasToMakeLogin()) {
               logger.debug("Process: do connect");
               doAutoConnectUser();
               return true;
            } else {
               //already connected
               logger.debug("Process: already connected");
               return false;
            }

         }
      } else {
         logger.debug("User is not a portal user");
         logger.debug("AutoConnect: do nothing");
         // not a portal user --> do nothink
         return false;
      }
   }


   /**
    * Sets the JForumUsername attribute of the PortalAutoConnectUserManager
    * object
    *
    * @param username The new JForumUsername value
    */
   private void setJForumUsername(String username) {
      request.getPortletSession().setAttribute("connectJForumUser", username);

   }


   /**
    * Gets the JForumUsername attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The JForumUsername value
    */
   private String getJForumUsername() {
      String connectJForumUser = (String) request.getPortletSession().getAttribute("connectJForumUser");
      return connectJForumUser;
   }


   /**
    * Gets the PortlatUser attribute of the PortalAutoConnectUserManager object
    *
    * @return The PortlatUser value
    */
   private boolean isPortlatUser() {
      String user = request.getRemoteUser();
      if (user == null) {
         return false;
      }

      // for jahia: non portal user = user GUEST
      return !user.equalsIgnoreCase(GUEST);
   }


   /**
    * Gets the PortalUserPassword attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The PortalUserPassword value
    */
   private String getPortalUserPassword() {
      String username = request.getRemoteUser();
      return MD5.crypt(username + MD5_KEY);
   }


   /**
    * Gets the RegisterUserAction attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The RegisterUserAction value
    */
   private boolean isRegisterUserAction() {
      String moduleValue = request.getParameter("module");
      if (moduleValue != null && moduleValue.equalsIgnoreCase("user")) {
         String actionValue = request.getParameter("action");
         if (actionValue != null && actionValue.equalsIgnoreCase("insertSave")) {
            return true;
         }
      }
      return false;
   }


   /**
    * Gets the LoginUserAction attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The LoginUserAction value
    */
   private boolean isLoginUserAction() {
      String moduleValue = request.getParameter("module");
      if (moduleValue != null && moduleValue.equalsIgnoreCase("user")) {
         String actionValue = request.getParameter("action");
         if (actionValue != null && actionValue.equalsIgnoreCase("validateLogin")) {
            return true;
         }
      }
      return false;
   }


   /**
    * Gets the LogoutUserAction attribute of the PortalAutoConnectUserManager
    * object
    *
    * @return The LogoutUserAction value
    */
   private boolean isLogoutUserAction() {
      String moduleValue = request.getParameter("module");
      if (moduleValue != null && moduleValue.equalsIgnoreCase("user")) {
         String actionValue = request.getParameter("action");
         if (actionValue != null && actionValue.equalsIgnoreCase("logout")) {
            return true;
         }
      }
      return false;
   }


   /**
    * Description of the Method
    */
   private void removeJForumUsername() {
      request.getPortletSession().removeAttribute("connectJForumUser");

   }


   /**
    * Auto connect user
    */
   private void doAutoConnectUser() {

      response.setContentType("text/html");
      System.out.println("Do auto login ");
      String username = request.getRemoteUser();
      String password = getPortalUserPassword();
      // build request
      PortletURL pUrl = response.createRenderURL();
      pUrl.setParameter("module", "user");
      pUrl.setParameter("action", "validateLogin");
      pUrl.setParameter("username", username);
      pUrl.setParameter("password", password);
      //pUrl.setParameter("requestUri", "");
      pUrl.setParameter("httpMethod", "POST");
      pUrl.setParameter(JForumPortletBridge.PROCESS_ACTION_PERFORMED_KEY, "1");
      String redirectUrl = pUrl.toString();
      // perform redirection
      autoLoginDone();
      doRedirection(redirectUrl);

   }


   /**
    * Do a javascript redirection
    *
    * @param redirectUrl Description of Parameter
    */
   private void doRedirection(String redirectUrl) {
      String script = "<script> document.location='" + redirectUrl + "'; </script>";
      try {
         this.redirect = true;
         response.getPortletOutputStream().write(script.getBytes());
      }
      catch (IOException ex) {
         ex.printStackTrace();
      }
   }


   /**
    * Auto register user
    */
   private void doAutoRegisteUser() {
      System.out.println("Do auto register ");

      // get user information
      String username = request.getRemoteUser();
      String password = getPortalUserPassword();
      String email = StringUtil.notNullValue(request.getProperty("user.email"), username + "@examplemail.com");

      // process action thanks to javascript redirection
      if (useJavacscriptRedirection) {
         System.out.println("User javascript redirection ");
         response.setContentType("text/html");

         // build redirect url
         PortletURL pUrl = response.createRenderURL();
         pUrl.setParameter("module", "user");
         pUrl.setParameter("action", "insertSave");
         pUrl.setParameter("username", username);
         pUrl.setParameter("password", password);
         pUrl.setParameter("email", email);

         //pUrl.setParameter("requestUri", "");
         pUrl.setParameter("httpMethod", "POST");
         pUrl.setParameter(JForumPortletBridge.PROCESS_ACTION_PERFORMED_KEY, "1");
         String redirectUrl = pUrl.toString();

         // perform redirection
         autoRegisterDone();
         doRedirection(redirectUrl);
      }
      // process action whithout javascript redirection
      else {
         // get user dao
         DataAccessDriver dad = DataAccessDriver.getInstance();
         UserDAO userDAO = dad.newUserDAO();

         // create new user
         User newUser = new User();
         newUser.setUsername(username);
         newUser.setPassword(MD5.crypt(password));
         newUser.setEmail(email);
         newUser.setLastVisit(new Date());

         int[] adminGrouId = {2};

         try {
            // add new user
            userDAO.addNew(newUser);

            //set it's group
            if (isAdministrator()) {
               userDAO.addToGroup(newUser.getId(), adminGrouId);
            }
         }
         catch (Exception ex) {
            ex.printStackTrace();
         }

      }
   }


   /**
    * Do auto logout
    */
   private void doAutoLogoutUser() {

      response.setContentType("text/html");
      System.out.println("Do auto logout ");

      // build redirect url
      PortletURL pUrl = response.createActionURL();
      pUrl.setParameter("module", "user");
      pUrl.setParameter("action", "logout");
      //pUrl.setParameter("requestUri", "");
      pUrl.setParameter("httpMethod", "POST");
      pUrl.setParameter(JForumPortletBridge.PROCESS_ACTION_PERFORMED_KEY, "1");
      String redirectUrl = pUrl.toString();

      // invalidate session
      request.getPortletSession().removeAttribute(AUTO_LOGIN_ATT);
      request.getPortletSession().removeAttribute(JForumPortletBridge.JFORUM_OUPUTSTREAM_RESULT_KEY);
      request.getPortletSession().invalidate();
      validateLogout();

      // perform redirection
      doRedirection(redirectUrl);
   }


   /**
    * Validate auto login
    */
   private void autoLoginDone() {
      request.getPortletSession().invalidate();
      setJForumUsername(request.getRemoteUser());
      request.getPortletSession().setAttribute(AUTO_LOGIN_ATT, "1");
   }


   /**
    * Description of the Method
    */
   private void validateLogout() {
      removeJForumUsername();
      request.getPortletSession().removeAttribute(AUTO_LOGIN_ATT);
   }


   /**
    * Validate auto register
    */
   private void autoRegisterDone() {
      request.getPortletSession().setAttribute(AUTO_REGISTER_ATT, "1");
   }


   /**
    * True if autologin has to be performed
    *
    * @return Description of the Returned Value
    */
   private boolean hasToMakeLogin() {
      //return SessionFacade.isLogged();
      PortletSession session = request.getPortletSession();
      Object o = session.getAttribute(AUTO_LOGIN_ATT);
      if (o == null) {
         return true;
      } else {
         return false;
      }

      //return checkJahiaEngineParam("login");
   }


   /**
    * Check if there is a Jahia portal disconnection
    *
    * @return True if there is a jahia portal disconnection
    */
   private boolean hasToMakeLogout() {
      return !isValidConnection() || checkJahiaEngineParam("logout");
   }


   /**
    * Check value of jahia param engine
    *
    * @param value value
    * @return true if jahai-egine-parma is value
    */
   private boolean checkJahiaEngineParam(String value) {
      String engineParam = (String) request.getParameter(JAHIA_PORTAL_ENGINE_PARAM);
      if (engineParam == null) {
         return false;
      }
      return engineParam.equalsIgnoreCase(value);
   }


   /**
    * True if autoregistering has to be performed
    *
    * @return True if an autoRegistration is nested
    */
   private boolean hasToMakeRegister() {
      return !isRegisterUserAction();
      /*
         *  PortletSession session = request.getPortletSession();
         *  Object o = session.getAttribute(AUTO_REGISTER_ATT);
         *  if (o == null) {
         *  return true;
         *  }
         *  else {
         *  return false;
         *  }
         */
	}

}
