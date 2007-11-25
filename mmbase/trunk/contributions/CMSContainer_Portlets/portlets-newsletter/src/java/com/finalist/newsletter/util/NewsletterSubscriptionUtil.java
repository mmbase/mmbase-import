package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import com.finalist.community.CommunityManager;

public abstract class NewsletterSubscriptionUtil {

	public static final String NEWSLETTER_THEME = "newslettertheme";
	public static final String PREFERRED_MIMETYPE = "preferredmimetype";

	public static final String MIMETYPE_HTML = "html";
	public static final String MIMETYPE_PLAIN = "plain";
	public static final String MIMETIPE_DEFAULT = MIMETYPE_HTML;
	public static final String USERNAME = "username";

	public static final String SUBSCRIPTION_STATUS_KEY = "newslettersubscriptionstatus";
	public static final String SUBSCRIPTION_STATUS_ACTIVE = "active";
	public static final String SUBSCRIPTION_STATUS_INACTIVE = "inactive";

	// public static final List AVAILABLE_MIMETYPES = new ArrayList<String>()
	// {MIMETYPE_HTML, MIMETYPE_PLAIN;;

	public static List<String> getUserSubscribedThemes(String userName, String newsletterNumber) {
		List<String> themeList = CommunityManager.getUserPreferenceValues(userName, "newslettertheme");
		return (themeList);
	}

	public static List<String> getUserSubscribedThemes(String userName) {
		List<String> themeList = CommunityManager.getUserPreferenceValues(userName, "newslettertheme");
		return (themeList);
	}
	
	
	public static String getPreferredMimeType(String userName) {
		String mimeType = CommunityManager.getUserPreference(userName, PREFERRED_MIMETYPE);
		// if (AVAILABLE_MIMETYPES.contains(mimeType)) {
		// mimeType = MIMETIPE_DEFAULT;
		// }
		return (mimeType);
	}

	public static boolean setPreferredMimeType(String userName, String mimeType) {
		return (true);
	}

	public static boolean subscribeToTheme(String userName, String theme) {
		CommunityManager.setUserPreference(userName, NEWSLETTER_THEME, theme);
		return (true);
	}

	public static boolean subscribeToThemes(String userName, String[] themes) {
		if (themes != null && themes.length > 0) {
			for (int i = 0; i < themes.length; i++) {
				String theme = themes[i];
				CommunityManager.setUserPreference(userName, NEWSLETTER_THEME, theme);
			}
			return (true);
		}
		return (false);
	}

	public static boolean unsubscribeFromTheme(String userName, String theme) {
		CommunityManager.removeUserPreference(userName, NEWSLETTER_THEME, theme);
		return (true);
	}

	public static boolean unsubscribeFromThemes(String userName, String[] themes) {
		if (themes != null && themes.length > 0) {
			for (int i = 0; i < themes.length; i++) {
				String theme = themes[i];
				CommunityManager.removeUserPreference(userName, NEWSLETTER_THEME, theme);
			}
		}
		return (true);
	}

	public static boolean terminateUserSubscription(String userName) {
		CommunityManager.removeUserPreference(userName, NewsletterSubscriptionUtil.NEWSLETTER_THEME);
		return (true);
	}

	public static boolean pauseUserSubscriptions(String userName) {
		CommunityManager.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_STATUS_INACTIVE);
		return (true);
	}

	public static boolean resumeUserSubscriptions(String userName) {
		CommunityManager.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_STATUS_ACTIVE);
		return (true);
	}

	public static String getSubscriptionStatus(String userName) {
		String status = CommunityManager.getUserPreference(userName, SUBSCRIPTION_STATUS_KEY);
		return (status);
	}

	public static List<String> compareToUserSubscribedThemes(List compareWithThemes, String userName, String newsletterNumber) {
		if (compareWithThemes == null || userName == null || newsletterNumber == null) {
			return (null);
		}
		List<String> userThemes = getUserSubscribedThemes(userName, newsletterNumber);
		List<String> themes = new ArrayList<String>();
		for (int i = 0; i < compareWithThemes.size(); i++) {
			String theme = (String) compareWithThemes.get(i);
			if (userThemes.contains(theme)) {
				themes.add(theme);
			}
		}
		return (themes);
	}

	public static List<String> getSubscribersForNewsletter(String newsletterNumber) {
		List<String> subscribers = CommunityManager.getUsersWithPreference(NewsletterUtil.NEWSLETTER, newsletterNumber);
		return (subscribers);
	}
}