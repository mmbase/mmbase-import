package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class AssetMassDeleteAction extends MMBaseFormlessAction {

   @SuppressWarnings("unchecked")
   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String offset = getParameter(request, "offset");
      String orderBy = getParameter(request, "orderby");
      String direction = getParameter(request, "direction");
      String channelnumber = getParameter(request, "channelnumber");
      Node channelNode = cloud.getNode(channelnumber);

      Enumeration<String> parameters = request.getParameterNames();
      while (parameters.hasMoreElements()) {
         String parameter = parameters.nextElement();

         if (parameter.startsWith("chk_")) {
            String objectnumber = request.getParameter(parameter);
            Node objectNode = cloud.getNode(objectnumber);

            RepositoryUtil.removeAssetFromChannel(objectNode, channelNode);
            RepositoryUtil.removeCreationRelForAsset(objectNode);
            RepositoryUtil.addAssetToChannel(objectNode, RepositoryUtil.getTrashNode(cloud));
         }
      }
      String url = mapping.findForward(SUCCESS).getPath() + "?type=asset&parentchannel=" + channelnumber;
      if(StringUtils.isNotEmpty(offset)) {
         url += "&offset="+offset;
      }
      if(StringUtils.isNotEmpty(orderBy)) {
         url += "&orderby="+orderBy;
      }
      if(StringUtils.isNotEmpty(direction)) {
         url += "&direction="+direction;
      }
      url += "&refreshchannel=true";
      return new ActionForward(url, true);
   }

}
