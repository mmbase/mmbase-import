package com.finalist.cmsc.services.community.domain;

import org.apache.commons.lang.StringUtils;

public class PreferenceVO {

   private String id;

   private String userId;

   private String authenticationId;

   private String module;

   private String key;

   private String value;

   private String method;

   public PreferenceVO() {
      super();
   }

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

   public PreferenceVO(Long id, String userId, Long authenticationId, String module, String key, String value){
      super();
      this.id = String.valueOf(id);
      this.userId = userId;
      this.authenticationId = String.valueOf(authenticationId);
      this.module = module;
      this.key = key;
      this.value = value;
      
   }
   public void clean(){
      this.authenticationId = null;
      this.id = null;
      this.key = null;
      this.method = null;
      this.module = null;
      this.userId = null;
      this.value = null;
   }

}
