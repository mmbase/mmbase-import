package com.finalist.preferences.domain;

/**
 * @author
 */
public class UserProfile implements java.io.Serializable {

   private static final long serialVersionUID = 1L;

   private String action;

   private String email;

   private String passwordText;

   private String passwordConfirmation;

   private String account;

   private String firstName;

   private String prefix;

   private String lastName;

   private String company;

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getAccount() {
      return account;
   }

   public void setAccount(String account) {
      this.account = account;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getCompany() {
      return company;
   }

   public void setCompany(String company) {
      this.company = company;
   }

   public String getPasswordText() {
      return passwordText;
   }

   public void setPasswordText(String passwordText) {
      this.passwordText = passwordText;
   }

   public String getPasswordConfirmation() {
      return passwordConfirmation;
   }

   public void setPasswordConfirmation(String passwordConfirmation) {
      this.passwordConfirmation = passwordConfirmation;
   }

   public void clear() {
      action = null;
      email = null;
      passwordText = null;
      passwordConfirmation = null;
      account = null;
      firstName = null;
      prefix = null;
      lastName = null;
      company = null;
   }

}
