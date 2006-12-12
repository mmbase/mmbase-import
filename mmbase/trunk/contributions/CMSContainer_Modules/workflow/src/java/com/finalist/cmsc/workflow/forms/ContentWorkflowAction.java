/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.workflow.forms;

import java.util.*;

import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.workflow.*;


public class ContentWorkflowAction extends WorkflowAction {

    @Override
    protected String getWorkflowType() {
        return ContentWorkflow.TYPE_CONTENT;
    }

    @Override
    protected List performWorkflowAction(String action, List<Node> nodes, String remark, Cloud cloud) {
        ContentWorkflow contentWorkflow = new ContentWorkflow(cloud);
        return performWorkflowAction(action, nodes, remark, contentWorkflow);
    }
    
    @Override
    protected NodeQuery createDetailQuery(Cloud cloud, String orderby) {
        NodeManager manager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
        NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);
        
        NodeManager channelManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
        wfQuery.addRelationStep(channelManager, RepositoryUtil.CREATIONREL, null);
        
        wfQuery.addField(ContentElementUtil.CONTENTELEMENT + ".number");
        wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.TITLE_FIELD);
        wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIEDDATE_FIELD);
        wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIER_FIELD);
        wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + "." + RepositoryUtil.NAME_FIELD);
        
        if(orderby.equals(ContentElementUtil.TITLE_FIELD)) {
            addOrderBy(manager, wfQuery, ContentElementUtil.TITLE_FIELD);
        }
        else if(orderby.equals(ContentElementUtil.LASTMODIFIER_FIELD)) {
            addOrderBy(manager, wfQuery, ContentElementUtil.LASTMODIFIER_FIELD);
        }
        else if(orderby.equals(ContentElementUtil.LASTMODIFIEDDATE_FIELD)) {
            addOrderBy(manager, wfQuery, ContentElementUtil.LASTMODIFIEDDATE_FIELD);
        }
        else if(orderby.equals(RepositoryUtil.CONTENTCHANNEL)) {
            addOrderBy(channelManager, wfQuery, RepositoryUtil.NAME_FIELD);
        }
        else if(orderby.equals("number")) {
            addOrderBy(manager, wfQuery, "number");
        }
        else if(orderby.equals( WorkflowManager.REMARK_FIELD)) {
            addOrderBy(WorkflowManager.getManager(cloud), wfQuery, WorkflowManager.REMARK_FIELD);
        }
        else {
            addOrderBy(manager, wfQuery, WorkflowManager.LASTMODIFIEDDATE_FIELD);
        }
        return wfQuery;
    }

}
