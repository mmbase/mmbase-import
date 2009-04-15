package com.finalist.newsletter.domain;

import java.util.Date;

public class Publication {

   private int id;
   private Date deliverTime;
   private STATUS status = STATUS.INITIAL;
   private Newsletter newsletter;
   private int newsletterId;
   private String url;
   private String subject;
   private String lastmodifier;
   private String description;
   private String intro;
   private Date lastmodifieddate;
   private Date publishdate;
   private Date sendtime;
   private int subscriptions;
   private int bounced;
   private String process_status;
   private String static_html;

   private String title;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public enum STATUS {
      DELIVERED, READY, INITIAL
   }

   public Date getDeliverTime() {
      return deliverTime;
   }

   public void setDeliverTime(Date deliverTime) {
      this.deliverTime = deliverTime;
   }

   public Newsletter getNewsletter() {
      return newsletter;
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

   public int getNewsletterId() {
      if (null != this.newsletter) {
         return newsletter.getId();
      }
      return newsletterId;
   }

   public void setNewsletterId(int newsletterId) {

      this.newsletterId = newsletterId;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getTitle() {
      if (null != this.newsletter) {
         return newsletter.getTitle();
      }
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Publication that = (Publication) o;

      return id == that.id;

   }

   public int hashCode() {
      return this.id;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getIntro() {
      return intro;
   }

   public void setIntro(String intro) {
      this.intro = intro;
   }

   public String getLastmodifier() {
      return lastmodifier;
   }

   public void setLastmodifier(String lastmodifier) {
      this.lastmodifier = lastmodifier;
   }

   public Date getLastmodifieddate() {
      return lastmodifieddate;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setLastmodifieddate(Date lastmodifieddate) {
      this.lastmodifieddate = lastmodifieddate;
   }

   public Date getPublishdate() {
      return publishdate;
   }

   public void setPublishdate(Date publishdate) {
      this.publishdate = publishdate;
   }
   
   public Date getSendtime() {
      return sendtime;
   }
   
   public void setSendtime(Date sendtime) {
      this.sendtime = sendtime;
   }

   public int getSubscriptions() {
      return subscriptions;
   }

   public void setSubscriptions(int subscriptions) {
      this.subscriptions = subscriptions;
   }

   public int getBounced() {
      return bounced;
   }

   public void setBounced(int bounced) {
      this.bounced = bounced;
   }

   public String getProcess_status() {
      return process_status;
   }

   public void setProcess_status(String process_status) {
      this.process_status = process_status;
   }

   public String getStatic_html() {
      return static_html;
   }

   public void setStatic_html(String static_html) {
      this.static_html = static_html;
   }

}
