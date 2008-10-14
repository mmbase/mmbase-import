package com.finalist.portlets.tagcloud.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.portlets.tagcloud.Tag;
import com.finalist.portlets.tagcloud.util.TagCloudUtil;

public class TagCloudPortlet extends TagRelatedPortlet {

	@Override
	protected void doView(RenderRequest req, RenderResponse res)
			throws PortletException, IOException {
		PortletPreferences preferences = req.getPreferences();
		String maxString = preferences.getValue("param_max", null);

		Integer max = (maxString == null) ? null : Integer.parseInt(maxString);
		String orderBy = preferences.getValue("param_orderBy", null);

		List<Tag> tags = TagCloudUtil.getTags(max, orderBy, "up");
		req.setAttribute("tags", tags);
		req.setAttribute("loadTags", "false");

		super.doView(req, res);
	}

	@Override
	protected void saveParameters(ActionRequest request, String portletId) {
		setPortletParameter(portletId, "param_max", request.getParameter("param_max"));
		setPortletParameter(portletId, "param_orderBy", request.getParameter("param_orderBy"));
		super.saveParameters(request, portletId);
	}

}
