package com.finalist.cmsc.portalImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;

public interface NavigationItemRenderer extends NavigationTreeItemRenderer {

   public abstract void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
         ServletContext servletContext, ServletConfig sc, PortalRegistry registry);
}
