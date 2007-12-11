package com.finalist.cmsc.subsite.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.publish.Publisher;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSitePublisher extends Publisher {

	public SubSitePublisher(Cloud cloud) {
		super(cloud);
	}

    @Override
	public boolean isPublishable(Node node) {
        return SubSiteUtil.isSubSiteType(node);
	}

}
