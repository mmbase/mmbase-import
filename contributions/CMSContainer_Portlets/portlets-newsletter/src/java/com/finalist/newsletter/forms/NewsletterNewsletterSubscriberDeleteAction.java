package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class NewsletterNewsletterSubscriberDeleteAction extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		if (StringUtils.isNotBlank(request.getParameter("authid"))) {
			String authId = request.getParameter("authid");
			Node newsletterNode = cloud.getNode(Integer.parseInt(request.getParameter("newsletterId")));
			List<Node> subscriptions = newsletterNode.getRelatedNodes(cloud.getNodeManager("subscriptionrecord"));
			for (Node subscription : subscriptions) {
				String subscriberId = subscription.getStringValue("subscriber");
				if (subscriberId.equals(authId)) {
					subscription.deleteRelations();
					subscription.delete();
					subscription.commit();
				}
			}
		}
		request.setAttribute("newsletterId", request.getParameter("newsletterId"));
		return mapping.findForward("success");
	}
}
