package com.finalist.cmsc.services.community.person;

public enum RegisterStatus {
   ACTIVE("Active"),
   UNCONFIRMED("Unconfirmed"),
   BLOCKED("Blocked");
   private String name;

   private RegisterStatus(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
