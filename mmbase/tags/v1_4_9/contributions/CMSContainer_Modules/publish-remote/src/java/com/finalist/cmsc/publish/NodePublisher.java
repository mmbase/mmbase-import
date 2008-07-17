package com.finalist.cmsc.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class NodePublisher extends Publisher{

   public NodePublisher(Cloud cloud) {
      super(cloud);
   }

   @Override
   public boolean isPublishable(Node node) {
      return true;
   }

   @Override
   public void publish(Node node, NodeList contentnodes) {
       publishNode(node, null);
   }

}
