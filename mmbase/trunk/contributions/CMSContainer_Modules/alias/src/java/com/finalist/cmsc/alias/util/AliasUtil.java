package com.finalist.cmsc.alias.util;

import org.mmbase.bridge.Node;

public class AliasUtil {
   public static final String ALIAS = "pagealias";
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";

   public static boolean isAliasType(Node node) {
      return node.getNodeManager().getName().equals(ALIAS);
   }
}
