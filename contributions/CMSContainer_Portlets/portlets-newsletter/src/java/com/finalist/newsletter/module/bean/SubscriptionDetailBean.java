package com.finalist.newsletter.module.bean;

import java.util.List;

public class SubscriptionDetailBean {

   private String userName;
   private String status;
   private String mimeType;
   private String emailAddress;
   private List<Integer> subscribedThemes;
   private List<Integer> subscribedNewsletters;
   private List<String> availableMimeTypes;
   private List<Integer> availableStatusOptions;

   public List<String> getAvailableMimeTypes() {
      return availableMimeTypes;
   }

   public void setAvailableMimeTypes(List<String> availableMimeTypes) {
      this.availableMimeTypes = availableMimeTypes;
   }

   public List<Integer> getAvailableStatusOptions() {
      return availableStatusOptions;
   }

   public void setAvailableStatusOptions(List<Integer> availableStatusOptions) {
      this.availableStatusOptions = availableStatusOptions;
   }

   public List<Integer> getSubscribedThemes() {
      return subscribedThemes;
   }

   public void setSubscribedThemes(List<Integer> subscribedThemes) {
      this.subscribedThemes = subscribedThemes;
   }

   public List<Integer> getSubscribedNewsletters() {
      return subscribedNewsletters;
   }

   public void setSubscribedNewsletters(List<Integer> subscribedNewsletters) {
      this.subscribedNewsletters = subscribedNewsletters;
   }

   public SubscriptionDetailBean() {

   }

   /**
    * @return the emailAddress
    */
   public String getEmailAddress() {
      return emailAddress;
   }

   /**
    * @return the mimeType
    */
   public String getMimeType() {
      return mimeType;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the userName
    */
   public String getUserName() {
      return userName;
   }

   /**
    * @param emailAddress
    *           the emailAddress to set
    */
   public void setEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
   }

   /**
    * @param mimeType
    *           the mimeType to set
    */
   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   /**
    * @param status
    *           the status to set
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * @param userName
    *           the userName to set
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

}
