package com.finalist.newsletter.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

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
   private String newesletterId;
   private Date pausedTill;
   private String resumeDate;
   public void setResumeDate(String s) {
      this.resumeDate = s;
   }

   public String getResumeDate() {
      return resumeDate;
   }

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

   public String getNewesletterId() {
      if(null!=newsletter){
         return Integer.toString(newsletter.getId());  
      }
      return newesletterId;
   }

   public void setNewesletterId(String newesletterId) {
      this.newesletterId = newesletterId;
   }

   public Date getPausedTill() {
      return pausedTill;
   }

   public void setPausedTill(Date pausedTill) {
      this.pausedTill = pausedTill;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Subscription that = (Subscription) o;

      if (newesletterId != null ? !newesletterId.equals(that.newesletterId) : that.newesletterId != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return 31 * (newesletterId != null ? newesletterId.hashCode() : 0);
   }
}
