package com.finalist.cmsc.security.forms;

import java.util.HashMap;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;

import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the ChangePasswordForm page.
 *
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class ChangeLanguageForm extends ActionForm {

   private String language;

	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
}