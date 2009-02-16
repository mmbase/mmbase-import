/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.portlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.pluto.om.window.PortletWindow;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.core.*;

@SuppressWarnings("serial")
public class PortletModeTag extends TagSupport {

   private String name;
   private boolean inverse;


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public boolean isInverse() {
      return inverse;
   }


   public void setInverse(boolean inverse) {
      this.inverse = inverse;
   }


   @Override
   public int doStartTag() {
      HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
      PortletFragment portletFragment = (PortletFragment) request.getAttribute(PortalConstants.FRAGMENT);

      PortletWindow portletWindow = portletFragment.getPortletWindow();

      PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
      PortalURL modeURL = env.getRequestedPortalURL();

      PortalControlParameter control = new PortalControlParameter(modeURL);
      String currentMode = control.getMode(portletWindow).toString();
      String customPortletMode = (String) request.getAttribute("portletMode");
      if (customPortletMode != null && !customPortletMode.trim().equals("")) {
         currentMode = customPortletMode;
      }
      if (inverse) {
         if (!currentMode.equals(name)) {
            return EVAL_BODY_INCLUDE;
         }
      }
      else {
         if (currentMode.equals(name)) {
            return EVAL_BODY_INCLUDE;
         }
      }
      return SKIP_BODY;
   }

}
