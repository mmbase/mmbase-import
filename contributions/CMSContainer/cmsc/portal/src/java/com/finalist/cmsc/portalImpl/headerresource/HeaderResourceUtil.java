/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.headerresource;

import java.util.Map;

import javax.portlet.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.*;

import org.apache.pluto.core.*;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;

public class HeaderResourceUtil {

    public static void addHeaderInfo(PortletRequest portletRequest, String elementName, Map attributes, String text) {
        HeaderResource resource = getHeaderResource(portletRequest);
        resource.addHeaderInfo(elementName, attributes, text);
    }
    
    public static void addJavaScript(PortletRequest portletRequest, PortletResponse portletResponse, String path, boolean defer) {
        HeaderResource resource = getHeaderResource(portletRequest);
        String resourcePath = getEncodedPath(portletResponse, path);
        resource.addJavaScript(resourcePath, defer);
    }

    public static void addJavaScript(PortletRequest portletRequest, PortletResponse portletResponse, String path) {
        HeaderResource resource = getHeaderResource(portletRequest);
        String resourcePath = getEncodedPath(portletResponse, path);
        resource.addJavaScript(resourcePath);
    }
    
    public static void addStyleSheet(PortletRequest portletRequest, PortletResponse portletResponse, String path) {
        HeaderResource resource = getHeaderResource(portletRequest);
        String resourcePath = getEncodedPath(portletResponse, path);
        resource.addStyleSheet(resourcePath);
    }
    
    public static void addMeta(PortletRequest portletRequest, String name, String value) {
        HeaderResource resource = getHeaderResource(portletRequest);
        resource.addMeta(name, value);
    }
    
    public static void addMeta(PortletRequest portletRequest, String name, String value, String lang) {
        HeaderResource resource = getHeaderResource(portletRequest);
        resource.addMeta(name, value, lang);
    }
    
    public static void addMeta(PortletRequest portletRequest, String name, String value, String lang, String header) {
        HeaderResource resource = getHeaderResource(portletRequest);
        resource.addMeta(name, value, lang, header);
    }
    
    
    private static HeaderResource getHeaderResource(PortletRequest portletRequest) {
        InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(portletRequest);
        ServletRequest servletRequest = ((HttpServletRequestWrapper) internalPortletRequest).getRequest();
        PortletFragment fragment = (PortletFragment) servletRequest.getAttribute(PortalConstants.FRAGMENT);
        HeaderResource resource = fragment.getHeaderResource();
        return resource;
    }

    private static String getEncodedPath(PortletResponse portletResponse, String path) {
        InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse(portletResponse);
        ServletResponse servletResponse = ((HttpServletResponseWrapper) internalPortletResponse).getResponse();
        String resourcePath = ((HttpServletResponse) servletResponse).encodeURL( path );
        return resourcePath;
    }
    
}
