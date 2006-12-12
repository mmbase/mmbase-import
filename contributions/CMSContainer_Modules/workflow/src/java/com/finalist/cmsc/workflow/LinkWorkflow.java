/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.workflow;

import java.util.*;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.services.workflow.Workflow;


public class LinkWorkflow extends RepositoryWorkflow {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(LinkWorkflow.class.getName());

    public static final String TYPE_LINK = "link";

    public LinkWorkflow(Cloud cloud) {
        super(cloud);
    }

    @Override
    public Node createFor(Node node, String remark) {
        return createFor(null, node, remark);
    }

    public Node createFor(Node content, Node channel, String remark) {
        Node wfItem = createFor(TYPE_LINK, remark, Workflow.STATUS_FINISHED);
        if (content != null) {
            RelationUtil.createRelation(wfItem, content, WORKFLOWREL);
        }
        if (channel != null) {
            RelationUtil.createRelation(wfItem, channel, WORKFLOWREL);
        }

        List users = getUsersWithRights(channel, Role.EDITOR);
        changeUserRelations(wfItem, users);

       log.debug("Link Workflow " + wfItem.getNumber() + " created for content " + (content != null ? content.getNumber() : ""));
        return wfItem;
     }

    public void finishWriting(Node content, String remark) {
        throw new UnsupportedOperationException("Linked workflows are always finished after linking");
    }

    /**
     * Status change to 'APPROVED'. The workflow appears on all chiefeditor workflow screens
     */
    public void accept(Node node, String remark) {
        Node wfItem;
        Node channel;
        if (RepositoryUtil.isContentChannel(node)) {
            wfItem = getWorkflowNode(node, TYPE_LINK);
            channel = node;
        }
        else {
            if (ContentElementUtil.isContentElement(node)) {
                wfItem = getWorkflowNode(node, TYPE_LINK);
                channel = RepositoryUtil.getCreationChannel(node);
            }
            else {
                wfItem = node;
                channel = getLinkChannel(wfItem);
            }
        }

       super.accept(wfItem, channel, remark);
    }


    public void reject(Node node, String remark) {
        Node wfItem;
        if (RepositoryUtil.isContentChannel(node)) {
            wfItem = getWorkflowNode(node, TYPE_LINK);
        }
        else {
            if (ContentElementUtil.isContentElement(node)) {
                wfItem = getWorkflowNode(node, TYPE_LINK);
            }
            else {
                wfItem = node;
            }
        }
        changeWorkflow(wfItem, STATUS_FINISHED, remark);
    }

    /**
     * Put content elements in publishqueue
     */
    public void publish(Node node) throws WorkflowException {
       publish(node, null);
    }

    public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
        Node channel;
        if (RepositoryUtil.isContentChannel(node)) {
            channel = node;
        }
        else {
            channel = getLinkChannel(node);
        }
       publish(channel, false, TYPE_LINK, publishNumbers);
    }

    public void complete(Node contentNode) {
        complete(contentNode, TYPE_LINK);
    }

    public boolean hasWorkflow(Node node) {
        return hasWorkflow(node, TYPE_LINK);
    }

   @Override
   public boolean isWorkflowElement(Node node) {
      return RepositoryUtil.isContentChannel(node);
   }

    @Override
    public UserRole getUserRole(Node node) {
        return RepositoryUtil.getRole(node.getCloud(), node, false);
    }
}
