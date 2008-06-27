package com.finalist.cmsc.rssfeed.publish;

import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.PagesUtil;
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
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        addRssFeedNodes(node, nodes);
        publishNodes(nodes);
    }

    
    private void addRssFeedNodes(Node node, Map<Node, Date> nodes) {
       Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);
       nodes.put(node, publishDate);
   }
}
