package com.finalist.cmsc.alias.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;

public class AliasUtil {
   public static final String ALIAS = "pagealias";
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";


   public static NodeList getOrderedChildren(Node parentNode) {
      return SearchUtil.findRelatedOrderedNodeList(parentNode, ALIAS, NavigationUtil.NAVREL,
            FRAGMENT_FIELD);
   }


   public static int getChildCount(Node node) {
      return TreeUtil.getChildCount(node, node.getCloud().getNodeManager(ALIAS), NavigationUtil.NAVREL);
   }


   public static boolean isAliasType(Node node) {
      return node.getNodeManager().getName().equals(ALIAS);
   }
}
