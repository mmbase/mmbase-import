package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.newsletter.domain.NewsletterBounce;
import com.finalist.newsletter.services.CommunityModuleAdapter;
public class NewsletterBounceUtil {

   public static List<NewsletterBounce> getBounceRecord(int offset, int pageSize, String order, String direction) {
      List<NewsletterBounce> bounces = new ArrayList<NewsletterBounce>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager bounceManager = cloud.getNodeManager("newsletterbounce");
      NodeQuery query = bounceManager.createQuery();
      if (null == order || bounceManager.hasField(order)) {
         query.setMaxNumber(pageSize);
         query.setOffset(offset);
         Queries.addSortOrders(query, order, direction);
      }
      NodeList bounceNodes = query.getList();
      bounces = convertNodeListToList(bounceNodes);
      if (null != order && !bounceManager.hasField(order)) {
         bounces = newsletterSort(bounces, offset, pageSize, direction, order);
      }
      return bounces;
   }

   private static List<NewsletterBounce> newsletterSort(List<NewsletterBounce> bounces, int offset, int pageSize,
         String direction, String order) {
      ComparisonUtil comparator = new ComparisonUtil();
      comparator.setFields_user(new String[] { order });
      Collections.sort(bounces, comparator);
      if ("down".equals(direction)) {
         Collections.reverse(bounces);
      }
      if (pageSize + offset < bounces.size()) {
         bounces = bounces.subList(offset, pageSize + offset);
      } else {
         bounces = bounces.subList(offset, bounces.size());
      }
      return bounces;
   }

   public static int getTotalCount() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList bounces = SearchUtil.findNodeList(cloud, "newsletterbounce");
      if (bounces != null) {
         return bounces.size();
      }
      return (0);
   }

   public static List<NewsletterBounce> convertNodeListToList(NodeList bounceNodes) {
      if (bounceNodes == null || bounceNodes.size() < 1) {
         return null;
      }
      List<NewsletterBounce> bounces = new ArrayList<NewsletterBounce>();
      for (int i = 0; i < bounceNodes.size(); i++) {
         Node bounceNode = bounceNodes.getNode(i);
         if (bounceNode == null) {
            continue;
         }
         NewsletterBounce bounce = new NewsletterBounce();
         copyProperties(bounceNode, bounce);
         bounces.add(bounce);
      }
      return bounces;
   }

   public static void copyProperties(Node srcBounceNode, NewsletterBounce desBounce) {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      desBounce.setId(srcBounceNode.getNumber());
      desBounce.setNewsletterId(srcBounceNode.getIntValue("newsletter"));
      desBounce.setUserId(srcBounceNode.getIntValue("userid"));

      if (srcBounceNode.getIntValue("newsletter") > 0) {
         Node publicationNode = cloud.getNode(srcBounceNode.getIntValue("newsletter"));
         desBounce.setNewsLetterTitle(publicationNode.getStringValue("title"));
      }
      if (srcBounceNode.getIntValue("userid") > 0) {
         String userName = CommunityModuleAdapter.getUserNameByAuthenticationId(srcBounceNode.getIntValue("userid"));
         if (userName != null) {
            desBounce.setUserName(userName);
         }
      }

      desBounce.setBounceDate(srcBounceNode.getDateValue("bouncedate"));
      desBounce.setBounceContent(srcBounceNode.getStringValue("content"));

   }

   public static NewsletterBounce getNewsletterBounce(int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node bounceNode = cloud.getNode(number);
      NewsletterBounce bounce = new NewsletterBounce();
      copyProperties(bounceNode, bounce);
      return bounce;
   }
}
