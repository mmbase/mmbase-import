package com.finalist.portlets.tagcloud.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.contentrepository.ContentRepository;
import com.finalist.cmsc.taglib.ContentUrlTag;
import com.finalist.portlets.tagcloud.Tag;

public class GetRelatedContentTag extends SimpleTagSupport {

	private String var;
	private Integer channel;
	private Integer element;
	private List<Tag> tags;
	
	public class Score {
		public Score(int number, int score) {
			this.number = number;
			this.score = score;
		}
		public int number;
		public int score;
		public int getNumber() {
			return number;
		}
		public int getScore() {
			return score;
		}
	}

	
	@SuppressWarnings("unchecked")
	public void doTag() throws JspException, IOException {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		
		ArrayList<Score> scores = new ArrayList<Score>();
		
		// calculate scores
		
		for(Tag tag:tags) {
			Node node = cloud.getNode(tag.getNumber());
			NodeList nodes = node.getRelatedNodes("contentelement");
			for(NodeIterator ni = nodes.nodeIterator(); ni.hasNext();) {
				Node contentNode = ni.nextNode();
				boolean found = false;
				int number = contentNode.getIntValue("number");
				for(Score score:scores) {
					if(score.number == number) {
						found = true;
						score.score += tag.getCount();
					}
				}
				if(!found) {
					scores.add(new Score(number, tag.getCount()));
				}
			}
		}
		
		// remove the nodes from this channel and/or element
		if(channel != null) {
			List<ContentElement> elements = ContentRepository.getContentElements(""+channel);
			for(ContentElement element:elements) {
				Score remove = null;
				for(Score score:scores) {
					if(element.getId() == score.number) {
						remove = score;
						break;
					}
				}
				if(remove != null) {
					scores.remove(remove);
				}
			}
		}
		if(element != null) {
			Score remove = null;
			for(Score score:scores) {
				if(element == score.number) {
					remove = score;
					break;
				}
			}
			if(remove != null) {
				scores.remove(remove);
			}
		}
		
		// sort the scores
		Collections.sort(scores, new Comparator() {
			public int compare(Object o1,Object o2){
				return ((Score)o2).score - ((Score)o1).score;
			}
		});
		
		getJspContext().setAttribute(var, scores);
	}
	

	public void setVar(String var) {
		this.var = var;
	}


	public void setChannel(String channel) {
		if(channel != null && channel.length() > 0) {
			this.channel = Integer.parseInt(channel);
		}
	}

	public void setElement(String element) {
		if(element != null && element.length() > 0) {
			this.element = Integer.parseInt(element);
		}
	}
	
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
