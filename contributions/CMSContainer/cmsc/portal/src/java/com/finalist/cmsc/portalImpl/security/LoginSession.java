package com.finalist.cmsc.portalImpl.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class LoginSession {
	private static Log log = LogFactory.getLog(LoginSession.class);

	private boolean authenticated;

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
