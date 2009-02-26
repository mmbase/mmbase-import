/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.filters.FriendlyUrlFilter;


/**
 * The controller filter filters out all navigation requests and dispatches them
 * through to the portal servlet.
 *
 * @author R.W. van 't Veer
 * @author Wouter Heijke
 */
public class ControllerFilter extends FriendlyUrlFilter {

   @Override
   protected String getServlet() {
      return PortalConstants.CMSC_PORTAL_SERVLET;
   }


   @Override
   protected boolean isFriendlyUrl(HttpServletRequest req, HttpServletResponse resp) {
      return PortalServlet.isNavigation(req, resp);
   }

}
