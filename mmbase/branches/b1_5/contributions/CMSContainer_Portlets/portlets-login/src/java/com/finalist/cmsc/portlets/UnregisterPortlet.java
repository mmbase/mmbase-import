package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class UnregisterPortlet extends AbstractLoginPortlet {
   private static final String USER_ACCOUNT_NOTEXIST = "view.account.notexist";
   private static final String REGISTER_EMAIL_MATCH = "register.email.match";
   private static final String REGISTER_EMAIL_EMPTY = "register.email.empty";
   private static final String REMOVE_SUCCESS = "removeSuccess";
   protected static final String EMAIL_REGISTER = "registerEmail";
   protected static final String ERRORMESSAGES = "errormessages";
   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String register_email = request.getParameter(EMAIL_REGISTER);
      Map<String, String> errorMessages = new HashMap<String, String>();
      if (StringUtils.isBlank(register_email)) {
         errorMessages.put(EMAIL_REGISTER, REGISTER_EMAIL_EMPTY);
      } else if (!isEmailAddress(register_email)) {
         errorMessages.put(EMAIL_REGISTER, REGISTER_EMAIL_MATCH);
      }
      AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory
      .getBean("authenticationService");
      PersonService personHibernateService = (PersonService) ApplicationContextFactory.getBean("personService");
      NewsletterSubscriptionServices subscriptionServices = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      if (authenticationService.authenticationExists(register_email)) {
        Long authId = authenticationService.getAuthenticationIdForUserId(register_email);
        // personHibernateService.
        subscriptionServices.deleteSubscriptionsByAuthId(authId);
        personHibernateService.deletePersonByAuthenticationId(authId);
        authenticationService.deleteAuthentication(authId);
      }
      else {
         errorMessages.put(EMAIL_REGISTER, USER_ACCOUNT_NOTEXIST);
      }
      if (errorMessages.size() > 0) {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
         request.getPortletSession().setAttribute(EMAIL_REGISTER, register_email);
      }
      request.getPortletSession().setAttribute(REMOVE_SUCCESS, "unregister.success");
   }
   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String template = "login/unregister.jsp";
      PortletSession portletSession = request.getPortletSession();
      Map<String, String> errorMessages = (HashMap<String, String>) portletSession.getAttribute(ERRORMESSAGES);
     
      if (errorMessages != null && errorMessages.size() > 0) {
         String registerEmail = (String) portletSession.getAttribute(EMAIL_REGISTER);
         portletSession.removeAttribute(ERRORMESSAGES);
         portletSession.removeAttribute(EMAIL_REGISTER);
         request.setAttribute(ERRORMESSAGES, errorMessages);
         request.setAttribute(EMAIL_REGISTER, registerEmail);
      }
      if(portletSession.getAttribute(REMOVE_SUCCESS) != null) {
         String remove_success = (String) portletSession.getAttribute(REMOVE_SUCCESS);
         portletSession.removeAttribute(REMOVE_SUCCESS);
         request.setAttribute(REMOVE_SUCCESS, remove_success);
      }
      doInclude("view", template, request, response);
   }
}
