package com.finalist.cmsc.rssfeed.util;

import java.util.*;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.repository.RepositoryUtil;

public class RssFeedUtil {
    
   private static final String DESTINATION = "destination";

   public static final String RSSFEED = "rssfeed";
   public static final String TYPEDEF = "typedef";
   public static final String ALLOWREL = "allowrel";
   
   public static final String FRAGMENT_FIELD = "urlfragment";
   public static final String TITLE_FIELD = "title";

   public static boolean isRssFeedType(Node node) {
      return node.getNodeManager().getName().equals(RSSFEED);
   }
   
   public static List<String> getAllowedTypes(Node node) {
       List<String> types = new ArrayList<String>();
       NodeList typedefs = node.getRelatedNodes(TYPEDEF, ALLOWREL, DESTINATION);
       for (Iterator<Node> iter = typedefs.iterator(); iter.hasNext();) {
          Node typedef = iter.next();
          types.add(typedef.getStringValue("name"));
       }
       return types;
    }

    public static Node getContentChannel(Node node) {
        return SearchUtil.findRelatedNode(node, RepositoryUtil.CONTENTCHANNEL, "related");
    }

}
