/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Remco Bos
 */
public class UserForm extends ActionForm {

   protected static final String ACTION_ADD = "add";

   protected static final String ACTION_EDIT = "edit";

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

   public void setBedrijf(String company) {
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

   public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
      ActionErrors actionErrors = new ActionErrors();
      if (account.equals("")) {
         actionErrors.add("account", new ActionMessage("userform.account.empty"));
      }
      if (email.equals("")) {
         actionErrors.add("email", new ActionMessage("userform.email.empty"));
      }
      if (!email.equals("") && !email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*") && !email.equalsIgnoreCase("admin")) {
         actionErrors.add("email", new ActionMessage("userform.email.not.regular"));
      }
      if (this.getAction().equalsIgnoreCase(ACTION_ADD)) {
         validatePassword(actionErrors);
      } else {
         if (this.getAction().equalsIgnoreCase(ACTION_EDIT)) {
            if (StringUtils.isNotBlank(passwordText) || StringUtils.isNotBlank(passwordConfirmation)) {
               validatePassword(actionErrors);
            }
         }
      }
      return actionErrors;
   }

   public void validatePassword(ActionErrors actionErrors) {
      // Only check this if an user is added
      if (StringUtils.isBlank(passwordText)) {
         actionErrors.add("password", new ActionMessage("userform.password.empty"));
      }
      if (StringUtils.isBlank(passwordConfirmation)) {
         actionErrors.add("passwordConfirmation", new ActionMessage("userform.password.empty"));
      }
      if (StringUtils.isNotBlank(passwordText) && StringUtils.isNotBlank(passwordConfirmation)
            && !passwordText.equals(passwordConfirmation)) {
         actionErrors.add("password", new ActionMessage("userform.passwords.not_equal"));
      }
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
