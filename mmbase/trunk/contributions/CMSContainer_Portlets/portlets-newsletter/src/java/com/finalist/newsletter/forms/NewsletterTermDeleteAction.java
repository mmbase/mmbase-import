package com.finalist.newsletter.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class NewsletterTermDeleteAction extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		int newsletterId = Integer.parseInt(getParameter(request, "newsletterId"));
		int termId = Integer.parseInt(getParameter(request, "termId"));

		Node newsletterNode = cloud.getNode(newsletterId);
		RelationList termRelList = newsletterNode.getRelations("posrel", cloud.getNodeManager("term"));
		Iterator relItor = termRelList.iterator();
		while (relItor.hasNext()) {
			Relation termRelation = (Relation) relItor.next();
			int dnumber = termRelation.getIntValue("dnumber");
			if (dnumber == termId) {
				termRelation.delete();
			}
		}
		request.setAttribute("newsletterId", newsletterId);
		return mapping.findForward("success");
	}

}
