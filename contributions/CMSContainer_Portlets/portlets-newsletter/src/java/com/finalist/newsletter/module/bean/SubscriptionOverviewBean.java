package com.finalist.newsletter.module.bean;

public class SubscriptionOverviewBean {

   private String userName;
   private String mimeType;
   private String status;
   private int numberOfNewsletters;
   private int numberOfThemes;

   /**
    * @return the userName
    */
   public String getUserName() {
      return userName;
   }

   /**
    * @param userName the userName to set
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * @return the mimeType
    */
   public String getMimeType() {
      return mimeType;
   }

   /**
    * @param mimeType the mimeType to set
    */
   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * @return the numberOfNewsletters
    */
   public int getNumberOfNewsletters() {
      return numberOfNewsletters;
   }

   /**
    * @param numberOfNewsletters the numberOfNewsletters to set
    */
   public void setNumberOfNewsletters(int numberOfNewsletters) {
      this.numberOfNewsletters = numberOfNewsletters;
   }

   /**
    * @return the numberOfThemes
    */
   public int getNumberOfThemes() {
      return numberOfThemes;
   }

   /**
    * @param numberOfThemes the numberOfThemes to set
    */
   public void setNumberOfThemes(int numberOfThemes) {
      this.numberOfThemes = numberOfThemes;
   }

   public SubscriptionOverviewBean() {

   }

}