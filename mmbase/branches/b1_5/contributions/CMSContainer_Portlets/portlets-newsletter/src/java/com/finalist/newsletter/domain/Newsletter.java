package com.finalist.newsletter.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Newsletter {

   private int id;

   private String format;

   private String status;

   private Date interval;

   private Set<Term> terms = new HashSet<Term>();

   private String title;

   private String replyName;

   private String replyAddress;

   private String fromName;

   private String fromAddress;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getFormat() {
      return format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public Date getInterval() {
      return interval;
   }

   public void setInterval(Date interval) {
      this.interval = interval;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getReplyName() {
      return replyName;
   }

   public void setReplyName(String replyName) {
      this.replyName = replyName;
   }

   public String getReplyAddress() {
      return replyAddress;
   }

   public void setReplyAddress(String replyAddress) {
      this.replyAddress = replyAddress;
   }

   public String getFromName() {
      return fromName;
   }

   public void setFromName(String fromName) {
      this.fromName = fromName;
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public Set<Term> getTerms() {
      return terms;
   }

   public void setTerms(Set<Term> terms) {
      this.terms = terms;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Newsletter that = (Newsletter) o;

      if (id != that.id) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return id;
   }
}
