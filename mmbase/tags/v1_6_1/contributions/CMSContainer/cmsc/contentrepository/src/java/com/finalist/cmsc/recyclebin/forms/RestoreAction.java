/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.recyclebin.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class RestoreAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      if (!RepositoryUtil.hasRecyclebinRights(cloud, "webmaster")) {
         return redirectLogin(request);
      }       
       
      String objectnumber = getParameter(request, "objectnumber");
      Node objectNode = cloud.getNode(objectnumber);

      NodeList contentchannels = RepositoryUtil.getDeletionChannels(objectNode);
      if (contentchannels.size() > 0) {
         if (contentchannels.size() == 1) {
            Node channelNode = contentchannels.getNode(0);
            RepositoryUtil.addContentToChannel(objectNode, channelNode);
            Workflow.create(objectNode, null);
         }
         else {
            String channelnumber = getParameter(request, "channelnumber");
            if (StringUtils.isNotEmpty(channelnumber)) {
               Node channelNode = cloud.getNode(channelnumber);
               RepositoryUtil.addContentToChannel(objectNode, channelNode);
               Workflow.create(objectNode, null);
            }
            else {
               addToRequest(request, "content", objectNode);
               addToRequest(request, "contentchannels", contentchannels);
               return mapping.findForward("restore");
            }
         }
      }
      else {
         String channelnumber = getParameter(request, "channelnumber");
         if (StringUtils.isNotEmpty(channelnumber)) {
            Node channelNode = cloud.getNode(channelnumber);
            RepositoryUtil.addContentToChannel(objectNode, channelNode);
            Workflow.create(objectNode, null);
         }
         else {
            contentchannels = RepositoryUtil.getAllContentChannels(cloud);
            addToRequest(request, "content", objectNode);
            addToRequest(request, "contentchannels", contentchannels);
            return mapping.findForward("restore");
         }

      }
      addToRequest(request, "fresh", "true");
      return mapping.findForward(SUCCESS);
   }
}
