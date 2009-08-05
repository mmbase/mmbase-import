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

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * Newsletter Publication Edit Action
 *
 * @author Lisa
 */
public class NewsletterPublicationEdit extends MMBaseFormlessAction {
   
   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward refresh NewsletterList
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");
      String forwardType = request.getParameter("forward");
      String parent = getParameter(request, "parent");

      if (StringUtils.isBlank(action)) {
         String objectnumber = getParameter(request, "number", true);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
                  + objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath()
                  + URLEncoder.encode("?forward=") + forwardType + URLEncoder.encode("&number=") + objectnumber
                  + URLEncoder.encode("&parent=" + parent));
         ret.setRedirect(true);
         return ret;
      } else {
         SecurityUtil.clearUserRoles(cloud);
         String ewnodelastedited = getParameter(request, "ewnodelastedited");
         addToRequest(request, "showpage", ewnodelastedited);
         ActionForward ret = null;
         if (StringUtils.isNotBlank(forwardType) && !"null".equals(forwardType)) {
            ret = new ActionForward(mapping.findForward(forwardType).getPath() + "?newsletterId=" + parent);
         } else {
            ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId=" + ewnodelastedited
                     + "&fresh=fresh");
         }
         ret.setRedirect(true);
         return ret;
      }
   }

}
