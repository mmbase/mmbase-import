package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.PortletParameter;
import com.finalist.cmsc.beans.om.View;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.SiteManagementAdmin;
import com.finalist.cmsc.util.bundles.CombinedResourceBundle;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

import net.sf.mmapps.commons.bridge.CloudUtil;

public class CmscPortlet extends GenericPortlet {

   private static final String CONTENT_TYPE_DEFAULT = "text/html";

   private static final String NODEPARAM_PREFIX = "nodeparam_";

   private static final String PARAM_PREFIX = "param_";

   private static Properties routings;

   private Log log;

   protected Log getLogger() {
      if (log == null) {
         log = LogFactory.getLog(this.getClass());
      }
      return log;
   }

   @Override
   public void init() throws PortletException {
      super.init();
      initRoutingRules();
   }

   /**
    * Answers whether the given {@link PortletMode} is restricted for this {@link Portlet} instance.
    * If a certain mode is restricted, an authorized user is required to continue the request.
    * 
    * @param mode
    *           the mode to check
    * @return <code>true</code> if the given mode is restricted, <code>false</code> otherwise.
    */
   protected boolean isRestrictedPortletMode(PortletMode mode) {
      return mode == null || PortletMode.EDIT.equals(mode)
            || CmscPortletMode.EDIT_DEFAULTS.equals(mode);
   }

   /**
    * Answers whether the given {@link PortletRequest} is allowed. A request is allowed if:
    * <ol>
    * <li>it's {@link PortletMode} is not restricted. See also
    * {@link #isRestrictedPortletMode(PortletMode)}.</li>
    * <li>it's {@link PortletMode} is restricted and the current user is allowed to request the
    * portlet in that mode.</li>
    * </ol>
    * Subclasses may override this method for different behavior.
    * 
    * @param request
    *           the request to check
    * @return <code>true</code> if the request is allowed, <code>false</code> otherwise.
    */
   protected boolean isRequestAllowed(PortletRequest request) {
      // TODO Move to a service?
      
      PortletMode mode = request.getPortletMode();
      if (isRestrictedPortletMode(mode)) {

         Cloud cloud = CloudUtil.getCloudFromThread();
         if (cloud != null) {
            Node userNode = SecurityUtil.getUserNode(cloud);
            if (userNode != null) {
               return SecurityUtil.isLoggedInUser(cloud, userNode);
            }
         }

         return false;
      }

      return true;
   }

