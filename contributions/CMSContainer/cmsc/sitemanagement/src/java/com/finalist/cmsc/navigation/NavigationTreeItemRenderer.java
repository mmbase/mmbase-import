package com.finalist.cmsc.navigation;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;

public interface NavigationTreeItemRenderer {

   public abstract TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model);
}
