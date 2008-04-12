// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GoogleMapPortlet.java

package com.finalist.portlets.googlemap;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.CmscPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

import java.io.IOException;
import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GoogleMapPortlet extends CmscPortlet

{
    private static Log log = LogFactory.getLog(GoogleMapPortlet.class);

   private static final String GOOGLE_KEY_PROPERTY = "google.key";

   private static final String ACTION_PARAM = "action";
   private static final String WINDOW = "window";
   private static final String PAGE = "page";
   private static final String ADDRESS = "address";
   private static final String INFO = "info";
   private static final String KEY = "key";


   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      request.setAttribute(ADDRESS, preferences.getValue(ADDRESS, null));
      request.setAttribute(INFO, preferences.getValue(INFO, null));
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
            setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
         }
         else if (log.isDebugEnabled())
            log.error((new StringBuilder()).append("Unknown action: '").append(action).append("'").toString());
         response.setPortletMode(PortletMode.VIEW);
      }
   }

}