   /*
    * @see javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   @Override
   public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
      if (getLogger().isDebugEnabled()) {
         getLogger().debug("===> process " + getPortletName() + " mode = " + req.getPortletMode());
      }

      if (isRequestAllowed(req)) {

         PortletMode mode = req.getPortletMode();
   
         if (mode.equals(PortletMode.VIEW)) {
            processView(req, res);
         }
         else if (mode.equals(CmscPortletMode.ABOUT)) {
            processAbout(req, res);
         }
         else if (mode.equals(CmscPortletMode.CONFIG)) {
            processConfig(req, res);
         }
         else if (mode.equals(PortletMode.EDIT)) {
            processEdit(req, res);
         }
         else if (mode.equals(CmscPortletMode.EDIT_DEFAULTS)) {
            processEditDefaults(req, res);
         }
         else if (mode.equals(PortletMode.HELP)) {
            processHelp(req, res);
         }
         else if (mode.equals(CmscPortletMode.PREVIEW)) {
            processPreview(req, res);
         }
         else if (mode.equals(CmscPortletMode.PRINT)) {
            processPrint(req, res);
         }
         else {
            throw new PortletException(mode.toString());
         }
      }
      else {
         getLogger().warn("Prevented unauthorised access to portlet: " + getPortletName());
      }
   }

   @SuppressWarnings("unused")
   public void processPrint(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   @SuppressWarnings("unused")
   public void processPreview(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   @SuppressWarnings("unused") 
   public void processHelp(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   public void processEditDefaults(ActionRequest request, ActionResponse response)
         throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         for (Enumeration<String> iterator = request.getParameterNames(); iterator
               .hasMoreElements();) {
            String parameterName = iterator.nextElement();

            if (parameterName.toLowerCase().startsWith(PARAM_PREFIX)) {
               String[] parameterValues = request.getParameterValues(parameterName);
               setPortletParameter(portletId, parameterName.substring(PARAM_PREFIX.length()),
                     parameterValues);
            }
            if (parameterName.toLowerCase().startsWith(NODEPARAM_PREFIX)) {
               String[] parameterValues = request.getParameterValues(parameterName);
               setPortletParameter(portletId, parameterName.substring(NODEPARAM_PREFIX.length()),
                     parameterValues);
            }
         }
         saveParameters(request, portletId);
      }
      else {
         getLogger().error("No portletId");
      }
      // switch to View mode
      response.setPortletMode(PortletMode.VIEW);
   }

   @SuppressWarnings("unused")
   protected void saveParameters(ActionRequest request, String portletId) {
      // convenience method
   }

   @SuppressWarnings("unused")
   public void processEdit(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   @SuppressWarnings("unused") 
   public void processConfig(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   @SuppressWarnings("unused") 
   public void processAbout(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   @SuppressWarnings("unused") 
   public void processView(ActionRequest req, ActionResponse res) throws PortletException,
         IOException {
      // convenience method
   }

   /*
    * @see javax.portlet.GenericPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
    */
   @Override
   protected void doDispatch(RenderRequest req, RenderResponse res) throws IOException, PortletException {

      if (getLogger().isDebugEnabled()) {
         getLogger().debug(
               "===> " + getPortletName() + " mode = " + req.getPortletMode() + " window = "
                     + req.getWindowState());
      }
      
      if (isRequestAllowed(req)) {

         WindowState state = req.getWindowState();
   
         if (!state.equals(WindowState.MINIMIZED)) {
            PortletMode mode = req.getPortletMode();
   
            if (mode.equals(PortletMode.VIEW)) {
               doView(req, res);
            }
            else if (mode.equals(CmscPortletMode.ABOUT)) {
               doAbout(req, res);
            }
            else if (mode.equals(CmscPortletMode.CONFIG)) {
               doConfig(req, res);
            }
            else if (mode.equals(PortletMode.EDIT)) {
               doEdit(req, res);
            }
            else if (mode.equals(CmscPortletMode.EDIT_DEFAULTS)) {
               doEditDefaults(req, res);
            }
            else if (mode.equals(PortletMode.HELP)) {
               doHelp(req, res);
            }
            else if (mode.equals(CmscPortletMode.PREVIEW)) {
               doPreview(req, res);
            }
            else if (mode.equals(CmscPortletMode.PRINT)) {
               doPrint(req, res);
            }
            else {
               throw new PortletException(mode.toString());
            }
         }
      }
      else {
         getLogger().warn("Prevented unauthorised access to portlet: " + getPortletName());
      }
   }

   protected List<Locale> getLocales(RenderRequest request) {
      PortletMode mode = request.getPortletMode();

      List<Locale> locales = new ArrayList<Locale>();

      Locale siteLocale = null;
      if (mode.equals(PortletMode.VIEW) || mode.equals(PortletMode.EDIT)) {
         siteLocale = (Locale) request.getAttribute("siteLocale");
         if (siteLocale == null) {
            siteLocale = request.getLocale();
         }
         locales.add(siteLocale);
      }
      Locale editorsLocale = getEditorLocale(request, siteLocale);

      if (editorsLocale != null && !editorsLocale.equals(siteLocale)) {
         locales.add(editorsLocale);
      }

      return locales;
   }

