package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.time.DateUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueBetweenConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.util.StatisticUtil;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;

public class NewsLetterStatisticCAOImpl implements NewsLetterStatisticCAO {

   private Cloud cloud;
   private static Logger log = Logging.getLoggerInstance(NewsLetterStatisticCAOImpl.class.getName());

   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   public List < StatisticResult > getAllRecords() {

      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(cloud.getNodeManager("newsletterdailylog"));
      query.setNodeStep(step);
      NodeList list = query.getList();
      StatisticUtil util = new StatisticUtil();
      return util.convertFromNodeList(list);
   }

   public List < StatisticResult > getRecordsByNewsletter(int newsletter) {
      NodeQuery query = cloud.createNodeQuery();
      NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      Step step1 = query.addStep(manager);
      StepField field1 = query.addField(step1, manager.getField("newsletter"));
      query.addField(step1, manager.getField("subscribe"));
      query.addField(step1, manager.getField("bounches"));
      query.addField(step1, manager.getField("post"));
      query.addField(step1, manager.getField("unsubscribe"));
      query.addField(step1, manager.getField("removed"));
      query.addField(step1, manager.getField("logdate"));
      BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(field1, newsletter);
      query.setConstraint(constraint);
      query.setNodeStep(step1);
      NodeList list = query.getList();
      StatisticUtil util = new StatisticUtil();
      return util.convertFromNodeList(list);
   }

   public List < StatisticResult > getAllRecordsByPeriod(Date start, Date end) {
      NodeQuery query = cloud.createNodeQuery();
      NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      Step step1 = query.addStep(manager);
      query.addField(step1, manager.getField("subscribe"));
      query.addField(step1, manager.getField("post"));
      query.addField(step1, manager.getField("bounches"));
      query.addField(step1, manager.getField("unsubscribe"));
      query.addField(step1, manager.getField("removed"));
      query.addField(step1, manager.getField("newsletter"));
      StepField field = query.addField(step1, manager.getField("logdate"));
      BasicFieldValueBetweenConstraint constraint = new BasicFieldValueBetweenConstraint(field, start, end);
      query.setConstraint(constraint);
      query.setNodeStep(step1);
      NodeList list = query.getList();
      StatisticUtil util = new StatisticUtil();
      return util.convertFromNodeList(list);
   }

   /**
    * @return List which sumLogs about StatisticResult 
    */
   public List < StatisticResult > getLogs() {
      NodeList nodelist = getNodelist();
      synchronized (this) {
         List < StatisticResult > logsList = new ArrayList < StatisticResult >();
         List < StatisticResult > resultList = getLogsByNode(nodelist);
         if (null != resultList) {
            // use newsletterId and userId as key to take the logic
            Map < String , StatisticResult > sumedMap = new HashMap < String , StatisticResult >();
            for (StatisticResult r : resultList) {
               String uniteKey = r.getNewsletterId() + "H" + r.getUserId();
               StatisticResult freq = (StatisticResult) sumedMap.get(uniteKey);
               if (freq == null) {
                  freq = r;
               } else {
                  freq = sumLogs(freq, r);
               }
               sumedMap.put(uniteKey, freq);
            }
            if (null != sumedMap) {
               deleteOldLogsByNode(nodelist);

               for (Iterator iter = sumedMap.keySet().iterator(); iter.hasNext();) {
                  StatisticResult result = new StatisticResult();
                  String element = (String) iter.next();
                  StatisticResult child = (StatisticResult) sumedMap.get(element);
                  result = sumLogs(result, child);
                  String[] i = element.split("H");
                  result.setNewsletterId(Integer.parseInt(i[0]));
                  result.setUserId(Integer.parseInt(i[1]));
                  logsList.add(result);
               }
            }
            return logsList;

         } else {
            return null;
         }
      }
   }

