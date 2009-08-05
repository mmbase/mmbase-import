/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.portalImpl.PortalConstants;

/**
 * IFramePortlet
 */
public class IFramePortlet extends CmscPortlet {

   /**
    * Configuration constants.
    */
   public static final String ALIGN_ATTR_PARAM = "align";

   public static final String CLASS_ATTR_PARAM = "class";

   public static final String FRAME_BORDER_ATTR_PARAM = "frameBorder";

   public static final String HEIGHT_ATTR_PARAM = "height";

   public static final String ID_ATTR_PARAM = "id";

   public static final String MARGIN_HEIGHT_ATTR_PARAM = "marginHeight";

   public static final String MARGIN_WIDTH_ATTR_PARAM = "marginWidth";

   public static final String NAME_ATTR_PARAM = "name";

   public static final String SCROLLING_ATTR_PARAM = "scrolling";

   public static final String SOURCE_ATTR_PARAM = "source";

   public static final String SOURCE_REQ_PARAM = "source";

   public static final String STYLE_ATTR_PARAM = "style";

   public static final String WIDTH_ATTR_PARAM = "width";
   public static final String USE_TABLE_ATTR_PARAM = "useTable";

   /**
    * Configuration default constants.
    */
   public static final String ALIGN_ATTR_DEFAULT = "bottom";

   public static final String FRAME_BORDER_ATTR_DEFAULT = "0";

   public static final String HEIGHT_ATTR_DEFAULT = null;

   public static final String MARGIN_HEIGHT_ATTR_DEFAULT = "0";

   public static final String MARGIN_WIDTH_ATTR_DEFAULT = "0";

   public static final String SCROLLING_ATTR_DEFAULT = "no";

   public static final String SOURCE_ATTR_DEFAULT = "about:blank";

