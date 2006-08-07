package com.finalist.cmsc.portalImpl.security;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class LoginSession {

	private boolean authenticated;

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
