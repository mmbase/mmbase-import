/*
 * WIAB - Web-in-a-Box
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Web-In-A-Box.
 */
package com.finalist.cmsc.maintenance.remotepublishing;

import org.mmbase.bridge.*;

import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.PublishException;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.util.logging.*;

import java.util.*;

/**
 * class able to clone "full" clouds
 * 
 * @author Kees Jongenburger
 * @version CloudDuplicator.java,v 1.2 2003/07/28 09:44:23 nico Exp
 */
public class CloudDuplicator implements Runnable {
   private static Logger log = Logging.getLoggerInstance(CloudDuplicator.class.getName());
   private CloudInfo sourceCloudInfo;
   private CloudInfo destinationCloudInfo;
   private List<String> nodeManagers;
   private List<String> messages;


   public CloudDuplicator() {
      messages = new ArrayList<String>();
   }


   public List<String> getMessages() {
      return messages;
   }


   public void setSourceCloudInfo(CloudInfo sourceCloudInfo) {
      this.sourceCloudInfo = sourceCloudInfo;
   }


   public void setDestinationCloudInfo(CloudInfo destinationCloudInfo) {
      this.destinationCloudInfo = destinationCloudInfo;
   }


   public void addMessage(String message) {
      messages.add("message:" + message);
      log.info("message:" + message);
   }


   public void addError(String error) {
      messages.add("error:" + error);
      log.info("error:" + error);
   }


   public void stepZero() {
      addMessage("0: Start cloud duplicator");

      try {
         // CloudManager.getCloudNumber(sourceCloudInfo, destinationCloudInfo);
         CloudInfo.getCloudNumberInRemoteCloud(sourceCloudInfo, destinationCloudInfo);
      }
      catch (BridgeException e) {
         addError("cloud number of destination cloud is missing,stopping");
         throw e;
      }

      try {
         // CloudManager.getCloudNumber(sourceCloudInfo, sourceCloudInfo);
         CloudInfo.getCloudNumberInRemoteCloud(sourceCloudInfo, sourceCloudInfo);
      }
      catch (BridgeException e) {
         addError("cloud number of source cloud is missing,stopping");
         throw e;
      }
   }


   public void stepOne() {
      addMessage("1: Create a list of node managers");

      NodeManagerList sourceNodeManagerList = sourceCloudInfo.getCloud().getNodeManagers();

      NodeManagerList destinationNodeManagerList = destinationCloudInfo.getCloud().getNodeManagers();

      RelationManagerList relationManagers = destinationCloudInfo.getCloud().getRelationManagers();
      List<String> relationManagerList = new ArrayList<String>();

      for (int x = 0; x < relationManagers.size(); x++) {
         relationManagerList.add(relationManagers.getRelationManager(x).getName());
      }

      List<String> sourceNodeManagers = new ArrayList<String>();

      for (int x = 0; x < sourceNodeManagerList.size(); x++) {
         sourceNodeManagers.add(sourceNodeManagerList.getNodeManager(x).getName());
      }

      addMessage("1: node manager count in source cloud:" + sourceNodeManagers.size());

      List<String> destinationNodeManagers = new ArrayList<String>();

      for (int x = 0; x < destinationNodeManagerList.size(); x++) {
         destinationNodeManagers.add(destinationNodeManagerList.getNodeManager(x).getName());
      }

      addMessage("1: node manager count in destination cloud:" + destinationNodeManagers.size());

      // we now have a list of the the node managers in both clouds
      // Collections.sort(sourceNodeManagers);
      // Collections.sort(destinationNodeManagers);
      // keep only the node from the source cloud that are also in the
      // destination cloud
      sourceNodeManagers.retainAll(destinationNodeManagers);
      sourceNodeManagers.removeAll(relationManagerList);
      nodeManagers = sourceNodeManagers;
   }


   public void stepTwo() {
      addMessage("2: step two");
      addMessage("2: number of node managers in common:" + nodeManagers.size());

      Iterator<String> iter = nodeManagers.iterator();
      String skipBuilders = "daymarks mmservers nodeversions reldef remotenodes syncnodes typedef typerel versions oalias icaches insrel";

      // wiabII
      // skipBuilders +=" cloud publishqueue category navigation";
      skipBuilders += " cloud publishqueue";

      while (iter.hasNext()) {
         String nodeManagerName = iter.next();

         if (skipBuilders.indexOf(nodeManagerName) == -1) {
            addMessage("2: publish node of type " + nodeManagerName);

            NodeList sourceNodes = sourceCloudInfo.getCloud().getNodeManager(nodeManagerName).getList(null, null, null);

            for (int x = 0; x < sourceNodes.size(); x++) {
               addMessage("2: publish node of (type)=(" + nodeManagerName + ")");

               Node sourceNode = sourceNodes.getNode(x);

               try {
                  if (!PublishManager.isPublished(sourceCloudInfo, sourceNode.getNumber(), destinationCloudInfo)) {
                     addMessage("2: publish node number " + sourceNode.getNumber());
                     PublishManager.createNodeAndRelations(sourceCloudInfo, sourceNode, destinationCloudInfo, true);
                  }
                  else {
                     addMessage("2: skip publish node number " + sourceNode.getNumber() + "(already published)");
                  }
               }
               catch (PublishException e) {
                  addError(e.getMessage());
               }
            }
         }
         else {
            addMessage("2: skipping node of type:" + nodeManagerName);
         }
      }
   }


   public void run() {
      try {
         stepZero();
         stepOne();
         stepTwo();
         addMessage("3: done");
      }
      catch (Throwable ex) {
         addError("3: failed");
         ex.printStackTrace();
      }
   }


   public static void main(String[] argv) {
      if (argv.length != 2) {
         System.err.println("Usage CloudDuplicator rmi://127.0.0.1:1111/staging rmi://127.0.0.1:1111/live");
         System.exit(1);
      }

      CloudDuplicator cloudDuplicator = new CloudDuplicator();

      Cloud sourceCloud = ContextProvider.getCloudContext(argv[0]).getCloud("mmbase");
      CloudInfo sourceCloudInfo = CloudInfo.getCloudInfo(sourceCloud);
      cloudDuplicator.setSourceCloudInfo(sourceCloudInfo);

      Cloud destinationCloud = ContextProvider.getCloudContext(argv[1]).getCloud("mmbase");
      CloudInfo destinationCloudInfo = CloudInfo.getCloudInfo(destinationCloud);
      cloudDuplicator.setDestinationCloudInfo(destinationCloudInfo);

      cloudDuplicator.setDestinationCloudInfo(destinationCloudInfo);

      Thread t = new Thread(cloudDuplicator);
      t.start();
   }
}
