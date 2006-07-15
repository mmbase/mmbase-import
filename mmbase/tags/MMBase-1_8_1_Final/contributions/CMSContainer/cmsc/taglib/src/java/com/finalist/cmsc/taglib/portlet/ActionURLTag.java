package com.finalist.cmsc.taglib.portlet;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.pluto.PortletURLImpl;

/**
 * Supporting class for the <CODE>actionURL</CODE> tag.
 * Creates a url that points to the current Portlet and triggers an action request
 * with the supplied parameters. 
 *
 */
public class ActionURLTag extends BasicURLTag
{

    protected PortletURL getRenderUrl() {
        PortletURL renderUrl = null;
        if (page != null && window != null) {
            Page pageObject = SiteManagement.getPage(Integer.parseInt(page));
            if (pageObject != null) {
                String link = SiteManagement.getPageLink(pageObject, !ServerUtil.useServerName());
                renderUrl = new PortletURLImpl(link, window, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), true);
            }
        }
        else {
            RenderResponse renderResponse = (RenderResponse)pageContext.getRequest().getAttribute("javax.portlet.response");
            if (renderResponse != null)
            {
                renderUrl = renderResponse.createActionURL();
            }
        }
        return renderUrl;
    }
}

