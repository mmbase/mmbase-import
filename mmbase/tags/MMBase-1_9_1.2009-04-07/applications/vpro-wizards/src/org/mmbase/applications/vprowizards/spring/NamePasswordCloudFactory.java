/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;

public class NamePasswordCloudFactory implements CloudFactory {
	private String username = null;
	private String password = null;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Cloud getCloud() {
		if (StringUtils.isEmpty(username)) {
			throw new RuntimeException("username is empty");
		}
		if (StringUtils.isEmpty(password)) {
			throw new RuntimeException("password is empty");
		}
		Map<String, String> credentials = new HashMap<String, String>();
		credentials.put("username", username);
		credentials.put("password", password);
		//TODO: how to call getCloud ???
		return LocalContext.getCloudContext().getCloud("een", "twee", credentials );
	}

	public Transaction createTransaction(HttpServletRequest request) {
		//TODO: what is the parameter for?
		return getCloud().getTransaction("transaction");
	}

}
