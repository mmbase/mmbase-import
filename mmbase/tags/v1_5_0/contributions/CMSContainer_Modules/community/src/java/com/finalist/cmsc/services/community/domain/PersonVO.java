package com.finalist.cmsc.services.community.domain;

import org.apache.struts.action.ActionForm;

public class PersonVO extends ActionForm {
   private String fullname;
   private String username;
   private String email;
   private String groups;
   private Long authId;
   private boolean inGroup;
   private String active;

   public String getActive() {
      return active;
   }

   public void setActive(String active) {
      this.active = active;
   }

   public String getFullname() {
      return fullname;
   }

   public void setFullname(String fullname) {
      this.fullname = fullname;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getGroups() {
      return groups;
   }

   public void setGroups(String groups) {
      this.groups = groups;
   }

   public Long getAuthId() {
      return authId;
   }

   public void setAuthId(Long authId) {
      this.authId = authId;
   }

   public boolean isInGroup() {
      return inGroup;
   }

   public void setInGroup(boolean inGroup) {
      this.inGroup = inGroup;
   }
}
