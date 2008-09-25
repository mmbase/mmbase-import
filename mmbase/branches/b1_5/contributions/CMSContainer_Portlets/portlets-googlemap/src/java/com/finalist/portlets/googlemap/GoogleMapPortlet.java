// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name: GoogleMapPortlet.java

package com.finalist.portlets.googlemap;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.CmscPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

import java.io.IOException;
import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GoogleMapPortlet extends CmscPortlet {

   /**
    * Configuration constants.
    */
   public static final String GOOGLE_KEY_PROPERTY = "google.key";
   public static final String ACTION_PARAM = "action";
   public static final String WINDOW = "window";
   public static final String PAGE = "page";
   public static final String ADDRESS = "address";
   public static final String INFO = "info";
   public static final String KEY = "key";
   public static final String HEIGHT = "height";
   public static final String WIDTH = "width";

   /**
    * Configuration default constants.
    */
   public static final String HEIGHT_ATTR_DEFAULT = "260px";
   public static final String WIDTH_ATTR_DEFAULT = "370px";

   private static Log log = LogFactory.getLog(GoogleMapPortlet.class);

   
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      request.setAttribute(ADDRESS, preferences.getValue(ADDRESS, null));
      request.setAttribute(INFO, preferences.getValue(INFO, null));
      request.setAttribute(HEIGHT, preferences.getValue(HEIGHT, HEIGHT_ATTR_DEFAULT));
      request.setAttribute(WIDTH, preferences.getValue(WIDTH, WIDTH_ATTR_DEFAULT));
      
      String key = StringUtils.isEmpty(preferences.getValue(KEY, null)) ? PropertiesUtil.getProperty(GOOGLE_KEY_PROPERTY)
            : preferences.getValue(KEY, null);
      request.setAttribute(KEY, key);
      super.doView(request, response);
   }


   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException {
      String action = request.getParameter(ACTION_PARAM);
      if (action == null)
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue("com.finalist.cmsc.beans.om.portletId", null);
         if (portletId != null) {
            setPortletParameter(portletId, ADDRESS, request.getParameter(ADDRESS));
            setPortletParameter(portletId, INFO, request.getParameter(INFO));
            setPortletParameter(portletId, WINDOW, request.getParameter(WINDOW));
            setPortletParameter(portletId, KEY, request.getParameter(KEY));
            setPortletParameter(portletId, WIDTH, request.getParameter(WIDTH));
            setPortletParameter(portletId, HEIGHT, request.getParameter(HEIGHT));
            setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
         }
         else if (log.isDebugEnabled())
            log.error((new StringBuilder()).append("Unknown action: '").append(action).append("'").toString());
         response.setPortletMode(PortletMode.VIEW);
      }
   }

}
