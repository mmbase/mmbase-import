package com.finalist.newsletter.module.bean;

public class NewsletterOverviewBean {

   private int number = 0;
   private String title = "";
   private int numberOfSubscriptions = 0;
   private int numberOfThemes = 0;
   private int numberOfPublications = 0;

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title
    *           the title to set
    */
   public void setTitle(String title) {
      this.title = "" + title;
   }

   /**
    * @return the numberOfSubscriptions
    */
   public int getNumberOfSubscriptions() {
      return numberOfSubscriptions;
   }

   /**
    * @param numberOfSubscriptions
    *           the numberOfSubscriptions to set
    */
   public void setNumberOfSubscriptions(int numberOfSubscriptions) {      
      this.numberOfSubscriptions = 0 +  numberOfSubscriptions;
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
      this.numberOfPublications = 0 +  numberOfPublications;
   }

   public NewsletterOverviewBean() {

   }

   /**
    * @return the number
    */
   public int getNumber() {
      return number;
   }

   /**
    * @param number the number to set
    */
   public void setNumber(int number) {
      this.number = number;
   }

}
