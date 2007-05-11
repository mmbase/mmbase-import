/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.packaging;

/**
 * Person class, used as a container for the people related info when building
 * a package/bundle. It keeps track of things like initiators, maintainers, supporters
 * and the like. Doesn't have any real functional task.
 *
 * @author Daniel Ockeloen
 * @since MMBase-1.7
 */
public class Person {
   private String name;
   private String company;
   private String reason;
   private String mailto;
    

   /**
   * Get the name if defined.
   *
   * @return name or null if not defined
   */
   public String getName() {
      return name;
   }

   /**
   * Get the mailto if defined.
   * @return mailto or null if not defined
   *
   */
   public String getMailto() {
      return mailto;
   }

   /**
   * Get the reason if defined. Reason can be things like bugfixes, sponsering
   *
   * @return reason or null if not defined
   */
   public String getReason() {
      return reason;
   }


   /**
   * Get the company if defined.
   *
   * @return company or null if not defined
   */
   public String getCompany() {
       return company;
   }

   /**
   * set the name
   */
   public void setName(String name) {
       this.name = name;
   }

   /**
   * set the company
   */
   public void setCompany(String company) {
       this.company = company;
   }

   /**
   * set the reason
   */
   public void setReason(String reason) {
       this.reason = reason;
   }

   /**
   * set the mailto
   */
   public void setMailto(String mailto) {
       this.mailto = mailto;
   }
}
