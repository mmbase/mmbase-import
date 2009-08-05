package com.finalist.pluto.container.factory;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.ServletContext;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * A PortletFactory
 * 
 * @author Wouter Heijke
 */
public interface PortletFactory {
   Portlet getPortletInstance(ServletContext servletContext, PortletDefinition pd) throws PortletException;


   void destroy();
}
