/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPause extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String objectnumber = getParameter(request, "number", true);
      Node newsletterNode = cloud.getNode(objectnumber);

      int number = newsletterNode.getNumber();
      NewsletterUtil.pauseNewsletter(number);      

      return mapping.findForward(SUCCESS);
   }
}
