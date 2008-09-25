/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.portlets.playlist;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;
import com.finalist.portlets.playlist.dto.Item;
import com.finalist.portlets.playlist.dto.MusibaseXmlPlaylistExport;
import com.finalist.portlets.playlist.dto.Playlist;

/**
 * Displays a playlist from an XML-file. The location of the XML-file must be
 * defined in the portlet.
 * 
 * @author robm
 * @author Cati Macarov
 */
public class PlayListPortlet extends ContentPortlet {

   private static final String PLAYLIST_LOCATION = "location";
   private static final String PLAYLIST_ITEMS = "items";
   private static final String PLAYLIST_DAY = "selectedDay";
   private static final String PLAYLIST_MONTH = "selectedMonth";
   private static final String PLAYLIST_YEAR = "selectedYear";
   private static final String PLAYLIST_HOUR = "selectedHour";

   private static Unmarshaller unmarshaller;


   @Override
   public void init(PortletConfig config) throws PortletException {
      try {
         JAXBContext jaxbContext = JAXBContext.newInstance("com.finalist.portlets.playlist.dto");
         unmarshaller = jaxbContext.createUnmarshaller();
      }
      catch (JAXBException ex) {
         getLogger().error("Failed to initialize playlist portlet.", ex);
         throw new PortletException(ex);
      }
      super.init(config);
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

      PortletPreferences preferences = request.getPreferences();
      String contentelement = preferences.getValue(CONTENTELEMENT, null);
      if (contentelement != null) {
         PortletSession portletSession = request.getPortletSession();
         Cloud cloud = getCloudForAnonymousUpdate();
         Node element = cloud.getNode(contentelement);

         String selectedYear = (String) portletSession.getAttribute(PLAYLIST_YEAR);
         String selectedMonth = (String) portletSession.getAttribute(PLAYLIST_MONTH);
         String selectedDay = (String) portletSession.getAttribute(PLAYLIST_DAY);
         String selectedHour = (String) portletSession.getAttribute(PLAYLIST_HOUR);

         if (selectedYear == null || selectedMonth == null || selectedDay == null || selectedHour == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, -1);
            selectedYear = String.valueOf(c.get(Calendar.YEAR));
            selectedMonth = String.format("%02d", c.get(Calendar.MONTH) + 1);
            selectedDay = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
            selectedHour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
         }

         request.setAttribute(PLAYLIST_DAY, selectedDay);
         request.setAttribute(PLAYLIST_MONTH, selectedMonth);
         request.setAttribute(PLAYLIST_YEAR, selectedYear);
         request.setAttribute(PLAYLIST_HOUR, selectedHour);

         portletSession.removeAttribute(PLAYLIST_DAY);
         portletSession.removeAttribute(PLAYLIST_MONTH);
         portletSession.removeAttribute(PLAYLIST_YEAR);
         portletSession.removeAttribute(PLAYLIST_HOUR);

         String location = element.getStringValue(PLAYLIST_LOCATION);
         String fileNamePattern = PropertiesUtil.getProperty("playlist.filename");
         if (fileNamePattern != null) {
            String fileName = String.format(fileNamePattern, selectedYear, selectedMonth, selectedDay, selectedHour);
            String filePath = location + fileName;
            filePath = filePath.replaceAll(" ", "%20");
            try {
               File file = new File(new URI(filePath));
               if (file.exists()) {
                  try {
                     MusibaseXmlPlaylistExport musibase = (MusibaseXmlPlaylistExport) unmarshaller.unmarshal(file);
                     Playlist playlist = musibase.getPlaylist();
                     List<Item> items = playlist.getItem();
                     Collections.sort(items, new StartTimeComparator());
                     request.setAttribute(PLAYLIST_ITEMS, items);
                  }
                  catch (Exception ex) {
                     // if xml file is corrupt jaxb throws NPE instead of
                     // JAXBExceptions sometimes
                     // the jsp will show a nice error message
                     getLogger().error("Failed to read playlist. Check location of playlist in portlet", ex);
                  }
               }
            }
            catch (URISyntaxException e) {
               getLogger().error("Incorrect file location:" + filePath, e);
            }
         }
         else {
            getLogger().error("Filename is not configured");
         }
      }
      else {
         getLogger().error("No contentelement");
      }
      super.doView(request, response);
   }


   public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      super.processEdit(request, response);

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(PortletMode.EDIT);
      }
      else if (action.equals("delete")) {
         String deleteNumber = request.getParameter("deleteNumber");
         Cloud cloud = getCloud();
         Node element = cloud.getNode(deleteNumber);
         element.delete(true);
      }
   }


   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String contentelement = preferences.getValue(CONTENTELEMENT, null);
         if (contentelement != null) {
            PortletSession portletSession = request.getPortletSession();
            portletSession.setAttribute(PLAYLIST_DAY, request.getParameter(PLAYLIST_DAY));
            portletSession.setAttribute(PLAYLIST_MONTH, request.getParameter(PLAYLIST_MONTH));
            portletSession.setAttribute(PLAYLIST_YEAR, request.getParameter(PLAYLIST_YEAR));
            portletSession.setAttribute(PLAYLIST_HOUR, request.getParameter(PLAYLIST_HOUR));
         }
         else {
            getLogger().error("No contentelement");
         }
         // switch to View mode
         response.setPortletMode(PortletMode.VIEW);
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }

}

/** For sorting with newest item first in list */
class StartTimeComparator implements Comparator<Item> {

   public int compare(Item o1, Item o2) {
      if (o1.getStarttime() != null && o2.getStarttime() != null) {
         return o2.getStarttime().compareTo(o1.getStarttime());
      }
      if (o1.getStarttime() == null && o2.getStarttime() == null) {
         return 0;
      }
      if (o1.getStarttime() == null && o2.getStarttime() != null) {
         return 1;
      }
      return -1;
   }

}
