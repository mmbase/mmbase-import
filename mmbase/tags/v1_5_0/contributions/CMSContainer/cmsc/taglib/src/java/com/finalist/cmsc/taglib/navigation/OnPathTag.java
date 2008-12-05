/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;

/**
 * Checks if a Site or Page is on the path
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.6 $
 */
public class OnPathTag extends CmscTag {
   /**
    * JSP variable name.
    */
   private String var;

   private NavigationItem origin;


   @Override
   public void doTag() throws JspException, IOException {
      boolean onpath = false;

      String path = getPath();
      List<NavigationItem> items = SiteManagement.getListFromPath(path);
      if (items != null) {
         for (int i = 0; i < items.size(); i++) {
            NavigationItem item = items.get(i);
            if (item.getId() == origin.getId()) {
               onpath = true;
            }
         }
      }

      if (var != null && var.length() > 0) {
         PageContext ctx = (PageContext) getJspContext();
         HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
         req.setAttribute(var, onpath);
      }
      else {
         if (onpath) {
            // handle body, call any nested tags
            JspFragment frag = getJspBody();
            if (frag != null) {
               frag.invoke(null);
            }
         }
      }
   }


   public void setOrigin(Object origin) {
       this.origin = SiteManagement.convertToNavigationItem(origin);
   }


   /**
    * Set the JSP variable name
    * 
    * @param var
    *           the JSP variable name
    */
   public void setVar(String var) {
      this.var = var;
   }

}
