package com.finalist.newsletter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Newsletter {

   private int id;
   private String format;
   private String status;
   private Date interval;
   private List<Tag> tags = new ArrayList<Tag>();
   private String title;

   private int number;
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

   public int getNumber() {
      return number;
   }

   public void setNumber(int number) {
      this.number = number;
   }

   public List<Tag> getTags() {
      return tags;
   }

   public void setTags(List<Tag> tags) {
      this.tags = tags;
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
}
