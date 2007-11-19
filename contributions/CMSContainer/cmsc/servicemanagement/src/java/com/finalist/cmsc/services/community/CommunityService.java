package com.finalist.cmsc.services.community;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.services.Service;

public abstract class CommunityService extends Service {
	public abstract boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText);
	public abstract boolean logoutUser(/**HttpServletRequest HttpRequest, **/ActionRequest request, ActionResponse response);
}
