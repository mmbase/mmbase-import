/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.reactform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.portlet.*;
import org.apache.commons.lang.StringUtils;

import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class ReactFormPortlet extends ContentPortlet {

   public void processView(ActionRequest request, ActionResponse response) throws PortletException {
      String action = request.getParameter(ACTION_PARAM);
      Map<String, String> errorMessages = new Hashtable<String, String>();

      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String contentelement = preferences.getValue(CONTENTELEMENT, null);

         if (contentelement != null) {
            String name = request.getParameter("name");
            String useremail = request.getParameter("useremail");
            String message = request.getParameter("message");
            String articleNumber = request.getParameter("articleNumber");
            if (StringUtils.isBlank(articleNumber)) {
               errorMessages.put("subject", "view.error.nosubject");
            }
            String emailRegex = PropertiesUtil.getProperty("email.regex");
            if (StringUtils.isBlank(name)) {
               errorMessages.put("name", "view.name.empty");
            }
            if (StringUtils.isBlank(useremail)) {
               errorMessages.put("useremail", "view.useremail.empty");
            }
            else if (!useremail.matches(emailRegex)) {
               errorMessages.put("useremail", "view.useremail.invalid");
            }
            if (StringUtils.isBlank(message)) {
               errorMessages.put("message", "view.message.empty");
            }

            if (errorMessages.size() == 0) {
               Cloud cloud = getCloudForAnonymousUpdate();
               Node reactform = cloud.getNode(contentelement);
               Node articleNode = cloud.getNode(Integer.parseInt(articleNumber));
               String subject = articleNode.getStringValue("title");
               String emailto = reactform.getStringValue("emailto");
               boolean sent = sendEmail(cloud, emailto, name, useremail, subject, message);
               if (!sent) {
                  errorMessages.put("sendemail", "view.error.sendemail");
               }
            }
            if (errorMessages.size() > 0) {
               Map<String, String> parameterMap = new HashMap<String, String>();
               parameterMap.put("name", name);
               parameterMap.put("useremail", useremail);
               parameterMap.put("message", message);
               parameterMap.put("articleNumber", articleNumber);
               request.getPortletSession().setAttribute("errormessages", errorMessages);
               request.getPortletSession().setAttribute("parameterMap", parameterMap);
            }
            else {
               request.getPortletSession().setAttribute("confirm", "confirm");
            }
         }
         else {
            getLogger().error("No contentelement");
         }
         // switch to View mode
         response.setPortletMode(PortletMode.VIEW);
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }


   private boolean sendEmail(Cloud cloud, String emailto, String name, String useremail, String subject, String message) {
      boolean sent = false;
      try {
         EmailUtil.send(cloud, null, emailto, name, useremail, subject, message);
         sent = true;
      }
      catch (Exception e) {
         getLogger().error("error sending email", e);
      }
      return sent;
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String contentelement = preferences.getValue(CONTENTELEMENT, null);
      PortletSession portletSession = request.getPortletSession();

      if (contentelement != null) {

         if (portletSession.getAttribute("confirm") != null) {
            String confirm = (String) portletSession.getAttribute("confirm");
            portletSession.removeAttribute("confirm");
            request.setAttribute("confirm", confirm);
         }
         if (portletSession.getAttribute("errormessages") != null) {
             Map<String, String> errormessages = (Map<String, String>) portletSession.getAttribute("errormessages");
            portletSession.removeAttribute("errormessages");
            request.setAttribute("errormessages", errormessages);
         }
         if (portletSession.getAttribute("parameterMap") != null) {
             Map<String, String> parameterMap = (Map<String, String>) portletSession.getAttribute("parameterMap");
            portletSession.removeAttribute("parameterMap");
            Iterator<String> keyIterator = parameterMap.keySet().iterator();
            while (keyIterator.hasNext()) {
               String keyValue = keyIterator.next();
               String entryValue = parameterMap.get(keyValue);
               request.setAttribute(keyValue, entryValue);
            }
         }
      }
      else {
         getLogger().error("No contentelement");
      }
      super.doView(request, response);
   }


   public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      super.processEdit(request, response);

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(PortletMode.EDIT);
      }
      else if (action.equals("delete")) {
         String deleteNumber = request.getParameter("deleteNumber");
         Cloud cloud = getCloud();
         Node element = cloud.getNode(deleteNumber);
         element.delete(true);
      }
   }

}
