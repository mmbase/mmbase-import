package com.finalist.newsletter.domain;

import java.util.HashSet;
import java.util.Set;

import com.finalist.cmsc.services.community.person.Person;

public class Subscription {

   private Person subscriber;

   private String mimeType;
   private STATUS status = STATUS.INACTIVE;

   private Set<Term> terms = new HashSet<Term>();
   private Newsletter newsletter;
   private String subscriberId;
   private String email;
   private int id;
   private String url;

   public enum STATUS {
      ACTIVE, PAUSED, INACTIVE
   }


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


   public Set<Term> getTerms() {
      return terms;
   }

   public void setTerms(Set<Term> terms) {
      this.terms = terms;
   }

   public void setNewsletter(Newsletter newsletter) {
      this.newsletter = newsletter;
   }

   public STATUS getStatus() {
      return status;
   }

   public void setStatus(STATUS status) {
      this.status = status;
   }

   public String getSubscriberId() {
      return subscriberId;
   }

   public void setSubscriberId(String subscriberId) {
      this.subscriberId = subscriberId;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }
}
