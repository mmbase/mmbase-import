package com.finalist.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *	Temporary Community interface implementation 
 */

public class CommunityManager {

	// A note to Menno: The community must be able to handle multiple
	// preferences with the same key. For example, there can be multiple
	// preferences with key "newslettertheme", but each key has a different
	// value attached to it.

	// This method returns a list of usernames (identifiers) who have the given
	// key/value pair in their preferences. For example, when given the
	// key/value pair of "newsletter" / "949" this method returns all users that
	// have this preference set.

	private static Map<String, Map<String, String>> prefs = new HashMap<String, Map<String, String>>();

	public static List<String> getUsersWithPreference(String key, String value) {
		List<String> users = new ArrayList<String>();
		for (int i = 0; i < prefs.size(); i++) {
			Map userPrefs = prefs.get(i);
			if (userPrefs.containsKey(key)) {
				if (((String) userPrefs.get(key)).equals(value)) {
					String userName = "Pino" + i;
					users.add(userName);
				}
			}
		}
		return users;
	}

	// This method retuns the value for the given username and key from the
	// users preferences
	public static String getUserPreference(String userName, String key) {
		Map userPrefs = prefs.get(userName);
		return((String) userPrefs.get(key));		
	}

	// This method can be used to store a preference for a user. It takes the
	// username and a key/value pair
	public static void setUserPreference(String userName, String key, String value) {
		Map userPrefs = prefs.get(userName);
		userPrefs.put(key, value);		
	}

	// This method returns a list of values for the specified user and key. This
	// method can be compared to the getParameterValues method of the
	// HTTPRequest For example, when given the userName "Jan" en the key
	// "newsletterTheme" it can return a list containing all occurences of
	// NEWSLETTERTHEME IN THE USERS PREFERENCES
	public static List<String> getUserPreferenceValues(String userName, String key) {
		List<String> values = new ArrayList<String>();
		return (values);
	}

	// I do not yet need this one, but may be handy. This method receives a list
	// or map of key/value paires and sets them for the given user.
	public static void setUserPreferenceValues(String userName, Map<String, String> preferences) {

	}

	// This method removes all occurrences of the given key from the givenusers
	// preference listing For example, if there is a key "newslettertheme" with
	// 5 related values, this method removes all of those values and the key
	// when given the key "newslettertheme"
	public static void removeUserPreference(String userName, String key) {

	}

	// This method removes the given key/value combination from the given users
	// preferencelist
	public static void removeUserPreference(String userName, String key, String value) {

	}

	// This methods allows a module or portlet to check if a given user has the
	// requested permission. The clinet does not care about the role or group of
	// the user.
	public static boolean hasPermission(String userName, String permission) {
		return (true); // temp
	}
}
