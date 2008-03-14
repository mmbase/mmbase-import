/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.services.security.LoginSession;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class ProtectedTag extends SimpleTagSupport {

   /**
    * Attribute that determines whether or not this tag actually just works the
    * other way around.
    */
   private boolean inverse = false;

   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      LoginSession ls = SiteManagement.getLoginSession(request);

      // handle body, call any nested tags
      JspFragment frag = getJspBody();
      if (frag != null) {
         // normally display only when authenticated
         boolean processBody = !isInverse() && (ls != null) && ls.isAuthenticated();

         // or display when inversed and *not* authenticated
         processBody = (processBody || (isInverse() && (ls == null || !ls.isAuthenticated())));

         if (processBody) {
            frag.invoke(null);
         }
      }
   }

   /**
    * Returns the value of the inverse attribute. Defaults to false.
    *
    * @return the inverse
    */
   public boolean isInverse() {
      return inverse;
   }

   /**
    * Sets the value of the inverse attribute.
    *
    * @param inverse
    *           the inverse to set
    */
   public void setInverse(boolean inverse) {
      this.inverse = inverse;
   }
}