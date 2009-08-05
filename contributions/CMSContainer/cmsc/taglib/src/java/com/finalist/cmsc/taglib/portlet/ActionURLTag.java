package com.finalist.cmsc.taglib.portlet;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.pluto.PortletURLImpl;

/**
 * Supporting class for the <CODE>actionURL</CODE> tag. Creates a url that
 * points to the current Portlet and triggers an action request with the
 * supplied parameters.
 */
public class ActionURLTag extends BasicURLTag {

   @Override
   protected PortletURL getRenderUrl() {
      PortletURL renderUrl = null;
      if (page != null && window != null) {
         String link = getLink();
         String host = getHost();
         renderUrl = new PortletURLImpl(host, link, window, (HttpServletRequest) pageContext.getRequest(),
               (HttpServletResponse) pageContext.getResponse(), true);
      }
      else {
         RenderResponse renderResponse = (RenderResponse) pageContext.getRequest().getAttribute(
               "javax.portlet.response");
         if (renderResponse != null) {
            renderUrl = renderResponse.createActionURL();
         }
      }
      return renderUrl;
   }
}
