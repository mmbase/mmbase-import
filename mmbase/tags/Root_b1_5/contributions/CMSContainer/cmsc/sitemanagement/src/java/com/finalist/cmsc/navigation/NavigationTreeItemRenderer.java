package com.finalist.cmsc.navigation;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;

public interface NavigationTreeItemRenderer {

   TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model);
   
   //Adds items to the site tree, as right-click options 
   void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId);

	//Show child-items in the navigation tree?
   boolean showChildren(Node parentNode);
}
