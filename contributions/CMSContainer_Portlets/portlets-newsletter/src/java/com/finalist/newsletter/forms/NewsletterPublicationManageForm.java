package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

public class NewsletterPublicationManageForm extends ActionForm {
	private String title;
	private String subject;
	private String period;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
}
