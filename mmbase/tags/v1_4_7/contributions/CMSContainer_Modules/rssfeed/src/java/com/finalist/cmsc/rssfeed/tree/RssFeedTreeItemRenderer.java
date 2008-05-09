/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.rssfeed.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class RssFeedTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final String RESOURCEBUNDLE = "cmsc-modules-rssfeed";
    private static final String FEATURE_WORKFLOW = "workflowitem";
    
    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
         Node parentParentNode = NavigationUtil.getParent(parentNode);
         UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
         
         String name = parentNode.getStringValue(RssFeedUtil.TITLE_FIELD);
         String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );

         String id = String.valueOf(parentNode.getNumber());
         TreeElement element = renderer.createElement(parentNode, role, name, fragment, false);

         if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.rss.edit",
                        RESOURCEBUNDLE, "../rssfeed/RssFeedEdit.do?number=" + id));
            element.addOption(renderer.createTreeOption("delete.png", "site.rss.remove", 
                    RESOURCEBUNDLE, "../rssfeed/RssFeedDelete.do?number=" + id));
         }
         
         if (SecurityUtil.isChiefEditor(role)) {
             element.addOption(renderer.createTreeOption("cut.png", "site.page.cut", "javascript:cut('" + id + "');"));
             /** Not a good idea until this is fully implemented for every scenario
             * element.addOption(renderer.createTreeOption("copy.png", "site.page.copy", "javascript:copy('" + id + "');"));
             * element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", "javascript:paste('" + id + "');"));
             */
          }
         
         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
             element.addOption(renderer.createTreeOption("publish.png", "site.page.publish",
                   "../workflow/publish.jsp?number=" + id));
         }


         element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
                 "../usermanagement/pagerights.jsp?number=" + id));
         
         return element;
      }

   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
      element.addOption(renderer.createTreeOption("rss_new.png", "site.rss.new",
    		  RESOURCEBUNDLE, "../rssfeed/RssFeedCreate.do?parentpage=" + parentId));
   }

   public boolean showChildren(Node parentNode) {
      return false; //Do not show children, because a RSSFeed can not have kids.
   }

}
