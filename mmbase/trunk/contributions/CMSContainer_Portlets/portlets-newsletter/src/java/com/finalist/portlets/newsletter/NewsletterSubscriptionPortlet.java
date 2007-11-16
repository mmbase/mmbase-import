/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.newsletter;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class NewsletterSubscriptionPortlet extends JspPortlet {

	private static final String TEMPLATE_LOGIN = "newsletter/subscription/login.jsp";
	private static final String TEMPLATE_OPTIONS = "newsletter/subscription/options.jsp";
	private static final String TEMPLATE_NEW = "newsletter/subscription/new.jsp";

	boolean needLogin = true;
	boolean loggedIn = true;
	boolean hasSubscription = true;

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		PortletSession session = request.getPortletSession();

		String userName = "Pino"; // Temp username till community works
		request.setAttribute("username", userName);
		String action = request.getParameter("action");
		String template = "" + request.getParameter("template");

		String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
		if (status.equals(NewsletterSubscriptionUtil.SUBSCRIPTION_STATUS_ACTIVE)) {
			request.setAttribute("isactive", true);
		} else {
			request.setAttribute("isactive", false);
		}

		boolean mayProceed = false;
		if (loggedIn == true || needLogin == false) {
			mayProceed = true;
		} else {
			doInclude("view", TEMPLATE_LOGIN, request, response);
		}

		if (mayProceed) {
			if (action == null) {
				if (hasSubscription == true) {
					doInclude("view", TEMPLATE_OPTIONS, request, response);
				} else {
					doInclude("view", TEMPLATE_NEW, request, response);
				}
			} else {
				if (template.length() > 0) {
					doInclude("view", template, request, response);
				}
			}
		}
	}

	@Override
	public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		PortletPreferences pref = request.getPreferences();
		PortletSession session = request.getPortletSession();

		String action = request.getParameter("action");
		String userName = request.getParameter("userName");
		if (action != null) {
			response.setWindowState(WindowState.MAXIMIZED);
			if (action.equals("subscribe")) {
				hasSubscription = true;
				String[] themes = request.getParameterValues(NewsletterSubscriptionUtil.NEWSLETTER_THEME);
				if (themes != null && themes.length > 0) {
					boolean result = NewsletterSubscriptionUtil.subscribeToThemes(userName, themes);
				}
			} else if (action.equals("change")) {
				String[] themes = request.getParameterValues("theme");
				// TODO, theme update
				String mimeType = request.getParameter("mimetype");
				if (mimeType != null) {
					boolean result = NewsletterSubscriptionUtil.setPreferredMimeType(userName, mimeType);
				}
			} else if (action.equals("terminate")) {
				boolean result = NewsletterSubscriptionUtil.terminateUserSubscription(userName);
			} else if (action.equals("pause")) {
				boolean result = NewsletterSubscriptionUtil.pauseUserSubscriptions(userName);
			} else if (action.equals("resume")) {
				boolean result = NewsletterSubscriptionUtil.resumeUserSubscriptions(userName);
			}
		}
	}
}
