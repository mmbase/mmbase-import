/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.ecard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.bridge.RelationUtil;
import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.ContentChannelPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalURL;

public class EcardPortlet extends ContentChannelPortlet {

   protected static final String ERRORMESSAGES = "errormessages";
   protected static final String PARAMETER_MAP = "parameterMap";

   protected static final String EMAIL_BODY_AFTER = "emailBodyAfter";
   protected static final String EMAIL_BODY_BEFORE = "emailBodyBefore";
   protected static final String EMAIL_SUBJECT = "emailSubject";
   protected static final String SENDER_NAME = "senderName";
   protected static final String SENDER_EMAIL = "senderEmail";
   protected static final String ECARD_WINDOW = "ecardWindow";
   protected static final String GALLERY_ID = "galleryId";
   protected static final String PAGE_ID = "pageId";

   protected static final String VIEW_ECARD_INVALID = "view.ecard.invalid";
   protected static final String FROM_EMAIL = "fromEmail";
   protected static final String TO_EMAIL = "toEmail";
   protected static final String FROM_NAME = "fromName";
   protected static final String TO_NAME = "toName";
   protected static final String TEXT_BODY = "textBody";
   protected static final String SELGALLERY = "selgallery";
   protected static final int TEXTAREA_MAXLENGTH = 1024;
   protected static final String DEFAULT_EMAILREGEX = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$";


   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      Map<String, String> errorMessages = new HashMap<String, String>();
      Map<String, String> parameterMap = new HashMap<String, String>();
      // elementId represents the image number
      String elementId = request.getParameter(ELEMENT_ID);
      String pageId = request.getParameter(PAGE_ID);
      String galleryId = request.getParameter(GALLERY_ID);
      String ecardWindow = request.getParameter(ECARD_WINDOW);
      String fromEmail = request.getParameter(FROM_EMAIL);
      parameterMap.put(FROM_EMAIL, fromEmail);
      String fromName = request.getParameter(FROM_NAME);
      parameterMap.put(FROM_NAME, fromName);
      String toEmail = request.getParameter(TO_EMAIL);
      parameterMap.put(TO_EMAIL, toEmail);
      String toName = request.getParameter(TO_NAME);
      parameterMap.put(TO_NAME, toName);
      String textBody = request.getParameter(TEXT_BODY);
      parameterMap.put(TEXT_BODY, textBody);

      if (StringUtil.isEmptyOrWhitespace(elementId) || StringUtil.isEmptyOrWhitespace(galleryId)) {
         errorMessages.put("noimage", "view.ecard.noimage");
         getLogger().error("image or galery not available");
      }
      if (StringUtil.isEmptyOrWhitespace(fromEmail)) {
         errorMessages.put(FROM_EMAIL, "view.ecard.fromEmail.empty");
      }
      else if (!fromEmail.matches(getEmailRegex())) {
         errorMessages.put(FROM_EMAIL, VIEW_ECARD_INVALID);
      }
      if (StringUtil.isEmptyOrWhitespace(toEmail)) {
         errorMessages.put(TO_EMAIL, "view.ecard.toEmail.empty");
      }
      else if (!toEmail.matches(getEmailRegex())) {
         errorMessages.put(TO_EMAIL, VIEW_ECARD_INVALID);
      }
      if (StringUtil.isEmptyOrWhitespace(fromName)) {
         errorMessages.put(FROM_NAME, "view.ecard.fromName.empty");
      }
      if (StringUtil.isEmptyOrWhitespace(toName)) {
         errorMessages.put(TO_NAME, "view.ecard.toName.empty");
      }
      if (StringUtil.isEmptyOrWhitespace(textBody)) {
         errorMessages.put(TEXT_BODY, "view.ecard.textBody.empty");
      }
      else if (textBody.length() > TEXTAREA_MAXLENGTH) {
         textBody = textBody.substring(0, TEXTAREA_MAXLENGTH - 1);
      }

