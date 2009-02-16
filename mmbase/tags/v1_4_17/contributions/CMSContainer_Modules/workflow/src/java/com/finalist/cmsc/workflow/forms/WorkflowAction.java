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

import com.finalist.cmsc.services.workflow.*;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.workflow.WorkflowManager;

public abstract class WorkflowAction extends MMBaseFormlessAction {

   public static final Object ACTION_FINISH = "finish";

   public static final Object ACTION_ACCEPT = "accept";

   public static final Object ACTION_REJECT = "reject";

   public static final Object ACTION_PUBLISH = "publish";

   public static final Object ACTION_RENAME = "rename";

   private static final Object REMARK_UNCHANGED = "[unchanged-item]";


   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String actionValueStr = request.getParameter("actionvalue");

      String remark = request.getParameter("remark");
      if (remark != null && remark.equals(REMARK_UNCHANGED)) {
         remark = null;
      }
      if (!StringUtil.isEmpty(actionValueStr)) {
         List<Node> nodes = new ArrayList<Node>();
         Enumeration<String> parameters = request.getParameterNames();
         while (parameters.hasMoreElements()) {
            String name = parameters.nextElement();
            if (name.startsWith("check_")) {
               int lastUScore = name.lastIndexOf("_");
               int number = Integer.parseInt(name.substring(lastUScore + 1));
               if (cloud.hasNode(number)) {
                  Node n = cloud.getNode(number);
                  nodes.add(n);
               }
            }
         }
         List<Node> workflowErrors = performWorkflowAction(actionValueStr, nodes, remark);

         if (workflowErrors != null && workflowErrors.size() > 0) {
            String url = mapping.getPath() + "?status=" + request.getParameter("status");
            request.setAttribute("errors", workflowErrors);
            request.setAttribute("returnUrl", url);
            request.setAttribute("contentNodeList", nodes);
            // return new ActionForward(url);
         }
      }

      String orderby = request.getParameter("orderby");
      if (StringUtil.isEmpty(orderby)) {
         orderby = "contenttype";
      }
      request.setAttribute("orderby", orderby);

      String laststatus = request.getParameter("laststatus");
      if (StringUtil.isEmpty(laststatus) || laststatus == null) {
         request.setAttribute("laststatus", true);
      }
      else {
         request.setAttribute("laststatus", laststatus.equals("true") ? false : true);
      }

      String status = Workflow.STATUS_DRAFT;
      String statusStr = request.getParameter("status");
      if (!StringUtil.isEmpty(statusStr)) {
         status = statusStr;
      }

      WorkflowStatusInfo ststusInfo = Workflow.getStatusInfo(cloud);
      request.setAttribute("statusInfo", ststusInfo);
      String type = getWorkflowType();
      addToRequest(request, "workflowType", type);

      NodeQuery listQuery = WorkflowManager.createListQuery(cloud);
      Queries.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, status));
      if (!Workflow.isAcceptedStepEnabled() && Workflow.STATUS_FINISHED.equals(status)) {
         SearchUtil.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, Workflow.STATUS_APPROVED),
               CompositeConstraint.LOGICAL_OR);
      }
      Queries.addConstraint(listQuery, WorkflowManager.getTypeConstraint(listQuery, type));
      NodeQuery wfQuery = createDetailQuery(cloud, orderby, (laststatus == null) ? false : (laststatus.equals("true")));

      addWorkflowListToRequest(request, cloud, wfQuery, listQuery, "results");

      request.setAttribute("acceptedEnabled", Workflow.isAcceptedStepEnabled());
      HttpSession session = request.getSession();
      session.setAttribute("workflow.type", type);
      session.setAttribute("workflow.status", status);
      return mapping.findForward(SUCCESS);
   }


   protected abstract String getWorkflowType();


   protected abstract NodeQuery createDetailQuery(Cloud cloud, String orderby, boolean AorD);


   protected List<Node> performWorkflowAction(String action, List<Node> nodes, String remark) {
      List<Node> errors = new ArrayList<Node>();

      if (ACTION_FINISH.equals(action)) {
         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Workflow.finish(i.next(), remark);
         }
      }
      if (ACTION_ACCEPT.equals(action)) {
         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Workflow.accept(i.next(), remark);
         }
      }
      if (ACTION_REJECT.equals(action)) {
         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node node = i.next();
            // Node in status published might be completed already before
            // request reaches this point.
            if (node.getCloud().hasNode(node.getNumber())) {
               Workflow.reject(node, remark);
            }
         }
      }
      if (ACTION_RENAME.equals(action)) {
         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Workflow.remark(i.next(), remark);
         }
      }
      if (ACTION_PUBLISH.equals(action)) {
         List<Integer> publishNumbers = new ArrayList<Integer>();
         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node publishNode = i.next();
            if (Workflow.isAllowedToPublish(publishNode)) {
               publishNumbers.add((publishNode).getNumber());
            }
         }

         for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            try {
               Node publishNode = i.next();
               if (publishNumbers.contains(publishNode.getNumber())) {
                  Workflow.publish(publishNode, publishNumbers);
               }
               else {
                  Workflow.accept(publishNode, "");
               }
            }
            catch (WorkflowException wfe) {
               errors.addAll(wfe.getErrors());
            }
         }
      }
      return errors;
   }


   private void addWorkflowListToRequest(HttpServletRequest request, Cloud cloud, NodeQuery detailQuery,
         NodeQuery listQuery, String attributeName) {
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
      Iterator<Node> wfIterator = workflowNumbers.iterator();
      while (wfIterator.hasNext()) {
         Node node = wfIterator.next();
         int workflowNumber = node.getIntValue(WorkflowManager.WORKFLOW_MANAGER_NAME + ".number");
         wfStep.addNode(workflowNumber);
      }
      return detailQuery;
   }


   protected void addOrderBy(NodeManager manager, NodeQuery query, String fieldname, boolean aord) {
      Step step = query.getStep(manager.getName());
      StepField sf = query.createStepField(step, manager.getField(fieldname));
      if (aord) {
         /*
          * System.out.println("WorkflowAction : orderby-- " + fieldname + "
          * descending");
          */
         query.addSortOrder(sf, SortOrder.ORDER_DESCENDING);
      }
      else {
         /*
          * System.out.println("WorkflowAction : orderby-- " + fieldname + "
          * ascending");
          */
         query.addSortOrder(sf, SortOrder.ORDER_ASCENDING);
      }
   }

}
