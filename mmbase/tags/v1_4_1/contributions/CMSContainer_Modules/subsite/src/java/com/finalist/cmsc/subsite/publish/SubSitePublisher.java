package com.finalist.cmsc.subsite.publish;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.publish.PagePublisher;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSitePublisher extends PagePublisher {

	public SubSitePublisher(Cloud cloud) {
		super(cloud);
	}

    public void publish(Node node) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        addPageNodes(node, nodes);
        
        addSubSiteChannel(node,nodes);
        
        publishNodes(nodes);
    }
	
    protected void addSubSiteChannel(Node node, Map<Node, Date> nodes) {
		//Publish content channel of PersonalPage or SubSite-object
    	
    	if (SubSiteUtil.isSubSiteType(node)) {
	    	Node subsiteNode = SubSiteUtil.getSubsiteChannel(node);
	    	addChannels(nodes,subsiteNode);
    	} else if (SubSiteUtil.isPersonalPageType(node)) {
    		Node ppNode = SubSiteUtil.getPersonalpageChannel(node);
	    	addChannels(nodes,ppNode);
    	}
    }

	@Override
	public boolean isPublishable(Node node) {
        return SubSiteUtil.isSubSiteType(node);
	}

}
