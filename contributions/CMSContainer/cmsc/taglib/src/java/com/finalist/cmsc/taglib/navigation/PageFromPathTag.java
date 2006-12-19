/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.CloudProvider;

import com.finalist.cmsc.navigation.NavigationUtil;

/**
 * path of pages
 */
public class PageFromPathTag extends SimpleTagSupport {

   private String path;
   private String var;
    
   public void doTag() throws JspException, IOException {

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

      int number = -1;
      Node page = NavigationUtil.getPageFromPath(cloud, path);
      if(page != null) {
         number = page.getNumber();
      }
      
      
      if(number != -1) {
         request.setAttribute(var, number);
      }
      else {
         request.setAttribute(var, null);
      }
   }

   public void setPath(String path) {
      this.path = path;
   }

   public void setVar(String var) {
      this.var = var;
   }
   
}
