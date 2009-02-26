package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

/**
 * using for searching newsletter subscriber by full name, user name, email, term
 *
 * @author Lisa
 */
public class NewsletterSubscriberSearchForm extends ActionForm {
   private String fullname = null;
   private String username = null;
   private String email = null;
   private String term = null;

   /**
    * @return the fullname
    */
   public String getFullname() {
      return fullname;
   }

   /**
    * @param fullname the fullname to set
    */
   public void setFullname(String fullname) {
      this.fullname = fullname;
   }

   /**
    * @return the username
    */
   public String getUsername() {
      return username;
   }

   /**
    * @param username the username to set
    */
   public void setUsername(String username) {
      this.username = username;
   }

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email the email to set
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return the term
    */
   public String getTerm() {
      return term;
   }

   /**
    * @param term the term to set
    */
   public void setTerm(String term) {
      this.term = term;
   }
}
