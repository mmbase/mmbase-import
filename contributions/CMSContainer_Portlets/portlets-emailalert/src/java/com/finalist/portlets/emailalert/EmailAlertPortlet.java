package com.finalist.portlets.emailalert;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;

import com.finalist.cmsc.mmbase.*;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class EmailAlertPortlet extends ContentPortlet {

   private static final String SUBSCRIBEREL = "subscriberel";
   private static final String SUBSCRIBER = "subscriber";
   private static final String ERRORMESSAGES = "errormessages";
   private static final String EMAILADDRESS = "emailaddress";
   private static final String SUBSCRIBEPAGE = "subscribepage";
   private static final String CONFIRM = "confirm";
   private static final String VALID = "valid";


   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException {
      String action = request.getParameter(ACTION_PARAM);
      Map<String, String> errorMessages = new Hashtable<String, String>();
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String contentElement = preferences.getValue(CONTENTELEMENT, null);

         if (contentElement != null) {
            String emailAddress = request.getParameter(EMAILADDRESS);
            String subscribePage = request.getParameter(SUBSCRIBEPAGE);
            String emailRegex = PropertiesUtil.getProperty("email.regex");
            if (StringUtils.isBlank(emailAddress)) {
               errorMessages.put(EMAILADDRESS, "view.emailaddress.empty");
            }
            else if (!emailAddress.matches(emailRegex)) {
               errorMessages.put(EMAILADDRESS, "view.emailaddress.invalid");
            }
            if (StringUtils.isBlank(subscribePage)) {
               errorMessages.put(SUBSCRIBEPAGE, "view.error.nopage");
            }
            if (errorMessages.size() == 0) {
               Cloud cloud = getCloudForAnonymousUpdate();
               Node emailAlert = cloud.getNode(contentElement);
               // check if the emailaddress already exists, otherwise create it
               Node subscriberNode = SearchUtil.findNode(cloud, SUBSCRIBER, EMAILADDRESS, emailAddress);
               if (subscriberNode == null) {
                  NodeManager subscriberNodeManager = cloud.getNodeManager(SUBSCRIBER);
                  subscriberNode = subscriberNodeManager.createNode();
                  subscriberNode.setStringValue(EMAILADDRESS, emailAddress);
                  subscriberNode.setBooleanValue(VALID, false);
                  subscriberNode.commit();
               }
               Node pageNode = cloud.getNode(subscribePage);
               // check if the relation between page and subscriber exists,
               // otherwise create it
               NodeManager relationNodeManager = cloud.getNodeManager(SUBSCRIBEREL);
               if (!existsRelation(relationNodeManager, Integer.parseInt(subscribePage), subscriberNode
                     .getIntValue("number"))) {
                  RelationUtil.createRelation(pageNode, subscriberNode, SUBSCRIBEREL);
                  boolean valid = subscriberNode.getBooleanValue(VALID);
                  if (!valid) {
                     boolean sent = sendConfirmationEmail(cloud, subscriberNode, emailAlert, request);
                     if (!sent) {
                        errorMessages.put("sendemail", "view.error.sendemail");
                     }
                  }
               }
               else {
                  errorMessages.put("sendemail", "view.subscriber.exists");
               }
            }
            if (errorMessages.size() > 0) {
               request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
               request.getPortletSession().setAttribute(EMAILADDRESS, emailAddress);
               request.getPortletSession().setAttribute(SUBSCRIBEPAGE, subscribePage);
            }
            else {
               request.getPortletSession().setAttribute(CONFIRM, CONFIRM);
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


   private boolean sendConfirmationEmail(Cloud cloud, Node subscriberNode, Node emailAlert, ActionRequest request) {
      boolean sent = false;
      String message = emailAlert.getStringValue("confirmationemailbody");
      String subject = emailAlert.getStringValue("confirmationemailsubject");
      String fromName = emailAlert.getStringValue("sendername");
      String fromEmailAddress = emailAlert.getStringValue("senderemailaddress");
      StringBuffer link = new StringBuffer();
      link.append(HttpUtil.getWebappUri((HttpServletRequest) request));
      link.append("alert/confirm.do?s=");
      link.append(subscriberNode.getStringValue(EMAILADDRESS));
      message = message.replace("#URL#", link.toString());
      message = message.trim();
      try {
         EmailUtil.send(cloud, null, subscriberNode.getStringValue(EMAILADDRESS), fromName, fromEmailAddress, subject,
               message);
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
         if (portletSession.getAttribute(CONFIRM) != null) {
            String confirm = (String) portletSession.getAttribute(CONFIRM);
            portletSession.removeAttribute(CONFIRM);
            request.setAttribute(CONFIRM, confirm);
         }
         if (portletSession.getAttribute(ERRORMESSAGES) != null) {
             Map<String, String> errormessages = (Map<String, String>) portletSession.getAttribute(ERRORMESSAGES);
            portletSession.removeAttribute(ERRORMESSAGES);
            request.setAttribute(ERRORMESSAGES, errormessages);
         }
         if (portletSession.getAttribute(EMAILADDRESS) != null) {
            request.setAttribute(EMAILADDRESS, portletSession.getAttribute(EMAILADDRESS));
            portletSession.removeAttribute(EMAILADDRESS);
         }
         if (portletSession.getAttribute(SUBSCRIBEPAGE) != null) {
            request.setAttribute(SUBSCRIBEPAGE, portletSession.getAttribute(SUBSCRIBEPAGE));
            portletSession.removeAttribute(SUBSCRIBEPAGE);
         }
      }
      else {
         getLogger().error("No contentelement");
      }
      super.doView(request, response);
   }


   @Override
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


   // TODO remove this and use the RelationUtil.exists(...) instead, when the
   // new mmapps is built
   private static boolean existsRelation(NodeManager builder, int source, int destination) {
      NodeQuery query = builder.createQuery();
      Constraint s = SearchUtil.createEqualConstraint(query, builder.getField("snumber"), source);
      Constraint d = SearchUtil.createEqualConstraint(query, builder.getField("dnumber"), destination);
      Constraint composite = query.createConstraint(s, CompositeConstraint.LOGICAL_AND, d);
      query.setConstraint(composite);

      NodeList list = builder.getList(query);
      return !list.isEmpty();
   }

}
