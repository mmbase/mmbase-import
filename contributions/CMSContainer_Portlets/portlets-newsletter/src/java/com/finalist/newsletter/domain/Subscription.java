package com.finalist.newsletter.domain;

import com.finalist.cmsc.services.community.person.Person;

public class Subscription extends Newsletter {

   private Person subscriber;
   private String mimeType;
   private String fromAddress;
   private String fromName;
   private String replyAddress;
   private String replyname;
   private String title;
   private int id;

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
      return null;  //To change body of created methods use File | Settings | File Templates.
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public String getFromName() {
      return fromName;
   }

   public void setFromName(String fromName) {
      this.fromName = fromName;
   }

   public String getReplyAddress() {
      return replyAddress;
   }

   public void setReplyAddress(String replyAddress) {
      this.replyAddress = replyAddress;
   }

   public String getReplyname() {
      return replyname;
   }

   public void setReplyname(String replyname) {
      this.replyname = replyname;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}
