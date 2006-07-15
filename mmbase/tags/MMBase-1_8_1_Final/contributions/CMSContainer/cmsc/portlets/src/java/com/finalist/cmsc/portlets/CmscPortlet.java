package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.*;

import javax.portlet.*;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.PortletParameter;
import com.finalist.cmsc.beans.om.View;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.*;

@SuppressWarnings("unused")
public abstract class CmscPortlet extends GenericPortlet {
	
    private Log log;
    
    protected Log getLogger() {
        if (log == null) {
            log = LogFactory.getLog(this.getClass());
        }
        return log;
    }
    
    /**
     * @see javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("===> process " + getPortletName() + " mode = " + req.getPortletMode());
        }
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
    
    public void processPrint(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processPreview(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processHelp(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processEditDefaults(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processEdit(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processConfig(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processAbout(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }

    public void processView(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        //convenience method
    }


    /**
     * @see javax.portlet.GenericPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    protected void doDispatch(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
    
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("===> " + getPortletName() + " mode = " + req.getPortletMode() + " window = " + req.getWindowState());
        }
        
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

    private void setResourceBundle(RenderRequest req, String baseName) {
        ResourceBundle bundle = null;
        if (StringUtil.isEmpty(baseName)) {
            bundle = getResourceBundle(req.getLocale());
        }
        else {
            try {
                bundle = ResourceBundle.getBundle(baseName, req.getLocale());
            }
            catch (java.util.MissingResourceException mre) {
                log.debug("Resource bundel not found for basename " + baseName);
            }
        }
        // this is JSTL specific, but the problem is that a RenderRequest is not a ServletRequest
        if (bundle != null) {
            LocalizationContext ctx = new LocalizationContext(bundle);
            req.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request", ctx);
        }
    }

    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, java.io.IOException {
        PortletPreferences preferences = req.getPreferences();
        String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
        doInclude("view", template, req, res);
    }
    
    protected void doEdit(RenderRequest req, RenderResponse res) 
    throws IOException, PortletException {
        PortletPreferences preferences = req.getPreferences();
        String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
        doInclude("edit", template, req, res);
    }

    protected void doHelp(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        doInclude("help", null, req, res);
    }
    
    protected void doAbout(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        doInclude("about", null, req, res);
        // throw new PortletException("doAbout method not implemented");
    }
    
    protected void doConfig(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        doInclude("config", null, req, res);
        // throw new PortletException("doConfig method not implemented");
    }
    
    protected void doEditDefaults(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        doInclude("edit_defaults", null, req, res);
        // throw new PortletException("doEditDefaults method not implemented");
    }

    protected void addViewInfo(RenderRequest req) {
        PortletPreferences preferences = req.getPreferences();
        String definitionId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_DEFINITIONID, null);

        List<View> views = SiteManagement.getViews(definitionId);
        setAttribute(req, "views", views);

        String viewId = preferences.getValue(PortalConstants.CMSC_OM_VIEW_ID, null);
        if (!StringUtil.isEmpty(viewId)) {
            setAttribute(req, "view", viewId);
        }
    }
    
    protected void doPreview(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        doInclude("preview", null, req, res);
        // throw new PortletException("doPreview method not implemented");
    }
    
    protected void doPrint(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        doInclude("print", null, req, res);
        // throw new PortletException("doPrint method not implemented");
    }

    protected void doInclude(String type, String template, RenderRequest request, RenderResponse response) throws PortletException, IOException {
        
        if (StringUtil.isEmpty(template)) {
            setResourceBundle(request, null);
        }
        else {
            String baseName = null;
            if (template.endsWith(".jsp")) {
                baseName = template.substring(0, template.length() - ".jsp".length());
            }
            else {
                baseName = template;
            }
            setResourceBundle(request, baseName);
        }
        
        response.setContentType("text/html");
        PortletRequestDispatcher rd = getRequestDispatcher(type, template);
        rd.include(request, response);
    }

    protected PortletRequestDispatcher getRequestDispatcher(String type, String template) {
        String baseDir = getPortletContext().getInitParameter("cmsc.portal." + type + ".base.dir");
        if (StringUtil.isEmpty(baseDir)) {
            String aggregationDir = getPortletContext().getInitParameter("cmsc.portal.aggregation.base.dir");    
            if (StringUtil.isEmpty(aggregationDir)) {
                aggregationDir = "/WEB-INF/templates/";
            }
            baseDir = aggregationDir + type + "/";
        }
        
        logInitParameters();
        
        
        if (StringUtil.isEmpty(template)) {
            template = getInitParameter("template." + type);
            if (StringUtil.isEmpty(template)) {
                template = getPortletName() + ".jsp";
            }
        }
        PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(baseDir + template);
        return rd;
    }

    protected void setAttribute(RenderRequest request, String var, Object value) {
        if (!StringUtil.isEmpty(var)) {
            // put in variable
            if (value != null) {
                request.setAttribute(var, value);
            } else {
                request.removeAttribute(var);
            }
        }
    }
    
    protected void setPorltetNodeParameter(String portletId, String key, String value) {
        if (value != null) {
            PortletParameter param = new PortletParameter();
            param.setKey(key);
            param.setValue(value);
            SiteManagement.setPortletNodeParameter(portletId, param);
        }
    }

    protected void setPorltetParameter(String portletId, String key, String value) {
        if (value != null) {
            PortletParameter param = new PortletParameter();
            param.setKey(key);
            param.setValue(value);
            SiteManagement.setPortletParameter(portletId, param);
        }
    }

    protected void setPortletView(String portletId, String viewId) {
        if (viewId != null) {
            SiteManagement.setPortletView(portletId, viewId);
        }
    }

    public String getUrlPath(RenderRequest request) {
        PortalEnvironment env = (PortalEnvironment) request.getAttribute(PortalEnvironment.REQUEST_PORTALENV);
        PortalURL currentURL = env.getRequestedPortalURL();
        return currentURL.getGlobalNavigationAsString();
    }

    protected void logInitParameters() {
        if (getLogger().isDebugEnabled()) {
            Enumeration enumeration = getInitParameterNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                getLogger().debug("Init-param " + name + " " + getInitParameter(name));
            }
        }
    }
    
    protected void logParameters(ActionRequest request) {
        if (getLogger().isDebugEnabled()) {
            Map map = request.getParameterMap();
            logMap(map);
        }
    }

    protected void logPreference(ActionRequest req) {
        if (getLogger().isDebugEnabled()) {
            PortletPreferences preferences = req.getPreferences();
            Map map = preferences.getMap();
            logMap(map);
        }
    }
    
    protected void logPreference(RenderRequest req) {
        if (getLogger().isDebugEnabled()) {
            PortletPreferences preferences = req.getPreferences();
            Map map = preferences.getMap();
            logMap(map);
        }
    }
    
    protected void logMap(Map map) {
        for (Object entry : map.entrySet()) {
            String key = (String) ((Map.Entry)entry).getKey();
            Object value = ((Map.Entry)entry).getValue();
            if (key != null && value != null) {
                if (value instanceof List) {
                    for (Iterator iterator = ((List)value).iterator(); iterator.hasNext();) {
                        String val = (String) iterator.next();
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

}