   protected Locale getEditorLocale(RenderRequest request, Locale defaultLocale) {
      Locale editorsLocale = (Locale) request.getAttribute("editorsLocale");
      if (editorsLocale == null) {
         Cloud cloud = CloudUtil.getCloudFromThread();
         if (cloud != null) {
            Locale userLocale = getUserLocale(cloud);
            if (userLocale != null) {
               editorsLocale = userLocale;
            }
         }

         if (editorsLocale == null) {
            if (defaultLocale == null) {
               defaultLocale = request.getLocale();
            }

            editorsLocale = defaultLocale;
         }
         request.setAttribute("editorsLocale", editorsLocale);
      }
      return editorsLocale;
   }

   private Locale getUserLocale(Cloud cloud) {
      Locale userLocale = null;
      String username = cloud.getUser().getIdentifier();
      if (username != null && !username.equals("anonymous")) {
         Node userNode = SecurityUtil.getUserNode(cloud);
         if (userNode != null) {
            String userLanguage = userNode.getStringValue("language");
            if (StringUtils.isNotEmpty(userLanguage)) {
               userLocale = new Locale(userLanguage);
            }
         }
      }
      return userLocale;
   }

   /**
    * This will set both the primary and the secondary resource bundle (which is used when in edit
    * modus)
    * 
    * @param req
    * @param template
    */
   protected void setResourceBundle(RenderRequest req, String template) {
      String baseName = getResourceBaseName(template);

      List<Locale> locales = getLocales(req);
      int count = 0;
      for (Locale locale : locales) {
         ResourceBundle bundle = getResourceBundle(locale, baseName);

         // this is JSTL specific, but the problem is that a RenderRequest is
         // not a ServletRequest
         if (bundle != null) {
            if (count == 0 || locales.size() == 1) {
               LocalizationContext ctx = new LocalizationContext(bundle, locale);
               req.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request", ctx);
            }
            else {
               LocalizationContext ctx = new LocalizationContext(bundle, locale);
               req.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request.editors", ctx);
            }
            count++;
         }
      }
   }

   protected String getResourceBaseName(String template) {
      String baseName = null;
      if (StringUtils.isNotEmpty(template)) {
         int extensionIndex = template.lastIndexOf(".");
         if (extensionIndex > -1) {
            baseName = template.substring(0, extensionIndex);
         }
         else {
            baseName = template;
         }
      }
      return baseName;
   }

   protected ResourceBundle getResourceBundle(Locale locale, String baseName) {
      ResourceBundle bundle = null;
      CombinedResourceBundle cbundle = null;

      while (StringUtils.isNotEmpty(baseName)) {
         try {
            ResourceBundle otherbundle = ResourceBundle.getBundle(baseName, locale);
            if (cbundle == null) {
               cbundle = new CombinedResourceBundle(otherbundle);
            }
            else {
               cbundle.addBundles(otherbundle);
            }
         }
         catch (java.util.MissingResourceException mre) {
            log.debug("Resource bundel not found for basename " + baseName);
         }
         int lastIndex = baseName.lastIndexOf("/");
         if (lastIndex > -1) {
            baseName = baseName.substring(0, lastIndex);
         }
         else {
            baseName = null;
         }
      }
      ResourceBundle portletbundle = getResourceBundle(locale);
      if (portletbundle == null) {
         bundle = cbundle;
      }
      else {
         if (cbundle == null) {
            bundle = portletbundle;
         }
         else {
            cbundle.addBundles(portletbundle);
            bundle = cbundle;
         }
      }
      return bundle;
   }

   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException,
         java.io.IOException {
      PortletPreferences preferences = req.getPreferences();
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);

      String contentType = req.getResponseContentType();
      String location = routings.getProperty(contentType);

