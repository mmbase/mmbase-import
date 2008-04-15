package com.finalist.newsletter.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationRenderer;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;

public class NewsletterPublicationTreeItemRenderer implements NavigationTreeItemRenderer {

   protected static final String FEATURE_WORKFLOW = "workflowitem";

   @SuppressWarnings("unused")
   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
      // nothing
   }

   public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
      UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
      boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);

      String name = parentNode.getStringValue(PagesUtil.TITLE_FIELD);
      String fragment = parentNode.getStringValue(NavigationUtil.getFragmentFieldname(parentNode));

      String id = String.valueOf(parentNode.getNumber());
      TreeElement element = renderer.createElement(parentNode, role, name, fragment, secure);

      if (SecurityUtil.isEditor(role)) {
         element.addOption(renderer.createTreeOption("edit_defaults.png", "site.newsletterpublication.edit", "newsletter",
               "../newsletter/NewsletterPublicationEdit.do?number=" + id));

         if (SecurityUtil.isWebmaster(role) || (model.getChildCount(parentNode) == 0 && !Publish.isPublished(parentNode))) {
            element.addOption(renderer.createTreeOption("delete.png", "site.newsletterpublication.remove", "newsletter",
                  "../newsletter/NewsletterPublicationDelete.do?number=" + id));
           // element.addOption(renderer.createTreeOption("mail.png", "site.newsletterpublication.publish", "newsletter",
           //       "../newsletter/NewsletterPublicationPublish.do?number=" + id));
            /*
            element.addOption(renderer.createTreeOption("mail.png", "site.newsletterpublication.test", "newsletter",
                  "../newsletter/NewsletterPublicationTest.do?number=" + id));
            */
            
             element.addOption(renderer.createTreeOption("mail.png", "site.newsletterpublication.test", "newsletter",
                   "../newsletter/NewsletterPublicationTest.do?number=" + id));
         }

         if (NavigationUtil.getChildCount(parentNode) >= 2) {
            element.addOption(renderer.createTreeOption("reorder.png", "site.page.reorder", "reorder.jsp?parent=" + id));
         }

         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
            element.addOption(renderer.createTreeOption("mail.png", "site.newsletterpublication.publish", "newsletter",
                  "../workflow/publish.jsp?number=" + id));
         }
      }
      element.addOption(renderer.createTreeOption("rights.png", "site.page.rights", "../usermanagement/pagerights.jsp?number=" + id));

      return element;
   }

   public boolean showChildren(Node parentNode) {
      return true;
   }

}