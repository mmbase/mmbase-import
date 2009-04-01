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
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class UrlCreateAction extends MMBaseAction {

   private static final String ALL = "all";
   private static final String SITEASSETS = "siteassets";
   private static final String SESSION_CREATION = "creation";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      UrlCreateForm urlCreateForm = (UrlCreateForm) form;
      String title = urlCreateForm.getTitle();
      String description = urlCreateForm.getDescription();
      String url = urlCreateForm.getUrl();
      String parentchannel = urlCreateForm.getParentchannel();
      String strict = urlCreateForm.getStrict();
      int nodeId = 0;
      

      if (parentchannel.equalsIgnoreCase(SITEASSETS)) {
         parentchannel = RepositoryUtil.getRoot(cloud);
      } else if (parentchannel.equalsIgnoreCase(ALL) || StringUtils.isEmpty(parentchannel)) {
         parentchannel = (String) request.getSession().getAttribute(SESSION_CREATION);
      }

      NodeManager manager = cloud.getNodeManager("urls");
      Node node = createUrl(manager, title, description, url);
      
      if(node!=null){
         nodeId = node.getNumber();
      }

      RelationUtil.createRelation(node, manager.getCloud().getNode(parentchannel), "creationrel");
      
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&strict=" + strict + "&channelid=" + parentchannel + "&createdNode=" + nodeId, true);
   }
   
   protected Node createUrl(NodeManager manager, String title, String description, String url) {
      Node node = manager.createNode();
      node.setValue("title", title);
      node.setValue("description", description);
      node.setValue("url", url);
      node.commit();
      return node;
   }
}