   public static final String WIDTH_ATTR_DEFAULT = "100%";
   public static final String USE_TABLE_DEFAULT = "true";

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response)
         throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         // get the values submitted with the form
         setPortletParameter(portletId, ALIGN_ATTR_PARAM, request.getParameter(ALIGN_ATTR_PARAM));

         setPortletParameter(portletId, CLASS_ATTR_PARAM, request.getParameter(CLASS_ATTR_PARAM));
         setPortletParameter(portletId, FRAME_BORDER_ATTR_PARAM, request
               .getParameter(FRAME_BORDER_ATTR_PARAM));
         setPortletParameter(portletId, HEIGHT_ATTR_PARAM, request.getParameter(HEIGHT_ATTR_PARAM));
         setPortletParameter(portletId, ID_ATTR_PARAM, request.getParameter(ID_ATTR_PARAM));
         setPortletParameter(portletId, MARGIN_HEIGHT_ATTR_PARAM, request
               .getParameter(MARGIN_HEIGHT_ATTR_PARAM));
         setPortletParameter(portletId, MARGIN_WIDTH_ATTR_PARAM, request
               .getParameter(MARGIN_WIDTH_ATTR_PARAM));
         setPortletParameter(portletId, NAME_ATTR_PARAM, request.getParameter(NAME_ATTR_PARAM));
         setPortletParameter(portletId, SCROLLING_ATTR_PARAM, request
               .getParameter(SCROLLING_ATTR_PARAM));
         setPortletParameter(portletId, SOURCE_ATTR_PARAM, request.getParameter(SOURCE_ATTR_PARAM));
         setPortletParameter(portletId, STYLE_ATTR_PARAM, request.getParameter(STYLE_ATTR_PARAM));
         setPortletParameter(portletId, WIDTH_ATTR_PARAM, request.getParameter(WIDTH_ATTR_PARAM));
         setPortletParameter(portletId, USE_TABLE_ATTR_PARAM, request.getParameter(USE_TABLE_ATTR_PARAM));

      }
      else {
         getLogger().error("No portletId");
      }
      // switch to View mode
      super.processEditDefaults(request, response);
   }

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      // TODO Auto-generated method stub
      super.doEditDefaults(req, res);
   }

   /**
    * Generate IFRAME with view source.
    */
   @Override
   public void doView(RenderRequest request, RenderResponse response) throws PortletException,
         IOException {
      PortletPreferences preferences = request.getPreferences();

      // get IFRAME source
      String source = getPreference(preferences, SOURCE_ATTR_PARAM, SOURCE_ATTR_DEFAULT);
      // allow for source suggested from other portlet
      if (request.getParameter(SOURCE_REQ_PARAM) != null)
         source = request.getParameter(SOURCE_REQ_PARAM);

      // IFRAME attribute members.
      String alignAttr = getPreference(preferences, ALIGN_ATTR_PARAM, ALIGN_ATTR_DEFAULT);
      String classAttr = getPreference(preferences, CLASS_ATTR_PARAM, null);
      String frameBorderAttr = getPreference(preferences, FRAME_BORDER_ATTR_PARAM,
            FRAME_BORDER_ATTR_DEFAULT);
      String heightAttr = getPreference(preferences, HEIGHT_ATTR_PARAM, HEIGHT_ATTR_DEFAULT);
      String idAttr = getPreference(preferences, ID_ATTR_PARAM, null);
      String marginHeightAttr = getPreference(preferences, MARGIN_HEIGHT_ATTR_PARAM,
            MARGIN_HEIGHT_ATTR_DEFAULT);
      String marginWidthAttr = getPreference(preferences, MARGIN_WIDTH_ATTR_PARAM,
            MARGIN_WIDTH_ATTR_DEFAULT);
      String nameAttr = getPreference(preferences, NAME_ATTR_PARAM, null);
      String scrollingAttr = getPreference(preferences, SCROLLING_ATTR_PARAM,
            SCROLLING_ATTR_DEFAULT);
      String styleAttr = getPreference(preferences, STYLE_ATTR_PARAM, null);
      String widthAttr = getPreference(preferences, WIDTH_ATTR_PARAM, WIDTH_ATTR_DEFAULT);

      String useTableAttr = getPreference(preferences, USE_TABLE_ATTR_PARAM, USE_TABLE_DEFAULT);
      boolean useTable = Boolean.parseBoolean(useTableAttr);

      // render IFRAME content
      // generate HTML IFRAME content
      StringBuffer content = new StringBuffer(4096);

      if (useTable) {
         content.append("<table width='100%'><tr><td>");
      }
      
      StringBuffer params = new StringBuffer();
      boolean first = true;
      for(String param:(Set<String>)(request.getParameterMap().keySet())) {
    	  params.append((first && !source.contains("?"))?"?":"&");
    	  first = false;
    	  params.append(param);
    	  params.append("=");
    	  params.append(request.getParameter(param));
      }
      
      content.append("<iframe");
      content.append(" src=\"").append(source).append(params).append("\"");
      if (alignAttr != null) content.append(" align=\"").append(alignAttr).append("\"");
      if (classAttr != null) content.append(" class=\"").append(classAttr).append("\"");
      if (frameBorderAttr != null)
         content.append(" frameborder=\"").append(frameBorderAttr).append("\"");
      if (idAttr != null) content.append(" id=\"").append(idAttr).append("\"");
      if (marginHeightAttr != null)
         content.append(" marginheight=\"").append(marginHeightAttr).append("\"");
      if (marginWidthAttr != null)
         content.append(" marginwidth=\"").append(marginWidthAttr).append("\"");
      if (nameAttr != null) content.append(" name=\"").append(nameAttr).append("\"");
      if (heightAttr != null) content.append(" height=\"").append(heightAttr).append("\"");
      if (scrollingAttr != null)
         content.append(" scrolling=\"").append(scrollingAttr).append("\"");
      if (styleAttr != null) content.append(" style=\"").append(styleAttr).append("\"");
      if (widthAttr != null) content.append(" width=\"").append(widthAttr).append("\"");
      content.append(">");
      content.append("<p style=\"text-align:center\"><a href=\"").append(source).append("\">")
            .append(source).append("</a></p>");
      content.append("</iframe>");

      if (useTable) {
         content.append("</td></tr></table>");
      }

      // set required content type and write HTML IFRAME content
      response.setContentType("text/html");
      response.getWriter().print(content.toString());
   }

   /**
    * Get IFRAME preference.
    */
   private String getPreference(PortletPreferences preferences, String name, String defaultValue) {
      String value = preferences.getValue(name, defaultValue);
      return (value != null && !value.equalsIgnoreCase("none")) ? value : null;
   }

}