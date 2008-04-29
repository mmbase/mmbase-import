package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.commons.bridge.RelationUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.newsletter.domain.Publication;

public abstract class NewsletterPublicationUtil {

   private static void copyContent(Node oldThemeNode, Node newThemeNode) {
      RelationList contentList = oldThemeNode.getRelations("newslettercontent");
      if (contentList != null && contentList.size() > 0) {
         for (int r = 0; r < contentList.size(); r++) {
            Relation contentRelation = contentList.getRelation(r);
            Node contentNode = contentRelation.getSource();
            RelationUtil.createRelation(newThemeNode, contentNode, "newslettercontent");
         }
      }
   }

   private static void copyOtherRelations(Node newsletterNode, Node publicationNode) {
      PagesUtil.copyPageRelations(newsletterNode, publicationNode);
   }

   private static void copyThemesAndContent(Node newsletterNode, Node publicationNode, boolean copyContent) {
      copyThemesAndContent(newsletterNode, publicationNode, copyContent, "newslettertheme");
      copyThemesAndContent(newsletterNode, publicationNode, copyContent, "defaulttheme");
   }

   private static void copyThemesAndContent(Node newsletterNode, Node publicationNode, boolean copyContent, final String relationName) {
      NodeList newsletterThemeList = newsletterNode.getRelatedNodes("newslettertheme", relationName, "DESTINATION");
      if (newsletterThemeList != null) {
         for (int i = 0; i < newsletterThemeList.size(); i++) {
            Node oldThemeNode = newsletterThemeList.getNode(i);
            Node newThemeNode = CloneUtil.cloneNode(oldThemeNode, "newsletterpublicationtheme");
            if (newThemeNode != null) {
               RelationUtil.createRelation(publicationNode, newThemeNode, relationName);
               if (copyContent == true) {
                  copyContent(oldThemeNode, newThemeNode);
               }
            }
         }
      }
   }

   public static Node createPublication(int newsletterNumber, boolean copyContent) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);
         createDefaultTerm(newsletterNode);
         Node publicationNode = CloneUtil.cloneNode(newsletterNode, "newsletterpublication");

         if (publicationNode != null) {
            String urlFragment = String.valueOf(publicationNode.getNumber());
            publicationNode.setStringValue("urlfragment", urlFragment);
            publicationNode.setStringValue("publishdate","null");
            publicationNode.setStringValue("status", Publication.STATUS.INITIAL.toString());
            publicationNode.commit();

            copyThemesAndContent(newsletterNode, publicationNode, copyContent);
            copyOtherRelations(newsletterNode, publicationNode);
            NavigationUtil.appendChild(newsletterNode, publicationNode);
            Node layoutNode = PagesUtil.getLayout(publicationNode);
            if (copyContent == true) {
               PagesUtil.linkPortlets(publicationNode, layoutNode);
            }
            return (publicationNode);
         }
      }
      return (null);
   }

   private static void createDefaultTerm(Node newsletterNode) {

      if(!hasDefaultTerm(newsletterNode)) {
         Node defaultTerm = newsletterNode.getCloud().getNodeManager("term").createNode();
         defaultTerm.setStringValue("name", "default");
         defaultTerm.setStringValue("subject", newsletterNode.getStringValue("title"));
         defaultTerm.commit();
         newsletterNode.createRelation(defaultTerm, newsletterNode.getCloud().getRelationManager("posrel")).commit();
      }
   }
   
   private static boolean hasDefaultTerm(Node newsletterNode) {
      NodeManager termNodeManager = newsletterNode.getCloud().getNodeManager("term");
      NodeList terms = newsletterNode.getRelatedNodes(termNodeManager);
      boolean hasDefaultTerm = false;
      for(int i = 0 ; i < terms.size() ; i++) {
        Node term = terms.getNode(i);
        if(term.getStringValue("name") != null && term.getStringValue("name").equals("default")) {
           hasDefaultTerm = true;
           break;
        }
      }
      return hasDefaultTerm;
   }
   
   // Delete a publication, only if not yet published
   public static void deletePublication(int publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);

      NodeList themes = publicationNode.getRelatedNodes("newsletterpublicationtheme");
      if (themes != null) {
         for (int i = themes.size() - 1; i >= 0; i--) {
            Node publicationThemeNode = themes.getNode(i);
            publicationThemeNode.delete(true);
         }
      }

      NavigationUtil.deleteItem(publicationNode);
   }

   public static List<String> getAllThemesForPublication(int publicationNumber) {
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