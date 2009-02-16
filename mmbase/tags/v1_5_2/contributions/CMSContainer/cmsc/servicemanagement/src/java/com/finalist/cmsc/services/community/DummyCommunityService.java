package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

/**
 * DummyCommunityService, this is a CMSc service class.
 * This class contains dummy methods that stand in the real uses service class.
 *
 * @author menno menninga / Remco Bos
 */
public class DummyCommunityService extends CommunityService {

	@Override public void login(String userName, String password) {}
	@Override public void logout() {}
	@Override public boolean isAuthenticated() { return false; }
	@Override public String getAuthenticatedUser() { return null; }
	@Override public List<String> getAuthorities() { return null; }
	@Override public boolean hasAuthority(String authority) { return false; }
	@Override public List<String> getPreferenceValues(String module, String userId, String key) { return null; }

	@Override public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value) { return null; }
	@Override public void createPreference(String module, String userId, String key, List<String> values) {}
	@Override public void removePreferences(String module, String userId, String key) {}
	@Override public Map<String, Map<String, String>> getUserProperty(String userName) { return null; }
   @Override boolean sendPassword(String email, String senderName, String senderEmail, String emailSubject, String emailBody) { return true; }
}
