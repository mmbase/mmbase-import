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

import org.mmbase.bridge.Node;
import org.mmbase.framework.*;
import org.mmbase.framework.Renderer.WindowState;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;


public class MMBaseFramework implements Framework {

    public static final Parameter<PortletRequest> PORTLETREQUEST = new Parameter<PortletRequest>("portletrequest", PortletRequest.class);
    public static final Parameter<PortletResponse> PORTLETRESPONSE = new Parameter<PortletResponse>("portletresponse", PortletResponse.class);

    public String getName() {
        return "CMSContainer";
    }
    
    public Parameters createFrameworkParameters() {
        return new Parameters(PORTLETREQUEST, PORTLETRESPONSE,
                                new Parameter<String>("component", String.class),
                                new Parameter<String>("block", String.class));
    }

    public StringBuilder getBlockUrl(Block block, Component component, Parameters blockParameters, Parameters frameworkParameters, WindowState state, boolean escapeAmps) {
        
        Object response = frameworkParameters.get(PORTLETRESPONSE);
        if (response instanceof RenderResponse) {
            RenderResponse renderResponse = (RenderResponse) response;
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
        }
        return null;
    }

    public StringBuilder getInternalUrl(String path, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        StringBuilder builder = new StringBuilder();
//        if (component != null) {
//            builder.append(component.getName()).append("/");
//        }
        return builder.append(path);
    }

    public StringBuilder getInternalUrl(String path, Processor processor, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        StringBuilder builder = new StringBuilder();
//        if (component != null) {
//            builder.append(component.getName()).append("/");
//        }
        return builder.append(path);
    }

    public StringBuilder getUrl(String path, Component component, Parameters urlParameters, Parameters frameworkParameters, boolean escapeAmps) {
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
        throw new UnsupportedOperationException("CMSC portal does not support this.");
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
