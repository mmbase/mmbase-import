package com.finalist.pluto.container.factory.impl;

import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.UnavailableException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.ControllerObjectAccess;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;

import com.finalist.pluto.container.factory.PortletFactory;

/**
 * Instantiate Portlets and remember them for later use
 *
 * @author Wouter Heijke
 */
public class PortletFactoryImpl implements PortletFactory {
	private static final Log log = LogFactory.getLog(PortletFactoryImpl.class);

	private HashMap portletCache;

	public PortletFactoryImpl() {
		this.portletCache = new HashMap();
	}

	public Portlet getPortletInstance(ServletContext servletContext, PortletDefinition pd) throws PortletException {
		Portlet portlet = null;

		if (pd != null) {
			String portletName = pd.getId().toString();
			try {
				synchronized (portletCache) {
					portlet = (Portlet) portletCache.get(portletName);
					if (portlet != null) {
						return portlet;
					}
					
					final String clazzName = pd.getClassName();
					portlet = (Portlet)Thread.currentThread().getContextClassLoader().loadClass(clazzName).newInstance();
		            PortletDefinitionCtrl portletDefCtrl = (PortletDefinitionCtrl)ControllerObjectAccess.get(pd);
		            portletDefCtrl.setPortletClassLoader(Thread.currentThread().getContextClassLoader());
					portletCache.put(portletName, portlet);
				}
			} catch (Throwable e) {
				throw new UnavailableException("Failed to load portlet " + pd.getClassName() + ": " + e.toString());
			}
		}
		return portlet;
	}

	public void destroy() {
		Iterator i = portletCache.keySet().iterator();
		while (i.hasNext()) {
			Portlet portletClass = (Portlet) i.next();
			portletClass.destroy();
		}	
	}
}
