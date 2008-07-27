package com.finalist.cmsc.community.forms;

import org.apache.struts.action.ActionForm;

import com.finalist.cmsc.struts.PagerForm;

public class PersonForShow extends ActionForm{
	private String fullname;
	private String username;
	private String email;
	private String groups;
	private Long authId;

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

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public Long getAuthId() {
		return authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	
}
