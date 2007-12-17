package com.finalist.newsletter.module.bean;

public class GlobalOverviewBean {

   private int numberOfNewsletters;
   private int numberOfThemes;
   private int numberOfPublications;
   private int numberOfSentEmails;
   private int numberOfSubscribtions;

   public GlobalOverviewBean() {
      this.numberOfNewsletters = 0;
      this.numberOfThemes = 0;
      this.numberOfPublications = 0;
      this.numberOfSentEmails = 0;
      this.numberOfSubscribtions = 0;
   }

   /**
    * @return the numberOfNewsletters
    */
   public int getNumberOfNewsletters() {
      return numberOfNewsletters;
   }

   /**
    * @param numberOfNewsletters
    *           the numberOfNewsletters to set
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
    * @param numberOfThemes
    *           the numberOfThemes to set
    */
   public void setNumberOfThemes(int numberOfThemes) {
      this.numberOfThemes = numberOfThemes;
   }

   /**
    * @return the numberOfPublications
    */
   public int getNumberOfPublications() {
      return numberOfPublications;
   }

   /**
    * @param numberOfPublications
    *           the numberOfPublications to set
    */
   public void setNumberOfPublications(int numberOfPublications) {
      this.numberOfPublications = numberOfPublications;
   }

   /**
    * @return the numberOfSentEmails
    */
   public int getNumberOfSentEmails() {
      return numberOfSentEmails;
   }

   /**
    * @param numberOfSentEmails
    *           the numberOfSentEmails to set
    */
   public void setNumberOfSentEmails(int numberOfSentEmails) {
      this.numberOfSentEmails = numberOfSentEmails;
   }

   /**
    * @return the numberOfSubscribtions
    */
   public int getNumberOfSubscribtions() {
      return numberOfSubscribtions;
   }

   /**
    * @param numberOfSubscribtions
    *           the numberOfSubscribtions to set
    */
   public void setNumberOfSubscribtions(int numberOfSubscribtions) {
      this.numberOfSubscribtions = numberOfSubscribtions;
   }
}
