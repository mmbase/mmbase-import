package com.finalist.newsletter.domain;

import java.util.Date;

public class Publication {

   private int id;
   private int newsletterId;
   private Date deliverTime;
   private STATUS status;


   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public enum STATUS {
      DELIVERED, READY
   }


   public Date getDeliverTime() {
      return deliverTime;
   }

   public void setDeliverTime(Date deliverTime) {
      this.deliverTime = deliverTime;
   }

   public int getNewsletterId() {
      return newsletterId;
   }

   public void setNewsletterId(int newsletterId) {
      this.newsletterId = newsletterId;
   }

   public STATUS getStatus() {
      return status;
   }

   public void setStatus(STATUS status) {
      this.status = status;
   }
}
