package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;
import com.finalist.cmsc.util.EmailSender;

public class RegisterPorlet extends AbstractLoginPortlet {

   private static final String ACEGI_SECURITY_FORM_EMAIL_KEY = "email";
   private static final String ACEGI_SECURITY_FORM_FIRSTNAME_KEY = "firstName";
   private static final String ACEGI_SECURITY_FORM_INFIX_KEY = "infix";
   private static final String ACEGI_SECURITY_FORM_LASTNAME_KEY = "lastName";
   private static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "passwordText";
   private static final String ACEGI_SECURITY_FORM_PASSWORDCONF_KEY = "passwordConfirmation";
   private static final String GROUPNAME = "groupName";
   private static final String ALLGROUPNAMES = "allGroupNames";
   private static final String NoGROUP = "nogroup";

   private static final Log log = LogFactory.getLog(RegisterPorlet.class);
   
   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
   PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, GROUPNAME, preferences.getValue(GROUPNAME,""));
      setAttribute(req, ALLGROUPNAMES, getAllGroupNames());
      
      super.doEditDefaults(req, res);
   }
   
   @Override
   public void processEditDefaults(ActionRequest request,
         ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      setPortletParameter(portletId, GROUPNAME, request.getParameter(GROUPNAME));

      super.processEditDefaults(request, response);
   }
   
   @Override
   public void processView(ActionRequest request, ActionResponse response)
         throws PortletException, IOException {
      
      PortletPreferences preferences = request.getPreferences();
      
      String email = request.getParameter(ACEGI_SECURITY_FORM_EMAIL_KEY).trim();
      String firstName = request.getParameter(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
      String infix = request.getParameter(ACEGI_SECURITY_FORM_INFIX_KEY);
      String lastName = request.getParameter(ACEGI_SECURITY_FORM_LASTNAME_KEY);
      String passwordText = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
      String passwordConfirmation = request.getParameter(ACEGI_SECURITY_FORM_PASSWORDCONF_KEY);
      String errorMessages = "";
      Long authId = null;
      if (StringUtils.isBlank(email)) {
         errorMessages = "register.email.empty";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      if (!isEmailAddress(email)) {
         errorMessages = "register.email.match";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      if (StringUtils.isEmpty(passwordText)) {
         errorMessages = "register.password.empty";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      if (!passwordText.equals(passwordConfirmation)) {
         errorMessages = "register.passwords.not_equal";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory
            .getBean("authenticationService");
      PersonService personHibernateService = (PersonService) ApplicationContextFactory
            .getBean("personService");
      Long authenticationId = authenticationService
            .getAuthenticationIdForUserId(email);
      if (authenticationId == null) {
         Authentication authentication = authenticationService
               .createAuthentication(email, passwordText);
         if (authentication.getId() != null) {
            authId = authentication.getId();
            Person person = personHibernateService.createPerson(firstName,
                  infix, lastName, authId,
                  RegisterStatus.UNCONFIRMED.getName(), new Date());
            person.setEmail(email);
            personHibernateService.updatePerson(person);
            
            String groupName = preferences.getValue(GROUPNAME, null);
            if(null != groupName && !NoGROUP.equals(groupName)){
               authenticationService.addAuthorityToUserByAuthenticationId(authId.toString(), groupName);
            }
            
             String emailSubject = preferences
                  .getValue(EMAIL_SUBJECT,
                        "Your account details associated with the given email address.\n");
            String emailText = preferences.getValue(EMAIL_TEXT, null);
            String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
            String nameFrom = preferences.getValue(EMAIL_FROMNAME, null);

            emailText = getEmailBody(emailText,request, authentication, person);
            try {
               if (StringUtils.isNotBlank(emailFrom) && !isEmailAddress(emailFrom)) {
                  throw new AddressException("Email address '" + emailFrom + "' is not available or working!");
               }
                  EmailSender.sendEmail(emailFrom, nameFrom, email, emailSubject, emailText,
                        email, "text/plain;charset=utf-8");
            } 
            catch (AddressException e) {
                  log.error("Email address failed",e);
            } 
            catch (MessagingException e) {
                  log.error("Email MessagingException failed",e);
            }

            response.setRenderParameter("email", email);
         } else {
            log.error("add authenticationId failed");
         }
      } else {
         errorMessages = "register.user.exists";
         response.setRenderParameter("errorMessages", errorMessages);
         log.error("add check1 failed for: " + email);
      }
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response)
         throws PortletException, IOException {
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      request.setAttribute("page", screenId);
      String template;
      String error = request.getParameter("errorMessages");
      String email = request.getParameter("email");
      String active = request.getParameter("active");
      if (StringUtils.isNotEmpty(active)) {
         request.setAttribute("active", active);
         template = "login/register_success.jsp";
      } else {
         if (StringUtils.isNotEmpty(email)) {
            template = "login/register_success.jsp";
         } else {
            if (StringUtils.isNotBlank(error)) {
               request.setAttribute("errormessages", error);
            }
            template = "login/register.jsp";
         }
      }
      doInclude("view", template, request, response);
   }

  private Set<String> getAllGroupNames() {
     AuthorityService authorityService = (AuthorityService) ApplicationContextFactory
     .getBean("authorityService");
     return new TreeSet<String>(authorityService.getAuthorityNames());
}
}
