package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.newsletter.domain.Publication;

public abstract class NewsletterPublicationUtil {

   private static void copyOtherRelations(Node newsletterNode, Node publicationNode,boolean copyContent) {
      copyPageRelations(newsletterNode, publicationNode,copyContent);
      copyImageAndAttachmentRelations(newsletterNode, publicationNode);
   }
   
   public static Node copyPageRelations(Node sourcePage, Node newPage,boolean copyContent) {
      CloneUtil.cloneRelations(sourcePage, newPage, PagesUtil.LAYOUTREL, PagesUtil.LAYOUT);
      if(copyContent) {
         PortletUtil.copyPortlets(sourcePage, newPage);
      }
      Node popupinfo = PagesUtil.getPopupinfo(sourcePage);
      if (popupinfo != null) {
         Node newPopupinfo = PagesUtil.copyPopupinfo(popupinfo);
         PagesUtil.addPopupinfo(newPage, newPopupinfo);
      }
      return newPage;
   }
   
   private static void copyImageAndAttachmentRelations(Node newsletterNode, Node publicationNode) {
      CloneUtil.cloneRelations(newsletterNode,publicationNode,"namedrel","images");
      CloneUtil.cloneRelations(newsletterNode,publicationNode,"posrel","attachments");
   }
   
   public static Node getNewsletterByPublicationNumber(int publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterPublicationNode = cloud.getNode(publicationNumber);
      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");
      return relatedNewsletters.get(0);
   }
   
   public static Node createPublication(int newsletterNumber, boolean copyContent) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);        
         Node publicationNode = CloneUtil.cloneNode(newsletterNode, "newsletterpublication");
         if (publicationNode != null) {
            String urlFragment = String.valueOf(publicationNode.getNumber());
            publicationNode.setStringValue("urlfragment", urlFragment);
            publicationNode.setStringValue("publishdate","null");
            publicationNode.setStringValue("status", Publication.STATUS.INITIAL.toString());
            publicationNode.commit();
            
          //  copyContent(newsletterNode, publicationNode);
            copyOtherRelations(newsletterNode, publicationNode,copyContent);
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

   public static void createDefaultTerm(Node newsletterNode) {

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

      NavigationUtil.deleteItem(publicationNode);
   }

   public static List<String> getAllTermsForPublication(int publicationNumber) {
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