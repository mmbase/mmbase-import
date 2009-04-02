package com.finalist.cmsc.community.forms;

import org.apache.struts.action.ActionForm;

public class SearchForm extends ActionForm {

   private String fullName;

   private String userName;

   private String emailAddr;

   private String groups;

   private String[] chk_;

   private String[] chk_group;

   private String groupName;

   private String group;

   private String option;

   public String getOption() {
      return option;
   }

   public void setOption(String option) {
      this.option = option;
   }

   public String getGroup() {
      return group;
   }

   public void setGroup(String group) {
      this.group = group;
   }

   public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public String[] getChk_() {
      return chk_;
   }

   public void setChk_(String[] chk_) {
      this.chk_ = chk_;
   }

   public String getEmailAddr() {
      return emailAddr;
   }

   public void setEmailAddr(String emailAddr) {
      this.emailAddr = emailAddr;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getemailAddr() {
      return emailAddr;
   }

   public void setemailAddr(String emailAddr) {
      this.emailAddr = emailAddr;
   }

   public String getGroups() {
      return groups;
   }

   public void setGroups(String groups) {
      this.groups = groups;
   }

   public String[] getChk_group() {
      return chk_group;
   }

   public void setChk_group(String[] chk_group) {
      this.chk_group = chk_group;
   }

}
