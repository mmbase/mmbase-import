/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterUtil;

/**
 * Create Newsletter Action
 *
 * @author Lisa
 */
public class NewsletterCreate extends MMBaseFormlessAction {
   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward , refresh the Newsletter List
    * @throws Exception
    */

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String parentnewsletter = getParameter(request, "parentnewsletter", true);
      String action = getParameter(request, "action");

      if (StringUtils.isBlank(action)) {
         request.getSession().setAttribute("parentnewsletter", parentnewsletter);
         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=create"
                  + "&contenttype=newsletter" + "&returnurl=" + mapping.findForward("returnurl").getPath());
         ret.setRedirect(true);
         return ret;
      } else {
         if ("save".equals(action)) {
            String ewnodelastedited = getParameter(request, "ewnodelastedited");
            NavigationUtil.appendChild(cloud, parentnewsletter, ewnodelastedited);

            Node newNewsletter = cloud.getNode(ewnodelastedited);
            Node layoutNode = PagesUtil.getLayout(newNewsletter);
            PagesUtil.linkPortlets(newNewsletter, layoutNode);
            request.getSession().removeAttribute("parentnewsletter");

            // NewsletterPublicationUtil.createDefaultTerm(newNewsletter);
            NewsletterUtil.addScheduleForNewsletter(newNewsletter);
            newNewsletter.setStringValue("scheduledescription", NewsletterUtil.getScheduleMessageByExpression(
                     newNewsletter.getStringValue("schedule")));
            newNewsletter.commit();
//            if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
//               Publish.publish(newNewsletter);
//            }
            addToRequest(request, "showpage", ewnodelastedited);
            ActionForward ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId="
                     + ewnodelastedited + "&fresh=fresh");
            return ret;
         }
         request.getSession().removeAttribute("parentnewsletter");
         ActionForward ret = mapping.findForward(CANCEL);
         return ret;
      }
   }
}
