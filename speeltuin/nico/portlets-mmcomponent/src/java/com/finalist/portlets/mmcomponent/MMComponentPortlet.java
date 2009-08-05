/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.portlets.mmcomponent;

import java.io.IOException;
import java.util.*;

import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.pluto.core.*;
import org.mmbase.framework.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

import com.finalist.cmsc.MMBaseFramework;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.CmscPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;


public class MMComponentPortlet extends CmscPortlet {

    private static final String COMPONENT = "component";
    protected static final String ACTION_PARAM = "action";
    private static final String BLOCK = "block";
    
    private String initComponent;

    @Override
    public void init(PortletConfig config) throws PortletException {
        String initComponent = config.getInitParameter(COMPONENT);
        if (!StringUtil.isEmpty(initComponent)) {
            this.initComponent = initComponent;
        }
        super.init(config);
    }
    
    @Override
    public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException {
        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
            if (portletId != null) {
                for (Enumeration<String> iterator = request.getParameterNames(); iterator.hasMoreElements();) {
                    String name = iterator.nextElement();
                    String value = request.getParameter(name);
                    if (value.startsWith("node.")) {
                        setPortletNodeParameter(portletId, name, value.substring("node.".length()));
                    }
                    else {
                        setPortletParameter(portletId, name, value);
                    }
                }
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }
    
    @Override
    public void processView(ActionRequest req, ActionResponse res) throws PortletException, IOException {
        PortletPreferences preferences = req.getPreferences();
        String componentName = preferences.getValue(COMPONENT, null);
        if (StringUtil.isEmpty(componentName)) {
            componentName = initComponent;
        }
        if (!StringUtil.isEmpty(componentName)) {
            Component component = ComponentRepository.getInstance().getComponent(componentName);
            String blockname = req.getParameter(BLOCK);
            Block block;
            if (!StringUtil.isEmpty(blockname)) {
                block = component.getDefaultBlock();
            }
            else {
                block = component.getBlock(blockname);
            }
        }
    }
    
    @Override
    protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
        if (!StringUtil.isEmpty(initComponent)) {
            addComponentInfo(req);
        }

        super.doEditDefaults(req, res);
    }
    
    protected void addComponentInfo(RenderRequest req) {
        PortletPreferences preferences = req.getPreferences();

        Collection<Component> views = ComponentRepository.getInstance().getComponents();
        setAttribute(req, "components", views);

        String componentId = preferences.getValue(COMPONENT, null);
        if (!StringUtil.isEmpty(componentId)) {
            setAttribute(req, "component", componentId);
        }
    }
    
    @Override
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        PortletPreferences preferences = req.getPreferences();
        
        Enumeration<String> p = preferences.getNames();
        while (p.hasMoreElements()) {
            String pref = p.nextElement();
            req.setAttribute(pref, preferences.getValue(pref, null));
        }
        
//        setResourceBundle(res, null);
        res.setContentType("text/html");

        
        String componentName = preferences.getValue(COMPONENT, null);
        if (StringUtil.isEmpty(componentName)) {
            componentName = initComponent;
        }
        if (!StringUtil.isEmpty(componentName)) {
            Component component = ComponentRepository.getInstance().getComponent(componentName);
            String blockname = req.getParameter(BLOCK);
            Block block;
            if (StringUtil.isEmpty(blockname)) {
                block = component.getDefaultBlock();
            }
            else {
                block = component.getBlock(blockname);
            }
            
            InternalPortletRequest internalRequest = CoreUtils.getInternalRequest(req);
            InternalPortletResponse internalResponse = CoreUtils.getInternalResponse(res);

            try
            {
                internalRequest.setIncluded(true);
                internalResponse.setIncluded(true);

                Renderer.WindowState state;
                if (req.getWindowState() == WindowState.MAXIMIZED) {
                    state = Renderer.WindowState.MAXIMIZED;
                }
                else {
                    if (req.getWindowState() == WindowState.MINIMIZED) {
                        state = Renderer.WindowState.MINIMIZED;
                    }
                    else {
                        state = Renderer.WindowState.NORMAL;
                    }
                }

                Parameters blockParameters = block.createParameters();
                blockParameters.set(Parameter.REQUEST, (javax.servlet.http.HttpServletRequest)req);
                blockParameters.set(Parameter.RESPONSE, (javax.servlet.http.HttpServletResponse)res);

                Parameters frameworkParameters = MMBase.getMMBase().getFramework().createFrameworkParameters();
//                frameworkParameters.set(MMBaseFramework.PORTLETREQUEST, req);
//                frameworkParameters.set(MMBaseFramework.PORTLETRESPONSE, res);
                frameworkParameters.set("component", componentName);
                frameworkParameters.set("block", blockname);
                
                try {
                    
                    Renderer renderer = block.getRenderer(Renderer.Type.BODY);
                    req.setAttribute(Renderer.KEY, renderer);
                    req.setAttribute("com.finalist.cmsc.MMBaseFramework.portal", Boolean.TRUE);
                    renderer.render(blockParameters, frameworkParameters, res.getWriter(), state);
                }
                catch (FrameworkException e) {
                    throw new PortletException(e);
                }
            }
            catch (java.io.IOException e)
            {
                throw e;
            }
            finally
            {
                internalRequest.setIncluded(false);
                internalResponse.setIncluded(false);
            }
        }
    }

}
