package com.finalist.cmsc.portlets.randomcontent;

import static com.finalist.cmsc.services.contentrepository.ContentRepository.countContentElements;
import static com.finalist.cmsc.services.contentrepository.ContentRepository.getContentElements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.AbstractContentPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * A Portlet implementation which randomly selects a number of content element from a channel.
 * 
 * @author Rob Schellhorn
 */
public class RandomContentPortlet extends AbstractContentPortlet {

   protected static final String CONTENTCHANNEL = "contentchannel";
   protected static final String ELEMENTS = "elements";
   protected static final String MAX_ELEMENTS = "maxElements";
   
   /**
    * Makes the content from the given channel available to the request.
    */
   protected void addContent(RenderRequest req, String channel) {
      String elementId = req.getParameter(ELEMENT_ID);
      if (StringUtils.isNotEmpty(elementId)) {
         // Detail view
         setMetaData(req, elementId);
      } else {
         // List view
         PortletPreferences preferences = req.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         List<String> contenttypes = SiteManagement.getContentTypes(portletId);
         
         int maxElements = Integer.parseInt(preferences.getValue(MAX_ELEMENTS, "-1"));
         if (maxElements <= 0) {
            // By default limit to 10 items
            maxElements = 10;
         }
         
         // Other channel options
         String orderby = null;
         String direction = null;
         boolean useLifecycle = true;
         String archive = null;
         int year = -1;
         int month = -1;
         int day = -1;

         int total = countContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, 0, maxElements, year, month , day);
         
         if (total < maxElements) {
            // In the case there are fewer elements in the channel than the number of elements we
            // want to select, limit to amount of elements in the channel
            maxElements = total;
         }
         
         Set<Integer> offsetsUsed = new HashSet<Integer>(maxElements);
         
         List<ContentElement> elements = new ArrayList<ContentElement>();         
         while (elements.size() < maxElements) {
            int offset = (int) (Math.random() * total);
            if (!offsetsUsed.add(offset)) {
               // Already used this offset, continue
               continue;
            }
            
            List<ContentElement> e = getContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, 1, year, month, day);
            assert e.size() <= 1 : "The number of selected elements should be less than one";
            
            elements.addAll(e);
         }
         
         setAttribute(req, ELEMENTS, elements);
      }
   }

   /*
    * @see com.finalist.cmsc.portlets.AbstractContentPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
    */
   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      PortletPreferences preferences = req.getPreferences();

      String channel = preferences.getValue(CONTENTCHANNEL, null);
      if (!StringUtils.isEmpty(channel)) {
         addContent(req, channel);
         super.doView(req, res);
      }
   }
   
   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      setPortletNodeParameter(portletId, CONTENTCHANNEL, request.getParameter(CONTENTCHANNEL));
      setPortletParameter(portletId, MAX_ELEMENTS, request.getParameter(MAX_ELEMENTS));
   }
}
