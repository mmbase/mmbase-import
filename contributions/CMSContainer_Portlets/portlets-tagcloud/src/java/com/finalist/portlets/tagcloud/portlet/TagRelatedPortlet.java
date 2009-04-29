package com.finalist.portlets.tagcloud.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.RelatedContentPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;
import com.finalist.portlets.tagcloud.Tag;
import com.finalist.portlets.tagcloud.util.TagCloudUtil;

public class TagRelatedPortlet extends RelatedContentPortlet {

	@Override
	protected void doView(RenderRequest req, RenderResponse res)
			throws PortletException, IOException {

		String relatedPage = req.getPreferences().getValue(RELATED_PAGE, null);
		String relatedWindow = req.getPreferences().getValue(RELATED_WINDOW, null);
		String elementId = getRelatedElementId(req, relatedPage, relatedWindow);

		if (elementId != null) {
			req.setAttribute("elementId", elementId);
			if(req.getAttribute("loadTags") == null || req.getAttribute("loadTags").equals("true")) {
				List<Tag> tags = TagCloudUtil.getRelatedTags(Integer.parseInt(elementId));
				req.setAttribute("tags", tags);
			}
		} else {
			String channelId = getIdFromScreen(req, relatedPage, relatedWindow, "contentchannel");
			if (channelId != null) {
				req.setAttribute("channelId", channelId);
				if(req.getAttribute("loadTags") == null || req.getAttribute("loadTags").equals("true")) {
					List<Tag> tags = TagCloudUtil.getChannelRelatedTags(new Integer(channelId));
					req.setAttribute("tags", tags);
				}
			} else {
				String tag = getIdFromScreen(req, relatedPage, relatedWindow, "tag");
				if(tag != null) {
					tag = tag.replaceAll("0x8", " ");
					req.setAttribute("tag", tag);

					if(req.getAttribute("loadTags") == null || req.getAttribute("loadTags").equals("true")) {
						List<Tag> tags = TagCloudUtil.getTagRelatedTags(tag);
						req.setAttribute("tags", tags);
					}
				}
			}
		}
		super.doView(req, res);
	}

	private String getIdFromScreen(RenderRequest req, String relatedPage, String relatedWindow, String var) {
		Integer pageId = Integer.valueOf(relatedPage);
		NavigationItem item = SiteManagement.getNavigationItem(pageId);
		if (item instanceof Page) {
			Page page = (Page) item;
			int portletId = page.getPortlet(relatedWindow);
			Portlet portlet = SiteManagement.getPortlet(portletId);
			if (portlet != null) {
				return portlet.getParameterValue(var);
			}
		}
		return null;
	}

	@Override
	protected void doEditDefaults(RenderRequest req, RenderResponse res)
			throws IOException, PortletException {
		String relatedPage = req.getPreferences().getValue(RELATED_PAGE, null);
		Integer pageid = Integer.valueOf(relatedPage);
		String pagepath = SiteManagement.getPath(pageid, true);

		if (pagepath != null) {
			Set<String> positions = SiteManagement.getPagePositions(pageid
					.toString());
			List<String> orderedPositions = new ArrayList<String>(positions);
			Collections.sort(orderedPositions);
			setAttribute(req, "relatedPagepositions", new ArrayList<String>(
					orderedPositions));
		}
		super.doEditDefaults(req, res);
	}

	/**
	 * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
	 *      javax.portlet.ActionResponse)
	 */
	@Override
	public void processEditDefaults(ActionRequest request,
			ActionResponse response) throws PortletException, IOException {
		String action = request.getParameter(ACTION_PARAM);
		if (action == null) {
			response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
		} else if (action.equals("edit")) {
			PortletPreferences preferences = request.getPreferences();
			String portletId = preferences.getValue(
					PortalConstants.CMSC_OM_PORTLET_ID, null);
			if (portletId != null) {
				setPortletParameter(portletId, RELATED_WINDOW, request
						.getParameter(RELATED_WINDOW));
			} else {
				getLogger().error("No portletId");
			}
		} else {
			getLogger().error("Unknown action: '" + action + "'");
		}
		super.processEditDefaults(request, response);
	}

}
