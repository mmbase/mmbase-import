package com.finalist.cmsc.subsite.util;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

public class SubSiteUtil {
   public static final String SUBSITE = "subsite";
   public static final String PERSONALPAGE = "personalpage";
   public static final String ALIAS_SUBSITE = "repository.subsite";

   public static NodeList getOrderedChildren(Node parentNode) {
      return SearchUtil.findRelatedOrderedNodeList(parentNode, SubSiteUtil.SUBSITE, NavigationUtil.NAVREL,
            PagesUtil.FRAGMENT_FIELD);
   }

   public static int getChildCount(Node node) {
      return TreeUtil.getChildCount(node, node.getCloud().getNodeManager(SUBSITE), NavigationUtil.NAVREL);
   }

   public static boolean isSubSiteType(Node node) {
      return node.getNodeManager().getName().equals(SUBSITE);
   }
   
   public static boolean isPersonalPageType(Node node) {
	      return node.getNodeManager().getName().equals(PERSONALPAGE);
   }
   
   public static Node createSubSiteContentChannel(Node subsite) {
      Node subsiteRootChannel = getRepositoryRoot(subsite.getCloud());
      return createContentChannel(subsite, subsiteRootChannel);
   }

   public static Node createPersonalPageContentChannel(Node personalpage) {
      Node personalpageChannel = getSubsiteChannel(personalpage);
      return createContentChannel(personalpage, personalpageChannel);
   }

   private static Node createContentChannel(Node page, Node parentChannel) {
      //create channel in the Content Repository
      Cloud cloud = page.getCloud();
      String fragment = page.getStringValue(PagesUtil.FRAGMENT_FIELD);
      Node channel = RepositoryUtil.createChannel(cloud, page.getStringValue(PagesUtil.TITLE_FIELD), fragment);
      RepositoryUtil.appendChild(parentChannel, channel);
      return channel;
   }
   
   public static Node getSubsiteChannel(Node personalpage) {
      Node subsite = NavigationUtil.getParent(personalpage);
      String fragment = subsite.getStringValue(PagesUtil.FRAGMENT_FIELD);
      Cloud cloud = personalpage.getCloud();
      Node repositoryRootChannel = getRepositoryRoot(cloud);
      return RepositoryUtil.getChild(repositoryRootChannel, fragment);
   }

   public static Node getPersonalpageChannel(Node personalpage) {
	  Node ppChannel = getSubsiteChannel(personalpage);
	  String fragment = personalpage.getStringValue(PagesUtil.FRAGMENT_FIELD);
      return RepositoryUtil.getChild(ppChannel, fragment);
   }
   
   
   public static Node getRepositoryRoot(Cloud cloud) {
      Node repositoryRootChannel = cloud.getNode(ALIAS_SUBSITE);
      return repositoryRootChannel;
   }

}
