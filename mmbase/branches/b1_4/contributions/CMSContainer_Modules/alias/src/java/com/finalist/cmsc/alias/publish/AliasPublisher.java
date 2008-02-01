package com.finalist.cmsc.alias.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.publish.Publisher;

public class AliasPublisher extends Publisher {

	public AliasPublisher(Cloud cloud) {
		super(cloud);
	}

    @Override
	public boolean isPublishable(Node node) {
        return AliasUtil.isAliasType(node);
	}

}