      if (errorMessages.size() == 0) {
         CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
         Cloud cloud = cloudProvider.getCloud();

         NodeManager ecardManager = cloud.getNodeManager("ecard");
         Node ecard = ecardManager.createNode();
         ecard.setStringValue("fromemail", fromEmail);
         ecard.setStringValue("fromname", fromName);
         ecard.setStringValue("toemail", toEmail);
         ecard.setStringValue("toname", toName);
         ecard.setStringValue("body", textBody);
         String mailkey = getMailKey();
         ecard.setStringValue("mailkey", mailkey);
         ecard.commit();
         Node image = cloud.getNode(elementId);
         RelationUtil.createRelation(ecard, image, "posrel");

         String url = getPageUrl(request, Integer.parseInt(pageId), ecardWindow, mailkey, ecard
               .getStringValue("number"), galleryId);
         sendEmail(request, cloud, toName, toEmail, url, fromName);
         response.setRenderParameter("emailsent", "true");
         response.setRenderParameter("ecardId", ecard.getStringValue("number"));
      }
      else {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
         request.getPortletSession().setAttribute(PARAMETER_MAP, parameterMap);
         response.setRenderParameter("pageId", pageId);
         response.setRenderParameter("ecardWindow", ecardWindow);
      }
      response.setRenderParameter("elementId", elementId);
      response.setRenderParameter("selgallery", galleryId);

   }


   private String getMailKey() {
      Random rand = new Random();
      String[] chars1 = new String[] { "b", "c", "d", "f", "g", "h", "j", "k", "m", "n", "p", "r", "s", "t", "v", "w",
            "x", "z", "Y", "G", "2", "0", "Q" };
      String[] chars2 = new String[] { "a", "e", "i", "o", "u", "y", "2", "3", "4", "5", "6", "7", "8", "9" };
      String account = "";
      for (int i = 0; i < 8; i++) {
         int ri1 = rand.nextInt(chars1.length);
         int ri2 = rand.nextInt(chars2.length);
         account += chars1[ri1];
         account += chars2[ri2];
      }
      return account;
   }


   private void sendEmail(ActionRequest request, Cloud cloud, String toName, String toEmail, String url, String fromName) {
      PortletPreferences preferences = request.getPreferences();
      String senderEmail = preferences.getValue(SENDER_EMAIL, null);
      String senderName = preferences.getValue(SENDER_NAME, null);
      String subject = preferences.getValue(EMAIL_SUBJECT, null);
      String bodyBefore = preferences.getValue(EMAIL_BODY_BEFORE, null);
      if (!StringUtil.isEmptyOrWhitespace(bodyBefore)) {
         bodyBefore = bodyBefore.replace("#TO#", toName);
         bodyBefore = bodyBefore.replace("#FROM#", fromName);
      }
      String bodyAfter = preferences.getValue(EMAIL_BODY_AFTER, null);

      if (senderEmail != null && subject != null) {
         StringBuffer body = new StringBuffer();
         if (!StringUtil.isEmptyOrWhitespace(bodyBefore)) {
            body.append(bodyBefore);
            body.append("\n");
         }
         body.append("\n");
         body.append(url);
         body.append("\n");
         body.append("\n");
         if (!StringUtil.isEmptyOrWhitespace(bodyAfter)) {
            body.append(bodyAfter);
         }
         EmailUtil.send(cloud, toName, toEmail, senderName, senderEmail, subject, body.toString());
      }
   }


   private String getPageUrl(ActionRequest request, int pageid, String portletWindowName, String mailkey,
         String ecardId, String galleryId) {
      HttpServletRequest servletRequest = (HttpServletRequest) request;
      String link = SiteManagement.getPath(pageid, !ServerUtil.useServerName());
      String host = null;
      if (ServerUtil.useServerName()) {
         NavigationItem item = SiteManagement.getNavigationItem(pageid);
         host = SiteManagement.getSite(item);
      }
      else {
         host = servletRequest.getServerName();
      }
      PortalURL u = new PortalURL(host, servletRequest, link);

      u.setRenderParameter(portletWindowName, "mailkey", new String[] { mailkey });
      u.setRenderParameter(portletWindowName, "ecardId", new String[] { ecardId });
      u.setRenderParameter(portletWindowName, GALLERY_ID, new String[] { galleryId });

      return u.toString();
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      PortletSession portletSession = request.getPortletSession();
      String window = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_LAYOUTID, null);
      request.setAttribute(ECARD_WINDOW, window);
      String parameter = request.getParameter(SELGALLERY);
      if (parameter != null) {
         request.setAttribute(SELGALLERY, parameter);
      }
      if (portletSession.getAttribute(ERRORMESSAGES) != null) {
         Map<String, String> errormessages = (Map<String, String>) portletSession.getAttribute(ERRORMESSAGES);
         portletSession.removeAttribute(ERRORMESSAGES);
         request.setAttribute(ERRORMESSAGES, errormessages);
      }
      if (portletSession.getAttribute(PARAMETER_MAP) != null) {
         Map<String, String> parameterMap = (Map<String, String>) portletSession.getAttribute(PARAMETER_MAP);
         portletSession.removeAttribute(PARAMETER_MAP);
         Iterator<String> keyIterator = parameterMap.keySet().iterator();
         while (keyIterator.hasNext()) {
            String keyValue = keyIterator.next();
            String entryValue = parameterMap.get(keyValue);
            request.setAttribute(keyValue, entryValue);
         }
      }
      super.doView(request, response);
   }


   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      super.saveParameters(request, portletId);
      setPortletParameter(portletId, SENDER_EMAIL, request.getParameter(SENDER_EMAIL));
      setPortletParameter(portletId, SENDER_NAME, request.getParameter(SENDER_NAME));
      setPortletParameter(portletId, EMAIL_SUBJECT, request.getParameter(EMAIL_SUBJECT));
      setPortletParameter(portletId, EMAIL_BODY_BEFORE, request.getParameter(EMAIL_BODY_BEFORE));
      setPortletParameter(portletId, EMAIL_BODY_AFTER, request.getParameter(EMAIL_BODY_AFTER));
      setPortletParameter(portletId, "confirmation", request.getParameter("confirmation"));
   }


   public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      super.processEdit(request, response);

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(PortletMode.EDIT);
      }
      else if (action.equals("delete")) {
         String deleteNumber = request.getParameter("deleteNumber");
         CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
         Cloud cloud = cloudProvider.getCloud();
         Node element = cloud.getNode(deleteNumber);
         element.delete(true);
      }
   }


   private String getEmailRegex() {
      String emailRegex = PropertiesUtil.getProperty("email.regex");
      if (!StringUtil.isEmptyOrWhitespace(emailRegex)) {
         return emailRegex;
      }
      else {
         return DEFAULT_EMAILREGEX;
      }
   }

}
