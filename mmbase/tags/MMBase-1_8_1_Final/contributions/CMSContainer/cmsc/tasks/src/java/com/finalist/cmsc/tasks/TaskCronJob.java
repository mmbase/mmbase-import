/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.tasks;

import java.util.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.security.SecurityUtil;


public class TaskCronJob implements CronJob {

    private long lastExecutionTime;
    
    public void init(CronEntry cronEntry) {
        Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
        lastExecutionTime = TasksUtil.getLastTaskCreationTime(cloud, TasksUtil.TYPE_EXPIRE);
    }

    public void stop() {
        // nothing to do
    }

    public void run() {
        long fromTime = lastExecutionTime;
        lastExecutionTime = System.currentTimeMillis();
        long toTime = lastExecutionTime;

        List<Node> usersToNotify = new ArrayList<Node>();
        
        Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
        NodeManager manager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
        Field notificationField = manager.getField(ContentElementUtil.NOTIFICATIONDATE_FIELD);
        
        NodeQuery query = manager.createQuery();
        SearchUtil.addDatetimeConstraint(query, notificationField, fromTime, toTime);

        HugeNodeListIterator contentElementListIterator = new HugeNodeListIterator(query);
        while (contentElementListIterator.hasNext()) {
           Node contentNode = contentElementListIterator.nextNode();
           if (!TasksUtil.hasTask(contentNode)) {
               Node userNode = ContentElementUtil.getOwner(contentNode);
               TasksUtil.createExpireTask(cloud, String.valueOf(contentNode.getNumber()),
                               contentNode.getStringValue(ContentElementUtil.TITLE_FIELD),
                               contentNode.getDateValue(ContentElementUtil.EXPIREDATE_FIELD),
                               userNode, contentNode.getNodeManager().getName(), contentNode);
               
               if (!usersToNotify.contains(userNode)) {
                   usersToNotify.add(userNode);
               }
           }
        }

        if (!usersToNotify.isEmpty()) {
            Node cloudUserNode = SecurityUtil.getUserNode(cloud); 
    
            for (Node user : usersToNotify) {
                NodeQuery taskQuery = SearchUtil.createRelatedNodeListQuery(user, 
                        TasksUtil.TASK, TasksUtil.ASSIGNEDREL, 
                        TasksUtil.STATUS, TasksUtil.STATUS_INIT, null, null, SearchUtil.SOURCE);
                int numberOfTasks = Queries.count(taskQuery);
                TasksUtil.sendExpireNotification(user, cloudUserNode, numberOfTasks);
    
                HugeNodeListIterator taskListIterator = new HugeNodeListIterator(taskQuery);
                while (taskListIterator.hasNext()) {
                    Node taskNode = taskListIterator.nextNode();
                    TasksUtil.notified(taskNode);
                }
            }
        }
    }

}
