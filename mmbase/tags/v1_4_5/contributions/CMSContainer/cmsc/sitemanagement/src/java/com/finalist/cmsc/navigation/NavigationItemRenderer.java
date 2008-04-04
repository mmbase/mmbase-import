package com.finalist.cmsc.navigation;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.beans.om.NavigationItem;

public interface NavigationItemRenderer {

   void render(NavigationItem item, HttpServletRequest request, 
           HttpServletResponse response, ServletConfig servletConfigc);
}
