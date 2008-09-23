package com.finalist.newsletter.domain;

public class Term {
   private int id;
   private String name;
   private boolean subscription = false;



   public boolean isSubscription() {
      return subscription;
   }

   public void setSubscription(boolean subscription) {
      this.subscription = subscription;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Term) {
         Term term = (Term) obj;
         if(this.name.equals(term.name)){
            return true;
         }
      }
      return false;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

}
