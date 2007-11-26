package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.newsletter.CloneUtil;

public abstract class NewsletterPublicationUtil {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationUtil.class.getName());


   public static Node getPublishedPublication(String publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      // TODO - Query that only selects newsletterpublications that have been
      // published (publicationdate)
      return (null);
   }


   public static Node getUnpublishedPublication(String newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      // TODO - Query to select a node from newsletterpublication that has not
      // yet been published ( publishdate )
      return (null);
   }


   public static Node createPublication(String newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      NodeList newsletterThemeList = newsletterNode.getRelatedNodes("newslettertheme");

      Node publicationNode = CloneUtil.cloneNodeWithRelations(newsletterNode, "newsletterpublication");
      for (int i = 0; i < newsletterThemeList.size(); i++) {
         Node oldThemeNode = newsletterThemeList.getNode(i);
         Node newThemeNode = CloneUtil.cloneNode(oldThemeNode, "newsletterpublicationtheme");
         CloneUtil.cloneRelations(oldThemeNode, newThemeNode, "newslettercontent", null);
      }
      NavigationUtil.appendChild(newsletterNode, publicationNode);
      Node layoutNode = PagesUtil.getLayout(newsletterNode);
      PagesUtil.linkPortlets(publicationNode, layoutNode);

      return (publicationNode);
   }


   // Delete a publication, only if not yet published
   public static void deletePublication(String publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager publicationManager = cloud.getNodeManager("newsletterpublication");
      NodeManager publicationThemeManager = cloud.getNodeManager("newsletterpublicationtheme");
      Node publicationNode = cloud.getNode(publicationNumber);
      NodeList publicationThemeList = publicationNode.getRelatedNodes(publicationThemeManager);

      for (int i = 0; i < publicationThemeList.size(); i++) {
         Node publicationThemeNode = publicationThemeList.getNode(i);
         publicationThemeNode.deleteRelations();
         publicationThemeNode.delete();
      }
      publicationNode.deleteRelations();
      publicationNode.delete();
      NavigationUtil.deletePage(publicationNode);

   }


   public static boolean acceptTestNewsletter(String publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);
      if (publicationNode == null) {
         return (false);
      }
      publicationNode.setBooleanValue("accepted", true);
      publicationNode.commit();
      return (true);
   }


   public static boolean isAcceptedPublication(String publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);
      boolean result = publicationNode.getBooleanValue("accepted");
      log.debug("Asking if the publication with number " + publicationNumber + " is accepted. Answer is: " + result);
      return (result);
   }


   public static List<String> getAllThemesForPublication(String publicationNumber) {
      List<String> themes = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(publicationNumber);
      NodeList themeList = newsletterNode.getRelatedNodes("newslettertheme");
      for (int i = 0; i < themeList.size(); i++) {
         Node themeNode = themeList.getNode(i);
         String theme = themeNode.getStringValue("number");
         themes.add(theme);
      }
      return (themes);
   }
}