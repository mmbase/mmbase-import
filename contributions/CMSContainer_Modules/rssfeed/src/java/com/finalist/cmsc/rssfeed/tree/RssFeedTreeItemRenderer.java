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


public class RssFeedTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final String RESOURCEBUNDLE = "cmsc-modules-rssfeed";

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

         return element;
      }

}
