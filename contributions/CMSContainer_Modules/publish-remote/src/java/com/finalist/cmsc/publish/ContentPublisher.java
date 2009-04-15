/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.publish;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.workflow.Workflow;


public class ContentPublisher extends Publisher {

    public ContentPublisher(Cloud cloud) {
        super(cloud);
    }

    @Override
    public boolean isPublishable(Node node) {
        return ContentElementUtil.isContentElement(node);
    }

    @Override
    public void publish(Node node) {
        Set<Integer> clouds = new HashSet<Integer>();

        if (isPublished(node)) {
            clouds.addAll(PublishManager.getPublishedClouds(node));
        }
        else {
            NodeList channels = RepositoryUtil.getContentChannelsForContent(node);
            for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
                Node channel = iter.next();
                if (isPublished(channel)) {
                    clouds.addAll(PublishManager.getPublishedClouds(channel));
                }
            }
        }
        if (!clouds.isEmpty()) {
            Date publishDate = node.getDateValue(ContentElementUtil.PUBLISHDATE_FIELD);
            List<Node> nodes = findContentBlockNodes(node);
            for (Integer cloudNumber : clouds) {
                for (Node pnode : nodes) {
                    PublishUtil.publishOrUpdateNode(cloud, pnode.getNumber(), cloudNumber, publishDate);
                    Versioning.setPublishVersion(pnode);
                }
            }
        }
        else {
            Workflow.complete(node);
        }
    }

    @Override
    public void remove(Node node) {
        List<Node> nodes = findContentBlockNodes(node);
        removeNodes(nodes);
    }
    
}
