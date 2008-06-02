package com.finalist.cmsc.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.remotepublishing.util.PublishUtil;

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
   public void unpublish(Node node) {
       PublishUtil.removeNode(cloud, node.getNumber());
   }

	@Override
	public void publish(Node node) {
	    PublishUtil.publishOrUpdateNode(node);
	}
	
	@Override
	public void remove(Node node) {
	    PublishUtil.removeFromQueue(node);
		
	}
}
