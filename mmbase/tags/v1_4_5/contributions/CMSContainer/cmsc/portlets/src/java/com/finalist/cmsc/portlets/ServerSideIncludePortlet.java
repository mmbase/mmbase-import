package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.util.http.HttpUtil;
import com.finalist.util.http.PageNotFoundException;

public class ServerSideIncludePortlet extends CmscPortlet {

   public static final String SOURCE_ATTR_PARAM = "source";

   /**
    * Configuration default constants.
    */
   public static final String SOURCE_ATTR_DEFAULT = "http://";


   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         // get the values submitted with the form
         setPortletParameter(portletId, SOURCE_ATTR_PARAM, request.getParameter(SOURCE_ATTR_PARAM));

      }
      else {
         getLogger().error("No portletId");
      }
      // switch to View mode
      response.setPortletMode(PortletMode.VIEW);
   }


   /**
    * Generate serverside view source.
    */
   @Override
   public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();

      String sourceAttr = getPreference(preferences, SOURCE_ATTR_PARAM, SOURCE_ATTR_DEFAULT);

      // render IFRAME content
      // generate HTML IFRAME content
      StringBuffer content = new StringBuffer(4096);

      try {
         String actionUrl = response.createActionURL().toString();

         Map parameterMap = request.getParameterMap();
         if (parameterMap == null) {
            parameterMap = new HashMap();
         }
         parameterMap.put("formAction", actionUrl);

         String html = null;

         // check if method is set to post by processView
         String method = (String) request.getPortletSession().getAttribute("method");
         getLogger().debug("Using method: " + method);
         if (method != null && method.equals("post")) {
            html = HttpUtil.doPost(sourceAttr.toString(), parameterMap);
         }
         else {
            html = HttpUtil.doGet(sourceAttr.toString(), parameterMap);
         }

         content.append("<div>");
         content.append("<!-- begin server side include of ").append(sourceAttr).append("-->");
         content.append(html);
         content.append("<!-- end server side include -->");
         content.append("</div>");
      }
      catch (PageNotFoundException e) {
         getLogger().error(e);
         content.append("Pagina niet gevonden!");
      }

      // always remove method so it is not reused by other requests
      request.getPortletSession().removeAttribute("method");

      // set required content type and write HTML IFRAME content
      response.setContentType("text/html");
      response.getWriter().print(content.toString());
   }


   /**
    * Get server side stuff preference.
    */
   private String getPreference(PortletPreferences preferences, String name, String defaultValue) {
      String value = preferences.getValue(name, defaultValue);
      return (value != null && !value.equalsIgnoreCase("none")) ? value : null;
   }


   /**
    * Set all the request parameters as the render parameters.
    */
   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      // set method to post so doView knows that is must use doPost
      request.getPortletSession().setAttribute("method", "post");
      Map parameterMap = request.getParameterMap();
      response.setRenderParameters(parameterMap);
   }

}
