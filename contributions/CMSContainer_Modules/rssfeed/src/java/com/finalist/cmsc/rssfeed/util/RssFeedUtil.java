package com.finalist.cmsc.rssfeed.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;

public class RssFeedUtil {
   public static final String RSSFEED = "rssfeed";
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";


   public static NodeList getOrderedChildren(Node parentNode) {
      return SearchUtil.findRelatedOrderedNodeList(parentNode, RssFeedUtil.RSSFEED, NavigationUtil.NAVREL,
            FRAGMENT_FIELD);
   }


   public static int getChildCount(Node node) {
      return TreeUtil.getChildCount(node, node.getCloud().getNodeManager(RSSFEED), NavigationUtil.NAVREL);
   }


   public static boolean isRssFeedType(Node node) {
      return node.getNodeManager().getName().equals(RSSFEED);
   }
}
