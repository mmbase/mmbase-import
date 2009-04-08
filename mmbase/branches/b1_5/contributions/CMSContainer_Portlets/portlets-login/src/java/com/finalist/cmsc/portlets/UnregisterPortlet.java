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

import com.finalist.cmsc.community.CommunityManager;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
//import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class UnregisterPortlet extends AbstractLoginPortlet {
   private static final String CONFIRMATION_TEXT = "confirmationText";
   private static final String USER_ACCOUNT_NOTEXIST = "view.account.notexist";
   private static final String REGISTER_EMAIL_MATCH = "register.email.match";
   private static final String REGISTER_EMAIL_EMPTY = "register.email.empty";
   private static final String REMOVE_SUCCESS = "removeSuccess";
   protected static final String EMAIL_REGISTER = "registerEmail";
   protected static final String ERRORMESSAGES = "errormessages";
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
   PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, CONFIRMATION_TEXT, preferences.getValue(CONFIRMATION_TEXT,""));
      super.doEditDefaults(req, res);
   }
   @Override
   public void processEditDefaults(ActionRequest request,
         ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         setPortletParameter(portletId, CONFIRMATION_TEXT, request.getParameter(CONFIRMATION_TEXT));
      }
      super.processEditDefaults(request, response);
      }
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
      if (authenticationService.authenticationExists(register_email)) {
        Long authId = authenticationService.getAuthenticationIdForUserId(register_email);
        personHibernateService.deletePersonByAuthenticationId(authId);
        authenticationService.deleteAuthentication(authId);
        CommunityManager.notify(authId);
      }
      else {
         errorMessages.put(EMAIL_REGISTER, USER_ACCOUNT_NOTEXIST);
      }
      if (errorMessages.size() > 0) {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
         request.getPortletSession().setAttribute(EMAIL_REGISTER, register_email);
      }
      request.getPortletSession().setAttribute(REMOVE_SUCCESS, "unregister.success");
      request.getPortletSession().setAttribute(CONFIRMATION_TEXT, preferences.getValue(CONFIRMATION_TEXT, null));
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
      if(portletSession.getAttribute(CONFIRMATION_TEXT) != null) {
         String confirmation_text = (String) portletSession.getAttribute(CONFIRMATION_TEXT);
         portletSession.removeAttribute(CONFIRMATION_TEXT);
         request.setAttribute("confirmText", confirmation_text);
      }
      doInclude("view", template, request, response);
   }
}
