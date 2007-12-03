package com.finalist.cmsc.services.community;

/* Security & JAAS imports */
import java.security.Principal;

/**
 * <p>
 * Basic implementation of the Principal class. By implementing our own
 * Principal for our application, we can more easily add and remove instances of
 * our principals in the authenticated Subject during the login and logout
 * process.
 * 
 * @see java.security.Principal
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public class HibernatePrincipal implements Principal, java.io.Serializable {

   private String name;


   /**
    * Create a <code>HibernatePrincipal</code> with no user name.
    */
   public HibernatePrincipal() {
      name = "";
   }


   /**
    * Create a <code>HibernatePrincipal</code> using a <code>String</code>
    * representation of the user name.
    * <p>
    * 
    * @param name
    *           the user identification number (UID) for this user.
    */
   public HibernatePrincipal(String newName) {
      name = newName;
   }


   /**
    * Compares the specified Object with this <code>HibernatePrincipal</code> for
    * equality. Returns true if the given object is also a
    * <code>HibernatePrincipal</code> and the two HibernatePrincipals have the same
    * name.
    * <p>
    * 
    * @param o
    *           Object to be compared for equality with this
    *           <code>HibernatePrincipal</code>.
    * @return true if the specified Object is equal equal to this
    *         <code>HibernatePrincipal</code>.
    */
   public boolean equals(Object o) {
      if (!(o instanceof HibernatePrincipal)) {
         return false;
      }
      return name.equals(((HibernatePrincipal) o).name);
   }


   /**
    * Return a hash code for this <code>HibernatePrincipal</code>.
    * <p>
    * 
    * @return a hash code for this <code>HibernatePrincipal</code>.
    */
   public int hashCode() {
      return (name.hashCode());
   }


   /**
    * Return a string representation of this <code>HibernatePrincipal</code>.
    * <p>
    * 
    * @return a string representation of this <code>HibernatePrincipal</code>.
    */
   public String toString() {
      return name;
   }


   /**
    * Return the user name for this <code>HibernatePrincipal</code>.
    * <p>
    * 
    * @return the user name for this <code>HibernatePrincipal</code>
    */
   public String getName() {
      return name;
   }
}
