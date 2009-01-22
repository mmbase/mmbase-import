package com.finalist.portlets.secure;

import java.util.*;

import javax.portlet.RenderRequest;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portlets.ContentChannelPortlet;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.community.Community;

public class SecureContentChannelPortlet extends ContentChannelPortlet {

	private static final HashMap<String,Object> NOT_SECURE_PARAMETER_MAP = new HashMap<String,Object>();

	static {
		NOT_SECURE_PARAMETER_MAP.put("secure", false);
	}

	@Override
	protected List<ContentElement> getContentElements(RenderRequest req, List<String> contenttypes, String channel, int offset, String orderby, String direction, String archive, int elementsPerPage, int year, int month, int day, boolean useLifecycleBool, int maxDays) {
		// TODO Auto-generated method stub
		if(isUserLoggedIn()) {
			return super.getContentElements(req, contenttypes, channel, offset, orderby, direction, archive, elementsPerPage, year, month, day,	useLifecycleBool, maxDays);
		}
		else {
		   Cloud cloud = getCloudForAnonymousUpdate();
			if (channel != null) {
				Node chan = cloud.getNode(channel);
				return getContentElements(chan, contenttypes, orderby, direction, useLifecycleBool, archive, offset, elementsPerPage, year, month, day, NOT_SECURE_PARAMETER_MAP);
			}
			return null;
		}
	}


	private boolean isUserLoggedIn() {
		return Community.isAuthenticated();
	}

	private List<ContentElement> getContentElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day, HashMap<String,Object> extraParameters) {
		List<ContentElement> result = new ArrayList<ContentElement>();
		if (channel != null) {
			NodeList l = RepositoryUtil.getLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day, extraParameters);
			for (int i = 0; i < l.size(); i++) {
				Node currentNode = l.getNode(i);
				ContentElement e = MMBaseNodeMapper.copyNode(currentNode, ContentElement.class);
				result.add(e);
			}
		}
		return result;
	}
}

