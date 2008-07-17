package com.finalist.newsletter.util;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.util.SearchUtil;

public abstract class NewsletterTermUtil {
   
   private static Log log = LogFactory.getLog(NewsletterTermUtil.class);
   
   public static void deleteTerm(int termNumber){
      if(!getCloud().hasNode(termNumber)) {
         return;
      }
      Node termNode = getCloud().getNode(termNumber);
      RelationList relations = termNode.getRelations();

      if(relations != null){
         termNode.deleteRelations();
      }
      termNode.delete();
   }
   
   public static void addTerm(String name){
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      Node termNode = termNodeManager.createNode();
      termNode.setStringValue("name", name);
      termNode.commit();
   }
   
   public static boolean hasTerm(String name) {
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      SearchUtil.addEqualConstraint(query, termNodeManager.getField("name"), name);
      NodeList terms = query.getList();
      return terms != null && terms.size() > 0; 
   }
   
   public static void updateTerm(int termNumber,String name){
      Node termNode = getTermNodeById(termNumber);
      termNode.setStringValue("name", name);
      termNode.commit();
   }
   
   public static Node getTermNodeById(int termNumber) {
      if(!getCloud().hasNode(termNumber)) {
         return null;
      }
      return getCloud().getNode(termNumber);
   }
   
   public static NodeList searchTerms(String name,int offset,int pageSize){

      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      if(StringUtils.isNotBlank(name)){
         SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
      }
      SearchUtil.addSortOrder(query, termNodeManager, "number", "down");
      query.setMaxNumber(pageSize);
      query.setOffset(offset);      
      return query.getList();
   }
   
   public static int countTotalTerms(String name) {
      int size = 0;
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      if(StringUtils.isNotBlank(name)){
         SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
      }
      int count = query.getMaxNumber();
      if(query.getList() != null) {
         size = query.getList().size();
      }
      return size;
   }
   
   public static Cloud getCloud() {
      return CloudProviderFactory.getCloudProvider().getCloud();
   }
}
