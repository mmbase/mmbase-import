package com.finalist.newsletter.module;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

public class NewsletterBean {

   private int newsletter = 0;
   private int themes = 0;
   private int publications = 0;
   private int subscriptions = 0;

   public NewsletterBean() {

   }
   
   private void getData()
   {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node newsletterNode = cloud.getNode(newsletter);
      themes = 0 + newsletterNode.countRelatedNodes("newslettertheme");
      publications = 0 + newsletterNode.countRelatedNodes("newsletterpublication");            
   }

   public int getNewsletter() {
      return newsletter;
   }

   public int getPublications() {
      return publications;
   }

   public int getSubscriptions() {
      return subscriptions;
   }

   public int getThemes() {
      return themes;
   }

   public void setNewsletter(int newsletter) {
      this.newsletter = newsletter;
      getData();
   }
}
