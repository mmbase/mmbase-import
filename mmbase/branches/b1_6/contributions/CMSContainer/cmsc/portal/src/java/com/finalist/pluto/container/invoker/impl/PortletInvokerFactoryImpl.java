/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto.container.invoker.impl;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.factory.PortletInvokerFactory;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Implementation of the PortletInvokerFactory
 * 
 * @author Wouter Heijke
 */
public class PortletInvokerFactoryImpl implements PortletInvokerFactory {
   private static Log log = LogFactory.getLog(PortletInvokerFactoryImpl.class);

   private ServletConfig servletConfig;


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.factory.PortletInvokerFactory#getPortletInvoker(org.apache.pluto.om.portlet.PortletDefinition)
    */
   public PortletInvoker getPortletInvoker(PortletDefinition portletDefinition) {
      PortletInvoker invoker = new PortletInvokerImpl(portletDefinition, servletConfig);
      return invoker;
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.factory.PortletInvokerFactory#releasePortletInvoker(org.apache.pluto.invoker.PortletInvoker)
    */
   public void releasePortletInvoker(PortletInvoker invoker) {
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.factory.Factory#init(javax.servlet.ServletConfig,
    *      java.util.Map)
    */
   public void init(ServletConfig config, Map properties) throws Exception {
      servletConfig = config;
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.factory.Factory#destroy()
    */
   public void destroy() throws Exception {
   }

}
