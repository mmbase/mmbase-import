/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.publish;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;


public class AssetPublisher extends Publisher {

    public AssetPublisher(Cloud cloud) {
        super(cloud);
    }

    @Override
    public boolean isPublishable(Node node) {
        return AssetElementUtil.isAssetElement(node);
    }

    @Override
    public void publish(Node node) {
        Set<Integer> clouds = new HashSet<Integer>();

        if (isPublished(node)) {
            clouds.addAll(PublishManager.getPublishedClouds(node));
        }
        else {
            Node channel = RepositoryUtil.getCreationChannel(node);
            if (isPublished(channel)) {
                 clouds.addAll(PublishManager.getPublishedClouds(channel));
            }
        }
        if (!clouds.isEmpty()) {
            Date publishDate = node.getDateValue(AssetElementUtil.PUBLISHDATE_FIELD);
            for (Integer cloudNumber : clouds) {
                PublishUtil.publishOrUpdateNode(cloud, node.getNumber(), cloudNumber, publishDate);
            }
        }
        else {
            Workflow.complete(node);
        }
    }

}
