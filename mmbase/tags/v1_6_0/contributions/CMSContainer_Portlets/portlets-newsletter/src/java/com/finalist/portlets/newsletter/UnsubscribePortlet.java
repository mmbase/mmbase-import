package com.finalist.portlets.newsletter;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.newsletter.util.NewsletterUtil;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

public class UnsubscribePortlet extends JspPortlet {
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

      String currentPath = getUrlPath(request);
      NavigationItem result = SiteManagement.getNavigationItemFromPath(currentPath);
      int newsletterId = result.getId();

      String url = String.format(
               "%seditors/newsletter/UnsubscribeAction.do?userId=$USERID$&newsletterId=%s",
               NewsletterUtil.getServerURL(),
               newsletterId
      );
      request.setAttribute("baseurl", url);
      doInclude("view", "newsletter/unsubscribe/unsubscribe.jsp", request, response);
   }

}
