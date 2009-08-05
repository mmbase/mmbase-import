/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.responseform;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.portlet.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.EmailSender;
import com.finalist.cmsc.util.ServerUtil;

public class ResponseFormPortlet extends ContentPortlet {

   protected static final String PARAMETER_MAP = "parameterMap";
   protected static final String ERRORMESSAGES = "errormessages";

   protected static final int DEFAULT_MAXFILESIZE = 6; // default file size in MB
   protected static final long MEGABYTE = 1024 * 1024; // 1 MB (in bytes)

   protected static final String ENCODING_UTF8 = "UTF-8";

   protected static final String FIELD_PREFIX = "field_";
   protected static final int TYPE_TEXTBOX = 1;
   protected static final int TYPE_TEXTAREA = 2;
   protected static final int TYPE_RADIO = 4;
   protected static final int TYPE_CHECKBOX = 6;
   protected static final int TYPE_ATTACHEMENT = 7;
   protected static final String CHECKBOX_NO = "nee";
//   private static final String CHECKBOX_YES = "ja";
   protected static final String RADIO_EMPTY = "[niets gekozen]";
   protected static final String TEXTBOX_EMPTY = "[niet ingevuld]";
   protected static final String REGEX = " ";
   protected static final String DEFAULT_EMAILREGEX = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$";


   @Override
   @SuppressWarnings("unchecked")
   public void processView(ActionRequest request, ActionResponse response) {
      Map<String, String> errorMessages = new Hashtable<String, String>();
      Map<String, Object> parameterMap = new HashMap<String, Object>();
      DataSource attachment = processUserRequest(request, errorMessages, parameterMap);

      // Add extensibility for extra parameters
      addParameterProcessor(request, response, parameterMap, errorMessages);
      
      PortletPreferences preferences = request.getPreferences();
      String contentelement = preferences.getValue(CONTENTELEMENT, null);

      if (contentelement != null) {
         if (parameterMap.size() > 0) {

            String userEmailAddress = null;
            StringBuffer data = new StringBuffer();
            Map<String, Object> formfields = new HashMap<String, Object>();

            Cloud cloud = getCloudForAnonymousUpdate();
            Node responseForm = cloud.getNode(contentelement);
            NodeList formfieldList = SearchUtil.findRelatedOrderedNodeList(responseForm, "formfield", "posrel",
                  "posrel.pos");

            NodeIterator formfieldIterator = formfieldList.nodeIterator();
            while (formfieldIterator.hasNext()) {
               Node formfield = formfieldIterator.nextNode();
               String number = formfield.getStringValue("number");
               String label = formfield.getStringValue("label");
               int type = formfield.getIntValue("type");
               String regex = formfield.getStringValue("regex");
               boolean isMandatory = formfield.getBooleanValue("mandatory");
               boolean sendEmail = formfield.getBooleanValue("sendemail");
               int maxlength = formfield.getIntValue("maxlength");
               String fieldIdentifier = FIELD_PREFIX + number;
               Object value = parameterMap.get(fieldIdentifier);
               String textValue = null;
               if (sendEmail) {
                  userEmailAddress = value.toString().trim();
               }
               if (type == TYPE_TEXTAREA && value != null && maxlength > 0 && value.toString().length() > maxlength) {
                  errorMessages.put(fieldIdentifier, Integer.valueOf(maxlength).toString());
               }
               if (isMandatory
                     && (((type == TYPE_RADIO || type == TYPE_CHECKBOX) && (value == null)) || (((type == TYPE_TEXTBOX)
                           || (type == TYPE_TEXTAREA) || (type == TYPE_ATTACHEMENT)) && value.equals("")))) {
                  errorMessages.put(fieldIdentifier, "view.formfield.empty");
               }
               if (!regex.equals("")
                       && (((type == TYPE_TEXTBOX) || (type == TYPE_TEXTAREA)) && !value.toString().matches(regex))) {
                  errorMessages.put(fieldIdentifier, "view.formfield.invalid");
               }
               
               if ((type == TYPE_TEXTBOX) && sendEmail) {   //If data is used as email address, then it should be valid
                   if (!isEmailAddress(userEmailAddress)) {
                     errorMessages.put(fieldIdentifier, "view.formfield.invalid");
                  }
               }

               if (type == TYPE_CHECKBOX) {
                  if(value != null && value instanceof String){
                     textValue = value.toString();
                  }
                  else if (value != null && value instanceof ArrayList){
                     textValue = transferParameterValues((ArrayList)value);
                  }
                  else{
                     textValue = CHECKBOX_NO;
                  }
               }
               else if (type == TYPE_RADIO) {
                  textValue = (value == null) ? RADIO_EMPTY : value.toString();
               }
               else {
                  textValue = (value == null || value.toString().trim().length() == 0) ? TEXTBOX_EMPTY : value.toString();
               }
               addFormFieldsData(data, label, textValue);
               formfields.put(number, textValue);

            }

            if (errorMessages.size() == 0) {
               boolean saveAnswer = responseForm.getBooleanValue("saveanswer");
               if (saveAnswer) {
                  saveResponseForm(cloud, formfields, responseForm);
               }
               String emailData = data.toString();
               boolean sent = sendResponseFormEmail(responseForm, userEmailAddress, emailData, attachment);
               if (!sent) {
                  errorMessages.put("sendemail", "view.error.sendemail");
               }
               else {
                   // if the responseform email has been sent, send also the email to the user
                   sent = sendUserEmail(responseForm, userEmailAddress, emailData, parameterMap);
                   if (!sent) {
                      errorMessages.put("sendemail", "view.error.sendemail");
                   }
               }
            }
         }
         if (errorMessages.size() > 0) {
            request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
            request.getPortletSession().setAttribute(PARAMETER_MAP, parameterMap);
         }
         else {
            request.getPortletSession().setAttribute("confirm", "confirm");
         }
      }
      else {
         getLogger().error("No contentelement");
      }
   }


