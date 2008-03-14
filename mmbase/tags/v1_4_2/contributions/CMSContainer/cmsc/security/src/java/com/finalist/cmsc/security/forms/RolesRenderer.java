package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.*;
import com.finalist.tree.html.HTMLTreeCellRenderer;
import com.finalist.tree.html.HTMLTreeElement;

/**
 * @author Nico Klasens
 */
public abstract class RolesRenderer implements HTMLTreeCellRenderer {

   protected RolesForm form = null;
   protected Node user = null;
   private HttpServletRequest request;


   public RolesRenderer(HttpServletRequest request, Cloud cloud, RolesForm form) {
      super();
      this.request = request;
      this.form = form;
      if (form.getId() > -1) {
         this.user = cloud.getNode(form.getId());
      }
      else {
         throw new RuntimeException("What is the user node? nodenumber is missing in UserForm for roles");
      }
   }


   /**
    * @see com.finalist.tree.TreeCellRenderer#getElement(com.finalist.tree.TreeModel,
    *      java.lang.Object, java.lang.String)
    */
   public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
      Node n = (Node) node;

      UserRole userRole = form.getRole(n.getNumber());
      if (userRole == null) {
         userRole = getRole(n);
         if (userRole != null) {
            form.addRole(n.getNumber(), userRole);
         }
      }

      int roleId;
      if (userRole == null || userRole.isInherited()) {
         roleId = -1;
      }
      else {
         roleId = userRole.getRole().getId();
      }

      String title = "";
      if (n.getNodeManager().hasField("name")) {
         title = n.getStringValue("name");
      }
      if (n.getNodeManager().hasField("title")) {
         title = n.getStringValue("title");
      }

      return new RoleTreeElement(request, null, id, title, null, "treeitem", n.getNumber(), roleId);
   }


   protected abstract UserRole getRole(Node channel);

   public boolean showChildren(Object node) {
      return true;
   }
}
