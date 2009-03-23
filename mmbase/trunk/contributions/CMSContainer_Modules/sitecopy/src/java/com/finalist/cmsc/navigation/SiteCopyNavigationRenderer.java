package com.finalist.cmsc.navigation;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.beans.om.NavigationItem;

public class SiteCopyNavigationRenderer implements NavigationItemRenderer {

   public String getContentType() {
      return null;
   }

   public void render(NavigationItem item, HttpServletRequest request,
         HttpServletResponse response, ServletConfig servletConfigc)
         throws IOException {
      
   }

}
