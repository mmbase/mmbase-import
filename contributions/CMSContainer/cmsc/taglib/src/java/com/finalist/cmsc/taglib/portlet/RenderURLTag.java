package com.finalist.cmsc.taglib.portlet;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.pluto.PortletURLImpl;

/**
 ** Supporting class for the <CODE>renderURL</CODE> tag.
 ** Creates a url that points to the current Portlet and triggers an render request
 ** with the supplied parameters. 
 **
 **/
public class RenderURLTag extends BasicURLTag
{

    protected PortletURL getRenderUrl() {
        PortletURL renderUrl = null;
        if (!StringUtil.isEmpty(page) && !StringUtil.isEmpty(window)) {
            Page pageObject = SiteManagement.getPage(Integer.parseInt(page));
            if (pageObject != null) {
                String link = SiteManagement.getPageLink(pageObject, !ServerUtil.useServerName());
                renderUrl = new PortletURLImpl(link, window, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), false);
            }
        }
        else {
            RenderResponse renderResponse = (RenderResponse)pageContext.getRequest().getAttribute("javax.portlet.response");
            if (renderResponse != null)
            {
                renderUrl = renderResponse.createRenderURL();
            }
        }
        return renderUrl;
    }
    
}

