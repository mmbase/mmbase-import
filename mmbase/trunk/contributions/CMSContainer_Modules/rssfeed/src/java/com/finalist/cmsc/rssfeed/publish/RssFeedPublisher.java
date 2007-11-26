package com.finalist.cmsc.rssfeed.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.publish.Publisher;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;

public class RssFeedPublisher extends Publisher {

   public RssFeedPublisher(Cloud cloud) {
      super(cloud);
   }


   @Override
   public boolean isPublishable(Node node) {
      return RssFeedUtil.isRssFeedType(node);
   }


   @Override
   public void publish(Node node) {
      PublishUtil.publishOrUpdateNode(cloud, node.getNumber());
   }


   @Override
   /**
    * no automatic unpublish possible
    */
   public void remove(Node node) {

   }


   @Override
   public void unpublish(Node node) {
      PublishUtil.removeNode(cloud, node.getNumber());
   }
}
