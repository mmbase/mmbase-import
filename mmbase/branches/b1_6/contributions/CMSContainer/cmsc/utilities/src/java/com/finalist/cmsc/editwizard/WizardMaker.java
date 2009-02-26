package com.finalist.cmsc.editwizard;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Create a link to a Editwizard
 *
 * @author Nico Klasens
 * @author Wouter Heijke
 * @version $Revision: 1.5 $
 */
public final class WizardMaker {
   private static final Logger log = Logging.getLoggerInstance(WizardMaker.class.getName());

   private static final String DEFAULT_SESSION_KEY = "editwizard";

   private Cloud cloud;

   private HttpServletRequest request;

   private String templates = "/editors/editwizards/";

   private String sessionKey = DEFAULT_SESSION_KEY;

   private String wizardJsp = "/mmbase/edit/wizard/jsp/wizard.jsp";

   private String referrer = "/editors/WizardCloseAction.do";

   private String wizardConfigName;

   private String objectNumber;

   private String language;

   private String returnUrl;

   private String popup;

   private String creation;

   private String[] contentTypes;

   private String action;

   private String contentType;

   private String newTypesUrl;


   public WizardMaker(HttpServletRequest request, Cloud cloud) {
      this.cloud = cloud;
      this.request = request;
      setSessionKey(request.getParameter("sessionkey"));
      setObjectNumber(request.getParameter("objectnumber"));
      setReturnUrl(request.getParameter("returnurl"));
      setPopup(request.getParameter("popup"));
      setCreation(request.getParameter("creation"));
      setWizardConfigName(request.getParameter("wizardConfigName"));
      setContentTypes(request.getParameterValues("contenttype"));
      setAction(request.getParameter("action"));
   }


   public String makeWizard() {

      if ("create".equals(action)) {
         objectNumber = "new";
      }

      HttpSession session = request.getSession();

      if (StringUtils.isNotEmpty(this.returnUrl)) {
         session.setAttribute("returnurl", this.returnUrl);
      }

      if (StringUtils.isNotEmpty(this.popup)) {
         session.setAttribute("popup", this.popup);
      }

      if (StringUtils.isNotEmpty(this.creation)) {
         session.setAttribute("creation", this.creation);
      }

      if (contentType != null && contentTypes == null) {
         contentTypes = new String[] { contentType };
      }

      if (contentTypes == null || contentTypes.length == 0) {
         if (objectNumber != null && !"new".equals(objectNumber)) {
            Node node = cloud.getNode(objectNumber);
            contentType = node.getNodeManager().getName();
         }
         else {
            throw new IllegalStateException("No criteria available to find a wizard."
                  + " Provide a contenttype or objectnumber");
         }
      }
      else {
         if (contentTypes.length == 1) {
            contentType = contentTypes[0];
         }
         else {
            List<String> list = Arrays.asList(contentTypes);
            request.setAttribute("contenttypes", list);
            return newTypesUrl;
         }
      }
      log.debug("contenttype='" + contentType + "'");

      session.setAttribute("contenttype", contentType);

      if (StringUtils.isEmpty(wizardConfigName)) {
         NodeList list = null;
         NodeManager manager = cloud.getNodeManager("editwizards");
         list = manager.getList("nodepath = '" + contentType + "'", null, null);
         if (list.isEmpty()) {
            throw new IllegalStateException("Unable to find a wizard for contenttype " + contentType + " or objectnumber "
                  + objectNumber);
         }

         Node wizard = list.getNode(0);
         wizardConfigName = wizard.getStringValue("wizard");
      }

      String contextpath = request.getContextPath();
      if (templates.startsWith(contextpath)) {
         templates = templates.substring(contextpath.length());
      }

      String link = wizardJsp + "?wizard=" + wizardConfigName + "&objectnumber=" + objectNumber + "&templates="
            + templates + "&referrer=" + referrer + "&sessionkey=" + sessionKey + "&language=" + language;

      log.debug("wizard link='" + link + "'");

      return link;

   }


   public void setAction(String parameter) {
      this.action = parameter;
   }


   public void setContentTypes(String[] types) {
      this.contentTypes = types;
   }


   public void setCreation(String parameter) {
      this.creation = parameter;
   }


   public void setPopup(String parameter) {
      this.popup = parameter;
   }


   public void setReturnUrl(String parameter) {
      this.returnUrl = parameter;
   }


   public void setWizardJsp(String parameter) {
      this.wizardJsp = parameter;
   }


   public void setTemplates(String parameter) {
      this.templates = parameter;
   }


   public void setReferrer(String parameter) {
      this.referrer = parameter;
   }


   public void setWizardConfigName(String parameter) {
      this.wizardConfigName = parameter;
   }


   public void setObjectNumber(String parameter) {
      this.objectNumber = parameter;
   }


   public void setContentType(String parameter) {
      this.contentType = parameter;
   }


   public void setSessionKey(String parameter) {
      if (parameter == null || parameter.length() == 0) {
         this.sessionKey = DEFAULT_SESSION_KEY;
      }
      else {
         this.sessionKey = parameter;
      }
   }


   public void setLanguage(String parameter) {
      this.language = parameter;
   }


   public String getSessionKey() {
      return this.sessionKey;
   }


   public void setNewTypesUrl(String parameter) {
      this.newTypesUrl = parameter;
   }

}
