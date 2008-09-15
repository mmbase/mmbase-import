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

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.RelatedContentPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;
import com.finalist.portlets.tagcloud.Tag;
import com.finalist.portlets.tagcloud.util.TagCloudUtil;

public class TagRelatedPortlet extends RelatedContentPortlet {

	protected static final String RELATED_WINDOW = "relatedWindow";

	@Override
	protected void doView(RenderRequest req, RenderResponse res)
			throws PortletException, IOException {

		String window = req.getPreferences().getValue(RELATED_WINDOW, null);
		String elementId = getRelatedElementId(req, window);

		if (elementId != null) {
			List<Tag> tags = TagCloudUtil.getRelatedTags(Integer
					.parseInt(elementId));
			req.setAttribute("tags", tags);
		}
		super.doView(req, res);
	}
	
	   @Override
	   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
	      Integer pageid = getCurrentPageId(req);
	      String pagepath = SiteManagement.getPath(pageid, true);

	      if (pagepath != null) {
	         Set<String> positions = SiteManagement.getPagePositions(pageid.toString());
	         List<String> orderedPositions = new ArrayList<String>(positions);
	         Collections.sort(orderedPositions);
	         setAttribute(req, "relatedPagepositions", new ArrayList<String>(orderedPositions));
	      }
	      super.doEditDefaults(req, res);
	   }

	   /**
	    * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
	    *      javax.portlet.ActionResponse)
	    */
	   @Override
	   public void processEditDefaults(ActionRequest request, ActionResponse response)
	         throws PortletException, IOException {
	      String action = request.getParameter(ACTION_PARAM);
	      if (action == null) {
	         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
	      }
	      else
	         if (action.equals("edit")) {
	            PortletPreferences preferences = request.getPreferences();
	            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
	            if (portletId != null) {
	               setPortletParameter(portletId, RELATED_WINDOW, request.getParameter(RELATED_WINDOW));
	            }
	            else {
	               getLogger().error("No portletId");
	            }
	         }
	         else {
	            getLogger().error("Unknown action: '" + action + "'");
	         }
	      super.processEditDefaults(request, response);
	   }


	   private Integer getCurrentPageId(RenderRequest req) {
	      String pageId = (String) req.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
	      return Integer.valueOf(pageId);
	   }
}
