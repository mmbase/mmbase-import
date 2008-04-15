package com.finalist.cmsc.tasks;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.AggregatedField;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.util.bundles.JstlUtil;

public class TasksUtil {

   public static final String TASK = "task";
   public static final String TASKREL = "taskrel";

   public static final String TITLE = "title";
   public static final String DESCRIPTION = "description";
   public static final String CREATIONDATE = "creationdate";
   public static final String DEADLINE = "deadline";
   public static final String NODETYPE = "nodetype";
   public static final String STATUS = "status";
   public static final String TYPE = "type";

   public static final String TYPE_USER = "user";
   public static final String TYPE_EXPIRE = "expire";

   public final static String STATUS_INIT = "task.status.init";
   public final static String STATUS_NOTIFIED = "task.status.notified";
   public final static String STATUS_DONE = "task.status.done";

   public static final String ASSIGNEDREL = "assignedrel";
   private static final String CREATORREL = "creatorrel";

   private static final String RESOURCEBUNDLE_BASENAME = "cmsc-tasks";

   private TasksUtil() {
      // Utility
   }


   public static Node createUserTask(Cloud cloud, String title, String description, Date deadline, String nodetype,
         int user) {
      String type = TYPE_USER;
      String status = STATUS_INIT;
      Node userNode = null;
      if (user > -1) {
         userNode = cloud.getNode(user);
      }
      if (userNode == null) {
         userNode = SecurityUtil.getUserNode(cloud);
      }
      if (SecurityUtil.isLoggedInUser(cloud, userNode)) {
         // "fixed" NIJ-615, this should stay but someone decided otherwise
         // status = STATUS_NOTIFIED;
         status = STATUS_INIT;
      }
      Node taskNode = createTask(cloud, title, description, deadline, type, status, userNode, nodetype, null);
      if (!SecurityUtil.isLoggedInUser(cloud, userNode)) {
         Node cloudUserNode = SecurityUtil.getUserNode(cloud);
         sendNotification(userNode, cloudUserNode, taskNode);
      }
      return taskNode;
   }


   public static Node createExpireTask(Cloud cloud, String title, String description, Date deadline, Node userNode,
         String nodetype, Node node) {
      return createTask(cloud, title, description, deadline, TYPE_EXPIRE, STATUS_INIT, userNode, nodetype, node);
   }


   public static Node createTask(Cloud cloud, String title, String description, Date deadline, String type,
         String status, Node userNode, String nodetype, Node node) {
      Node taskNode = cloud.getNodeManager(TASK).createNode();
      taskNode.setStringValue(TITLE, title);
      taskNode.setStringValue(DESCRIPTION, description);
      taskNode.setDateValue(DEADLINE, deadline);
      taskNode.setStringValue(STATUS, status);
      taskNode.setStringValue(TYPE, type);
      taskNode.setStringValue(NODETYPE, nodetype);
      taskNode.commit();

      if (userNode != null) {
         RelationUtil.createRelation(taskNode, userNode, ASSIGNEDREL);
      }
      Node cloudUserNode = SecurityUtil.getUserNode(cloud);
      if (cloudUserNode != null) {
         RelationUtil.createRelation(taskNode, cloudUserNode, CREATORREL);
      }
      if (node != null) {
         RelationUtil.createRelation(taskNode, node, TASKREL);
      }
      return taskNode;
   }


   public static void finishTask(Cloud cloud, String id) {
      Node task = cloud.getNode(id);
      finishTask(task);
   }


   public static void finishTask(Node task) {
      task.setStringValue(STATUS, STATUS_DONE);
      task.commit();
   }


   public static void notified(Cloud cloud, String id) {
      Node task = cloud.getNode(id);
      notified(task);
   }


   public static void notified(Node task) {
      task.setStringValue(STATUS, STATUS_NOTIFIED);
      task.commit();
   }


   public static void sendNotification(Node userNode, Node userFromNode, Node taskNode) {
      boolean emailSignal = userNode.getBooleanValue("emailsignal");
      if (emailSignal) {
         String title = taskNode.getStringValue(TITLE);
         String decription = taskNode.getStringValue(DESCRIPTION);

         String language = userNode.getStringValue("language");
         Locale locale = new Locale(language);

         String subject = JstlUtil.getMessage(RESOURCEBUNDLE_BASENAME, locale, "tasks.email.user.subject");
         String emailMessage = JstlUtil.getMessage(RESOURCEBUNDLE_BASENAME, locale, "tasks.email.user.message");

         String name = SecurityUtil.getFullname(userNode);
         String email = userNode.getStringValue("emailaddress");

         String emailFrom = null;
         String nameFrom = null;
         if (userFromNode != null) {
            nameFrom = SecurityUtil.getFullname(userFromNode);
            emailFrom = userFromNode.getStringValue("emailaddress");
         }

         Object[] arguments = { name, nameFrom, title, decription };
         emailMessage = MessageFormat.format(emailMessage, arguments);

         EmailUtil.send(taskNode.getCloud(), name, email, nameFrom, emailFrom, subject, emailMessage);
      }
   }


   public static void sendExpireNotification(Node userNode, Node userFromNode, int numberOfTasks) {
      boolean emailSignal = userNode.getBooleanValue("emailsignal");
      if (emailSignal) {

         String language = userNode.getStringValue("language");
         Locale locale = new Locale(language);
         
         String subject = JstlUtil.getMessage(RESOURCEBUNDLE_BASENAME, locale, "tasks.email.expire.subject");
         String emailMessage = JstlUtil.getMessage(RESOURCEBUNDLE_BASENAME, locale, "tasks.email.expire.message");

         String name = SecurityUtil.getFullname(userNode);
         String email = userNode.getStringValue("emailaddress");

         String emailFrom = null;
         String nameFrom = null;
         if (userFromNode != null) {
            nameFrom = SecurityUtil.getFullname(userFromNode);
            emailFrom = userFromNode.getStringValue("emailaddress");
         }

         Object[] arguments = { name, nameFrom, numberOfTasks };
         emailMessage = MessageFormat.format(emailMessage, arguments);

         EmailUtil.send(userNode.getCloud(), name, email, nameFrom, emailFrom, subject, emailMessage);
      }
   }


   public static Node getAssignedUser(Node task) {
      return SearchUtil.findRelatedNode(task, SecurityUtil.USER, ASSIGNEDREL);
   }


   public static long getLastTaskCreationTime(Cloud cloud, String type) {
      NodeManager taskManager = cloud.getNodeManager(TASK);
      NodeQuery query = taskManager.createQuery();
      SearchUtil.addEqualConstraint(query, taskManager.getField(TYPE), type);

      Query aggregate = query.aggregatingClone();
      aggregate.addAggregatedField(query.getStep(TASK), taskManager.getField(CREATIONDATE),
            AggregatedField.AGGREGATION_TYPE_MAX);

      NodeList list = cloud.getList(aggregate);
      if (list.size() > 0) {
         Node task = list.getNode(0);
         if (task != null) {
            Date lastCreationDate = task.getDateValue(TasksUtil.CREATIONDATE);
            return lastCreationDate.getTime();
         }
      }
      return 0;
   }


   public static boolean hasTask(Node contentNode) {
      NodeManager manager = contentNode.getCloud().getNodeManager(TASK);
      int count = contentNode.countRelatedNodes(manager, TASKREL, "source");
      return count > 0;
   }

}
