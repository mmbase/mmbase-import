package com.finalist.cmsc.subsite.util;

import java.util.List;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

public class SubSiteUtil {

    /** Denotes the mmbase type for a subsite */
   public static final String SUBSITE = "subsite";
   /** Denotes the mmbase type for a personal page */
   public static final String PERSONALPAGE = "personalpage";
   /** Denotes the mmbase type for an article item used in the subsite */
   public static final String SUBSITE_ARTICLE = "subsitearticle";

   /** Alias for root of repository for subsite contentchannels  */
   public static final String ALIAS_SUBSITE = "repository.subsite";

   /** personal page userids field */
   public static final String USERID_FIELD = "userid";

   /** subsite article status field */
   public static final String SUBSITESTATUS_FIELD = "subsitestatus";

   private static final String STATUS_PUBLISHED = "published";
   
   /** request attribute name where personal page userid is stored */   
   public static final String PERSONAL_PAGE_ID = "personalPageId";

   
   public static boolean isSubSiteType(Node node) {
      return node.getNodeManager().getName().equals(SUBSITE);
   }

   public static boolean isPersonalPageType(Node node) {
      return node.getNodeManager().getName().equals(PERSONALPAGE);
   }

   public static boolean isSubsiteArticle(Node node) {
       return SUBSITE_ARTICLE.equals(node.getNodeManager().getName());
   }

   public static Node createSubSiteContentChannel(Node subsite) {
      Node subsiteRootChannel = getRepositoryRoot(subsite.getCloud());
      return createContentChannel(subsite, subsiteRootChannel);
   }

   public static Node createPersonalPageContentChannel(Node personalpage) {
      return createPersonalPageContentChannel(personalpage, null);
   }

   public static Node createPersonalPageContentChannel(Node personalpage, List<String> childChannels) {
      Node personalpageChannel = getSubsiteChannel(personalpage);
      return createContentChannel(personalpage, personalpageChannel, childChannels);
   }

   private static Node createContentChannel(Node page, Node parentChannel) {
      return createContentChannel(page, parentChannel, null);
   }

   private static Node createContentChannel(Node page, Node parentChannel, List<String> childChannelNames) {
      // create channel in the Content Repository
      Cloud cloud = page.getCloud();
      String fragment = page.getStringValue(PagesUtil.FRAGMENT_FIELD);
      Node channel = RepositoryUtil.createChannel(cloud, page.getStringValue(PagesUtil.TITLE_FIELD), fragment);
      RepositoryUtil.appendChild(parentChannel, channel);

      if (childChannelNames != null) {
         for (String childChannelName : childChannelNames) {
            Node childChannel = RepositoryUtil.createChannel(cloud, childChannelName);
            RepositoryUtil.appendChild(channel, childChannel);
         }
      }

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

      if (ppChannel == null) {
         return null;
      }

      String fragment = personalpage.getStringValue(PagesUtil.FRAGMENT_FIELD);

      return RepositoryUtil.getChild(ppChannel, fragment);
   }

   public static Node getPersonalpageNodeByUserId(Cloud cloud, String userId) {
      if (userId == null || StringUtil.isEmpty(userId)) {
         return null;
      }
      return SearchUtil.findNode(cloud, PERSONALPAGE, USERID_FIELD, userId);
   }

   public static Node getRepositoryRoot(Cloud cloud) {
      Node repositoryRootChannel = cloud.getNode(ALIAS_SUBSITE);
      return repositoryRootChannel;
   }

   public static Node createPersonalPage(Cloud cloud, String name, Node layout, String personalPageId) {
      Node personalPage = PagesUtil.createPage(cloud, name, layout, PERSONALPAGE);
      personalPage.setStringValue(USERID_FIELD, personalPageId);
      personalPage.commit();

      return personalPage;
   }
   
   public static boolean isSubSiteArticlePublished(Node node) {
       if (isSubsiteArticle(node)) {
           String subsitestatus = node.getStringValue(SUBSITESTATUS_FIELD);
           return STATUS_PUBLISHED.equals(subsitestatus);
       }
       return true;
   }

}
