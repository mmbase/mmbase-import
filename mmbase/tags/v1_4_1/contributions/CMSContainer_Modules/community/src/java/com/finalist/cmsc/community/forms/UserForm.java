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

	private String password;

	private String passwordConfirmation;

	private String account;

	private String voornaam;

	private String tussenVoegsels;

	private String achterNaam;

	private String bedrijf;

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

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getTussenVoegsels() {
		return tussenVoegsels;
	}

	public void setTussenVoegsels(String tussenVoegsels) {
		this.tussenVoegsels = tussenVoegsels;
	}

	public String getAchterNaam() {
		return achterNaam;
	}

	public void setAchterNaam(String achterNaam) {
		this.achterNaam = achterNaam;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(String bedrijf) {
		this.bedrijf = bedrijf;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		if (password.equals("")) {
			actionErrors.add("password", new ActionMessage("password.empty"));
		}
		if (passwordConfirmation.equals("")) {
			actionErrors.add("passwordConfirmation", new ActionMessage("passwordConfirmation.empty"));
		}
		if (!password.equals("") && !passwordConfirmation.equals("") && !password.equals(passwordConfirmation)) {
			actionErrors.add("password", new ActionMessage("passwords.not_equal"));
		}
		return actionErrors;
	}

}
