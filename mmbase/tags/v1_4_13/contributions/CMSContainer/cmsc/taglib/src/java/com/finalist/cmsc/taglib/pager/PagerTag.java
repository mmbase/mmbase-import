/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.pager;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.jsp.JspException;

public class PagerTag extends com.jsptags.navigation.pager.PagerTag {

   private PortletURL renderUrl;


   public void setUrl(String value) {
      throw new IllegalArgumentException("Portal does not support the url attribute.");
   }


   public String getUrl() {
      throw new IllegalArgumentException("Portal does not support the url attribute.");
   }


   protected PortletURL getRenderUrl() {
      PortletURL renderUrl = null;
      RenderResponse renderResponse = (RenderResponse) pageContext.getRequest().getAttribute("javax.portlet.response");
      if (renderResponse != null) {
         renderUrl = renderResponse.createRenderURL();
      }
      return renderUrl;
   }


   public int doStartTag() throws JspException {
      renderUrl = getRenderUrl();
      return super.doStartTag();
   }


   protected void addParam(String name, String value) {
      if (value != null) {
         renderUrl.setParameter(name, value);
      }
      else {
         String[] values = pageContext.getRequest().getParameterValues(name);

         if (values != null) {
            for (int i = 0, l = values.length; i < l; i++) {
               renderUrl.setParameter(name, value);
            }
         }
      }
   }


   protected String getOffsetUrl(int pageOffset) {
      // resetting offset parameter in PortletURL.
      // Pager method removed the offset parameter agai, but that is not
      // possible with the PortletURL
      renderUrl.setParameter(idOffsetParam, String.valueOf(pageOffset));
      return renderUrl.toString();
   }


   public void release() {
      renderUrl = null;
   }
}
