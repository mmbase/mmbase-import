/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.newsletter.forms;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterUtil;

/**
 * Newsletter Edit Action
 *
 * @author Lisa
 */
public class NewsletterEdit extends MMBaseFormlessAction {

   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward, refresh NewsletterList
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");
      String forwardType = getParameter(request, "forward");

      if (StringUtils.isBlank(action)) {
         String objectnumber = getParameter(request, "number", true);
         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
                  + objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath()
                  + URLEncoder.encode("?forward") + "=" + forwardType + URLEncoder.encode("&objectnumber") + "="
                  + objectnumber);
         ret.setRedirect(true);
         return ret;
      } else {
         SecurityUtil.clearUserRoles(cloud);

         String ewnodelastedited = getParameter(request, "ewnodelastedited");
         Node newsletterNode = cloud.getNode(ewnodelastedited);
         NewsletterUtil.addScheduleForNewsletter(newsletterNode);
         addToRequest(request, "showpage", ewnodelastedited);

//         if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
//            Publish.publish(newsletterNode);
//         }

         int nodeId = Integer.parseInt(request.getParameter("objectnumber"));
         ActionForward ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId=" + nodeId+ "&fresh=fresh");
         if (forwardType.equals("manage")) {
            ret = mapping.findForward("newslettermanage");
         }
         return ret;
      }
   }

}
