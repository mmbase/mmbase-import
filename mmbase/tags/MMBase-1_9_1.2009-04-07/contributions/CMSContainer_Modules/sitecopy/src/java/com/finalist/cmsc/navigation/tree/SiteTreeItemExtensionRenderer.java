package com.finalist.cmsc.navigation.tree;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationRenderer;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;

public class SiteTreeItemExtensionRenderer implements NavigationTreeItemRenderer {
   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
     
      if(SiteUtil.isSite(CloudProviderFactory.getCloudProvider().getCloud().getNode(parentId))) {
      element.addOption(
               renderer.createTreeOption(
                        "copy.png", "site.deep.copy", "cmsc-deepcopy", "../deepcopy/sitecopy.jsp?number="+parentId
               )
      );
      }
   }

   public TreeElement getTreeElement(NavigationRenderer renderer,
         Node parentNode, TreeModel model) {
      return null;
   }

   public boolean showChildren(Node parentNode) {
      return false;
   }  
}
