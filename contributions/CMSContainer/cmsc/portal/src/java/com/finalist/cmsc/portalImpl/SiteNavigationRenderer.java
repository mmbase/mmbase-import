package com.finalist.cmsc.portalImpl;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationInformationProvider;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;

public class SiteNavigationRenderer extends PageNavigationRenderer {

   public TreeElement getTreeElement(NavigationInformationProvider renderer, Node parentNode, NavigationItem item,
         TreeModel model) {
      UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
      boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);
      TreeElement element = renderer.createElement(item, role, renderer.getOpenAction(parentNode, secure));

      if (SecurityUtil.isEditor(role)) {

         if (SecurityUtil.isChiefEditor(role)) {
            element.addOption(renderer.createOption("edit_defaults.png", "site.site.edit", "SiteEdit.do?number="
                  + parentNode.getNumber()));
         }

         element.addOption(renderer.createOption("new.png", "site.page.new", "PageCreate.do?parentpage="
               + parentNode.getNumber()));

         if (ModuleUtil.checkFeature(FEATURE_RSSFEED)) {
            element.addOption(renderer.createOption("rss_new.png", "site.rss.new",
                  "../rssfeed/RssFeedCreate.do?parentpage=" + parentNode.getNumber()));
         }

         if (SecurityUtil.isChiefEditor(role)
               && ((model.getChildCount(parentNode) == 0) || SecurityUtil.isWebmaster(role))) {
            element.addOption(renderer.createOption("delete.png", "site.site.remove", "SiteDelete.do?number="
                  + parentNode.getNumber()));
         }

         if (NavigationUtil.getChildCount(parentNode) >= 2) {
            element.addOption(renderer.createOption("reorder.png", "site.page.reorder", "reorder.jsp?parent="
                  + parentNode.getNumber()));
         }

         if (SecurityUtil.isChiefEditor(role)) {
            element.addOption(renderer.createOption("paste.png", "site.page.paste", "javascript:paste('"
                  + parentNode.getNumber() + "');"));
         }

         if (ModuleUtil.checkFeature(FEATURE_PAGEWIZARD)) {
            element.addOption(renderer.createOption("wizard.png", "site.page.wizard",
                  "../pagewizard/StartPageWizardAction.do?number=" + parentNode.getNumber()));
         }

         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
            element.addOption(renderer.createOption("publish.png", "site.page.publish",
                  "../workflow/publish.jsp?number=" + parentNode.getNumber()));
            element.addOption(renderer.createOption("masspublish.png", "site.page.masspublish",
                  "../workflow/masspublish.jsp?number=" + parentNode.getNumber()));
         }
      }
      element.addOption(renderer.createOption("rights.png", "site.page.rights",
            "../usermanagement/pagerights.jsp?number=" + parentNode.getNumber()));

      return element;
   }
}
