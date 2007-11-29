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

      String parentnewsletter = getParameter(request, "parentnewsletter", true);
      String action = getParameter(request, "action");

      if (StringUtil.isEmptyOrWhitespace(action)) {
         request.getSession().setAttribute("parentnewsletter", parentnewsletter);
         // Initialize the new publication
         Node publicationNode = NewsletterPublicationUtil.createPublication(parentnewsletter);
         request.getSession().removeAttribute("parentnewsletter");

         addToRequest(request, "showpage", publicationNode);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=EDIT" + "&contenttype=newsletterpublication"
               + "&returnurl=" + mapping.findForward("returnurl").getPath());
         ret.setRedirect(true);
         return ret;
      }
      request.getSession().removeAttribute("parentnewsletter");
      ActionForward ret = mapping.findForward(CANCEL);
      return ret;
   }
}
