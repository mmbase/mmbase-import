package com.finalist.pluto.container.invoker.impl;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

import com.finalist.pluto.container.factory.PortletFactory;

/**
 * @author Wouter Heijke
 */
public class DirectPortletInvoker implements PortletInvoker {
   private static Log log = LogFactory.getLog(DirectPortletInvoker.class);

   /** servlet configuration */
   private final ServletConfig servletConfig;

   /** The portlet definition */
   private final PortletDefinition portletDefinition;

   /** The portlet */
   private Portlet portlet;

   private final ServletContext servletContext;

   private final PortletFactory portletFactory;


   public DirectPortletInvoker(PortletDefinition portletDefinition, ServletConfig servletConfig,
         PortletFactory portletFactory) {
      this.portletDefinition = portletDefinition;
      this.servletConfig = servletConfig;
      this.portletFactory = portletFactory;
      this.servletContext = servletConfig.getServletContext();
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.invoker.PortletInvoker#action(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   public void action(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      portlet = portletFactory.getPortletInstance(servletContext, portletDefinition);

      if (portlet == null) {
         throw new PortletException("Unable to instantiate portlet from class " + portletDefinition.getClassName());
      }
      portlet.processAction(request, response);
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.invoker.PortletInvoker#render(javax.portlet.RenderRequest,
    *      javax.portlet.RenderResponse)
    */
   public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      portlet = portletFactory.getPortletInstance(servletContext, portletDefinition);

      if (portlet == null) {
         throw new PortletException("Unable to instantiate portlet from class " + portletDefinition.getClassName());
      }

      try {
         request.setAttribute(org.apache.pluto.Constants.METHOD_ID, org.apache.pluto.Constants.METHOD_RENDER);
         request.setAttribute(org.apache.pluto.Constants.PORTLET_REQUEST, request);
         request.setAttribute(org.apache.pluto.Constants.PORTLET_RESPONSE, response);
         portlet.render(request, response);
      }
      finally {
         request.removeAttribute(org.apache.pluto.Constants.METHOD_ID);
         request.removeAttribute(org.apache.pluto.Constants.PORTLET_REQUEST);
         request.removeAttribute(org.apache.pluto.Constants.PORTLET_RESPONSE);
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.invoker.PortletInvoker#load(javax.portlet.PortletRequest,
    *      javax.portlet.RenderResponse)
    */
   public void load(PortletRequest request, RenderResponse response) throws PortletException {
      PortletContext portletContext = null;
      PortletConfig portletConfig = null;

      portlet = portletFactory.getPortletInstance(servletContext, portletDefinition);

      if (portlet == null) {
         throw new PortletException("Unable to instantiate portlet from class " + portletDefinition.getClassName());
      }
      portletContext = PortletObjectAccess.getPortletContext(servletContext, portletDefinition
            .getPortletApplicationDefinition());
      portletConfig = PortletObjectAccess.getPortletConfig(servletConfig, portletContext, portletDefinition);

      portlet.init(portletConfig);
   }

}
