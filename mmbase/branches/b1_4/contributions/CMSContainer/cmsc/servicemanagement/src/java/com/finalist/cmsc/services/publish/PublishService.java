/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.services.Service;

public abstract class PublishService extends Service {

   public abstract boolean isPublished(Node node);


   public abstract void publish(Node node);


   public abstract void publish(Node node, NodeList nodes);


   public abstract boolean isPublishable(Node node);


   public abstract void remove(Node node);


   public abstract void unpublish(Node node);


   public abstract int getRemoteNumber(Node node);


   public abstract Node getRemoteNode(Node node);


   public abstract String getRemoteContentUrl(Node node);


   public abstract String getRemoteUrl(String appPath);

}
