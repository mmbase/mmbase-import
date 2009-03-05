package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
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

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.EmailSender;

public class RegisterPortlet extends AbstractLoginPortlet {

   public static final String ACEGI_SECURITY_FORM_EMAIL_KEY = "email";
   public static final String ACEGI_SECURITY_FORM_FIRSTNAME_KEY = "firstName";
   public static final String ACEGI_SECURITY_FORM_INFIX_KEY = "infix";
   public static final String ACEGI_SECURITY_FORM_LASTNAME_KEY = "lastName";
   public static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "passwordText";
   public static final String ACEGI_SECURITY_FORM_PASSWORDCONF_KEY = "passwordConfirmation";
   public static final String ACEGI_SECURITY_FORM_TERMS = "agreedToTerms";
   public static final String ACEGI_SECURITY_DEFAULT = "defaultmessages";
   public static final String GROUPNAME = "groupName";

   public static final String ALLGROUPNAMES = "allGroupNames";
   public static final String NOGROUP = "nogroup";
   protected static final String ERRORMESSAGES = "errormessages";

   public static final String USE_TERMS = "useterms";
   public static final String TERMS_PAGE = "page";

   private static final Log log = LogFactory.getLog(RegisterPortlet.class);

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, GROUPNAME, preferences.getValue(GROUPNAME, ""));
      setAttribute(req, ALLGROUPNAMES, getAllGroupNames());
      setAttribute(req, USE_TERMS, preferences.getValue(USE_TERMS, ""));

      String pageid = preferences.getValue(TERMS_PAGE, null);
      if (StringUtils.isNotEmpty(pageid)) {
         String pagepath = SiteManagement.getPath(Integer.valueOf(pageid), true);

         if (pagepath != null) {
            setAttribute(req, "pagepath", pagepath);
         }
      }

      super.doEditDefaults(req, res);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      setPortletParameter(portletId, GROUPNAME, request.getParameter(GROUPNAME));
      setPortletParameter(portletId, USE_TERMS, request.getParameter(USE_TERMS));
      setPortletParameter(portletId, TERMS_PAGE, request.getParameter(TERMS_PAGE));

      super.processEditDefaults(request, response);
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {

      Map<String, String> errorMessages = new Hashtable<String, String>();
      PortletPreferences preferences = request.getPreferences();

      String email = request.getParameter(ACEGI_SECURITY_FORM_EMAIL_KEY).trim();
      String firstName = request.getParameter(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
      String infix = request.getParameter(ACEGI_SECURITY_FORM_INFIX_KEY);
      String lastName = request.getParameter(ACEGI_SECURITY_FORM_LASTNAME_KEY);
      String passwordText = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
      String passwordConfirmation = request.getParameter(ACEGI_SECURITY_FORM_PASSWORDCONF_KEY);

      Long authId = null;

      if (StringUtils.isBlank(email)) {
         errorMessages.put(ACEGI_SECURITY_FORM_EMAIL_KEY, "register.email.empty");
      } else if (!isEmailAddress(email)) {
         errorMessages.put(ACEGI_SECURITY_FORM_EMAIL_KEY, "register.email.match");
      }

      validateInputFields(request, errorMessages, preferences, firstName, lastName, passwordText, passwordConfirmation);

      AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory.getBean("authenticationService");
      PersonService personHibernateService = (PersonService) ApplicationContextFactory.getBean("personService");
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(email);

      if (authenticationId == null) {
         Authentication authentication = authenticationService.createAuthentication(email, passwordText);
         if (authentication.getId() != null) {
            authId = authentication.getId();

            //If the names are not needed in the form, they can be emptied and stored.
            if (firstName == null) firstName="";
            if (infix == null) infix="";
            if (lastName == null) lastName = "";
            
            Person person = personHibernateService.createPerson(firstName, infix, lastName, authId, RegisterStatus.UNCONFIRMED.getName(), new Date());
            person.setEmail(email);
            personHibernateService.updatePerson(person);

            String groupName = preferences.getValue(GROUPNAME, null);
            if (null != groupName && !NOGROUP.equals(groupName)) {
               authenticationService.addAuthorityToUserByAuthenticationId(authId.toString(), groupName);
            }

            String emailSubject = preferences.getValue(EMAIL_SUBJECT, "Your account details associated with the given email address.\n");
            String emailText = preferences.getValue(EMAIL_TEXT, null);
            String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
            String nameFrom = preferences.getValue(EMAIL_FROMNAME, null);

            emailText = getEmailBody(emailText, request, authentication, person);
            try {
               if (!isEmailAddress(emailFrom)) {
                  throw new AddressException("Email address '" + emailFrom + "' is not available or working!");
               }
               EmailSender.sendEmail(emailFrom, nameFrom, email, emailSubject, emailText, email, "text/plain;charset=utf-8");
            } catch (AddressException e) {
               log.error("Email address failed", e);
            } catch (MessagingException e) {
               log.error("Email MessagingException failed", e);
            }

            response.setRenderParameter("email", email);
         } else {
            log.error("add authenticationId failed");
         }
      } else {
         errorMessages.put(ACEGI_SECURITY_DEFAULT, "register.user.exists");
      }

      if (errorMessages.size() > 0) {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
      }
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      PortletSession portletSession = request.getPortletSession();

      request.setAttribute("page", screenId);
      String template;
      
      Map<String, String> errorMessages = (Map<String, String>) portletSession.getAttribute(ERRORMESSAGES);
      
      
      String email = request.getParameter("email");
      String active = request.getParameter("active");
      
      if (StringUtils.isNotEmpty(active)) {
         request.setAttribute("active", active);
         template = "login/register_success.jsp";
      } else {
         if (StringUtils.isNotEmpty(email)) {
            template = "login/register_success.jsp";
         } else {
            template = "login/register.jsp";
         }
      }

      if (errorMessages != null && errorMessages.size() > 0) {
         portletSession.removeAttribute(ERRORMESSAGES);
         request.setAttribute(ERRORMESSAGES, errorMessages);
      }

      doInclude("view", template, request, response);
   }

   protected void validateInputFields(ActionRequest request, Map<String, String> errorMessages, PortletPreferences preferences, String firstName,
         String lastName, String passwordText, String passwordConfirmation) {
      if (StringUtils.isBlank(firstName)) {
         errorMessages.put(ACEGI_SECURITY_FORM_FIRSTNAME_KEY, "register.firstName.empty");
      }
      if (StringUtils.isBlank(lastName)) {
         errorMessages.put(ACEGI_SECURITY_FORM_LASTNAME_KEY, "register.lastName.empty");
      }

      if (StringUtils.isEmpty(passwordText)) {
         errorMessages.put(ACEGI_SECURITY_FORM_PASSWORD_KEY, "register.password.empty");
      }
      if (!passwordText.equals(passwordConfirmation)) {
         errorMessages.put(ACEGI_SECURITY_FORM_PASSWORD_KEY, "register.passwords.not_equal");
      }

      String terms = request.getParameter(ACEGI_SECURITY_FORM_TERMS);
      if (preferences.getValue(USE_TERMS, "").equalsIgnoreCase("yes") && StringUtils.isBlank(terms)) {
         errorMessages.put(ACEGI_SECURITY_FORM_TERMS, "register.terms.agree");
      }
   }

   protected Set<String> getAllGroupNames() {
      AuthorityService authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");
      return new TreeSet<String>(authorityService.getAuthorityNames());
   }
}
