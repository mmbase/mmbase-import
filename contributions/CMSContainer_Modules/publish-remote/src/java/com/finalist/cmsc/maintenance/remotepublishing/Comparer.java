package com.finalist.cmsc.maintenance.remotepublishing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeManagerIterator;
import org.mmbase.bridge.NodeManagerList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationManagerIterator;
import org.mmbase.bridge.RelationManagerList;

/**
 * TODO: javadoc
 * 
 * @author Nico Klasens
 */
public class Comparer {

   public static List<String> compareModels(Cloud localCloud, Cloud remoteCloud) {
      List<String> messages = new ArrayList<String>();

      compareNodeManagers(localCloud, remoteCloud, messages);
      compareRelationManagers(localCloud, remoteCloud, messages);
      return messages;
   }


   public static void compareNodeManagers(Cloud localCloud, Cloud remoteCloud, List<String> messages) {
      NodeManagerList localManagers = localCloud.getNodeManagers();
      NodeManagerList remoteManagers = remoteCloud.getNodeManagers();

      NodeManagerIterator localIter = localManagers.nodeManagerIterator();
      while (localIter.hasNext()) {
         NodeManager localManager = localIter.nextNodeManager();
         String localName = localManager.getName();

         NodeManagerIterator remoteIter = remoteManagers.nodeManagerIterator();
         while (remoteIter.hasNext()) {
            NodeManager remoteManager = remoteIter.nextNodeManager();
            String remoteName = remoteManager.getName();

            if (localName.equals(remoteName)) {
               localIter.remove();
               remoteIter.remove();
               messages.add("NodeManager: " + localName + " found in both clouds");
               compareFields(localManager, remoteManager, messages);
               break;
            }
         }
      }

      NodeManagerIterator localIter2 = localManagers.nodeManagerIterator();
      while (localIter2.hasNext()) {
         NodeManager localManager = localIter2.nextNodeManager();
         messages.add("ERR NodeManager: " + localManager.getName() + " NOT found in remote cloud");
      }
      NodeManagerIterator remoteIter2 = remoteManagers.nodeManagerIterator();
      while (remoteIter2.hasNext()) {
         NodeManager remoteManager = remoteIter2.nextNodeManager();
         messages.add("ERR NodeManager: " + remoteManager.getName() + " NOT found in local cloud");
      }
   }


   public static void compareFields(NodeManager localManager, NodeManager remoteManager, List<String> messages) {
      FieldList localFields = localManager.getFields();
      FieldList remoteFields = remoteManager.getFields();

      FieldIterator localIter = localFields.fieldIterator();
      while (localIter.hasNext()) {
         Field localField = localIter.nextField();
         String localName = localField.getName();

         FieldIterator remoteIter = remoteFields.fieldIterator();
         while (remoteIter.hasNext()) {
            Field remoteField = remoteIter.nextField();
            String remoteName = remoteField.getName();

            if (localName.equals(remoteName)) {
               localIter.remove();
               remoteIter.remove();

               if (localField.getState() != remoteField.getState()) {
                  messages.add("ERR Field: " + localName + " state local = " + localField.getState() + " and remote = "
                        + remoteField.getState());
               }
               if (localField.getType() != remoteField.getType()) {
                  messages.add("ERR Field: " + localName + " type local = " + localField.getType() + " and remote = "
                        + remoteField.getType());
               }
               if (!localField.getGUIType().equals(remoteField.getGUIType())) {
                  messages.add("ERR Field: " + localName + " guitype local = " + localField.getDataType().getName()
                        + " and remote = " + remoteField.getDataType().getName());
               }
               if (localField.getMaxLength() != remoteField.getMaxLength()) {
                  messages.add("ERR Field: " + localName + " maxlength local = " + localField.getMaxLength()
                        + " and remote = " + remoteField.getMaxLength());
               }
               break;
            }
         }
      }

      FieldIterator localIter2 = localFields.fieldIterator();
      while (localIter2.hasNext()) {
         Field localField = localIter2.nextField();
         messages.add("ERR Field: " + localField.getName() + " NOT found in remote cloud");
      }
      FieldIterator remoteIter2 = remoteFields.fieldIterator();
      while (remoteIter2.hasNext()) {
         Field remoteField = remoteIter2.nextField();
         messages.add("ERR Field: " + remoteField.getName() + " NOT found in local cloud");
      }
   }


