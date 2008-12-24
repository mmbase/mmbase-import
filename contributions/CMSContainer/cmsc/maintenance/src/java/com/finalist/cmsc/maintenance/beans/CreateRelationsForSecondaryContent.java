package com.finalist.cmsc.maintenance.beans;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Relation;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.publish.Publish;

public class CreateRelationsForSecondaryContent {

   private Cloud cloud ;
   private Integer parentNumber ;
   public CreateRelationsForSecondaryContent(Cloud cloud,Integer parentNumber) {
      this.cloud = cloud;
      this.parentNumber = parentNumber;
   }
   
   public String execute() throws Exception {
      createRelations();
      return "success";
   }
   
   private void createRelations()  throws Exception{

      NodeManager assetManager = cloud.getNodeManager("assetelement");
      NodeManager ownerManager = cloud.getNodeManager("user");
      NodeQuery query = assetManager.createQuery();

      Node root = null;
      if (parentNumber == null || parentNumber.intValue() < 1) {
         root = RepositoryUtil.getRootNode(cloud);
      }
      else {
         root = cloud.getNode(parentNumber);
      }
      if(root == null) {
         throw new Exception("Parent number must be refered to");
      }
      Node user = SecurityUtil.getUserNode(cloud);
      NodeList assets = query.getList();
      
      for (int i = 0 ; i < assets.size() ; i++) {
         Node asset = assets.getNode(i);
         if (!RepositoryUtil.hasCreationChannel(asset)) {
            Relation relation = RelationUtil.createRelation(asset, root, "creationrel");
            Publish.publish(relation);
         }
         int owners = asset.countRelatedNodes(ownerManager, "ownerrel", "destination");
         if (owners < 1) {  
            Relation ownerRelation = RelationUtil.createRelation(asset, user, "ownerrel");
            Publish.publish(ownerRelation);
         }
        
      }
      
   }
}
