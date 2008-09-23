/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Wouter Heijke
 */
public class GroupForm extends ActionForm {

   protected static final String ACTION_ADD = "add";

   protected static final String ACTION_EDIT = "edit";

   private static final long serialVersionUID = 1L;

   private String action;

   private String name;

   private String[] members = new String[] {};

   private String[] users = new String[] {};

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
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
      setMembers(new String[] {});
      setUsers(new String[] {});
   }

}
