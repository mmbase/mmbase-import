package com.finalist.cmsc.egemmail.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class EgemSearchForm extends ActionForm {

	private String title;
	private String keywords;
	private String author;
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
