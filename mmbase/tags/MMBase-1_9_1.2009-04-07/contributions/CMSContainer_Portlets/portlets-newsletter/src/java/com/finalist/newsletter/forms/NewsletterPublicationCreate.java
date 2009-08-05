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
import org.mmbase.bridge.RelationManager;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

/**
 * Newsletter Publication Create Action
 *
 * @author Lisa
 */
public class NewsletterPublicationCreate extends MMBaseFormlessAction {
   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward refresh NewsletterList
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      String forwardType = request.getParameter("forward");

      if (StringUtils.isBlank(action)) {
         int parent = Integer.parseInt(getParameter(request, "parent", true));
         boolean copyContent = Boolean.valueOf(getParameter(request, "copycontent", true));
         Node publicationNode = NewsletterPublicationUtil.createPublication(parent, copyContent);
         Node parentNode = cloud.getNode(parent);
         Node pubNode = cloud.getNode(publicationNode.getNumber());
         RelationManager relManager = cloud.getRelationManager("newsletter", "newsletterpublication", "related");
         relManager.createRelation(parentNode, pubNode).commit();

         String objectnumber = String.valueOf(publicationNode.getNumber());
         request.getSession().removeAttribute("parent");
         ActionForward ret = null;
         if (StringUtils.isNotEmpty(forwardType)) {
            ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber=" + objectnumber
                     + "&returnurl=" + mapping.findForward("publicationedit").getPath() + URLEncoder.encode("?forward")
                     + "=" + forwardType + URLEncoder.encode("&newsletterId") + "=" + parent);
         } else {
            ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber=" + objectnumber
                     + "&returnurl=" + mapping.findForward("returnurl").getPath());
         }
         ret.setRedirect(true);
         return ret;
      }
      String ewnodelastedited = getParameter(request, "ewnodelastedited");
      addToRequest(request, "showpage", ewnodelastedited);
      ActionForward ret;
      if (StringUtils.isNotEmpty(forwardType)) {
         ret = new ActionForward(mapping.findForward("publicationedit").getPath() + "?newsletterId="
                  + request.getParameter("newsletterId"));
      } else {
         ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId=" + ewnodelastedited
                  + "&fresh=fresh");
      }
      return ret;
   }
}