   protected void addParameterProcessor(ActionRequest request, ActionResponse response, Map<String, Object> parameterMap,
         Map<String, String> errorMessages) {
      // Add extensibility for extra parameters, nothing to do now
   }


   private void addFormFieldsData(StringBuffer data, String label, String textValue) {
      data.append(label);
      data.append(": ");
      data.append(textValue);
      data.append(System.getProperty("line.separator"));
   }

   @SuppressWarnings("unchecked")
   private String transferParameterValues(List textValues) {
      StringBuffer temp = new StringBuffer();
      for(Object textValue : textValues){
         temp.append(textValue+REGEX);
      }
      return temp.toString();
   }


   private void saveResponseForm(Cloud cloud, Map<String, Object> formfields, Node responseForm) {

      NodeManager savedFormMgr = cloud.getNodeManager("savedform");
      Node savedResponse = savedFormMgr.createNode();
      savedResponse.commit();

      RelationUtil.createRelation(responseForm, savedResponse, "posrel");
      if (ServerUtil.isLive()) {
         Publish.publish(savedResponse);
      }

      NodeManager savedFieldMgr = cloud.getNodeManager("savedfieldvalue");
      Iterator<String> keyIterator = formfields.keySet().iterator();

      while (keyIterator.hasNext()) {
         String key = keyIterator.next();
         String formFieldNumber = key; // key represents the node number of the
                                       // form field from staging
         if (ServerUtil.isLive()) {
            Node liveFormFieldNode = cloud.getNode(key);
            Node stagingFormFieldNode = Publish.getRemoteNode(liveFormFieldNode);
            formFieldNumber = String.valueOf(stagingFormFieldNode.getNumber());
         }
         Node savedFieldValue = savedFieldMgr.createNode();
         savedFieldValue.setStringValue("field", formFieldNumber);
         savedFieldValue.setValue("value", formfields.get(key));
         savedFieldValue.commit();

         RelationUtil.createRelation(savedResponse, savedFieldValue, "posrel");
         if (ServerUtil.isLive()) {
            Publish.publish(savedFieldValue);
         }

      }
   }