   public static void compareRelationManagers(Cloud localCloud, Cloud remoteCloud, List<String> messages) {
      RelationManagerList localManagers = localCloud.getRelationManagers();
      RelationManagerList remoteManagers = remoteCloud.getRelationManagers();

      RelationManagerIterator localIter = localManagers.relationManagerIterator();
      while (localIter.hasNext()) {
         RelationManager localManager = localIter.nextRelationManager();
         String localName = localManager.getName();
         String localForwardRole = localManager.getForwardRole();
         String localReciprocalRole = localManager.getReciprocalRole();
         int localDir = localManager.getDirectionality();
         String localSourceName = localManager.getSourceManager().getName();
         String localSestinationName = localManager.getDestinationManager().getName();

         RelationManagerIterator remoteIter = remoteManagers.relationManagerIterator();
         while (remoteIter.hasNext()) {
            RelationManager remoteManager = remoteIter.nextRelationManager();
            String remoteName = remoteManager.getName();
            String remoteForwardRole = remoteManager.getForwardRole();
            String remoteReciprocalRole = remoteManager.getReciprocalRole();
            int remoteDir = remoteManager.getDirectionality();
            String remoteSourceName = remoteManager.getSourceManager().getName();
            String remoteSestinationName = remoteManager.getDestinationManager().getName();

            if (localName.equals(remoteName) && localForwardRole.equals(remoteForwardRole)
                  && localReciprocalRole.equals(remoteReciprocalRole) && localDir == remoteDir
                  && localSourceName.equals(remoteSourceName) && localSestinationName.equals(remoteSestinationName)) {

               localIter.remove();
               remoteIter.remove();
               messages.add("RelationManager: " + localName + " found in both clouds (" + localSourceName + ","
                     + localForwardRole + "," + localSestinationName + ")");
               break;
            }
         }
      }

      RelationManagerIterator localIter2 = localManagers.relationManagerIterator();
      while (localIter2.hasNext()) {
         RelationManager localManager = localIter2.nextRelationManager();
         messages.add("ERR RelationManager: " + localManager.getName() + " NOT found in remote cloud ("
               + localManager.getSourceManager().getName() + "," + localManager.getForwardRole() + ","
               + localManager.getDestinationManager().getName() + ")");
      }
      RelationManagerIterator remoteIter2 = remoteManagers.relationManagerIterator();
      while (remoteIter2.hasNext()) {
         RelationManager remoteManager = remoteIter2.nextRelationManager();
         messages.add("ERR RelationManager: " + remoteManager.getName() + " NOT found in local cloud ("
               + remoteManager.getSourceManager().getName() + "," + remoteManager.getForwardRole() + ","
               + remoteManager.getDestinationManager().getName() + ")");
      }
   }


   public static List<String> compareManagers(Cloud localCloud, Cloud remoteCloud, String managerName, String keyField) {
      List<String> messages = new ArrayList<String>();

      NodeManager localManager = localCloud.getNodeManager(managerName);
      NodeManager remoteManager = remoteCloud.getNodeManager(managerName);

      NodeList localProps = localManager.getList(null, null, null);
      NodeList remoteProps = remoteManager.getList(null, null, null);

      NodeIterator localIter = localProps.nodeIterator();
      while (localIter.hasNext()) {
         Node localNode = localIter.nextNode();
         Object localName = localNode.getValue(keyField);

         NodeIterator remoteIter = remoteProps.nodeIterator();
         while (remoteIter.hasNext()) {
            Node remoteNode = remoteIter.nextNode();
            Object remoteName = remoteNode.getValue(keyField);

            if (localName.equals(remoteName)) {
               localIter.remove();
               remoteIter.remove();
               messages.add(managerName + ": " + localName + " found in both clouds");
               compareNodes(messages, localManager, localNode, remoteNode);
               break;
            }
         }
      }

      NodeIterator localIter2 = localProps.nodeIterator();
      while (localIter2.hasNext()) {
         Node localNode = localIter2.nextNode();
         messages.add("ERR Property: " + localNode.getStringValue(keyField) + " NOT found in remote cloud");
      }
      NodeIterator remoteIter2 = remoteProps.nodeIterator();
      while (remoteIter2.hasNext()) {
         Node remoteNode = remoteIter2.nextNode();
         messages.add("ERR Property: " + remoteNode.getStringValue(keyField) + " NOT found in local cloud");
      }

      return messages;
   }


   public static void compareNodes(List<String> messages, NodeManager localManager, Node localNode, Node remoteNode) {
      FieldList fields = localManager.getFields();
      for (Iterator<Field> iter = fields.iterator(); iter.hasNext();) {
         Field field = iter.next();
         if (!localNode.getValue(field.getName()).equals(remoteNode.getValue(field.getName()))) {
            messages.add("ERR: field '" + field.getName() + "' not equal");
         }
      }
   }

}
