package com.finalist.cmsc.workflow.forms;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class WorkflowAdminAction extends MMBaseFormlessAction {

   private static AddWorkflowTask task;

   private static final String STATUS_INIT = "init";
   private static final String STATUS_RUNNING = "running";
   private static final String STATUS_DONE = "done'";


   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      AddWorkflowTask workflowTask = getAddWorkflowTask();
      request.setAttribute("task", workflowTask);
      getResources(request);
      if (!STATUS_RUNNING.equals(workflowTask.getStatus()) && "start".equals(action)) {
         workflowTask.start();
      }

      return mapping.findForward("success");
   }


   public AddWorkflowTask getAddWorkflowTask() {
      if (task == null) {
         task = new AddWorkflowTask();
      }
      return task;
   }


   @Override
   public String getRequiredRankStr() {
      return ADMINISTRATOR;
   }

   public class AddWorkflowTask implements Runnable {

      private String status = STATUS_INIT;
      private Date startTime;
      private Date estimatedTime;


      AddWorkflowTask() {
         // nothing
      }


      public void start() {
         status = STATUS_INIT;
         startTime = new Date();
         new Thread(this).start();
      }


      public void run() {
         status = STATUS_RUNNING;
         try {
            startTime = new Date();
            Cloud adminCloud = CloudProviderFactory.getCloudProvider().getAdminCloud();

            NodeManager manager = adminCloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
            NodeQuery query = manager.createQuery();
            ContentElementUtil.addLifeCycleConstraint(query, System.currentTimeMillis());
            NodeList contentElementList = query.getList();
            NodeIterator nodeIterator = contentElementList.nodeIterator();
            int count = 1;
            int listSize = contentElementList.size();
            while (nodeIterator.hasNext()) {
               Node node = nodeIterator.nextNode();
               if (!Workflow.hasWorkflow(node) && !Publish.isPublished(node)) {
                  Workflow.create(node, "");
               }
               long estimatedTime = System.currentTimeMillis();
               estimatedTime += (estimatedTime - startTime.getTime()) * (listSize - count++) / listSize;
               setEstimatedTime(new Date(estimatedTime));
            }
         }
         finally {
            status = STATUS_DONE;
         }

      }


      public String getStatus() {
         return status;
      }


      public void setStatus(String status) {
         this.status = status;
      }


      public Date getStartTime() {
         return (Date) startTime.clone();
      }


      public void setStartTime(Date startTime) {
         this.startTime = (Date) startTime.clone();
      }


      public Date getEstimatedTime() {
         return (Date) estimatedTime.clone();
      }


      public void setEstimatedTime(Date estimatedTime) {
         this.estimatedTime = estimatedTime;
      }
   }
}
