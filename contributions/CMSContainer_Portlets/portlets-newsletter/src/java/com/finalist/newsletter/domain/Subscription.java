package com.finalist.newsletter.domain;

import com.finalist.cmsc.services.community.person.Person;

public class Subscription{

   private Person subscriber;
   private String mimeType;
   private int id;
   private Newsletter newsletter;

   public Person getSubscriber() {
      return subscriber;
   }

   public void setSubscriber(Person subscriber) {
      this.subscriber = subscriber;
   }

   public String getMimeType() {
      return mimeType;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Newsletter getNewsletter() {
      return newsletter;
   }

   public void setNewsletter(Newsletter newsletter) {
      this.newsletter = newsletter;
   }
}
