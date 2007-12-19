package com.finalist.newsletter.module.bean;

public class SubscriptionDetailBean {

   private String userName;
   private String status;
   private String mimeType;
   private String emailAddress;

   public SubscriptionDetailBean() {

   }

   /**
    * @return the userName
    */
   public String getUserName() {
      return userName;
   }

   /**
    * @param userName
    *           the userName to set
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @param status
    *           the status to set
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * @return the mimeType
    */
   public String getMimeType() {
      return mimeType;
   }

   /**
    * @param mimeType
    *           the mimeType to set
    */
   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   /**
    * @return the emailAddress
    */
   public String getEmailAddress() {
      return emailAddress;
   }

   /**
    * @param emailAddress
    *           the emailAddress to set
    */
   public void setEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
   }

}
