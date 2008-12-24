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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class CreateRelationsForSecondaryContent {

   private Cloud cloud;
   private Integer parentNumber;

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(CreateRelationsForSecondaryContent.class.getName());


   public CreateRelationsForSecondaryContent(Cloud cloud, Integer parentNumber) {
      this.cloud = cloud;
      this.parentNumber = parentNumber;
   }

   public String execute() throws Exception {
      //Creating relations between assets and content channel
      //Only process assets that don't have creationrels!
      
      NodeManager assetManager = cloud.getNodeManager("assetelement");
      NodeManager ownerManager = cloud.getNodeManager("user");
      NodeQuery query = assetManager.createQuery();

      Node root = null;
      if (parentNumber == null || parentNumber.intValue() < 1) {
         root = RepositoryUtil.getRootNode(cloud);
      } else {
         root = cloud.getNode(parentNumber);
      }
      if (root == null || !RepositoryUtil.isChannel(root) ) {
         throw new Exception("Parent number must be refered to");
      }
      Node user = SecurityUtil.getUserNode(cloud);
      NodeList assets = query.getList();
      
      log.info("Assets - Found total Assets: " + assets.size());
      int counter = 0;
      
      for (int i = 0; i < assets.size(); i++) {
         Node asset = assets.getNode(i);
         if (!RepositoryUtil.hasCreationChannel(asset)) {
            counter++;
            Relation relation = RelationUtil.createRelation(asset, root, "creationrel");
            Publish.publish(relation); // This method checks if it need to publish
                                       // otherwise, it doesn't harm anyone
         }

         /*int owners = asset.countRelatedNodes(ownerManager, "ownerrel", "destination");
         if (owners < 1) {
            Relation relation = RelationUtil.createRelation(asset, user, "ownerrel");
            Publish.publish(relation);
         }*/
      }
      log.info("Assets - Added relations to " + counter + " Asset elements.");
  
      return "success";
   }

}
