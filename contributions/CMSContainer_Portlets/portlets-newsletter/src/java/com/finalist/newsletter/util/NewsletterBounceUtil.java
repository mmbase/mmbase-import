package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.newsletter.domain.NewsletterBounce;
import com.finalist.newsletter.services.CommunityModuleAdapter;

public class NewsletterBounceUtil {
   
   private static Log log = LogFactory.getLog(NewsletterBounceUtil.class);

   public static List<NewsletterBounce> getBounceRecord(int offset,int pageSize){
      List<NewsletterBounce> bounces = new ArrayList<NewsletterBounce>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager bounceManager = cloud.getNodeManager("newsletterbounce");
      NodeQuery query = bounceManager.createQuery();
     // query.addStep(bounceManager);
      
      //SearchUtil.addLimitConstraint(query, offset, pageSize);
      query.setMaxNumber(pageSize);
      query.setOffset(offset);
      NodeList bounceNodes = query.getList();
      bounces = convertNodeListToList(bounceNodes);
      return bounces;
   }
   
   public static int getTotalCount() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList bounces = SearchUtil.findNodeList(cloud, "newsletterbounce");
      if(bounces != null) {
         return bounces.size();
      }
      return (0);
   }
   
   public static List<NewsletterBounce> convertNodeListToList(NodeList bounceNodes) {
      if(bounceNodes == null || bounceNodes.size() <1){
         return null;
      }
      List<NewsletterBounce> bounces = new ArrayList<NewsletterBounce>();
      for(int i = 0 ; i < bounceNodes.size() ; i++) {
         Node bounceNode = bounceNodes.getNode(i);
         if(bounceNode == null){
            continue;
         }
         NewsletterBounce bounce = new NewsletterBounce();
         copyProperties(bounceNode,bounce);
         bounces.add(bounce);
      }
      return bounces;
   }
   
   public static void copyProperties(Node srcBounceNode,NewsletterBounce desBounce){

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      desBounce.setId(srcBounceNode.getNumber());
      desBounce.setNewsletterId(srcBounceNode.getIntValue("newsletter"));
      desBounce.setUserId(srcBounceNode.getIntValue("userid"));

      if(srcBounceNode.getIntValue("newsletter") > 0){
         Node publicationNode = cloud.getNode(srcBounceNode.getIntValue("newsletter"));
         desBounce.setNewsLetterTitle(publicationNode.getStringValue("title"));
      }
      if(srcBounceNode.getIntValue("userid") > 0 ){
         String userName = CommunityModuleAdapter.getUserNameByAuthenticationId(srcBounceNode.getIntValue("userid"));
         if(userName != null){
            desBounce.setUserName(userName);
         }
      }

      desBounce.setBounceDate(srcBounceNode.getDateValue("bouncedate"));
      desBounce.setBounceContent(srcBounceNode.getStringValue("content"));
      
   }
   
    
   public static NewsletterBounce getNewsletterBounce(int number){
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node bounceNode = cloud.getNode(number);
      NewsletterBounce bounce = new NewsletterBounce();
      copyProperties(bounceNode,bounce);
      return bounce;
   }
   
   public static void add(){
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager bounceManager = cloud.getNodeManager("newsletterbounce"); 
      for(int i = 0 ; i < 20 ; i++){
         Node node = bounceManager.createNode();
         node.setIntValue("newsletter", i);
         node.setIntValue("userid", i);
         
         node.setStringValue("content","ddddddddddddddddddd");
         node.setDateValue("bouncedate",new Date());
         node.commit();
         
      }
   }
}
