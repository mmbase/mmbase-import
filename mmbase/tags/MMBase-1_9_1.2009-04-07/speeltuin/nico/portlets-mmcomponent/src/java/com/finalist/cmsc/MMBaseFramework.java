/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc;

import java.io.Writer;
import java.util.Map;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;
import org.mmbase.framework.*;
import org.mmbase.framework.Renderer.WindowState;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;


public class MMBaseFramework extends BasicFramework implements Framework {

    public static final Parameter<PortletRequest> PORTLETREQUEST = new Parameter<PortletRequest>("portletrequest", PortletRequest.class);
    public static final Parameter<PortletResponse> PORTLETRESPONSE = new Parameter<PortletResponse>("portletresponse", PortletResponse.class);

    public String getName() {
        return "CMS Container";
    }
    
    public Parameters createFrameworkParameters() {
        return new Parameters(Parameter.REQUEST, new Parameter<String>("component", String.class),
                                new Parameter<String>("block", String.class));
    }

    public StringBuilder getBlockUrl(Block block, Component component, Parameters blockParameters, Parameters frameworkParameters, WindowState state, boolean escapeAmps) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        Boolean isPortalMode = (Boolean) request.getAttribute("com.finalist.cmsc.MMBaseFramework.portal");
        if (isPortalMode == null || !isPortalMode) {
            return super.getBlockUrl(block, component, blockParameters, frameworkParameters, state, escapeAmps);
        }
        
        RenderResponse renderResponse = (RenderResponse)request.getAttribute("javax.portlet.response");

        if (renderResponse != null)
        {
            PortletURL pUrl = renderResponse.createRenderURL();
            pUrl.setParameter("block", block.getName());
            
            for (Map.Entry<String, ? extends Object> entry : blockParameters.toMap().entrySet()) {
                Object value = entry.getValue();
                if (value != null && Casting.isStringRepresentable(value.getClass())) { // if not string representable, that suppose it was an 'automatic' parameter which does need presenting on url
                    if (value instanceof Iterable) {
                        for (Object v : (Iterable) value) {
                            pUrl.setParameter(entry.getKey(), Casting.toString(v));
                        }
                    } else {
                        pUrl.setParameter(entry.getKey(), Casting.toString(value));
                    }
                }
            }
            
            try {
                if (state == Renderer.WindowState.MAXIMIZED) {
                        pUrl.setWindowState(javax.portlet.WindowState.MAXIMIZED);
                }
                else {
                    if (state == Renderer.WindowState.MINIMIZED) {
                        pUrl.setWindowState(javax.portlet.WindowState.MINIMIZED);
                    }
                    else {
                        pUrl.setWindowState(javax.portlet.WindowState.NORMAL);
                    }
                }
            }
            catch (WindowStateException e) {
                // WindowState must be supported by portal according to jsr-168
            }
            String url = pUrl.toString();
            url = url.substring(request.getContextPath().length());
            return new StringBuilder(url);
        }
        return null;
    }

    public StringBuilder getInternalUrl(String path, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (request != null) {
            Boolean isPortalMode = (Boolean) request.getAttribute("com.finalist.cmsc.MMBaseFramework.portal");
            if (isPortalMode == null || !isPortalMode) {
                return super.getInternalUrl(path, renderer, component, blockParameters, frameworkParameters);
            }
        }
        StringBuilder builder = new StringBuilder();
//        if (component != null) {
//            builder.append(component.getName()).append("/");
//        }
        return builder.append(path);
    }

    public StringBuilder getInternalUrl(String path, Processor processor, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (request != null) {
            Boolean isPortalMode = (Boolean) request.getAttribute("com.finalist.cmsc.MMBaseFramework.portal");
            if (isPortalMode == null || !isPortalMode) {
                return super.getInternalUrl(path, processor, component, blockParameters, frameworkParameters);
            }
        }
        StringBuilder builder = new StringBuilder();
//        if (component != null) {
//            builder.append(component.getName()).append("/");
//        }
        return builder.append(path);
    }

    public StringBuilder getUrl(String path, Component component, Parameters urlParameters, Parameters frameworkParameters, boolean escapeAmps) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        Boolean isPortalMode = (Boolean) request.getAttribute("com.finalist.cmsc.MMBaseFramework.portal");
        if (isPortalMode == null || !isPortalMode) {
            return super.getUrl(path, component, urlParameters, frameworkParameters, escapeAmps);
        }
        StringBuilder builder = new StringBuilder();
        if (component != null) {
            builder.append("/").append(component.getName());
        }
        return builder.append(path);
    }

    public void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException {
        throw new UnsupportedOperationException("CMSC portal does not support this.");
    }

    public void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w, WindowState state) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        request.setAttribute("com.finalist.cmsc.MMBaseFramework.portal", Boolean.FALSE);
        super.render(renderer, blockParameters, frameworkParameters, w, state);
    }

    public String getUserBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public Node getUserNode(Parameters frameworkParameters) {
        // TODO Auto-generated method stub
        return null;
    }

}