   protected boolean sendUserEmail(Node responseform, String userEmailAddress, String responseformData,
         Map<String, Object> parameterMap) {
      boolean sent = false;
      String userEmailSubject = responseform.getStringValue("useremailsubject").trim();
      String userEmailSenderAddress = responseform.getStringValue("useremailsender").trim();
      String userEmailSenderName = responseform.getStringValue("useremailsendername").trim();
      String userEmailTextBefore = responseform.getStringValue("useremailbody");
      String userEmailTextAfter = responseform.getStringValue("useremailbodyafter");
      boolean includedata = responseform.getBooleanValue("includedata");
      StringBuffer userEmailText = new StringBuffer();

      userEmailTextBefore = userEmailTextBefore.trim();
      userEmailText.append(userEmailTextBefore);
      if (includedata) {
         userEmailText.append(System.getProperty("line.separator"));
         userEmailText.append(responseformData);
      }
      if (userEmailTextAfter != null) {
         userEmailTextAfter = userEmailTextAfter.trim();
         userEmailText.append(System.getProperty("line.separator"));
         userEmailText.append(userEmailTextAfter);
      }

      if (StringUtils.isNotBlank(userEmailText.toString())
            && StringUtils.isNotBlank(userEmailSenderName)
            && isEmailAddress(userEmailAddress)
            && isEmailAddress(userEmailSenderAddress)) {
         try {
            EmailSender.sendEmail(userEmailSenderAddress, userEmailSenderName, userEmailAddress,
                  userEmailSubject, userEmailText.toString());
            sent = true;
         }
         catch (UnsupportedEncodingException e) {
            getLogger().error("error in mail data: userEmailText = '" + userEmailText +"' " +
                       " userEmailSenderName = '" + userEmailSenderName +"' " +
                       " userEmailAddress = '" + userEmailAddress +"' " +
                       " userEmailSenderAddress = '" + userEmailSenderAddress +"' ");
            getLogger().error("error sending email", e);
         }
         catch (MessagingException e) {
            getLogger().error("error in mail data: userEmailText = '" + userEmailText +"' " +
                       " userEmailSenderName = '" + userEmailSenderName +"' " +
                       " userEmailAddress = '" + userEmailAddress +"' " +
                       " userEmailSenderAddress = '" + userEmailSenderAddress +"' ");
            getLogger().error("error sending email", e);
         }
      }
      else {
         // no need to send, but there is need to tell it was a success
          sent = true;
      }
      return sent;

   }


   private boolean sendResponseFormEmail(Node responseform, final String userEmailAddress, String responseformData,
         DataSource attachment) {
      boolean sent = false;
      StringBuffer emailText = new StringBuffer();

      String emailTextBefore = responseform.getStringValue("emailbody");
      String emailTextAfter = responseform.getStringValue("emailbodyafter");

      String senderEmailAddress = responseform.getStringValue("useremailsender").trim();
      String senderName = responseform.getStringValue("useremailsendername").trim();
      if (StringUtils.isNotBlank(userEmailAddress)) {
         senderName = userEmailAddress + " [CMS]";
      }
      if (!isEmailAddress(senderEmailAddress)) {
         return false; //Last check email address
      }

      emailTextBefore = emailTextBefore.trim();
      emailText.append(emailTextBefore);
      emailText.append(System.getProperty("line.separator"));
      emailText.append(responseformData);
      if (emailTextAfter != null) {
         emailTextAfter = emailTextAfter.trim();
         emailText.append(System.getProperty("line.separator"));
         emailText.append(emailTextAfter);
      }

      String emailAddressesValue = responseform.getStringValue("emailaddresses");
      String emailSubject = responseform.getStringValue("emailsubject").trim();

      List<String> emailList = splitEmailAddresses(emailAddressesValue);

      if (!isEmailAddress(emailList)) {
         getLogger().error("error sending email. Some of the following emailaddresses are incorrect: " + emailList.toString());
         return false; //Could not sent email because of false email address
      }

      try {
         EmailSender.sendEmail(senderEmailAddress, senderName, emailList, emailSubject, emailText.toString(),
               attachment, userEmailAddress);
         sent = true;
      }
      catch (UnsupportedEncodingException e) {
         getLogger().error("error sending email", e);
      }
      catch (MessagingException e) {
         getLogger().error("error sending email", e);
      }
      return sent;
   }


   public List<String> splitEmailAddresses(String emailAddressesValue) {
      List<String> emailList = new ArrayList<String>();
      StringTokenizer addresssTokenizer = new StringTokenizer(emailAddressesValue, " ,;");
      while(addresssTokenizer.hasMoreTokens()) {
         String address = addresssTokenizer.nextToken();
         emailList.add(address);
      }
      return emailList;
   }


