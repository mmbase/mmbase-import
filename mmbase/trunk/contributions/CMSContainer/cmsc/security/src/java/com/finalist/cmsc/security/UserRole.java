/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.security;

public class UserRole {

   Role role; // Rol. @see Roles
   boolean inherited; // inherited from super?


   public UserRole(int role, boolean inherited) {
      this(Role.getRole(role), inherited);
   }


   public UserRole(String role, boolean inherited) {
      this(Role.getRole(role), inherited);
   }


   public UserRole(Role role, boolean inherited) {
      this.role = role;
      this.inherited = inherited;
   }


   public boolean isInherited() {
      return inherited;
   }


   public Role getRole() {
      return role;
   }


   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return role + " " + inherited;
   }

}
