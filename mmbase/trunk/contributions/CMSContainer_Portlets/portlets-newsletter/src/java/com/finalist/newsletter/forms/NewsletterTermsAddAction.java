package com.finalist.newsletter.forms;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class NewsletterTermsAddAction extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub

		if (StringUtils.isNotBlank(request.getParameter("newsletterId"))) {
			Node newsletterNode = cloud.getNode(Integer.parseInt(request.getParameter("newsletterId")));
			List<Node> relTerms = newsletterNode.getRelatedNodes(cloud.getNodeManager("term"), "posrel", "destination");
			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String parameter = parameterNames.nextElement().trim();
				if (parameter.startsWith("chk_")) {
					int chkNumber = Integer.parseInt(request.getParameter(parameter));
					Node termNode = cloud.getNode(chkNumber);
					boolean hasTerm = false;
					if (relTerms.size() > 0) {
						for (Node relTerm : relTerms) {
							if (termNode.getIntValue("number") == relTerm.getIntValue("number")) {
								hasTerm = true;
								break;
							}
						}
						
						if(!hasTerm) {
							RelationUtil.createRelation(newsletterNode, termNode, "posrel");
						}
					} else {
						RelationUtil.createRelation(newsletterNode, termNode, "posrel");
					}
				}
			}
		}
		request.setAttribute("newsletterId", request.getParameter("newsletter"));
		return mapping.findForward("success");
	}
}
