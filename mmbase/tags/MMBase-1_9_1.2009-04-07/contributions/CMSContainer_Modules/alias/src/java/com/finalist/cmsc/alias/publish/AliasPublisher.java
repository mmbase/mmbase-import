package com.finalist.cmsc.alias.publish;

import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.publish.Publisher;

public class AliasPublisher extends Publisher {

	public AliasPublisher(Cloud cloud) {
		super(cloud);
	}

    @Override
	public boolean isPublishable(Node node) {
        return AliasUtil.isAliasType(node);
	}

    @Override
    public void publish(Node node) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        addAliasNodes(node, nodes);
        
        publishNodes(nodes);
    }
    
    private void addAliasNodes(Node node, Map<Node, Date> nodes) {
       Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);

       nodes.put(node, publishDate);

       Node externalUrl = AliasUtil.getUrl(node);
       if (externalUrl != null) {
           nodes.put(externalUrl, publishDate);
       }
   }

}
