/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.workflow.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicStep;

import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.workflow.*;


public abstract class WorkflowAction extends MMBaseFormlessAction {

    public static final Object ACTION_FINISH = "finish";
    public static final Object ACTION_ACCEPT = "accept";
    public static final Object ACTION_REJECT = "reject";
    public static final Object ACTION_PUBLISH = "publish";
    
    public static final Object ACTION_RENAME = "rename";
    
	private static final Object REMARK_UNCHANGED = "[unchanged-item]";
    
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud)
            throws Exception {
        String actionValueStr = request.getParameter("actionvalue");

        String remark = request.getParameter("remark");
        if(remark != null && remark.equals(REMARK_UNCHANGED)) {
        	remark = null;
        }
        
        if (!StringUtil.isEmpty(actionValueStr)) {
            List<Node> nodes = new ArrayList<Node>();
            Enumeration parameters = request.getParameterNames();
            while (parameters.hasMoreElements()) {
                String name = (String) parameters.nextElement();
                if (name.startsWith("check_")) {
                    int lastUScore = name.lastIndexOf("_");
                    int number = Integer.parseInt(name.substring(lastUScore + 1));
                    if (cloud.hasNode(number)) {
                        Node n = cloud.getNode(number);
                        nodes.add(n);
                    }
                }
            }
            List workflowErrors = performWorkflowAction(actionValueStr, nodes, remark, cloud);

            if (workflowErrors != null && workflowErrors.size() > 0) {
                String url = mapping.getPath() + "?status=" + request.getParameter("status");
                request.setAttribute("errors", workflowErrors);
                request.setAttribute("returnUrl", url);
                request.setAttribute("contentNodeList", nodes);
//                return new ActionForward(url);
            }
        }
        
        String orderby = request.getParameter("orderby");
        if (StringUtil.isEmpty(orderby)) {
            orderby = "contenttype";
        }
        request.setAttribute("orderby", orderby);

        String status = WorkflowManager.STATUS_DRAFT;

        String statusStr = request.getParameter("status");
        if (!StringUtil.isEmpty(statusStr)) {
            status = statusStr;
        }
        
        Query statusQuery = WorkflowManager.createStatusQuery(cloud);
        NodeList statusList = cloud.getList(statusQuery);

        WorkflowStatusInfo ststusInfo = new WorkflowStatusInfo(statusList);
        
        request.setAttribute("statusInfo", ststusInfo);

        String type = getWorkflowType();
        addToRequest(request, "workflowType", type);
        
        NodeQuery listQuery = WorkflowManager.createListQuery(cloud);
        Queries.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, status));
        if (!Workflow.isAcceptedStepEnabled() && WorkflowManager.STATUS_FINISHED.equals(status)) {
           SearchUtil.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, WorkflowManager.STATUS_APPROVED), CompositeConstraint.LOGICAL_OR);
        }
        Queries.addConstraint(listQuery, WorkflowManager.getTypeConstraint(listQuery, type));

        NodeQuery wfQuery = createDetailQuery(cloud, orderby);

        addWorkflowListToRequest(request, cloud, wfQuery, listQuery, "results");
        
        request.setAttribute("acceptedEnabled", Workflow.isAcceptedStepEnabled());
        HttpSession session = request.getSession();
        session.setAttribute("workflow.type", type);
        session.setAttribute("workflow.status", status);
        
        return mapping.findForward(SUCCESS);
    }

    protected abstract String getWorkflowType();
    protected abstract List performWorkflowAction(String actionValueStr, List<Node> nodes, String remark, Cloud cloud);
    protected abstract NodeQuery createDetailQuery(Cloud cloud, String orderby);

    protected List<Node> performWorkflowAction(String action, List<Node> nodes, String remark, WorkflowManager manager) {
        List<Node> errors = new ArrayList<Node>();

        if (ACTION_FINISH.equals(action)) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                manager.finishWriting((Node) i.next(), remark);
            }
        }
        if (ACTION_ACCEPT.equals(action)) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                manager.accept((Node) i.next(), remark);
            }
        }
        if (ACTION_REJECT.equals(action)) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                // Node in status published might be completed already before
                // request reaches this point.
                if (node.getCloud().hasNode(node.getNumber())) {
                    manager.reject(node, remark);
                }
            }
        }
        if (ACTION_RENAME.equals(action)) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                manager.rename((Node) i.next(), remark);
            }
        }
        if (ACTION_PUBLISH.equals(action)) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                manager.accept((Node) i.next(), remark);
            }
            List<Integer> publishNumbers = new ArrayList<Integer>();
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                publishNumbers.add(((Node) i.next()).getNumber());
            }
            
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                try {
                    manager.publish((Node) i.next(), publishNumbers);
                }
                catch (WorkflowException wfe) {
                    errors.addAll(wfe.getErrors());
                }
            }
        }
        return errors;
    }
    
    private void addWorkflowListToRequest(HttpServletRequest request, Cloud cloud, NodeQuery detailQuery, NodeQuery listQuery, String attributeName) {
        NodeList ceWorkflowNumbers = cloud.getList(listQuery);
        if (!ceWorkflowNumbers.isEmpty()) {
            NodeQuery numbersQuery = createDetailsWithNumbersQuery(detailQuery, ceWorkflowNumbers);
            NodeList dataList = cloud.getList(numbersQuery);
            request.setAttribute(attributeName, dataList);
        }
    }
    
    private NodeQuery createDetailsWithNumbersQuery(NodeQuery wfQuery, NodeList workflowNumbers) {
        NodeQuery detailQuery = (NodeQuery) wfQuery.clone();
        
        BasicStep wfStep = (BasicStep) detailQuery.getStep(WorkflowManager.WORKFLOW_MANAGER_NAME);
        // Retrieve the data of the content element workflow items.
        Iterator wfIterator = workflowNumbers.iterator();
        while (wfIterator.hasNext()) {
           Node node = ((Node) wfIterator.next());
           int workflowNumber = node.getIntValue(WorkflowManager.WORKFLOW_MANAGER_NAME+".number");
           wfStep.addNode(workflowNumber);
        }
        return detailQuery;
    }
    
    protected void addOrderBy(NodeManager manager, NodeQuery query, String fieldname) {
        Step step = query.getStep(manager.getName());
        StepField sf = query.createStepField(step, manager.getField(fieldname));
        query.addSortOrder(sf, SortOrder.ORDER_ASCENDING);
    }

}
