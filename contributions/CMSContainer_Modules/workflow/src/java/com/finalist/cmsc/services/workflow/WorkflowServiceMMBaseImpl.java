/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.workflow;

import java.util.List;

import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.workflow.ContentWorkflow;
import com.finalist.cmsc.workflow.LinkWorkflow;
import com.finalist.cmsc.workflow.PageWorkflow;
import com.finalist.cmsc.workflow.WorkflowManager;
import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;


public class WorkflowServiceMMBaseImpl extends WorkflowService {


   public Node create(Node node, String remark){
        return getManager(node).createFor(node, remark);
    }

    public void finish(Node node, String remark){
        getManager(node).finishWriting(node, remark);
    }

    public void accept(Node node, String remark){
       if (isAcceptedStepEnabled()) {
         getManager(node).accept(node, remark);
       }
       else {
          getManager(node).complete(node);
       }
    }

    public void reject(Node node, String remark) {
        getManager(node).reject(node, remark);
    }

    public void publish(Node node) throws WorkflowException {
        getManager(node).publish(node);
    }

    public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
        getManager(node).publish(node, publishNumbers);
    }
    
    public void complete(Node node) {
        getManager(node).complete(node);
    }
    
    public void remove(Node node) {
        getManager(node).remove(node);
    }
    
    private WorkflowManager getManager(Node node) {
        WorkflowManager manager = getLinkWorkflow(node.getCloud());
        if (manager.isWorkflowElement(node)) {
            return manager;
        }
        manager = getContentWorkflow(node.getCloud());
        if (manager.isWorkflowElement(node)) {
            return manager;
        }
        manager = getPageWorkflow(node.getCloud());
        if (manager.isWorkflowElement(node)) {
            return manager;
        }
        throw new IllegalArgumentException("Node was not a workflow element " + node);
    }
    
    private ContentWorkflow getContentWorkflow(Cloud cloud) {
        return new ContentWorkflow(cloud);
    }

    private LinkWorkflow getLinkWorkflow(Cloud cloud) {
        return new LinkWorkflow(cloud);
    }

    private PageWorkflow getPageWorkflow(Cloud cloud) {
        return new PageWorkflow(cloud);
    }

    @Override
    public String getStatus(Node node) {
        return getManager(node).getStatus(node);
    }

    @Override
    public boolean hasWorkflow(Node node) {
        return getContentWorkflow(node.getCloud()).hasWorkflow(node) 
            || getPageWorkflow(node.getCloud()).hasWorkflow(node)
            || getLinkWorkflow(node.getCloud()).hasWorkflow(node);
    }

    @Override
    public boolean isWorkflowType(String type) {
        Cloud cloud = getUserCloud();
        return getContentWorkflow(cloud).isWorkflowType(type) 
            || getPageWorkflow(cloud).isWorkflowType(type);
    }

    public boolean isWorkflowElement(Node node) {
        Cloud cloud = node.getCloud();
        return getContentWorkflow(cloud).isWorkflowElement(node) 
            || getPageWorkflow(cloud).isWorkflowElement(node);
    }
    
    private Cloud getUserCloud() {
        Cloud cloud = CloudUtil.getCloudFromThread();
        if (cloud == null) {
            cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
        }
        return cloud;
    }


    @Override
    public boolean mayEdit(Node node) {
        UserRole userrole = getManager(node).getUserRole(node);
        return mayEdit(node, userrole);
    }
    
    @Override
    public boolean mayEdit(Node node, UserRole userrole) {
        String status = getStatus(node);
        
        boolean deny = WorkflowManager.STATUS_PUBLISHED.equals(status) 
            || (WorkflowManager.STATUS_APPROVED.equals(status) 
                && (userrole.getRole() == Role.EDITOR || userrole.getRole() == Role.WRITER));

        return !deny;
    }

    @Override
    public boolean mayPublish(Node node) {
        UserRole userrole = getManager(node).getUserRole(node);
        return mayPublish(node, userrole);
    }

    @Override
    public boolean mayPublish(Node node, UserRole userrole) {
        String status = getStatus(node);
        
        boolean deny = WorkflowManager.STATUS_DRAFT.equals(status) 
                || !(userrole.getRole() == Role.CHIEFEDITOR || userrole.getRole() == Role.WEBMASTER);

        return !deny;
    }

    
    @Override
    public List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
        return getManager(node).isReadyToPublish(node, publishNumbers);
    }

   protected Log getLogger() {
      return LogFactory.getLog(WorkflowServiceMMBaseImpl.class);
   }

}
