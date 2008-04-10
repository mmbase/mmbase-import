package com.finalist.newsletter.domain;

public class StatisticResult {
   private int newsletterId = 0;
   private String name;
   private int post = 0;

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


}
