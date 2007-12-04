package com.finalist.cmsc.services.community;

import javax.security.auth.login.LoginContext;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletSession;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.finalist.cmsc.services.community.data.User;
import com.finalist.cmsc.services.community.data.NewsPref;
import com.finalist.cmsc.services.community.HibernateService;

public class CommunityServiceMysqlImpl extends CommunityService {

   private static Log log = LogFactory.getLog(CommunityServiceMysqlImpl.class);

   private PortletSession session;
   
   private ApplicationContext aC;
   
   // @Override
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
         while (it.hasNext()) {
            String tempPrin = it.next().toString();
            String values[] = tempPrin.split(" ");
            for (int i = 0; i < values.length; i++) {
               if (i == 0) {
                  String def = "Username: ";
                  userName = values[i];
                  System.out.println("USERNAME USERNAME USERNAME: " + userName);
                  session.setAttribute("userName", userName, PortletSession.APPLICATION_SCOPE);
               }
               if (i == 1) {
                  String def = "First name: ";
                  firstName = values[i];
                  session.setAttribute("firstName", firstName, PortletSession.APPLICATION_SCOPE);
               }
               if (i == 2) {
                  String def = "Last name: ";
                  lastName = values[i];
                  session.setAttribute("lastName", lastName, PortletSession.APPLICATION_SCOPE);
               }
               if (i == 3) {
                  String def = "E-mail adres: ";
                  emailAdress = values[i];
                  session.setAttribute("emailAdress", emailAdress, PortletSession.APPLICATION_SCOPE);
               }
            }
         }
         session.setAttribute("logout", "false", PortletSession.APPLICATION_SCOPE);

         //it = lc.getSubject().getPublicCredentials(Properties.class).iterator();

         //while (it.hasNext())
            //log.info(it.next().toString());

         lc.logout();
         loginSuccesfull = true;
      }
      catch (Exception e) {
         log.error("Caught Exception: ", e);
         loginSuccesfull = false;
      }

      return loginSuccesfull;
   }


   public boolean logoutUser(/** HttpServletRequest HttpRequest, * */
   ActionRequest request, ActionResponse response) {
      boolean logoutSuccesfull;

      PortletSession session = request.getPortletSession();

      try {
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
   
   public List<String> getUsersWithPreferences(String key, String value){

      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateService hibservice = (HibernateService)aC.getBean("service");;

      List<String> resultList = hibservice.getUsersWithPreferences(key, value);
      
      return resultList;
   }
   
   public String getUserPreference(String userName, String key) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateService hibservice = (HibernateService)aC.getBean("service");;
      
      String preference = hibservice.getUserPreference(userName, key);
      
      return preference;
   }
   
   public List<String> getUserPreferences(String userName, String key) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateService hibservice = (HibernateService)aC.getBean("service");;
      
      List<String> preferenceList = hibservice.getUserPreferences(userName, key);
      
      return preferenceList;
   }
   
   public boolean setUserPreference(String userName, String key, String value){
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      HibernateService hibservice = (HibernateService)aC.getBean("service");
      try{
         NewsPref newsPref = hibservice.createUserPreference(userName, key, value);
         if(newsPref != null){
            succes = true;
         }
         else{
            succes = false;
         }
      }
      catch (Exception e){
         succes = false;
      }
      
      return succes;
   }
   
   public boolean setUserPreferenceValues(String userName, Map<String, String> preferences) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      NewsPref newsPref = new NewsPref();
      HibernateService hibservice = (HibernateService)aC.getBean("service");
      try{
         
         Iterator keys = preferences.keySet().iterator();
         Iterator values = preferences.values().iterator();
         
         while(keys.hasNext() && values.hasNext()){
            newsPref = hibservice.createUserPreference(userName, keys.next().toString(), values.next().toString());
         }
         if (newsPref != null){
            succes = true;
         }
         else{
            succes = false;
         }
      }
      catch (Exception e){
         succes = false;
      }
      return succes;
   }
   
   public boolean removeUserPreference(String userName, String key){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      HibernateService hibservice = (HibernateService)aC.getBean("service");
      succes = hibservice.removeUserPreference(userName, key);
      return succes;
   }
   
   public void removeUserPreference(String userName, String key, String value){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      HibernateService hibservice = (HibernateService)aC.getBean("service");
      hibservice.removeUserPreference(userName, key, value);
   }
   
   public boolean hasPermission(String userName, String permission){
      
      boolean permissionB;
      
      if(userName == "jaspers" || userName == "admin"){
         permissionB = true;
      }
      else{
         permissionB = false;
      }
      return permissionB;
   }
}
