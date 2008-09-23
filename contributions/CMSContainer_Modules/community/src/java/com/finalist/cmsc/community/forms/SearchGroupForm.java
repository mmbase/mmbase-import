package com.finalist.cmsc.community.forms;

import org.apache.struts.action.ActionForm;

public class SearchGroupForm extends ActionForm {

   private String groupname;

   private String member;

   public String getMember() {
      return member;
   }

   public void setMember(String member) {
      this.member = member;
   }

   public String getGroupname() {
      return groupname;
   }

   public void setGroupname(String groupname) {
      this.groupname = groupname;
   }

   public String[] processNames(String temp) {
      String[] userUtil = temp.split(",");
      return userUtil;
   }
}
