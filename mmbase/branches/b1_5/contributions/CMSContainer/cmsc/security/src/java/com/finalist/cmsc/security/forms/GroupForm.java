/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.struts.MMBaseForm;

@SuppressWarnings("serial")
public class GroupForm extends MMBaseForm {

   private String name;
   private String description;

   private String[] members = new String[] {};
   private String[] users = new String[] {};


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String[] getMembers() {
      return members;
   }


   public void setMembers(String[] members) {
      this.members = members;
   }


   public String[] getUsers() {
      return users;
   }


   public void setUsers(String[] users) {
      this.users = users;
   }


   @Override
   public void reset(ActionMapping mapping, HttpServletRequest request) {
      setName(null);
      setDescription(null);
      setMembers(new String[] {});
      setUsers(new String[] {});
   }

}
