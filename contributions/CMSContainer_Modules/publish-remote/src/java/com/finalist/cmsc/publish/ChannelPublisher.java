package com.finalist.cmsc.publish;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.mmbase.remotepublishing.util.PublishUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class ChannelPublisher extends Publisher{

   public ChannelPublisher(Cloud cloud) {
      super(cloud);
   }

   public boolean isPublishable(Node node) {
      return RepositoryUtil.isContentChannel(node);
   }

   public void publish(Node node) {
      PublishUtil.publishOrUpdateNode(cloud, node.getNumber());
   }

    @Override
    public void remove(Node node) {
        PublishUtil.removeFromQueue(node);
    }
    
    @Override
    public void unpublish(Node node) {
        PublishUtil.removeNode(cloud, node.getNumber());
    }
}
