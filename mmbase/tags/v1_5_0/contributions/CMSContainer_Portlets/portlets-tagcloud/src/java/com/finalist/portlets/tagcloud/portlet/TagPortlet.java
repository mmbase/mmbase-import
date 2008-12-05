package com.finalist.portlets.tagcloud.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.portlets.AbstractContentPortlet;

public class TagPortlet extends AbstractContentPortlet {

	@Override
	protected void doView(RenderRequest req, RenderResponse res)
			throws PortletException, IOException {
		if (req.getParameter("tag") != null) {
			req.setAttribute("tag", req.getParameter("tag"));
		} else {
			PortletPreferences preferences = req.getPreferences();
			String tag = preferences.getValue("tag", null);
			req.setAttribute("tag", tag);
		}
		super.doView(req, res);
	}

	@Override
	protected void saveParameters(ActionRequest request, String portletId) {
		setPortletParameter(portletId, "tag", request.getParameter("tag"));
	}

}
