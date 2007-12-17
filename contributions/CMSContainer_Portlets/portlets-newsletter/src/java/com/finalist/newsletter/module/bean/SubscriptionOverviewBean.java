package com.finalist.newsletter.module.bean;

public class SubscriptionOverviewBean {

   private String userName;
   private String preferredMimeType;
   private String subscriptionStatus;
   private int numberOfSubscriptions;

   public SubscriptionOverviewBean() {

   }

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
    * @return the preferredMimeType
    */
   public String getPreferredMimeType() {
      return preferredMimeType;
   }

   /**
    * @param preferredMimeType the preferredMimeType to set
    */
   public void setPreferredMimeType(String preferredMimeType) {
      this.preferredMimeType = preferredMimeType;
   }

   /**
    * @return the subscriptionStatus
    */
   public String getSubscriptionStatus() {
      return subscriptionStatus;
   }

   /**
    * @param subscriptionStatus the subscriptionStatus to set
    */
   public void setSubscriptionStatus(String subscriptionStatus) {
      this.subscriptionStatus = subscriptionStatus;
   }

   /**
    * @return the numberOfSubscriptions
    */
   public int getNumberOfSubscriptions() {
      return numberOfSubscriptions;
   }

   /**
    * @param numberOfSubscriptions the numberOfSubscriptions to set
    */
   public void setNumberOfSubscriptions(int numberOfSubscriptions) {
      this.numberOfSubscriptions = numberOfSubscriptions;
   }

}
