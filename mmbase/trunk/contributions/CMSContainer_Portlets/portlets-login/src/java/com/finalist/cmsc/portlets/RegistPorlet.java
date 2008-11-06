package com.finalist.cmsc.portlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.util.Encode;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

public class RegistPorlet extends CmscPortlet{
   protected static final String ACTION_PARAMETER = "action";

   private static final String ACEGI_SECURITY_FORM_EMAIL_KEY = "email";
   private static final String ACEGI_SECURITY_FORM_FIRSTNAME_KEY = "firstName";
   private static final String ACEGI_SECURITY_FORM_INFIX_KEY = "infix";
   private static final String ACEGI_SECURITY_FORM_LASTNAME_KEY = "lastName";
   private static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "passwordText";
   private static final String ACEGI_SECURITY_FORM_PASSWORDCONF_KEY = "passwordConfirmation";
   private static final Log log = LogFactory.getLog(RegistPorlet.class);

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String email = request.getParameter(ACEGI_SECURITY_FORM_EMAIL_KEY);
      String firstName = request.getParameter(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
      String infix = request.getParameter(ACEGI_SECURITY_FORM_INFIX_KEY);
      String lastName = request.getParameter(ACEGI_SECURITY_FORM_LASTNAME_KEY);
      String passwordText = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
      String passwordConfirmation = request.getParameter(ACEGI_SECURITY_FORM_PASSWORDCONF_KEY);
      String errorMessages = "";
      Long authId = null;
      if(StringUtils.isEmpty(passwordText)) { 
         errorMessages = "register.password.empty";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      if(!passwordText.equals(passwordConfirmation)) {
         errorMessages = "register.passwords.not_equal";
         response.setRenderParameter("errorMessages", errorMessages);
         return; 
      }
      AuthenticationService authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
      PersonService personHibernateService = (PersonService)ApplicationContextFactory.getBean("personService");
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(email);
      if (authenticationId == null) {
         Authentication authentication = authenticationService.createAuthentication(email, passwordText);
         if (authentication.getId() != null) {
            authId = authentication.getId();
            Person person = personHibernateService.createPerson(firstName, infix, lastName,authId,RegisterStatus.UNCONFIRMED.getName(),new Date());
            person.setEmail(email);
            personHibernateService.updatePerson(person);
            EmailUtil.send(null, email, "confirmation", getEmailBody(request,email));
            response.setRenderParameter("email", email);
         } else {
            log.info("add authenticationId failed");
         }
      } else {
         errorMessages = "register.user.exsit";
         response.setRenderParameter("errorMessages", errorMessages);
         log.info("add check1 failed for: " + email);
      }
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      
      String template;
      String error = request.getParameter("errorMessages");
      String email = request.getParameter("email");
      if(StringUtils.isNotEmpty(email)) {
         template = "login/register_success.jsp";
      }
      else {
         if (StringUtils.isNotBlank(error)) {
            request.setAttribute("errormessages", error);
         }
         template = "login/regist.jsp";
      }
      doInclude("view", template, request, response);
   }
   protected String getEmailBody(ActionRequest request,String email) {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("../templates/view/login/confirmation.txt");
      if(is == null) {
         throw new NullPointerException("The confirmation template file in directory 'templates/view/login' does't exist.");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String strLine ;
      try {
         while( (strLine = reader.readLine()) != null) {
            sb.append(strLine);
         }
      } 
      catch (IOException e) {
         log.error("error happen when reading email template",e);
      }
      Encode encoder = new org.mmbase.util.Encode("BASE64");
      String confirmUrl = request.getContextPath()+"/login/confirm.do?s="+encoder.encode(email);
      return String.format(sb.toString(), email,confirmUrl);
   }
}
