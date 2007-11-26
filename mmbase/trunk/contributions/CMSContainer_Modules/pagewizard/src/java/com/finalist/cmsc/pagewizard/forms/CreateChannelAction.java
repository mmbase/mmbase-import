package com.finalist.cmsc.pagewizard.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class CreateChannelAction extends MMBaseAction {

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      CreateChannelForm channelForm = (CreateChannelForm) form;
      String action = channelForm.getAction();

      if (action != null) {
         if (action.equals(CreateChannelForm.ACTION_NEW)) {
            executeNew(cloud, channelForm);
         }
         else if (action.equals(CreateChannelForm.ACTION_ADD_CONTENT)) {
            executeAddContent(cloud, channelForm);
         }
         else if (action.equals(CreateChannelForm.ACTION_REMOVE_CONTENT)) {
            executeRemoveContent(cloud, channelForm);
         }
      }

      ActionForward forward = mapping.findForward(SUCCESS);
      return forward;
   }


   private void executeNew(Cloud cloud, CreateChannelForm channelForm) {
      int parent = channelForm.getParentNumber();
      String name = channelForm.getChannelName();

      Node parentChannelNode = cloud.getNode(parent);
      Node newChannelNode = RepositoryUtil.createChannel(cloud, name);
      RepositoryUtil.appendChild(parentChannelNode, newChannelNode);

      channelForm.setChannelNumber(newChannelNode.getNumber());
   }


   private void executeAddContent(Cloud cloud, CreateChannelForm channelForm) {
      int channelNumber = channelForm.getChannelNumber();
      Node channelNode = cloud.getNode(channelNumber);

      int contentNumber = channelForm.getContentNumber();
      Node contentNode = cloud.getNode(contentNumber);
      RepositoryUtil.addContentToChannel(contentNode, channelNode);
   }


   private void executeRemoveContent(Cloud cloud, CreateChannelForm channelForm) {
      int channelNumber = channelForm.getChannelNumber();
      Node channelNode = cloud.getNode(channelNumber);

      int contentNumber = channelForm.getContentNumber();
      Node contentNode = cloud.getNode(contentNumber);
      RepositoryUtil.removeContentFromChannel(contentNode, channelNode);
   }

}
