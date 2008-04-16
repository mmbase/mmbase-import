package com.finalist.cmsc.resources.forms;

@SuppressWarnings("serial")
public class NewletterPublicationForm extends SearchForm {
	
	private String title;
	private String description;
	private String intro;
	private String subject;
	
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
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
