/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.authorization;

/**
 * @author Edwin van der Elst
 * Date :Oct 13, 2003
 * 
 */
public class UserRole {
   int rol ; // Rol. @see Roles
   boolean inherited; // inherited from super-rubriek?
   
   /**
    * @param rol
    * @param inherited
    */
   public UserRole(int rol, boolean inherited) {
      super();
      this.rol = rol;
      this.inherited = inherited;
   }
   
   /**
    * @return
    */
   public boolean isInherited() {
      return inherited;
   }

   /**
    * @param inherited
    */
   public void setInherited(boolean inherited) {
      this.inherited = inherited;
   }

   /**
    * @return
    */
   public int getRol() {
      return rol;
   }

   /**
    * @param rol
    */
   public void setRol(int rol) {
      this.rol = rol;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      // TODO Auto-generated method stub
      return ""+rol+" "+inherited;
   }

}
