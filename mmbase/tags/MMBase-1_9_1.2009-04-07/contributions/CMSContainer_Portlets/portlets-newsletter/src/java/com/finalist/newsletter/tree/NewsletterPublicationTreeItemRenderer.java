package com.finalist.newsletter.tree;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationRenderer;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.forms.NewsletterPublicationPublish;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class NewsletterPublicationTreeItemRenderer implements NavigationTreeItemRenderer {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationTreeItemRenderer.class.getName());

   protected static final String FEATURE_WORKFLOW = "workflowitem";

   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
      // nothing
   }

   public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
      UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
      boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);

      String name = parentNode.getStringValue(PagesUtil.TITLE_FIELD);
      String fragment = parentNode.getStringValue(NavigationUtil.getFragmentFieldname(parentNode));

      int id = parentNode.getNumber();
      TreeElement element = renderer.createElement(parentNode, role, name, fragment, secure);

      if (SecurityUtil.isWriter(role)) {
         if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.newsletteredition.edit", "newsletter",
                  "../newsletter/NewsletterPublicationEdit.do?number=" + id));
         }
         
         String edition_status = NewsletterPublicationUtil.getEditionStatus(parentNode);
         boolean isSingleApplication = true;
         boolean isPublished;
         isSingleApplication = ServerUtil.isSingle();
         if (isSingleApplication) {
           // NewsletterPublicationService publicationService = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
            Publication.STATUS status = NewsletterPublicationUtil.getStatus(parentNode.getCloud(),parentNode.getNumber());
            isPublished = Publication.STATUS.DELIVERED.equals(status);
         } else {
            isPublished = Publish.isPublished(parentNode);
         }

         log.debug("Publication " + parentNode.getNumber() + "'s publication status:" + isPublished + " in single:" + isSingleApplication);

         if (SecurityUtil.isChiefEditor(role) || (model.getChildCount(parentNode) == 0 && !isPublished && SecurityUtil.isEditor(role))) {
            element.addOption(renderer.createTreeOption("delete.png", "site.newsletteredition.remove", "newsletter",
                     "../newsletter/NewsletterPublicationDelete.do?number=" + id));
         } 
         
         boolean skipFreezing = "true".equalsIgnoreCase(PropertiesUtil.getProperty(NewsletterPublicationPublish.NEWSLETTER_FREEZE_PROPERTY));
         boolean skipApproving = "true".equalsIgnoreCase(PropertiesUtil.getProperty(NewsletterPublicationPublish.NEWSLETTER_APPROVE_PROPERTY)); 

         //Only show Send Newsletter Edition when user is webmaster or when newsletter is in proper state, or when property allows it.
         if (SecurityUtil.isWebmaster(role)) {
            element.addOption(renderer.createTreeOption("type/email_error.png", "site.newsletteredition.sendmail", "newsletter", "../newsletter/NewsletterPublicationPublish.do?number=" + id));
         } else {
            
            if(skipFreezing && skipApproving ) {
               element.addOption(renderer.createTreeOption("type/email_error.png", "site.newsletteredition.sendmail", "newsletter", "../newsletter/NewsletterPublicationPublish.do?number=" + id));
            } else
            {
               if(EditionStatus.APPROVED.value().equals(edition_status)) {
                  element.addOption(renderer.createTreeOption("type/email_error.png", "site.newsletteredition.sendmail", "newsletter", "../newsletter/NewsletterPublicationPublish.do?number=" + id));
               }
            }
         }
         
         element.addOption(renderer.createTreeOption("type/email_go.png", "site.newsletteredition.test", "newsletter",
               "../newsletter/NewsletterPublicationTest.do?number=" + id));

         if (SecurityUtil.isChiefEditor(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
            if (NavigationUtil.getChildCount(parentNode) >= 2) {
               element.addOption(renderer.createTreeOption("reorder.png", "site.page.reorder", "reorder.jsp?parent=" + id));
            }
            element.addOption(renderer.createTreeOption("publish.png", "site.newsletteredition.publish", "newsletter",
                     "../workflow/publish.jsp?number=" + id));
         }
         
         if (EditionStatus.INITIAL.value().equals(edition_status)) {
            element.addOption(renderer.createTreeOption("status_finished.png", "site.newsletteredition.freeze", "newsletter", "../newsletter/NewsletterEditionFreeze.do?number=" + id));
         } 
         else if (EditionStatus.FROZEN.value().equals(edition_status)) {
            element.addOption(renderer.createTreeOption("status_approved.png", "site.newsletteredition.defrost", "newsletter", "../newsletter/NewsletterEditionDefrost.do?number=" + id));
         }
            
         if (EditionStatus.FROZEN.value().equals(edition_status) ||
            skipFreezing && EditionStatus.INITIAL.value().equals(edition_status))
         {
            element.addOption(renderer.createTreeOption("status_published.png", "site.newsletteredition.approve", "newsletter","../newsletter/NewsletterEditionApprove.do?number=" + id));
         }
         else if(EditionStatus.APPROVED.value().equals(edition_status)){
            element.addOption(renderer.createTreeOption("status_onlive.png", "site.newsletteredition.revokeapproval", "newsletter","../newsletter/NewsletterEditionRevoke.do?number=" + id));
         }
          
      }
      
      element.addOption(renderer.createTreeOption("rights.png", "site.page.rights", "../usermanagement/pagerights.jsp?number=" + id));
      return element;
   }

   public boolean showChildren(Node parentNode) {
      return true;
   }

}