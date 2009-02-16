/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.XsltUtil;
import com.finalist.pluto.PortletURLImpl;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class XsltPortlet extends CmscPortlet {

   private static final String ERROR_HEADER_HTML = "<div class=\"summary-inverted\"><h3>XML-feed</h3>";
   private static final String ERROR_FOOTER_HTML = "</div>";

   public static final String SOURCE_ATTR_PARAM = "source";
   private static final String VIEW = "view";
   
   protected static final String PAGE = "page";
   protected static final String WINDOW = "window";


   /**
    * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      getLogger().debug("===>MenuPortlet.EDIT mode");
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);

      String action = request.getParameter("action");
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         if (portletId != null) {
            // get the values submitted with the form
            setPortletParameter(portletId, SOURCE_ATTR_PARAM, request.getParameter(SOURCE_ATTR_PARAM));
            setPortletView(portletId, request.getParameter(VIEW));
            setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
            setPortletParameter(portletId, WINDOW, request.getParameter(WINDOW));
         }
         else {
            getLogger().error("No portletId");
         }
         // switch to View mode
         response.setPortletMode(PortletMode.VIEW);
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }


   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      addViewInfo(req);
      
      /** 
       * process clickon prefs 
       * NB this is a copy of the same functionality in AbstractContentPortlet.
       */
      PortletPreferences preferences = req.getPreferences();
      String pageid = preferences.getValue(PAGE, null);
      if (!StringUtil.isEmpty(pageid)) {

         String pagepath = SiteManagement.getPath(Integer.valueOf(pageid), true);

         if (pagepath != null) {
            setAttribute(req, "pagepath", pagepath);

            Set<String> positions = SiteManagement.getPagePositions(pageid);
            ArrayList<String> orderedPositions = new ArrayList<String>(positions);
            Collections.sort(orderedPositions);
            setAttribute(req, "pagepositions", new ArrayList<String>(orderedPositions));
         }
      }
      /** END process clickon prefs */
      
      super.doEditDefaults(req, res);
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
      String page = preferences.getValue(PAGE, null);
      String window = preferences.getValue(WINDOW, null);
      String xsl = getTemplate("view", template, "xsl");
      String xmlSource = preferences.getValue(SOURCE_ATTR_PARAM, null);

      // set required content type and write content
      response.setContentType("text/html");
      if (!StringUtil.isEmpty(xsl) && !StringUtil.isEmpty(xmlSource)) {
         try {
            HashMap<String, Object> xslParams = getXsltParams(preferences);
            
            /** get renderUrl */
            PortletURL renderUrl = null;
            if (page != null && window != null) {
				String link = "";
				NavigationItem item = SiteManagement.convertToNavigationItem(page);
				if (item != null) {
                  link = SiteManagement.getPath(item, !ServerUtil.useServerName());
               } else {
                  link = page;
               }
               String host = null;
               if (ServerUtil.useServerName()) {
                  host = SiteManagement.getSite(item);
               }

               renderUrl = new PortletURLImpl(host, link, window, (HttpServletRequest) request, (HttpServletResponse) response, false);

            } else {
               renderUrl = response.createRenderURL();
            }
            xslParams.put("RENDERURL", renderUrl);
            
            StringBuffer content = new StringBuffer(4096);

            String html = transformXml(xsl, xmlSource, xslParams);
            if (html != null) {
               content.append(html);
            }
            response.getWriter().print(content.toString());
         }
         catch (TransformerException e) {
            getLogger().error("Xslt portlet transformer error: " + e.getMessage());
         }
         catch (ParserConfigurationException e) {
            getLogger().error("Xslt portlet parser error: " + e.getMessage());
         }
         catch (SAXException e) {
            getLogger().error("Xslt portlet sax error: " + e.getMessage());
         }
         catch (MalformedURLException e) {
            getLogger().error("Xslt portlet URL error: " + e.getMessage());
            String errmsg = "error.url.malformed";
            response.getWriter().print(createErrorMessage(request, errmsg, xmlSource));
         }
         catch (UnknownHostException uhe) {
            getLogger().error("Xslt portlet connection error: " + uhe.getMessage());
            String errmsg = "error.url.unknownhost";
            response.getWriter().print(createErrorMessage(request, errmsg, xmlSource));
         }
         catch (ConnectException ce) {
            getLogger().error("Xslt portlet connection error: " + ce.getMessage());
            String errmsg = "error.url.connect";
            response.getWriter().print(createErrorMessage(request, errmsg, xmlSource));
         }
      }
   }


   protected String transformXml(String xsl, String xml, HashMap<String, Object> xslParams)
         throws TransformerException, ParserConfigurationException, SAXException, IOException, MalformedURLException {

      InputStream xslSrc = getPortletContext().getResourceAsStream(xsl);
      URL xmlURL = new URL(xml);
      XsltUtil xsltUtil = new XsltUtil(xmlURL, xslSrc, null);
      return xsltUtil.transformToString(xslParams);
   }


   private String createErrorMessage(RenderRequest request, String errmsg, String sourceXMLAttr) {
      if (ServerUtil.isStaging()) {
         Locale locale = getEditorLocale(request, request.getLocale());
         ResourceBundle portletbundle = getResourceBundle(locale);
         String resourceString = portletbundle.getString(errmsg);
         return ERROR_HEADER_HTML + MessageFormat.format(resourceString, sourceXMLAttr) + ERROR_FOOTER_HTML;
      }
      return "";
   }


   private HashMap<String, Object> getXsltParams(PortletPreferences preferences) {
      HashMap<String, Object> xslParams = new HashMap<String, Object>();

      Enumeration<String> p = preferences.getNames();
      while (p.hasMoreElements()) {
         String pref = p.nextElement();
         String value = preferences.getValue(pref, null);
         if (!StringUtils.isBlank(value)) {
            xslParams.put(pref, value);
         }
      }
      return xslParams;
   }

}
