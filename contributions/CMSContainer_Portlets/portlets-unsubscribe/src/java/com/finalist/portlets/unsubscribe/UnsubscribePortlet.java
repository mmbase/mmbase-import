package com.finalist.portlets.unsubscribe;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class UnsubscribePortlet extends JspPortlet {
	  protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		  String currentPath = getUrlPath(request);
	      NavigationItem result = SiteManagement.getNavigationItemFromPath(currentPath);
	      int userId = 12345;
	      int newsletterId = result.getId();
		  
		  String unsubscribeUrl = request.getContextPath()+"/unsubscribe/UnsubscribeAction.do?"+"userId="+userId+"&newsletterId="+newsletterId;
		  request.setAttribute("baseurl",unsubscribeUrl);
		  doInclude("view", "/unsubscribe/unsubscribe.jsp", request, response);
	  }

}
