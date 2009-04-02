package com.finalist.newsletter.domain;

import java.util.Date;

public class NewsletterBounce {

   private int userId;
   
   private int newsletterId;
   
   private String userName;
   
   private String newsLetterTitle;
   
   private int id;
   
   private Date bounceDate;
   
   private String bounceContent;

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   public int getNewsletterId() {
      return newsletterId;
   }

   public void setNewsletterId(int newsletterId) {
      this.newsletterId = newsletterId;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getNewsLetterTitle() {
      return newsLetterTitle;
   }

   public void setNewsLetterTitle(String newsLetterTitle) {
      this.newsLetterTitle = newsLetterTitle;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Date getBounceDate() {
      return bounceDate;
   }

   public void setBounceDate(Date bounceDate) {
      this.bounceDate = bounceDate;
   }

   public String getBounceContent() {
      return bounceContent;
   }

   public void setBounceContent(String bounceContent) {
      this.bounceContent = bounceContent;
   }
   
}