   private StatisticResult sumLogs(StatisticResult freq, StatisticResult r) {
      StatisticResult result = new StatisticResult();
      result.setBounches(freq.getBounches() + r.getBounches());
      result.setName(r.getName());
      result.setPost(freq.getPost() + r.getPost());
      result.setRemoved(freq.getRemoved() + r.getRemoved());
      result.setSubscribe(freq.getSubscribe() + r.getSubscribe());
      result.setUnsubscribe(freq.getUnsubscribe() + r.getUnsubscribe());
      return result;
   }

   public NodeList getNodelist() {
      Date now = new Date();
      Date startDate = DateUtils.addHours(now, -23);
      Date endDate = DateUtils.addDays(now, 0);
      NodeQuery query = cloud.createNodeQuery();
      NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      Step step = query.addStep(manager);
      StepField field = query.addField(step, manager.getField("logdate"));
      StepField field1 = query.addField(step, manager.getField("post"));
      BasicFieldValueBetweenConstraint constraint = new BasicFieldValueBetweenConstraint(field, startDate, endDate);
      BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(field1, 0);
      constraint1.setOperator(FieldCompareConstraint.GREATER);
      BasicCompositeConstraint constraints = new BasicCompositeConstraint(2);
      constraints.addChild(constraint);
      constraints.addChild(constraint1);
      query.setConstraint(constraints);
      query.setNodeStep(step);
      NodeList list = null;
      try {
         list = query.getList();
      } catch (NullPointerException e) {
         log.debug(e.toString());
      }
      return list;
   }

   private List < StatisticResult > getLogsByNode(NodeList list) {
      StatisticUtil util = new StatisticUtil();
      return util.convertFromNodeList(list);
   }

   private boolean deleteOldLogsByNode(NodeList list) {
      boolean flag = false;
      if (null != list) {
         for (int i = 0; i < list.size(); i++) {
            Node subscriptionNode = list.getNode(i);
            subscriptionNode.delete();
            subscriptionNode.commit();
         }
         flag = true;
      }
      return flag;

   }

   public List < StatisticResult > getRecordsByNewsletterAndPeriod(Date start, Date end, int newsletter) {
      NodeQuery query = cloud.createNodeQuery();
      NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      Step step1 = query.addStep(manager);
      query.addField(step1, manager.getField("subscribe"));
      query.addField(step1, manager.getField("post"));
      query.addField(step1, manager.getField("bounches"));
      query.addField(step1, manager.getField("unsubscribe"));
      query.addField(step1, manager.getField("removed"));
      BasicCompositeConstraint constraints = new BasicCompositeConstraint(2);
      StepField field = query.addField(step1, manager.getField("logdate"));
      BasicFieldValueBetweenConstraint constraint1 = new BasicFieldValueBetweenConstraint(field, start, end);
      constraints.addChild(constraint1);
      StepField field2 = query.addField(step1, manager.getField("newsletter"));
      BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(field2, newsletter);
      constraints.addChild(constraint2);
      query.setConstraint(constraints);
      query.setNodeStep(step1);
      List < Node > list = query.getList();
      StatisticUtil util = new StatisticUtil();
      return util.convertFromNodeList((NodeList) list);
   }

   public void logPubliction(int userId, int newsletterId, HANDLE handle) {
      if (!mayLog(userId, newsletterId, handle)) {
         return;
      }
      Node logNode = null;
      if (handle.equals(HANDLE.BOUNCE)) {
         logNode = getLogNode(userId, newsletterId);
      }
      if (logNode != null) {
         logNode.setIntValue("bounches", logNode.getIntValue("bounches") + 1);
         logNode.setDateValue("logdate", new Date());
         logNode.commit();
      } else {
         NodeManager logManager = cloud.getNodeManager("newsletterdailylog");
         // Node newsletter = cloud.getNode(newsletterId);
         logNode = logManager.createNode();
         logNode.setIntValue("newsletter", newsletterId);
         logNode.setIntValue("post", 0);
         logNode.setIntValue("bounches", 0);
         logNode.setIntValue("subscribe", 0);
         logNode.setIntValue("unsubscribe", 0);
         logNode.setIntValue("removed", 0);
         logNode.setIntValue("userid", userId);
         logNode.setDateValue("logdate", new Date());
         if (handle.equals(HANDLE.ACTIVE)) {
            logNode.setIntValue("subscribe", 1);
         } else if (handle.equals(HANDLE.INACTIVE)) {
            logNode.setIntValue("unsubscribe", 1);
         } else if (handle.equals(HANDLE.REMOVE)) {
            logNode.setIntValue("removed", 1);
         } else if (handle.equals(HANDLE.BOUNCE)) {
            logNode.setIntValue("bounches", 1);
         } else if (handle.equals(HANDLE.POST)) {
            logNode.setIntValue("post", 1);
         }
         logNode.commit();
      }

   }

