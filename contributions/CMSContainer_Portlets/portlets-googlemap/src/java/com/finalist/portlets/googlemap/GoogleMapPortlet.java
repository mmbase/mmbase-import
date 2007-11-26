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

   private static Log log = LogFactory.getLog(com.finalist.portlets.googlemap.GoogleMapPortlet.class);

   private static final String ACTION_PARAM = "action";
   private static final String WINDOW = "window";
   private static final String PAGE = "page";
   private static final String ADDRESS = "address";
   private static final String INFO = "info";
   private static final String KEY = "key";


   public GoogleMapPortlet() {
   }


   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      request.setAttribute("address", preferences.getValue("address", null));
      request.setAttribute("info", preferences.getValue("info", null));
      String key = StringUtils.isEmpty(preferences.getValue("key", null)) ? PropertiesUtil.getProperty("google.key")
            : preferences.getValue("key", null);
      request.setAttribute("key", key);
      super.doView(request, response);
   }


   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String action = request.getParameter("action");
      if (action == null)
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue("com.finalist.cmsc.beans.om.portletId", null);
         if (portletId != null) {
            setPortletParameter(portletId, "address", request.getParameter("address"));
            setPortletParameter(portletId, "info", request.getParameter("info"));
            setPortletParameter(portletId, "window", request.getParameter("window"));
            setPortletParameter(portletId, "key", request.getParameter("key"));
            setPortletNodeParameter(portletId, "page", request.getParameter("page"));
         }
         else if (log.isDebugEnabled())
            log.error((new StringBuilder()).append("Unknown action: '").append(action).append("'").toString());
         response.setPortletMode(PortletMode.VIEW);
      }
   }

}
