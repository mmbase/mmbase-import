/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;
import javax.portlet.*;

import org.apache.commons.lang.StringUtils;

/**
 * Portlet to edit content elements
 * 
 * @author Wouter Heijke
 */
public class ContentPortlet extends AbstractContentPortlet {

   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      setPortletNodeParameter(portletId, CONTENTELEMENT, request.getParameter(CONTENTELEMENT));
      setPortletParameter(portletId, USE_LIFECYCLE, request.getParameter(USE_LIFECYCLE));
   }


   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      String elementId = req.getParameter(ELEMENT_ID);
      if (StringUtils.isEmpty(elementId)) {
         PortletPreferences preferences = req.getPreferences();
         elementId = preferences.getValue(CONTENTELEMENT, null);
      }
      getLogger().debug("doView for elementId: " + elementId);

      if (StringUtils.isNotEmpty(elementId)) {
         setAttribute(req, ELEMENT_ID, elementId);
         setMetaData(req, elementId);
         super.doView(req, res);
      }

   }


   @Override
   protected void doEdit(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      String elementId = req.getParameter(ELEMENT_ID);
      if (StringUtils.isEmpty(elementId)) {
         PortletPreferences preferences = req.getPreferences();
         elementId = preferences.getValue(CONTENTELEMENT, null);
      }
      getLogger().debug("doEdit for elementId: " + elementId);

      if (StringUtils.isNotEmpty(elementId)) {
         setAttribute(req, ELEMENT_ID, elementId);
         setMetaData(req, elementId);
         doEdit(req, res, elementId);
      }
   }

}
