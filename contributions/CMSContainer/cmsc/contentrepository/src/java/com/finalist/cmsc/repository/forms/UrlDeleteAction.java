/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class UrlDeleteAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String channelnumber = getParameter(request, "channelnumber");
      Node channelNode = cloud.getNode(channelnumber);
      String objectnumber = getParameter(request, "objectnumber");
      String strict = getParameter(request, "strict");

      Node objectNode = cloud.getNode(objectnumber);

      RepositoryUtil.removeAssetFromChannel(objectNode, channelNode);
      RepositoryUtil.removeCreationRelForAsset(objectNode);
      RepositoryUtil.addAssetToChannel(objectNode, RepositoryUtil.getTrashNode(cloud));

      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?&createdNode=0&channelid=" + channelnumber + "&strict=" + strict, true);
   }
}
