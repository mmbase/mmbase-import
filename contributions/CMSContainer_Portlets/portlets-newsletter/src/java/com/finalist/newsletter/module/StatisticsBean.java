package com.finalist.newsletter.module;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

public class StatisticsBean {
   
   private int totalNewsletters = 0;
   private int totalThemes = 0;
   private int totalPublications = 0;
   private int totalSubscriptions = 0;
      
   public StatisticsBean()
   {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      NodeManager newsletterManager = cloud.getNodeManager("newsletter");

      
      
   }

   public int getTotalNewsletters() {
      return totalNewsletters;
   }

   public int getTotalPublications() {
      return totalPublications;
   }

   public int getTotalSubscriptions() {
      return totalSubscriptions;
   }

   public int getTotalThemes() {
      return totalThemes;
   }

   public void setTotalNewsletters(int totalNewsletters) {
      this.totalNewsletters = totalNewsletters;
   }

   public void setTotalPublications(int totalPublications) {
      this.totalPublications = totalPublications;
   }

   public void setTotalSubscriptions(int totalSubscriptions) {
      this.totalSubscriptions = totalSubscriptions;
   }

   public void setTotalThemes(int totalThemes) {
      this.totalThemes = totalThemes;
   }

}
