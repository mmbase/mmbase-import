/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;

/**
 * Tag to show the title of a Screen
 * 
 * @author Wouter Heijke
 */
public class TitleTag extends CmscTag {

   /**
    * JSP variable name.
    */
   public String var;

   /**
    * include site title
    */
   public boolean site = true;


   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
      if (container != null) {
         Page page = container.getPage();
         String title = page.getTitle();
         if (site) {
            String path = getPath();
            Site site = SiteManagement.getSiteFromPath(path);
            if (!site.equals(page)) {
               title = site.getTitle() + " - " + title;
            }
         }

         // handle result
         if (var != null) {
            // put in variable
            if (title != null) {
               request.setAttribute(var, title);
            }
            else {
               request.removeAttribute(var);
            }
         }
         else {
            // write
            title = StringEscapeUtils.escapeHtml(title);
            ctx.getOut().print(title);
         }
      }
      else {
         throw new JspException("Couldn't find screen tag");
      }
   }


   public void setVar(String var) {
      this.var = var;
   }


   public void setSite(boolean site) {
      this.site = site;
   }
}
