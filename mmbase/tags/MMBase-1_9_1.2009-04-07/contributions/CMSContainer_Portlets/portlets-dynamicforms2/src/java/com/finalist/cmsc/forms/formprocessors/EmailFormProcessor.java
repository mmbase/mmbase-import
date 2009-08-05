/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.forms.formprocessors;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.activation.DataSource;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.value.*;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.util.EmailSender;

public class EmailFormProcessor extends FormProcessor {

   private static final Log log = LogFactory.getLog(EmailFormProcessor.class);
   private static final String DEFAULT_EMAILREGEX = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$";
   /** This constant is used to prevent Outlook from removing line breaks in plain text emails */
   private static final String OUTLOOK_LINE_BREAK_PREVENTER = "  ";
   
   String lineSeparator = System.getProperty("line.separator");
   
   private StringBuilder formFieldData = new StringBuilder(OUTLOOK_LINE_BREAK_PREVENTER);
   
   private String emailpath;
   private String emailaddresses;
   private String subject;
   private String senderaddress;
   private String sendername;
   
   private String before;
   private String after;
   
   @Override
   public String processForm(ValueObject valueObject) {
      processObject(valueObject);
      
      ValueField userEmailField = ValuePathUtil.getFieldFromPath(valueObject, emailpath);
      if (userEmailField == null) {
         return "emailaddress not found";
      }
      String userEmailAddress = userEmailField.getStringValue();
      if (!isEmailAddress(userEmailAddress)) {
         return "emailaddress invalid";
      }
      
      sendEmail(userEmailAddress, formFieldData.toString(), null);
      return null;
   }

   @Override
   protected void processField(String path, String value) {
      formFieldData.append(path).append(" : ").append(value).append(lineSeparator).append(OUTLOOK_LINE_BREAK_PREVENTER);
   }
   
   private boolean sendEmail(String userEmailAddress, String responseformData, DataSource attachment) {
      boolean sent = false;
      StringBuffer emailText = new StringBuffer();

      String emailTextBefore = before;
      String emailTextAfter = after;

      if (emailTextBefore != null) {
         emailTextBefore = emailTextBefore.trim();
         emailText.append(emailTextBefore);
         emailText.append(lineSeparator);
      }
      emailText.append(responseformData);
      if (emailTextAfter != null) {
         emailTextAfter = emailTextAfter.trim();
         emailText.append(lineSeparator);
         emailText.append(emailTextAfter);
      }

      if (!isEmailAddress(senderaddress)) {
         return false; //Last check email address
      }

      List<String> emailList = splitEmailAddresses(emailaddresses);
      if (!isEmailAddress(emailList)) {
         log.error("error sending email. Some of the following emailaddresses are incorrect: " + emailList.toString());
         return false; //Could not sent email because of false email address
      }

      try {
         EmailSender.sendEmail(senderaddress, sendername, emailList, subject, emailText.toString(),
               attachment, userEmailAddress);
         sent = true;
      }
      catch (UnsupportedEncodingException e) {
         log.error("error sending email", e);
      }
      catch (MessagingException e) {
         log.error("error sending email", e);
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
   
   public boolean isEmailAddress(String email) {
      if (email == null || StringUtils.isBlank(email)) {
         return false;
      }
      String emailRegex = getEmailRegex();
      return email.trim().matches(emailRegex);
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

   protected String getEmailRegex() {
      String emailRegex = PropertiesUtil.getProperty("email.regex");
      if (StringUtils.isNotBlank(emailRegex)) {
         return emailRegex;
      }
      return DEFAULT_EMAILREGEX;
   }

   
   public String getEmailpath() {
      return emailpath;
   }

   
   public void setEmailpath(String userEmailPath) {
      this.emailpath = userEmailPath;
   }

   
   public String getEmailaddresses() {
      return emailaddresses;
   }

   
   public void setEmailaddresses(String emailaddresses) {
      this.emailaddresses = emailaddresses;
   }

   
   public String getSubject() {
      return subject;
   }

   
   public void setSubject(String subject) {
      this.subject = subject;
   }

   
   public String getSenderaddress() {
      return senderaddress;
   }

   
   public void setSenderaddress(String senderEmailAddress) {
      this.senderaddress = senderEmailAddress;
   }

   
   public String getSendername() {
      return sendername;
   }

   
   public void setSendername(String senderName) {
      this.sendername = senderName;
   }

   
   public String getBefore() {
      return before;
   }

   
   public void setBefore(String before) {
      this.before = before;
   }

   
   public String getAfter() {
      return after;
   }

   
   public void setAfter(String after) {
      this.after = after;
   }
}
