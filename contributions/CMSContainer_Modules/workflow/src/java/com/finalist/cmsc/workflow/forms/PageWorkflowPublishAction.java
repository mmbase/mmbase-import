package com.finalist.cmsc.workflow.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.struts.StrutsUtil;

public class PageWorkflowPublishAction extends MMBaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
	      HttpServletResponse response, Cloud cloud) throws Exception {

		String tmpAction = request.getParameter("actionvalue");

		int nodeId = Integer.parseInt(request.getParameter("number"));
		Node pageNode = cloud.getNode(nodeId);

		ResourceBundle rbundle = ResourceBundle.getBundle("cmsc-workflow", StrutsUtil.getLocale(request));

		//Create a workflow for the page if there is non
		if (!Workflow.hasWorkflow(pageNode)) {
         Workflow.create(pageNode, "");
      }
		
	   response.setCharacterEncoding("utf-8");
	    
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(pageNode);
		List<Node> workflowErrors = WorkflowUtil.performWorkflowAction(tmpAction, nodes, null);
		if (workflowErrors != null && workflowErrors.size() > 0) {
			response.getWriter().print(rbundle.getString("workflow.action." + tmpAction)
							+ rbundle.getString("publish.failednode"));
		} else {
		
   		if (tmpAction.equals(WorkflowUtil.ACTION_PUBLISH)) {
   		   response.getWriter().print(rbundle.getString("publish.published"));
   		} else {
   		   if (tmpAction.equals(WorkflowUtil.ACTION_ACCEPT)) {
	               tmpAction = "approv";
            }
   		   response.getWriter().print(rbundle.getString("workflow.status.page") + " " + 
   		         rbundle.getString("workflow.tab." + tmpAction  + "ed").toLowerCase() + ".");
   		}
		}
		return null;
	}

}