   @SuppressWarnings("unchecked")
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
         if (portletSession.getAttribute(ERRORMESSAGES) != null) {
            Map<String, String> errormessages = (Map<String, String>) portletSession.getAttribute(ERRORMESSAGES);
            portletSession.removeAttribute(ERRORMESSAGES);
            request.setAttribute(ERRORMESSAGES, errormessages);
         }
         if (portletSession.getAttribute(PARAMETER_MAP) != null) {
            Map<String, Object> parameterMap = (Map<String, Object>) portletSession.getAttribute(PARAMETER_MAP);
            portletSession.removeAttribute(PARAMETER_MAP);
            Iterator<String> keyIterator = parameterMap.keySet().iterator();
            while (keyIterator.hasNext()) {
               String keyValue = keyIterator.next();
               if(parameterMap.get(keyValue) instanceof String) {
                  String entryValue = parameterMap.get(keyValue).toString();
                  request.setAttribute(keyValue, entryValue);
               }
               else if (parameterMap.get(keyValue) instanceof ArrayList){
                  List<String> entryValue = (List<String>)parameterMap.get(keyValue);
                  String fieldValue = "";
                  for (String value :entryValue) {
                     fieldValue += value+":";
                  }
                  request.setAttribute(keyValue, fieldValue);
               }
            }
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

   @SuppressWarnings("unchecked")
   private DataSource processUserRequest(ActionRequest request, Map<String, String> errorMessages,
         Map<String, Object> parameterMap) {
      List<FileItem> fileItems = null;
      DataSource attachment = null;

      List<String> parameterValues = null;
      try {
         DiskFileItemFactory factory = new DiskFileItemFactory();
         PortletFileUpload upload = new PortletFileUpload(factory);
         upload.setHeaderEncoding(ENCODING_UTF8);
         fileItems = upload.parseRequest(request);
      }
      catch (FileUploadException e) {
         getLogger().error("error parsing request", e);
         errorMessages.put("sendemail", "view.error.sendemail");
      }
      if (fileItems != null) {
         Iterator<FileItem> itFileItems = fileItems.iterator();
         while (itFileItems.hasNext()) {
            FileItem fileItem = itFileItems.next();
            if (fileItem.isFormField()) {
               try {
                  if (parameterMap.containsKey(fileItem.getFieldName())) {
                     if (parameterMap.get(fileItem.getFieldName()) instanceof String) {
                        parameterValues = new ArrayList<String>(8);
                        parameterValues.add(parameterMap.get(fileItem.getFieldName()).toString());
                     }
                     else if (parameterMap.get(fileItem.getFieldName()) instanceof ArrayList){
                        parameterValues = (ArrayList<String>) parameterMap.get(fileItem.getFieldName());
                     }
                     parameterValues.add(fileItem.getString(ENCODING_UTF8));
                     parameterMap.put(fileItem.getFieldName(), parameterValues);
                  }
                  else{
                  parameterMap.put(fileItem.getFieldName(), fileItem.getString(ENCODING_UTF8));
                  }
               }
               catch (UnsupportedEncodingException e) {
                  getLogger().error("UnsupportedEncoding " + ENCODING_UTF8);
               }
            }
            else {
               if (StringUtils.isNotBlank(fileItem.getName())) {
                  if (fileItem.getSize() <= getMaxAttachmentSize()) {
                     attachment = new WrappedFileItem(fileItem);
                  }
                  else {
                     errorMessages.put("sendemail", "view.error.filesize");
                  }
               }
               parameterMap.put(fileItem.getFieldName(), fileItem.getName());
            }
         }
      }
      return attachment;

   }

   public boolean isEmailAddress(String emailAddress) {
      if (emailAddress == null) {
         return false;
      }
      if (StringUtils.isBlank(emailAddress)) {
         return false;
      }

      String emailRegex = getEmailRegex();
      return emailAddress.trim().matches(emailRegex);
   }

   public boolean isEmailAddress(List<String> emailList) {
      if (emailList == null) {
         return false;
      }
      if (emailList.isEmpty()) {
         return false;
      }

      String emailRegex = getEmailRegex();
      for (String email : emailList) {
      	if (email == null || StringUtils.isBlank(email)) {
            return false;
         }
         if (!email.matches(emailRegex)) {
            return false;
         }
      }

      return true;
   }

   private long getMaxAttachmentSize() {
      long maxFileSize = DEFAULT_MAXFILESIZE;
      String maxFileSizeValue = PropertiesUtil.getProperty("email.maxattachmentsize");
      if (StringUtils.isNotBlank(maxFileSizeValue)) {
         try {
            maxFileSize = Integer.parseInt(maxFileSizeValue);
         }
         catch (NumberFormatException e) {
            getLogger().info(
                  "incorrect value for email.maxattachmentsize=" + maxFileSizeValue + ", default value="
                        + DEFAULT_MAXFILESIZE + " is used");
         }
      }
      return maxFileSize * MEGABYTE;
   }

   protected String getEmailRegex() {
      String emailRegex = PropertiesUtil.getProperty("email.regex");
      if (StringUtils.isNotBlank(emailRegex)) {
         return emailRegex;
      }
      return DEFAULT_EMAILREGEX;
   }

}
