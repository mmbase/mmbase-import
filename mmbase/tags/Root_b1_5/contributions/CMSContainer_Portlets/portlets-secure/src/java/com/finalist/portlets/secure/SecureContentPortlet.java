package com.finalist.portlets.secure;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.portlets.AbstractContentPortlet;
import com.finalist.cmsc.services.community.Community;


public class SecureContentPortlet extends AbstractContentPortlet {
	  @Override
	   protected void saveParameters(ActionRequest request, String portletId) {
	      setPortletNodeParameter(portletId, CONTENTELEMENT, request.getParameter(CONTENTELEMENT));
	      setPortletParameter(portletId, USE_LIFECYCLE, request.getParameter(USE_LIFECYCLE));
	   }


	   @Override
	   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
	      String elementId = req.getParameter(ELEMENT_ID);
	      if (StringUtils.isEmpty(elementId)) {
	         PortletPreferences preferences = req.getPreferences();
	         elementId = preferences.getValue(CONTENTELEMENT, null);
	      }
	      getLogger().debug("doView for elementId: " + elementId);

	      if (StringUtils.isNotEmpty(elementId)) {
	    	  if(!isSecure(elementId) || isUserLoggedIn()) {
		         setAttribute(req, ELEMENT_ID, elementId);
		         setMetaData(req, elementId);
		         super.doView(req, res);
	    	  }
	      }

	   }


	private boolean isUserLoggedIn() {
		return Community.isAuthenticated();
	}


	private boolean isSecure(String elementId) {
	   Cloud cloud = getCloudForAnonymousUpdate();
		Node node = cloud.getNode(elementId);
		return(node.getNodeManager().hasField("secure") && node.getBooleanValue("secure"));
	}


	@Override
	   protected void doEdit(RenderRequest req, RenderResponse res) throws IOException, PortletException {
	      String elementId = req.getParameter(ELEMENT_ID);
	      if (StringUtils.isEmpty(elementId)) {
	         PortletPreferences preferences = req.getPreferences();
	         elementId = preferences.getValue(CONTENTELEMENT, null);
	      }
	      getLogger().debug("doEdit for elementId: " + elementId);

	      if (StringUtils.isNotEmpty(elementId)) {
	         setAttribute(req, ELEMENT_ID, elementId);
	         setMetaData(req, elementId);
	         doEdit(req, res, elementId);
	      }
	   }
}
