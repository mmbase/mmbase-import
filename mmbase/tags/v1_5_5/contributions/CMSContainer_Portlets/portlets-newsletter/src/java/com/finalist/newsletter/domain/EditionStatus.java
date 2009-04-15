package com.finalist.newsletter.domain;

public enum EditionStatus {
   INITIAL("concept edition"),
   FROZEN("Frozen"),
   APPROVED("Approved"),
   BEING_SENT("Being sent"),
   IS_SENT("is sent");
   
   
   private String value;
   
   EditionStatus(String value) {
      this.value = value;
   }
   
   public String value() {
      return value;
   }
}
