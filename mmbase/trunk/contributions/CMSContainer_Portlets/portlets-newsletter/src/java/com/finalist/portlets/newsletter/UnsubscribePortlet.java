package com.finalist.portlets.newsletter;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.portlets.AbstractLoginPortlet;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.util.EmailSender;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class UnsubscribePortlet extends AbstractLoginPortlet {
   private static final Log log = LogFactory.getLog(UnsubscribePortlet.class);
   
   private static final String USER_ACCOUNT_NOTEXIST = "view.account.notexist";
   private static final String REGISTER_EMAIL_MATCH = "unsubscribe.email.match";
   private static final String REGISTER_EMAIL_EMPTY = "unsubscribe.email.empty";
   private static final String REMOVE_SUCCESS = "removeSuccess";
   private static final String ACEGI_SECURITY_DEFAULT = "defaultmessages";
   protected static final String EMAIL_UNSUBSCRIBE = "email";
   protected static final String ERRORMESSAGES = "errormessages";

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String email = request.getParameter(EMAIL_UNSUBSCRIBE);
      Map<String, String> errorMessages = new HashMap<String, String>();
      if (StringUtils.isBlank(email)) {
         errorMessages.put(EMAIL_UNSUBSCRIBE, REGISTER_EMAIL_EMPTY);
      } else if (!isEmailAddress(email)) {
         errorMessages.put(EMAIL_UNSUBSCRIBE, REGISTER_EMAIL_MATCH);
      } else {
         NewsletterSubscriptionServices newsletterSubscriptionServices = (NewsletterSubscriptionServices) ApplicationContextFactory
               .getBean("subscriptionServices");
         AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory
               .getBean("authenticationService");
         if (authenticationService.authenticationExists(email)) {
            Authentication authentication = authenticationService.findAuthentication(email);
            Long authId = authentication.getId();
            newsletterSubscriptionServices.deleteSubscriptionsByAuthId(authId);
            String emailSubject = preferences.getValue(EMAIL_SUBJECT,
                  "Your account details associated with the given email address.\n");
            String template = preferences.getValue(EMAIL_TEXT, null);
            String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
            String nameFrom = preferences.getValue(EMAIL_FROMNAME, "Senders Name");
            if (StringUtils.isBlank(template)) {
               template = getConfirmationTemplate();
            }
            String emailText = formatEmailText(template, email);
            if (!isEmailAddress(emailFrom)) {
               errorMessages.put(ACEGI_SECURITY_DEFAULT, "Email address '" + emailFrom
                     + "' set in the edit_defaults properties is not available or working!");
            } else {
               try {
                  if(ServerUtil.isProduction()){
                     EmailSender.sendEmail(emailFrom, nameFrom, email, emailSubject, emailText, email, "text/plain;charset=utf-8");
                  }
               } catch (Exception ex) {
                  log.error("Subscriptions are successfully cancelled but email could not be sent.", ex);
               }
            }
         } else {
            errorMessages.put(EMAIL_UNSUBSCRIBE, USER_ACCOUNT_NOTEXIST);
         }
      }
      if (errorMessages.size() > 0) {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
         request.getPortletSession().setAttribute(EMAIL_UNSUBSCRIBE, email);
      } else {
         request.getPortletSession().setAttribute(REMOVE_SUCCESS, "unsubscribe.success");
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String template = "newsletter/unsubscribe_basic.jsp";
      PortletSession portletSession = request.getPortletSession();
      Map<String, String> errorMessages = (HashMap<String, String>) portletSession.getAttribute(ERRORMESSAGES);
      if (errorMessages != null && errorMessages.size() > 0) {
         String registerEmail = (String) portletSession.getAttribute(EMAIL_UNSUBSCRIBE);
         portletSession.removeAttribute(ERRORMESSAGES);
         portletSession.removeAttribute(EMAIL_UNSUBSCRIBE);
         request.setAttribute(ERRORMESSAGES, errorMessages);
         request.setAttribute(EMAIL_UNSUBSCRIBE, registerEmail);
      }
      if (portletSession.getAttribute(REMOVE_SUCCESS) != null) {
         portletSession.removeAttribute(REMOVE_SUCCESS);
         template = "newsletter/unsubscribe_success.jsp";
      }
      doInclude("view", template, request, response);
   }

   protected String formatEmailText(String template, String email) {
      return String.format(template, email);
   }
   
   protected String getEmailConfirmTemplate() {
      return "../templates/view/newsletter/unsubscribe_confirmation.txt";
   }

}
