package com.finalist.newsletter.cao.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;

public class NlUtil {

	public static List<Newsletter> convertFromNodeList (List nodelist){

		List<Newsletter> list = new ArrayList<Newsletter>();
		Iterator<Node> it = nodelist.iterator();
		while (it.hasNext()) {
			Newsletter letter = new Newsletter();
			Node node = it.next();
			letter.setTitle(node.getStringValue("title"));
			letter.setId(node.getNumber());
			list.add(letter);
		}
		return list;
	}
}
