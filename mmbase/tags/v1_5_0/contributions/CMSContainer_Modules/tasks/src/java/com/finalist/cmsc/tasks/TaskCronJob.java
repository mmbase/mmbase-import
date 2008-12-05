/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.tasks;

import java.util.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.security.SecurityUtil;

public class TaskCronJob extends AbstractCronJob implements CronJob {
   private static final Logger log = Logging.getLoggerInstance(TaskCronJob.class.getName());

   private long lastExecutionTime;

   private ResourceBundle bundle;


   @Override
   public void init() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
      lastExecutionTime = TasksUtil.getLastTaskCreationTime(cloud, TasksUtil.TYPE_EXPIRE);
      bundle = ResourceBundle.getBundle("cmsc-tasks");
   }

   @Override
   public void run() {
      log.debug("TaskCronJob running");
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
            // TODO use bundle of user language
            String description = bundle.getString("task.cronjob.expire");
            TasksUtil.createExpireTask(cloud, contentNode.getStringValue(ContentElementUtil.TITLE_FIELD), description,
                  contentNode.getDateValue(ContentElementUtil.EXPIREDATE_FIELD), userNode, contentNode.getNodeManager()
                        .getName(), contentNode);

            if (!usersToNotify.contains(userNode)) {
               usersToNotify.add(userNode);
            }
         }
      }

      if (!usersToNotify.isEmpty()) {
         Node cloudUserNode = SecurityUtil.getUserNode(cloud);

         for (Node user : usersToNotify) {
            NodeQuery taskQuery = SearchUtil.createRelatedNodeListQuery(user, TasksUtil.TASK, TasksUtil.ASSIGNEDREL,
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

      // clean old tasks
      NodeManager tman = cloud.getNodeManager(TasksUtil.TASK);
      Field deadlineField = tman.getField(TasksUtil.DEADLINE);

      Calendar c = Calendar.getInstance();
      c.roll(Calendar.DAY_OF_YEAR, -7);
      toTime = c.getTimeInMillis();

      NodeQuery cleanup = tman.createQuery();
      SearchUtil.addDatetimeConstraint(cleanup, deadlineField, 0, toTime);

      HugeNodeListIterator taskListIterator = new HugeNodeListIterator(cleanup);
      while (taskListIterator.hasNext()) {
         Node taskNode = taskListIterator.nextNode();
         taskNode.delete(true);
      }

   }

}
