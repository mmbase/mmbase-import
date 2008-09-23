package com.finalist.cmsc.services.community.domain;

public class PreferenceVO {

   private String id;

   private String userId;

   private String authenticationId;

   private String module;

   private String key;

   private String value;

   private String method;

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getModule() {
      return module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getMethod() {
      return method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getAuthenticationId() {
      return authenticationId;
   }

   public void setAuthenticationId(String authenticationId) {
      this.authenticationId = authenticationId;
   }

}
