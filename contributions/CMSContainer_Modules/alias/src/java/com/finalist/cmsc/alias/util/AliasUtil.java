package com.finalist.cmsc.alias.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.ResourcesUtil;

public class AliasUtil {
   public static final String ALIAS = "pagealias";
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";

   public static boolean isAliasType(Node node) {
      return node.getNodeManager().getName().equals(ALIAS);
   }

   public static Node getPage(Node node) {
       return SearchUtil.findRelatedNode(node, "page", "related");
   }

   public static Node getUrl(Node node) {
       return SearchUtil.findRelatedNode(node, ResourcesUtil.URLS, "related");
   }

   public static String getUrlStr(Node node) {
       Node url = getUrl(node);
       if (url != null) {
           return url.getStringValue("url");
       }
       return null;
   }

}
