package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class UrlCreateAction extends MMBaseAction {

   private static final String ALL = "all";
   private static final String CREATION = "creation";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      UrlCreateForm urlCreateForm = (UrlCreateForm) form;
      String name = urlCreateForm.getName();
      String description = urlCreateForm.getDescription();
      String url = urlCreateForm.getUrl();
      String parentchannel = urlCreateForm.getParentchannel();
      
      int nodeId = 0;

      if (parentchannel.equalsIgnoreCase(ALL) || StringUtils.isEmpty(parentchannel)) {
         parentchannel = (String) request.getSession().getAttribute(CREATION);
      }

      NodeManager manager = cloud.getNodeManager("urls");
      Node node = createUrl(manager, name, description, url);
      
      if(node!=null){
         nodeId = node.getNumber();
      }

      RelationUtil.createRelation(node, manager.getCloud().getNode(parentchannel), "creationrel");
      
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&channelid=" + parentchannel + "&createdNode=" + nodeId, true);
   }
   
   protected Node createUrl(NodeManager manager, String name, String description, String url) {
      Node node = manager.createNode();
      node.setValue("title", name);
      node.setValue("description", description);
      node.setValue("url", url);
      node.commit();
      return node;
   }
}
