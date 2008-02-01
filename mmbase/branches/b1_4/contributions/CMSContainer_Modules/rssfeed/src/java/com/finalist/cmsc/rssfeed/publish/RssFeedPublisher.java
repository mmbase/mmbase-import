package com.finalist.cmsc.rssfeed.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

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

}
