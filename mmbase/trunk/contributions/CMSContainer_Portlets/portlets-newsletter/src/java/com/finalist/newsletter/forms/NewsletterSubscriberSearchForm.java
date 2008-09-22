package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

public class NewsletterSubscriberSearchForm extends ActionForm {
   private String fullname = null;
   private String username = null;
   private String email = null;
	private String term = null;
   
   public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
}