   private boolean mayLog(int userId, int newsletterId, HANDLE handle) {

      if (!handle.equals(HANDLE.ACTIVE) && !handle.equals(HANDLE.INACTIVE)) {
         return true;
      }

      boolean isLog = false;
      NodeManager logNodeManager = cloud.getNodeManager("newsletterdailylog");
      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(logNodeManager);
      query.setNodeStep(parameterStep);
      Queries.addSortOrders(query, "logdate", "DOWN");
      query.setMaxNumber(3);
      SearchUtil.addEqualConstraint(query, logNodeManager.getField("newsletter"), new Integer(newsletterId));

      SearchUtil.addEqualConstraint(query, logNodeManager.getField("userid"), new Integer(userId));
      NodeList logs = query.getList();
      if (logs.size() < 2) {
         isLog = true;
      } else {
         int count = 0;
         for (int i = 0; i < logs.size(); i++) {
            Node log = logs.getNode(i);
            if (DateUtils.isSameDay(new Date(), log.getDateValue("logdate"))
                  && (log.getIntValue("subscribe") > 0 || log.getIntValue("unsubscribe") > 0)) {
               count++;
            }
         }
         if (count == 3) {
            isLog = false;
         }
      }
      return isLog;
   }

   public Node getLogNode(int userId, int newsletterId) {
      log.info("-------------------logPubliction   -in process...getLogNode....:   ");
      NodeManager logNodeManager = cloud.getNodeManager("newsletterdailylog");
      Node logNode = null;
      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(logNodeManager);
      query.setNodeStep(parameterStep);
      Queries.addSortOrders(query, "logdate", "DOWN");
      query.setMaxNumber(1);
      SearchUtil.addEqualConstraint(query, logNodeManager.getField("newsletter"), new Integer(newsletterId));
      SearchUtil.addEqualConstraint(query, logNodeManager.getField("userid"), new Integer(userId));
      FieldValueConstraint liConstraint = query.createConstraint((query.getStepField(logNodeManager
            .getField("bounches"))), FieldCompareConstraint.GREATER, new Integer(0));
      SearchUtil.addConstraint(query, liConstraint);
      NodeList logs = query.getList();
      if (logs != null && logs.size() > 0) {
         if (DateUtils.isSameDay(new Date(), logs.getNode(0).getDateValue("logdate"))) {
            logNode = logs.getNode(0);
         }
      }
      return logNode;
   }

   /**
    * @param listRecorder which get from data
    * @return how many SumedLogs insert
    */
   public int insertSumedLogs(List < StatisticResult > logsList) {
      int i = 0;
      NodeManager logManager = cloud.getNodeManager("newsletterdailylog");
      Node logNode;
      Date now = new Date();
      Date start = DateUtils.addHours(now, -12);
      for (StatisticResult r : logsList) {
         logNode = logManager.createNode();
         logNode.setIntValue("newsletter", r.getNewsletterId());
         logNode.setIntValue("post", r.getPost());
         logNode.setIntValue("userid", r.getUserId());
         logNode.setDateValue("logdate", start);
         logNode.setIntValue("bounches", r.getBounches());
         logNode.setIntValue("subscribe", r.getSubscribe());
         logNode.setIntValue("unsubscribe", r.getUnsubscribe());
         logNode.setIntValue("removed", r.getRemoved());
         logNode.commit();
         i++;
      }
      return i;
   }

}
