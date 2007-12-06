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
   
   public List<String> getAllUsers(){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      HibernateService hibservice = (HibernateService)aC.getBean("service");
      List<String> users = hibservice.getAllUsers();
      if (users != null && users.size() > 0){
         return users;
      }
      return (null);
   }
   
   public void setUser(String userName, String password, String firstName, String lastName, String emailadres){
      
         aC = new ClassPathXmlApplicationContext("applicationContext.xml");
         HibernateService hibservice = (HibernateService)aC.getBean("service");
         try{
            User user = hibservice.createUser(userName, password, firstName, lastName, emailadres);
         }
         catch (Exception e){
            //werkt niet
         }
      }
   }
