package com.finalist.newsletter.domain;

import java.util.Date;

public class StatisticResult {

   public enum HANDLE {
      BOUNCE, ACTIVE, INACTIVE, REMOVE, POST
   }

   private int newsletterId = 0;

   private int userId = 0;

   private String name;

   private int post = 0;

   private int bounches = 0;

   private int subscribe = 0;

   private int unsubscribe = 0;

   private int removed = 0;

   private Date logdate;

   private String showingdate;

   public String getShowingdate() {

      return showingdate;
   }

   public void setShowingdate(String showingdate) {

      this.showingdate = showingdate;
   }

   public int getNewsletterId() {

      return newsletterId;
   }

   public void setNewsletterId(int newsletterId) {

      this.newsletterId = newsletterId;
   }

   public String getName() {

      return name;
   }

   public void setName(String name) {

      this.name = name;
   }

   public int getPost() {

      return post;
   }

   public void setPost(int post) {

      this.post = post;
   }

   public int getBounches() {

      return bounches;
   }

   public void setBounches(int bounches) {

      this.bounches = bounches;
   }

   public int getSubscribe() {

      return subscribe;
   }

   public void setSubscribe(int subscribe) {

      this.subscribe = subscribe;
   }

   public int getUnsubscribe() {

      return unsubscribe;
   }

   public void setUnsubscribe(int unsubscribe) {

      this.unsubscribe = unsubscribe;
   }

   public int getRemoved() {

      return removed;
   }

   public void setRemoved(int removed) {

      this.removed = removed;
   }

   public Date getLogdate() {

      return logdate;
   }

   public void setLogdate(Date logdate) {

      this.logdate = logdate;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }


}
