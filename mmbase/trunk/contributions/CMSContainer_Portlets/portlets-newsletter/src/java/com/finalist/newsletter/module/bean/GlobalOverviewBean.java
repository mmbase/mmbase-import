package com.finalist.newsletter.module.bean;

public class GlobalOverviewBean {

   private int numberOfNewsletters = 0;
   private int numberOfThemes = 0;
   private int numberOfPublications = 0;
   private int numberOfSentEmails = 0;
   private int numberOfSubscribtions = 0;

   public GlobalOverviewBean() {
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
      this.numberOfNewsletters = 0 + numberOfNewsletters;
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
      this.numberOfThemes = 0 + numberOfThemes;
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
      this.numberOfPublications = 0 + numberOfPublications;
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
      this.numberOfSentEmails = 0 + numberOfSentEmails;
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
      this.numberOfSubscribtions = 0 + numberOfSubscribtions;
   }
}