      log.debug(String.format("Use %s as view from %s", template, location));
      doInclude(location.trim(), template, req, res);
   }

   @Override
   protected void doEdit(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      PortletPreferences preferences = req.getPreferences();
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
      doInclude("edit", template, req, res);
   }

   @Override
   protected void doHelp(RenderRequest req, RenderResponse res) throws PortletException,
         IOException {
      doInclude("help", null, req, res);
   }

   protected void doAbout(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      doInclude("about", null, req, res);
   }

   protected void doConfig(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      doInclude("config", null, req, res);
   }

   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      PortletPreferences preferences = req.getPreferences();
      for (Enumeration<String> iterator = preferences.getNames(); iterator.hasMoreElements();) {
         String parameterName = iterator.nextElement();
         if (req.getAttribute(parameterName) == null) {
            String[] parameterValues = preferences.getValues(parameterName, null);
            if (parameterValues != null && parameterValues.length != 0) {
               if (parameterValues.length > 1) {
                  List<String> values = Arrays.asList(parameterValues);
                  setAttribute(req, parameterName, values);
               }
               else {
                  setAttribute(req, parameterName, parameterValues[0]);
               }
            }
         }
      }
      doInclude("edit_defaults", null, req, res);
   }

   protected void addViewInfo(RenderRequest req) {
      PortletPreferences preferences = req.getPreferences();
      String definitionId = preferences
            .getValue(PortalConstants.CMSC_OM_PORTLET_DEFINITIONID, null);

      List<View> views = SiteManagement.getViews(definitionId);
      setAttribute(req, "views", views);

      String viewId = preferences.getValue(PortalConstants.CMSC_OM_VIEW_ID, null);
      if (StringUtils.isNotEmpty(viewId)) {
         setAttribute(req, "view", viewId);
      }
   }

   protected void doPreview(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      doInclude("preview", null, req, res);
   }

   protected void doPrint(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      doInclude("print", null, req, res);
   }

   protected void doInclude(String type, String template, RenderRequest request,
         RenderResponse response) throws PortletException, IOException {
      setResourceBundle(request, template);

      String contentType = request.getResponseContentType();
      if (contentType == null) {
         contentType = CONTENT_TYPE_DEFAULT;
      }
      response.setContentType(contentType);
      PortletRequestDispatcher rd = getRequestDispatcher(type, template);
      rd.include(request, response);
   }

   protected PortletRequestDispatcher getRequestDispatcher(String type, String template) {
      String resourceExtension = "jsp";
      String fullTemplate = getTemplate(type, template, resourceExtension);

      if (!templateExists(fullTemplate)) {
         fullTemplate = getTemplate(type, "missing.jsp", resourceExtension);
         if (!templateExists(fullTemplate)) {
            String aggregationDir = getAggregationDir();
            fullTemplate = aggregationDir + "missing.jsp";
         }
      }

      PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(fullTemplate);
      return rd;
   }

   private boolean templateExists(String fullTemplate) {
      Set<String> webInfResources = getPortletContext().getResourcePaths(fullTemplate);
      /*
       * @see javax.servlet.ServletContext#getResourcePaths(String) getResourcePaths returns a Set
       *      containing the directory listing, or null if there are no resources in the web
       *      application whose path begins with the supplied path. we are using a full path instead
       *      of a partial path. webInfResources.isEmpty() is true when the resource exists
       */
      return webInfResources != null;
   }

   protected String getTemplate(String type, String template, String resourceExtension) {
      String baseDir = getPortletContext().getInitParameter("cmsc.portal." + type + ".base.dir");
      if (StringUtils.isEmpty(baseDir)) {
         String aggregationDir = getAggregationDir();
         baseDir = aggregationDir + type + "/";
      }

      logInitParameters();

      if (StringUtils.isEmpty(template)) {
         template = getInitParameter("template." + type);
         if (StringUtils.isEmpty(template)) {
            template = getPortletName() + "." + resourceExtension;
         }
      }
      return baseDir + template;
   }

   private String getAggregationDir() {
      String aggregationDir = getPortletContext().getInitParameter(
            "cmsc.portal.aggregation.base.dir");
      if (StringUtils.isEmpty(aggregationDir)) {
         aggregationDir = "/WEB-INF/templates/";
      }
      return aggregationDir;
   }

   protected void setAttribute(RenderRequest request, String var, Object value) {
      if (StringUtils.isNotEmpty(var)) {
         // put in variable
         if (value != null) {
            request.setAttribute(var, value);
         }
         else {
            request.removeAttribute(var);
         }
      }
   }

   protected void setPortletNodeParameter(String portletId, String key, String value) {
      PortletParameter param = new PortletParameter();
      param.setKey(key);
      param.setValue(value);
      SiteManagementAdmin.setPortletNodeParameter(portletId, param);
   }

   protected void setPortletParameter(String portletId, String key, String value) {
      PortletParameter param = new PortletParameter();
      param.setKey(key);
      param.setValue(value);
      SiteManagementAdmin.setPortletParameter(portletId, param);
   }

   protected void setPortletNodeParameter(String portletId, String key, String[] values) {
      PortletParameter param = new PortletParameter();
      param.setKey(key);
      param.setValues(values);
      SiteManagementAdmin.setPortletNodeParameter(portletId, param);
   }

   protected void setPortletParameter(String portletId, String key, String[] values) {
      PortletParameter param = new PortletParameter();
      param.setKey(key);
      param.setValues(values);
      SiteManagementAdmin.setPortletParameter(portletId, param);
   }

   protected void setPortletView(String portletId, String viewId) {
      if (viewId != null) {
         SiteManagementAdmin.setPortletView(portletId, viewId);
      }
   }

   public String getUrlPath(RenderRequest request) {
      PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
      PortalURL currentURL = env.getRequestedPortalURL();
      return currentURL.getGlobalNavigationAsString();
   }

   protected void logInitParameters() {
      if (getLogger().isDebugEnabled()) {
         Enumeration<String> enumeration = getInitParameterNames();
         while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            getLogger().debug("Init-param " + name + " " + getInitParameter(name));
         }
      }
   }

   protected void logParameters(ActionRequest request) {
      if (getLogger().isDebugEnabled()) {
         Map<String, ?> map = request.getParameterMap();
         logMap(map);
      }
   }

   protected void logPreference(ActionRequest req) {
      if (getLogger().isDebugEnabled()) {
         PortletPreferences preferences = req.getPreferences();
         Map<String, ?> map = preferences.getMap();
         logMap(map);
      }
   }

   protected void logPreference(RenderRequest req) {
      if (getLogger().isDebugEnabled()) {
         PortletPreferences preferences = req.getPreferences();
         Map<String, ?> map = preferences.getMap();
         logMap(map);
      }
   }

   protected void logMap(Map<String, ?> map) {
      for (Map.Entry<String, ?> entry : map.entrySet()) {
         String key = entry.getKey();
         Object value = entry.getValue();
         if (key != null && value != null) {
            if (value instanceof List) {
               for (Iterator<String> iterator = ((List<String>) value).iterator(); iterator
                     .hasNext();) {
                  String val = iterator.next();
                  if (val != null) {
                     getLogger().debug("key: " + key + " value: " + val);
                  }
               }
            }
            else {
               if (value instanceof String[]) {
                  for (int i = 0; i < ((String[]) value).length; i++) {
                     String val = ((String[]) value)[i];
                     if (val != null) {
                        getLogger().debug("key: " + key + " value: " + val);
                     }
                  }
               }
               else {
                  if (value instanceof String) {
                     getLogger().debug("key: " + key + " value: " + (String) value);
                  }
                  else {
                     getLogger().debug("key: " + key + " value: " + value.toString());
                  }
               }
            }
         }
      }
   }

   protected PortletFragment getPortletFragment(PortletRequest request) {
      InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(request);
      ServletRequest servletRequest = ((HttpServletRequestWrapper) internalPortletRequest)
            .getRequest();
      return (PortletFragment) servletRequest.getAttribute(PortalConstants.FRAGMENT);
   }

   private void initRoutingRules() {
      if (routings == null) {
         InputStream inputStream = CmscPortlet.class.getResourceAsStream("viewrouting.properties");
         routings = new Properties();
         try {
            routings.load(inputStream);
         }
         catch (IOException e) {
            log.error(e);
         }
      }
   }
}
