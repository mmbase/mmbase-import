package com.finalist.newsletter.cao.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Tag;

public class NewsletterSubscriptionUtil {
	public static Newsletter populateNewsletter(Node node,Newsletter newsletter){
		newsletter.setTitle(node.getStringValue("title"));
		return newsletter;
	}
	
	public static Newsletter convertNodeListtoTagList(NodeList list,Newsletter newsletter){
		Iterator<Node> nodelist = list.iterator();
		Set<Tag> taglist= new HashSet<Tag>();
		
		for (int j = 0; j < list.size(); j++) {
			Tag tag = new Tag();
			Node node = nodelist.next();
			tag.setName(node.getStringValue("name"));
			taglist.add(tag);
		}
		newsletter.setTags(taglist);
		return newsletter;
	}

}
