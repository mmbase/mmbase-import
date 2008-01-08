/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterPublicationCreate extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = request.getParameter("action");

      if (StringUtil.isEmptyOrWhitespace(action)) { // Initialize the new
         int parent = Integer.parseInt(getParameter(request, "parent", true));
         boolean copyContent = Boolean.valueOf(getParameter(request, "copycontent", true));
         Node publicationNode = NewsletterPublicationUtil.createPublication(parent, copyContent);
         String objectnumber = String.valueOf(publicationNode.getNumber());
         request.getSession().removeAttribute("parent");
         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber=" + objectnumber + "&returnurl="
               + mapping.findForward("returnurl").getPath());
         ret.setRedirect(true);
         return ret;
      }

      String ewnodelastedited = getParameter(request, "ewnodelastedited");
      addToRequest(request, "showpage", ewnodelastedited);
      ActionForward ret = mapping.findForward("SUCCESS");
      return ret;
   }
}
