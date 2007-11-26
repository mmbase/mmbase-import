package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("serial")
public class User extends NodeBean {

   private String surname;
   private String firstname;
   private String prefix;
   private String emailaddress;
   private String department;
   private String function;
   private String note;
   private String website;
   private boolean emailsignal;
   private String account;
   private String password;


   public String getAccount() {
      return account;
   }


   public void setAccount(String account) {
      this.account = account;
   }


   public String getDepartment() {
      return department;
   }


   public void setDepartment(String department) {
      this.department = department;
   }


   public String getEmailaddress() {
      return emailaddress;
   }


   public void setEmailaddress(String emailaddress) {
      this.emailaddress = emailaddress;
   }


   public boolean isEmailsignal() {
      return emailsignal;
   }


   public void setEmailsignal(boolean emailsignal) {
      this.emailsignal = emailsignal;
   }


   public String getFirstname() {
      return firstname;
   }


   public void setFirstname(String firstname) {
      this.firstname = firstname;
   }


   public String getFunction() {
      return function;
   }


   public void setFunction(String function) {
      this.function = function;
   }


   public String getNote() {
      return note;
   }


   public void setNote(String note) {
      this.note = note;
   }


   public String getPassword() {
      return password;
   }


   public void setPassword(String password) {
      this.password = password;
   }


   public String getPrefix() {
      return prefix;
   }


   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }


   public String getSurname() {
      return surname;
   }


   public void setSurname(String surname) {
      this.surname = surname;
   }


   public String getWebsite() {
      return website;
   }


   public void setWebsite(String website) {
      this.website = website;
   }

}
