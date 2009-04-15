/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;

import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.InformationProviderAccess;
import org.apache.pluto.services.information.PortletURLProvider;

import com.finalist.pluto.portalImpl.core.PortletURLProviderImpl;

public class PortletURLImpl extends org.apache.pluto.core.impl.PortletURLImpl {

   protected String windowid;
   protected String page;
   protected String host;


   public PortletURLImpl(PortletWindow portletWindow, javax.servlet.http.HttpServletRequest servletRequest,
         javax.servlet.http.HttpServletResponse servletResponse, boolean isAction) {
      super(portletWindow, servletRequest, servletResponse, isAction);
   }


   public PortletURLImpl(String host, String page, String windowid, javax.servlet.http.HttpServletRequest servletRequest,
         javax.servlet.http.HttpServletResponse servletResponse, boolean isAction) {
      super(null, servletRequest, servletResponse, isAction);
      this.windowid = windowid;
      this.host = host;
      this.page = page;
   }


   public void setPortletMode(PortletMode portletMode) throws PortletModeException {

      // check if portal supports portlet mode
      // DynamicInformationProvider provider =
      // InformationProviderAccess.getDynamicProvider(servletRequest);
      if (provider == null)
         provider = InformationProviderAccess.getDynamicProvider(servletRequest);
      if (portletMode != null && provider.isPortletModeAllowed(portletMode)) {
         if (isPortletModeSupported(portletMode, portletWindow)) {
            mode = portletMode;
            return;
         }
      }
      throw new PortletModeException("unsupported Portlet Mode used: " + portletMode, portletMode);
   }


   private boolean isPortletModeSupported(PortletMode requestedPortletMode, PortletWindow referencedPortletWindow) {
      // PLT 8.1: View Portlet Mode should always be supported by a portlet,
      // even if not defined in the descriptor
      if (requestedPortletMode.equals(PortletMode.VIEW) || requestedPortletMode.toString().equalsIgnoreCase("view")) {
         return true;
      }
      if (requestedPortletMode != null) {
         PortletDefinition portletDefinition = referencedPortletWindow.getPortletEntity().getPortletDefinition();
         ContentTypeSet contentTypes = portletDefinition.getContentTypeSet();
         return contentTypes.supportsPortletMode(requestedPortletMode);
      }
      else {
         return false;
      }
   }


   public String toString() {
      StringBuffer url = new StringBuffer(200);

      PortletURLProvider urlProvider = InformationProviderAccess.getDynamicProvider(servletRequest)
            .getPortletURLProvider(portletWindow);
      if (urlProvider instanceof PortletURLProviderImpl && page != null && windowid != null) {
         PortletURLProviderImpl extendedUrlProvider = (PortletURLProviderImpl) urlProvider;
         extendedUrlProvider.setHost(host);
         extendedUrlProvider.setPage(page);
         extendedUrlProvider.setWindowid(windowid);
      }

      if (mode != null) {
         urlProvider.setPortletMode(mode);
      }
      if (state != null) {
         urlProvider.setWindowState(state);
      }
      if (isAction) {
         urlProvider.setAction();
      }
      if (secure) {
         urlProvider.setSecure();
      }
      urlProvider.clearParameters();
      urlProvider.setParameters(parameters);

      url.append(urlProvider.toString());

      return url.toString();
   }

}
