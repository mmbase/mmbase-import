package com.finalist.cmsc.navigation;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;

public interface NavigationTreeItemRenderer {

   public abstract TreeElement getTreeElement(NavigationInformationProvider renderer, Node parentNode,
         NavigationItem item, TreeModel model);
}
