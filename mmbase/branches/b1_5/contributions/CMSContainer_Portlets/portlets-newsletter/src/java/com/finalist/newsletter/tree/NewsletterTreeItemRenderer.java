package com.finalist.newsletter.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;

public class NewsletterTreeItemRenderer implements NavigationTreeItemRenderer {

   protected static final String FEATURE_PAGEWIZARD = "pagewizarddefinition";
   protected static final String FEATURE_WORKFLOW = "workflowitem";

   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
      element.addOption(
               renderer.createTreeOption(
                        "mail.png", "site.newsletter.new", "newsletter", op("NewsletterCreate", "parentnewsletter", parentId)
               )
      );
   }

   public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {

      UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
      boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);

      String name = parentNode.getStringValue(PagesUtil.TITLE_FIELD);
      String fragment = parentNode.getStringValue(NavigationUtil.getFragmentFieldname(parentNode));

      String id = String.valueOf(parentNode.getNumber());
      TreeElement element = renderer.createElement(parentNode, role, name, fragment, secure);

      if (SecurityUtil.isEditor(role)) {
         addEditorOptions(renderer, id, element);

         if (SecurityUtil.isWebmaster(role) || (model.getChildCount(parentNode) == 0 && !Publish.isPublished(parentNode))) {
            addWebmasterOptions(renderer, id, element);
         }

         if (NavigationUtil.getChildCount(parentNode) >= 2) {
            element.addOption(renderer.createTreeOption("reorder.png", "site.page.reorder", "reorder.jsp?parent=" + id));
         }

         if (SecurityUtil.isChiefEditor(role)) {
            addChiefEditorOptions(renderer, id, element);
         }
      }

      element.addOption(
               renderer.createTreeOption("rights.png", "site.page.rights", "../usermanagement/pagerights.jsp?number=" + id)
      );

      return element;
   }

   private void addChiefEditorOptions(NavigationRenderer renderer, String id, TreeElement element) {
      element.addOption(
            renderer.createTreeOption("delete.png", "site.newsletter.remove", "newsletter",
                     "../newsletter/NewsletterDelete.do?number=" + id
            )
      );
      element.addOption(renderer.createTreeOption("cut.png", "site.page.cut", "javascript:cut('" + id + "');"));
      element.addOption(renderer.createTreeOption("copy.png", "site.page.copy", "javascript:copy('" + id + "');"));
      element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", "javascript:paste('" + id + "');"));
/*      
      element.addOption(
               renderer.createTreeOption("switch.png", "site.newsletter.switchtoplain", "newsletter",
                        String.format("../newsletter/SwitchMIMEAction.do?target=%s&number=%s", "text/plain", id)
               )
      );
      element.addOption(
               renderer.createTreeOption("switch.png", "site.newsletter.switchtohtml", "newsletter",
                        String.format("../newsletter/SwitchMIMEAction.do?target=%s&number=%s", "text/html", id)
               )
      );
      element.addOption(
               renderer.createTreeOption("switch.png", "site.newsletter.switchtowap", "newsletter",
                        String.format("../newsletter/SwitchMIMEAction.do?target=%s&number=%s", "application/vnd.wap.xhtml+xml", id)
               )
      );*/
      if (ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
//         element.addOption(
//               renderer.createTreeOption("publish.png", "site.newsletter.publish", "newsletter",
//                     "../newsletter/NewsletterPublish.do?number=" + id
//            )
//         );
         element.addOption(renderer.createTreeOption("publish.png", "site.newsletter.publish","newsletter",
               "../workflow/publish.jsp?number=" + id));
      }
   }

   private void addWebmasterOptions(NavigationRenderer renderer, String id, TreeElement element) {

      //todo remove the code
      //This  has been implement in the wizard.

//      boolean isPaused = NewsletterUtil.isPaused(Integer.parseInt(id));
//      if (isPaused) {
//         element.addOption(
//               renderer.createTreeOption("resume.png", "site.newsletter.resume", "newsletter",
//                     "../newsletter/NewsletterResume.do?number=" + id
//               )
//         );
//      }
//      else {
//         element.addOption(
//               renderer.createTreeOption("pause.png", "site.newsletter.pause", "newsletter",
//                     "../newsletter/NewsletterPause.do?number=" + id
//               )
//         );
//      }
   }

   private void addEditorOptions(NavigationRenderer renderer, String id, TreeElement element) {
      element.addOption(
               renderer.createTreeOption("edit_defaults.png", "site.newsletter.edit", "newsletter",
                        "../newsletter/NewsletterEdit.do?number=" + id
               )
      );
      element.addOption(
            renderer.createTreeOption("mail.png", "site.newsletteredition.new.blank", "newsletter",
                        "../newsletter/NewsletterPublicationCreate.do?parent=" + id + "&copycontent=false"
               )
      );
      element.addOption(
            renderer.createTreeOption("mail.png", "site.newsletteredition.new.withcontent", "newsletter",
                        "../newsletter/NewsletterPublicationCreate.do?parent=" + id + "&copycontent=true"
               )
      );
      
   }

   public boolean showChildren(Node parentNode) {
      return true;
   }


   private String op(String action, String param, String value) {
      return String.format("../newsletter/%s.do?%s=%s", action, param, value);
   }
}
