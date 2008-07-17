package com.finalist.cmsc.security.forms;

import java.util.HashMap;
import java.util.Map;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseForm;

/**
 * Form bean for the RolesForm page.
 * 
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class RolesForm extends MMBaseForm {

   private RolesInfo info;

   private String name;
   private String description;

   private Map<Integer, UserRole> roles = new HashMap<Integer, UserRole>();


   public void addRequestRoles(Map<Integer, UserRole> requestRoles) {
      roles.putAll(requestRoles);
   }


   public void clear() {
      roles.clear();
      name = "";
      description = "";
      setId(-1);
   }


   public RolesInfo getRolesInfo() {
      return info;
   }


   public UserRole getRole(int number) {
      return roles.get(new Integer(number));
   }


   public void addRole(int number, UserRole role) {
      roles.put(new Integer(number), role);
   }


   public Map<Integer, UserRole> getRoles() {
      return roles;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setRolesInfo(RolesInfo info) {
      this.info = info;
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }

}