package com.finalist.newsletter.domain;

import com.finalist.cmsc.navigation.NavigationUtil;

import java.util.Date;

public class Publication {

   private int id;
   private Date deliverTime;
   private STATUS status = STATUS.INITIAL;
   private String url;
   private Newsletter newsletter;


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

   public String getUrl() {
//      String newsletterPath = NavigationUtil.getPathToRootString(publicationNode, true);
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
