package com.finalist.cmsc.services.community;

/**
 * CommunityServiceMysqlImpl, this is a CMSc service class.
 * This class extends the service classes from the servicemanagement
 * in the CMScontainer.
 * All the request's to the community module come through here.
 * This class will find out the class that is needed for each
 * request and call the method in the needed class.
 * 
 * In this class the LoginContext will be called within the loginUser method.
 * 
 * <pre>
 *    LoginContext lc = new LoginContext("jaasdb", cbh);
 *          lc.login();
 * </pre>
 * 
 * This context is made by the jaas.config file in the class path. This config file
 * contains the required class for the login. In this case "HibernateLoginModule"
 * in this context lc means HibernateLoginModule.
 * 
 * @author menno menninga
 */
import javax.security.auth.login.LoginContext;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.portlet.PortletSession;
import com.finalist.cmsc.services.community.HibernateCommunityService;

public class CommunityServiceMysqlImpl extends CommunityService {

   private static Log log = LogFactory.getLog(CommunityServiceMysqlImpl.class);

   private PortletSession session;
   
   ApplicationContext aC;
   
   HibernateCommunityService hibservice;
   
   public boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText) {
      
      boolean loginSuccesfull;
      String userName = "";
      String firstName = "";
      String lastName = "";
      String emailAdress = "";
      
      try {
         PassiveCallbackHandler cbh = new PassiveCallbackHandler(userText, passText);

         LoginContext lc = new LoginContext("jaasdb", cbh);

         lc.login();
         
         log.info("PortletSession: " + request.getPortletSession());

         PortletSession session = request.getPortletSession();
         Iterator it = lc.getSubject().getPrincipals().iterator();
         session.setAttribute("LoginContext", lc);
         String tempPrin = it.next().toString();
         String values[] = tempPrin.split(",");
         for(int i = 0; i < values.length; i++){
            if(i == 0){
               userName = values[i];
               session.setAttribute("userName", userName, PortletSession.APPLICATION_SCOPE);
            }
            if(i == 1){
               firstName = values[i];
               session.setAttribute("firstName", firstName, PortletSession.APPLICATION_SCOPE);
            }
            if(i == 2){
               lastName = values[i];
               session.setAttribute("lastName", lastName, PortletSession.APPLICATION_SCOPE);
            }
            if(i == 3){
               emailAdress = values[i];
               session.setAttribute("emailAdress", emailAdress, PortletSession.APPLICATION_SCOPE);
            }
         }
         session.setAttribute("logout", "false", PortletSession.APPLICATION_SCOPE);
         
         lc.logout();
         loginSuccesfull = true;
      }
      catch (Exception e) {
         log.error("Caught Exception: ", e);
         loginSuccesfull = false;
      }

      return loginSuccesfull;
   }


   public boolean logoutUser(ActionRequest request, ActionResponse response) {
      boolean logoutSuccesfull;

      PortletSession session = request.getPortletSession();

      LoginContext lc = (LoginContext)session.getAttribute("LoginContext");
      
      try {
         lc.logout();
         session.removeAttribute("userName", PortletSession.APPLICATION_SCOPE);
         session.removeAttribute("firstName", PortletSession.APPLICATION_SCOPE);
         session.removeAttribute("lastName", PortletSession.APPLICATION_SCOPE);
         session.removeAttribute("emailAdress", PortletSession.APPLICATION_SCOPE);
         session.setAttribute("logout", "true", PortletSession.APPLICATION_SCOPE);
         if (request.getPortletSession().getAttribute("firstName") == null) {
            log.info("logout succesvol");
         }
         logoutSuccesfull = true;
      }
      catch (Exception e) {
         log.error("Caught Exception: ", e);
         logoutSuccesfull = false;
      }
      return logoutSuccesfull;
   }
   
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      hibservice = (HibernateCommunityService)aC.getBean("serviceCommunity");
      return hibservice.getPreferences(module, userId, key, value);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      hibservice = (HibernateCommunityService)aC.getBean("serviceCommunity");
      hibservice.createPreference(module, userId, key, values);
   }
   
   public void removePreferences(String module, String userId, String key){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      hibservice = (HibernateCommunityService)aC.getBean("serviceCommunity");
      hibservice.removePreferences(module, userId, key);
   }
}
