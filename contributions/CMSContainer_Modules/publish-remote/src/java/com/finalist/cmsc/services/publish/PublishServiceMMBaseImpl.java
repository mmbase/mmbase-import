/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.remotepublishing.PublishListener;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.builders.PublishingQueueBuilder;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.publish.*;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.util.bundles.JstlUtil;


public class PublishServiceMMBaseImpl extends PublishService implements PublishListener {

   public PublishServiceMMBaseImpl() {
       PublishingQueueBuilder.addPublishListener(this);
    }

    @Override
    public boolean isPublished(Node node) {
        return PublishManager.isPublished(node);
    }

    @Override
    public void publish(Node node) {
        getPublisher(node).publish(node);
    }

    @Override
    public void remove(Node node) {
        getPublisher(node).remove(node);
    }

    @Override
    public void unpublish(Node node) {
        getPublisher(node).unpublish(node);
    }
    
    @Override
    public boolean isPublishable(Node node) {
        Cloud cloud = node.getCloud();
        return !TypeUtil.isSystemType(node.getNodeManager().getName())
            && (getContentPublisher(cloud).isPublishable(node)
                    || getPagePublisher(cloud).isPublishable(node)
                    || getChannelPublisher(cloud).isPublishable(node));
    }
    
    private Publisher getPublisher(Node node) {
        Publisher publisher = getContentPublisher(node.getCloud());
        if (publisher.isPublishable(node)) {
            return publisher;
        }
        publisher = getPagePublisher(node.getCloud());
        if (publisher.isPublishable(node)) {
            return publisher;
        }
        publisher = getChannelPublisher(node.getCloud());
        if (publisher.isPublishable(node)) {
            return publisher;
        }
        throw new IllegalArgumentException("Node was not publishable " + node);
    }

    private Publisher getPagePublisher(Cloud cloud) {
        return new PagePublisher(cloud);
    }

    private Publisher getContentPublisher(Cloud cloud) {
        return new ContentPublisher(cloud);
    }

    private Publisher getChannelPublisher(Cloud cloud) {
        return new ChannelPublisher(cloud);
    }

   public void published(Node publishedNode) {
      if (Workflow.isWorkflowElement(publishedNode)) {
         Workflow.complete(publishedNode);
      }
   }


   public void publishedFailed(Node publishedNode, String systemMessage) {
      if (Workflow.isWorkflowElement(publishedNode) && Workflow.hasWorkflow(publishedNode)) {
         Workflow.reject(publishedNode, "Publiceren is gefaald (systeem melding: "+systemMessage+")");
      }
   }

}
