package com.finalist.cmsc.subsite.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;

public class PersonalPageUtil {
   public static final String PERSONALPAGE = "personalpage";
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";

   public static NodeList getOrderedChildren(Node parentNode) {
      return SearchUtil.findRelatedOrderedNodeList(parentNode, PersonalPageUtil.PERSONALPAGE, NavigationUtil.NAVREL,
            FRAGMENT_FIELD);
   }


   public static int getChildCount(Node node) {
      return TreeUtil.getChildCount(node, node.getCloud().getNodeManager(PERSONALPAGE), NavigationUtil.NAVREL);
   }


   public static boolean isPersonalPageType(Node node) {
      return node.getNodeManager().getName().equals(PERSONALPAGE);
   }
}
