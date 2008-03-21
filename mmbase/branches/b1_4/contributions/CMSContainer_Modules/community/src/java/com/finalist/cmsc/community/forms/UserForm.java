/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Remco Bos
 */
public class UserForm extends ActionForm {

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
		if (email.equals("")) {
			actionErrors.add("email", new ActionMessage("email.empty"));
		}
		if (passwordText.equals("")) {
			actionErrors.add("password", new ActionMessage("password.empty"));
		}
		if (passwordConfirmation.equals("")) {
			actionErrors.add("passwordConfirmation", new ActionMessage("passwordConfirmation.empty"));
		}
		if (!passwordText.equals("") && !passwordConfirmation.equals("") && !passwordText.equals(passwordConfirmation)) {
			actionErrors.add("password", new ActionMessage("passwords.not_equal"));
		}
		return actionErrors;
	}

}
