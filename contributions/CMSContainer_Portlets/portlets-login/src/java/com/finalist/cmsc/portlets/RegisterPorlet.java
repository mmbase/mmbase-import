package com.finalist.cmsc.portlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.Encode;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;

public class RegisterPorlet extends CmscPortlet{

   private static final String ACEGI_SECURITY_FORM_EMAIL_KEY = "email";
   private static final String ACEGI_SECURITY_FORM_FIRSTNAME_KEY = "firstName";
   private static final String ACEGI_SECURITY_FORM_INFIX_KEY = "infix";
   private static final String ACEGI_SECURITY_FORM_LASTNAME_KEY = "lastName";
   private static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "passwordText";
   private static final String ACEGI_SECURITY_FORM_PASSWORDCONF_KEY = "passwordConfirmation";
   private static final String DEFAULT_EMAILREGEX = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$";

   private static final String EMAIL_SUBJECT = "emailSubject";
   private static final String EMAIL_TEXT = "emailText";
   private static final String EMAIL_FROMEMAIL = "emailFromEmail";
   private static final String EMAIL_FROMNAME = "emailFromName";
   private static final Log log = LogFactory.getLog(RegisterPorlet.class);

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
      if(StringUtils.isBlank(email)) { 
         errorMessages = "register.email.empty";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
      if(!isEmailAddress(email)) { 
         errorMessages = "register.email.match";
         response.setRenderParameter("errorMessages", errorMessages);
         return;
      }
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
            PortletPreferences preferences = request.getPreferences();
            String emailSubject = preferences.getValue(EMAIL_SUBJECT, "Your account details associated with the given email address.\n");
            String emailText = preferences.getValue(EMAIL_TEXT, null);
            String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
            String nameFrom = preferences.getValue(EMAIL_FROMNAME, null);

            if (StringUtils.isBlank(emailText)) {
               emailText = getEmailBody(request,authentication,person);
            }
            else {
               emailText = emailText+"\n\n"+getEmailBody(request,authentication,person);
            }
            //EmailUtil.send(null, email, subject, getEmailBody(request,authentication,person));
            EmailUtil.send(null, null, email, nameFrom, emailFrom, emailSubject, emailText);
            response.setRenderParameter("email", email);
         } else {
            log.info("add authenticationId failed");
         }
      } else {
         errorMessages = "register.user.exists";
         response.setRenderParameter("errorMessages", errorMessages);
         log.info("add check1 failed for: " + email);
      }
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      
      String template;
      String error = request.getParameter("errorMessages");
      String email = request.getParameter("email");
      String active = request.getParameter("active");
      if(StringUtils.isNotEmpty(active)) {
         request.setAttribute("active", active);
         template = "login/register_success.jsp";
      }
      else {
         if(StringUtils.isNotEmpty(email)) {
            template = "login/register_success.jsp";
         }
         else {
            if (StringUtils.isNotBlank(error)) {
               request.setAttribute("errormessages", error);
            }
            template = "login/register.jsp";
         }
      }
      doInclude("view", template, request, response);
   }
   
   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);

      String emailSubject = request.getParameter(EMAIL_SUBJECT);
      String emailText = request.getParameter(EMAIL_TEXT);
      String emailFromEmail = request.getParameter(EMAIL_FROMEMAIL);
      String emailFromName = request.getParameter(EMAIL_FROMNAME);
      
      setPortletParameter(portletId, EMAIL_SUBJECT, emailSubject);
      setPortletParameter(portletId, EMAIL_TEXT, emailText);
      setPortletParameter(portletId, EMAIL_FROMEMAIL, emailFromEmail);
      setPortletParameter(portletId, EMAIL_FROMNAME, emailFromName);

      super.processEditDefaults(request, response);
   }
   
   protected String getEmailBody(ActionRequest request,Authentication authentication,Person person) {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("../templates/view/login/confirmation.txt");
      if(is == null) {
         throw new NullPointerException("The confirmation template file confirmation.txt in directory 'templates/view/login' does't exist.");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String strLine ;
      try {
         while( (strLine = reader.readLine()) != null) {
            sb.append(strLine+"\n");
         }
      } 
      catch (IOException e) {
         log.error("error happen when reading email template",e);
      }   
      Cloud cloud = getCloudForAnonymousUpdate(false);
      String url = getConfirmationLink(cloud);
      //String confirmUrl = HttpUtil.getWebappUri((HttpServletRequest) request)+"login/confirm.do?s="+encoder.encode(person.getEmail())+"&url="+encoder.encode(url);
      String confirmUrl = HttpUtil.getWebappUri((HttpServletRequest) request)+"login/confirm.do?s="+authentication.getId()+url;
      return String.format(sb.toString(), authentication.getUserId(),authentication.getPassword(),person.getFirstName(),person.getInfix(),person.getLastName(),confirmUrl);

   }
   
   public Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }
   private String getConfirmationLink(Cloud cloud) {
      String link = null;
      NodeList portletDefinations = SearchUtil.findNodeList(cloud, "portletdefinition", "definition", this.getPortletName());
      Node regiesterPortletDefination = portletDefinations.getNode(0);
      if (portletDefinations.size() > 1) {
         log.error("found " + portletDefinations.size() + " regiesterPortlet nodes; first one will be used");
      }
      NodeList portlets = regiesterPortletDefination.getRelatedNodes("portlet", "definitionrel", SearchUtil.SOURCE);
      Node  portlet = portlets.getNode(0);
      NodeList pages = portlet.getRelatedNodes("page");
      if (pages != null && pages.size() >= 1) {
         Node page = pages.getNode(pages.size() - 1);
//         link = SiteManagement.getPath(page.getNumber(), true);
         link = "&pn="+page.getNumber();
        // link = "content/" + page.getNumber();
         RelationList relations = portlet.getRelations("portletrel", page.getNodeManager());
         String name = relations.getRelation(0).getStringValue("name");
//         link += "/_rp_".concat(name).concat("_").concat("active").concat("/1_");
         link += "&nm="+name;
      
      }
      return link;
   }
   private  boolean isEmailAddress(String emailAddress) {
      if (emailAddress == null) {
         return false;
      }
      if (StringUtils.isBlank(emailAddress)) {
         return false;
      }

      String emailRegex = getEmailRegex();
      return emailAddress.trim().matches(emailRegex);
   }
   protected String getEmailRegex() {
      String emailRegex = PropertiesUtil.getProperty("email.regex");
      if (StringUtils.isNotBlank(emailRegex)) {
         return emailRegex;
      }
      return DEFAULT_EMAILREGEX;
   }
}
