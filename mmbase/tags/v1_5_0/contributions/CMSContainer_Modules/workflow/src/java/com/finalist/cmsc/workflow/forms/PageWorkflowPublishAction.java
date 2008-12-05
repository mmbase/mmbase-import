package com.finalist.cmsc.workflow.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.struts.MMBaseAction;

public class PageWorkflowPublishAction extends MMBaseAction {

	public static final String ACTION_FINISH = "finish";

	public static final String ACTION_ACCEPT = "accept";

	public static final String ACTION_PUBLISH = "publish";

	private List<Node> performWorkflowAction(String action, Node node) {
		// TODO Auto-generated method stub
		List<Node> error = new ArrayList<Node>();

		if (ACTION_FINISH.equals(action)) {
			Workflow.finish(node, "");
		}
		if (ACTION_ACCEPT.equals(action)) {
			Workflow.accept(node, "");
		}

		if (ACTION_PUBLISH.equals(action)) {
			List<Integer> publishNumbers = new ArrayList<Integer>();

			if (Workflow.isAllowedToPublish(node)) {
				publishNumbers.add((node).getNumber());
			}
			try {
				if (publishNumbers.contains(node.getNumber())) {
					Workflow.publish(node, publishNumbers);
				} else {
					Workflow.accept(node, "");
				}
			} catch (WorkflowException wfe) {
				error.addAll(wfe.getErrors());
			}
		}
		return error;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		String tmpAction = request.getParameter("actionvalue");

		int nodeId = Integer.parseInt(request.getParameter("number"));
		Node pageNode = cloud.getNode(nodeId);

		ResourceBundle rbundle = ResourceBundle.getBundle("cmsc-workflow");

		List<Node> workitemList = pageNode.getRelatedNodes("workflowitem");
		Iterator<Node> itor = workitemList.iterator();
		Node workitemNode = null;

		response.setCharacterEncoding("utf-8");

		if (itor.hasNext()) {
			workitemNode = itor.next();
			List<Node> workflowErrors = performWorkflowAction(tmpAction,
					workitemNode);
			if (workflowErrors != null && workflowErrors.size() > 0) {
				response.getWriter().print(
						rbundle.getString("workflow.action." + tmpAction)
								+ rbundle.getString("publish.failednode"));
			}
			String tmpActionValue;
			if (tmpAction.equals("accept")) {
            tmpActionValue = rbundle.getString("workflow.tab.approved");
         }
         else {
            tmpActionValue = rbundle.getString("workflow.tab." + tmpAction
						+ "ed");
         }
			response.getWriter().print(
					rbundle.getString("workflow.status.page") + tmpActionValue);
		} else {
			response.getWriter().print(
					rbundle.getString("workflow.status.page")
							+ rbundle.getString("publish.published"));
		}
		return null;
	}

}
