package com.finalist.cmsc.services.community.person;

public enum RegisterStatus {
   ACTIVE("active"),
   UNCONFIRMED("UNCONFIRMED");
   private String name;

   private RegisterStatus(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
