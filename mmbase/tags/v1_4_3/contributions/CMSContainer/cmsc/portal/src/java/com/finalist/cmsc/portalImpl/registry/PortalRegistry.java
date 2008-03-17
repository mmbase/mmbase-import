/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.registry;

import javax.servlet.http.HttpServletRequest;

import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;

/**
 * Registry to keep track of things in the CMSC Portal
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.5 $
 */
public class PortalRegistry {

   private static final String PORTAL_REGISTRY = "com.finalist.pluto.portalImpl.core.PortalRegistry";

   private ScreenFragment screen;


   public static PortalRegistry getPortalRegistry(HttpServletRequest request) {
      PortalRegistry reg = (PortalRegistry) request.getSession().getAttribute(PORTAL_REGISTRY);
      if (reg == null) {
         reg = new PortalRegistry();
         request.getSession().setAttribute(PORTAL_REGISTRY, reg);
      }

      return reg;
   }


   public void setScreen(ScreenFragment screen) {
      this.screen = screen;
   }


   public ScreenFragment getScreen() {
      return this.screen;
   }

}
