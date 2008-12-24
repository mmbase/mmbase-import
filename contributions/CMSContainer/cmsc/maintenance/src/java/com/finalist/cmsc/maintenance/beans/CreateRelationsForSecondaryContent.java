package com.finalist.cmsc.maintenance.beans;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

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
      createRelations(cloud);
      createRelations(Publish.getRemoteCloud(cloud));
      return "success";
   }
   
   private void createRelations(Cloud localOrRemoteCloud)  throws Exception{
      if (localOrRemoteCloud == null) {
         throw new Exception("cloud is null");
      } 
      NodeManager assetManager = localOrRemoteCloud.getNodeManager("assetelement");
      NodeManager channelManager = localOrRemoteCloud.getNodeManager("assetelement");
      NodeManager ownerManager = localOrRemoteCloud.getNodeManager("user");
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
         int channels = asset.countRelatedNodes(channelManager, "creationrel", "destination");
         if (channels < 1) {
            RelationUtil.createRelation(asset, root, "creationrel");
         }
         int owners = asset.countRelatedNodes(ownerManager, "ownerrel", "destination");
         if (owners < 1) {
            RelationUtil.createRelation(asset, user, "ownerrel");
         }
      }
      
   }
   /**
    * @param args
    */
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      
   }

}
