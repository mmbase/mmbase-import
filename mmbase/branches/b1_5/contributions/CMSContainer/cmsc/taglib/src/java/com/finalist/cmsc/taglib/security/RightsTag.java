package com.finalist.cmsc.taglib.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import net.sf.mmapps.commons.bridge.CloudUtil;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.UserRole;

public class RightsTag extends SimpleTagSupport {

   private String var;
   private int nodeNumber;


   @Override
   public void doTag() {
      Cloud cloud = CloudUtil.getCloudFromThread();

      Node node = cloud.getNode(nodeNumber);
      UserRole role = null;

      // if it is a content channel
      if (RepositoryUtil.isContentChannel(node)) {
         role = RepositoryUtil.getRole(cloud, node, true);
      }
      // if it is a collection channel (use the rights of the parent)
      else if (RepositoryUtil.isCollectionChannel(node)) {
         node = RepositoryUtil.getParent(node);
         role = RepositoryUtil.getRole(cloud, node, true);
      }
      else {
         // if it is a page
         if (PagesUtil.isPageType(node)) {
            role = NavigationUtil.getRole(cloud, node, true);
         }
         // else, try the content itself
         else {
            role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(node), true);
         }
      }

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      if (role != null) {
         request.setAttribute(var, role.getRole().getName());
      }
   }


   public void setNodeNumber(int nodeNumber) {
      this.nodeNumber = nodeNumber;
   }


   public void setVar(String var) {
      this.var = var;
   }
}
