/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public final class Role {

   public static final Role NONE = new Role(0, "none"); // DENY all rights
   public static final Role WRITER = new Role(1, "writer");
   public static final Role EDITOR = new Role(2, "editor");
   public static final Role CHIEFEDITOR = new Role(3, "chiefeditor");
   public static final Role WEBMASTER = new Role(100, "webmaster"); // ALL

   private static List<Role> roles = new ArrayList<Role>();
   static {
      roles.add(NONE);
      roles.add(WRITER);
      roles.add(EDITOR);
      roles.add(CHIEFEDITOR);
      roles.add(WEBMASTER);
   }

   private int id;
   private String name;


   private Role(int id, String name) {
      this.id = id;
      this.name = name;
   }


   public int getId() {
      return id;
   }


   public String getName() {
      return name;
   }


   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      }
      if (other == this) {
         return true;
      }
      if (other instanceof Role) {
         return this.id == ((Role) other).id;
      }
      return false;
   }


   @Override
   public int hashCode() {
      return id;
   }


   public static Role getRole(int id) {
      for (int i = 0; i < roles.size(); i++) {
         Role role = roles.get(i);
         if (role.id == id) {
            return role;
         }
      }
      throw new IllegalArgumentException("Role with id " + id + " does not exist");
   }


   public static Role getRole(String name) {
      if (StringUtils.isEmpty(name)) {
         throw new IllegalArgumentException("Role with empty name does not exist");
      }
      for (int i = 0; i < roles.size(); i++) {
         Role role = roles.get(i);
         if (role.name.equals(name)) {
            return role;
         }
      }
      throw new IllegalArgumentException("Role with name " + name + " does not exist");
   }


   @Override
   public String toString() {
      return name;
   }

}
