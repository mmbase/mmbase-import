package com.finalist.cmsc.maintenance.beans;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Relation;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;
import javax.servlet.jsp.PageContext;

public class CreateRelationsForSecondaryContent {

   private static final String OWNERREL = "ownerrel";
   private static final String CONTENTELEMENT = "contentelement";
   private static final String IMAGEREL = "imagerel";
   private static final String IMAGEINLINEREL = "imageinlinerel";
   private static final String POSREL = "posrel";
   private static final String SOURCE = "SOURCE";
   private static final String INLINEREL = "inlinerel";
   private static final String DESTINATION = "DESTINATION";
   private static final String CREATIONREL = "creationrel";
   private static final String CONTENTCHANNEL = "contentchannel";

   private Cloud cloud;
   private Integer parentNumber;
   private String type ;
   public PageContext ctx ;
   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(CreateRelationsForSecondaryContent.class.getName());

   public CreateRelationsForSecondaryContent(Cloud cloud,PageContext pageContext) {
      this.cloud = cloud;
      ctx =  pageContext;
   }

   public String execute(Integer parentNumber,String type) throws Exception {
      //Creating relations between assets and content channel
      //Only process assets that don't have creationrels!
      this.parentNumber = parentNumber;
      this.type = type;

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
      ctx.setAttribute("totalCount", assets.size());
      int counter = 0;
      
      for (int i = 0; i < assets.size(); i++) {
         Node asset = assets.getNode(i);
         if (!RepositoryUtil.hasCreationChannel(asset)) {
            counter++;
            Relation relation = null;
            //if type is not null . create relations acording to the realations
            if(StringUtils.isNotBlank(type)) {
               
               Node channel = getRelatedChannel(asset);
               if(channel == null) {
                  relation = RelationUtil.createRelation(asset, root, CREATIONREL);
               }
               else {
                  relation = RelationUtil.createRelation(asset, channel, CREATIONREL);
               }
            }
            else {
               relation = RelationUtil.createRelation(asset, root, CREATIONREL);
            }
            if(Publish.isPublishable(relation)) {
               Publish.publish(relation); // This method checks if it need to publish
            }                        // otherwise, it doesn't harm anyone
         }
         //Add a version for a asset element.
         try {
            Versioning.addVersion(asset);
         } 
         catch (VersioningException e) {
           log.error("Add version error for node"+asset.getNumber(),e);
         }

         /*int owners = asset.countRelatedNodes(ownerManager, "ownerrel", "destination");
         if (owners < 1) {
            Relation relation = RelationUtil.createRelation(asset, user, "ownerrel");
            Publish.publish(relation);
         }*/
      }
      ctx.setAttribute("addedRelationCount", counter);
      log.info("Assets - Added relations to " + counter + " Asset elements.");
  
      return "success";
   }

   private Node getRelatedChannel(Node asset) {
      Node channel = null;
      NodeManager contentManager = cloud.getNodeManager(CONTENTELEMENT);

      if("images".equals(asset.getNodeManager().getName())) {
         NodeList contents = asset.getRelatedNodes(contentManager, IMAGEREL, SOURCE);
         channel = getChannel(contents);
         if (channel != null) {
            return channel;
         }
         contents = asset.getRelatedNodes(contentManager, IMAGEINLINEREL, SOURCE);
         channel = getChannel(contents);
         return channel;
      }
      else if ("attachments".equals(asset.getNodeManager().getName()) || "urls".equals(asset.getNodeManager().getName())) {

         NodeList contents = asset.getRelatedNodes(contentManager, POSREL, SOURCE);
         channel = getChannel(contents);
         if (channel != null) {
            return channel;
         }
         contents = asset.getRelatedNodes(contentManager, INLINEREL, SOURCE);
         channel = getChannel(contents);
         return channel;
     
      }
      return channel;
   }

   private Node getChannel(NodeList contents) {
      NodeManager channelManager = cloud.getNodeManager(CONTENTCHANNEL);
      if(contents.size() > 0) {
         for (int i = 0 ; i < contents.size() ; i++) {
            Node relatedChannel = contents.getNode(i).getRelatedNodes(channelManager, CREATIONREL, DESTINATION).getNode(0);
            if(RepositoryUtil.isTrash(relatedChannel)) {
               continue;
            }
            else {
               return relatedChannel;
            }
         }
      }
      return null;
   }
}
