package com.finalist.portlets.newsletter;


import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;

/**
 * This portlet is a basic example of the usage of <code>NewsletterRegistrationPortlet</code>. 
 * It can be used to subscriber to a newsletter with only an e-mail address supplied.
 *
 * 
 * @author rob
 *
 */
public class BasicNewsletterRegistrationPortlet extends NewsletterRegistrationPortlet {
   
   private static Logger log = Logging.getLoggerInstance(BasicNewsletterRegistrationPortlet.class.getName());

   @Override
   protected void validateInputFields(ActionRequest request, Map<String, String> errorMessages, PortletPreferences preferences) {
       log.debug("No validation needed, only email is used");
       // empty
       // no validation needed, only email address is used, which will be verified by the RegistrationPortlet
   }

   @Override
   protected String getTemplate(String key) {
       if ("register".equals(key)) {
           // provide basic register page with only email address required
           return "newsletter/register_basic.jsp";
       }
       return super.getTemplate(key);
   }

   @Override
   protected String formatConfirmationText(String emailText, Authentication authentication, Person person, String confirmUrl) {
      return String.format(emailText == null ? getConfirmationTemplate() : emailText, authentication
            .getUserId(), confirmUrl);
   }

   @Override
   protected String getEmailConfirmTemplate() {
      // overriding DEFAULT_EMAIL_CONFIRM_TEMPLATE does not work
      return "../templates/view/newsletter/confirmation.txt";
   }
   

}
