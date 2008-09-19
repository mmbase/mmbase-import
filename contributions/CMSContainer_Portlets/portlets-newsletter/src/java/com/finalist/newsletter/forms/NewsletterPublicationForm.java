package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

public class NewsletterPublicationForm extends ActionForm {
	private String title ;
	private String description;
	private String subject;
	private String intro;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
}
