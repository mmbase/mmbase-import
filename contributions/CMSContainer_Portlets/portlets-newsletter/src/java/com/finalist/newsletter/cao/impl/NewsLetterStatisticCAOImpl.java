package com.finalist.newsletter.cao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueBetweenConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.util.StatisticUtil;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import net.sf.mmapps.commons.bridge.RelationUtil;

public class NewsLetterStatisticCAOImpl implements NewsLetterStatisticCAO {

	private Cloud cloud;
	
   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<StatisticResult> getAllRecords (){

		NodeQuery query = cloud.createNodeQuery();
		Step step = query.addStep(cloud.getNodeManager("newsletterdailylog"));
		query.setNodeStep(step);
		NodeList list = query.getList();
		
		StatisticUtil util = new StatisticUtil();
		return util.convertFromNodeList(list);
	}

	public List<StatisticResult> getRecordsByNewsletter (int newsletter){

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
		BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(
				field1, newsletter);
		query.setConstraint(constraint);
		query.setNodeStep(step1);
		NodeList list = query.getList();

		StatisticUtil util = new StatisticUtil();
		return util.convertFromNodeList(list);
	}

	public List<StatisticResult> getAllRecordsByPeriod (Date start, Date end){

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
		BasicFieldValueBetweenConstraint constraint = new BasicFieldValueBetweenConstraint(
				field, start, end);
		query.setConstraint(constraint);
		query.setNodeStep(step1);
		NodeList list = query.getList();

		StatisticUtil util = new StatisticUtil();
		return util.convertFromNodeList(list);
	}

	public List<StatisticResult> getRecordsByNewsletterAndPeriod (Date start,
			Date end, int newsletter){

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
		BasicFieldValueBetweenConstraint constraint1 = new BasicFieldValueBetweenConstraint(
				field, start, end);
		constraints.addChild(constraint1);
		StepField field2 = query.addField(step1, manager.getField("newsletter"));
		BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(
				field2, newsletter);
		constraints.addChild(constraint2);
		query.setConstraint(constraints);
		query.setNodeStep(step1);
		List<Node> list = query.getList();

		StatisticUtil util = new StatisticUtil();
		return util.convertFromNodeList((NodeList) list);
	}
	
	public void logPubliction(int userId,int newsletterId, HANDLE handle) {
      if(!mayLog(userId,newsletterId)) {
         return;
      }
      NodeManager logManager = cloud.getNodeManager("newsletterdailylog");
      Node newsletter = cloud.getNode(newsletterId);
      Node logNode = logManager.createNode();
      logNode.setIntValue("newsletter",newsletterId);
      logNode.setIntValue("post",0);
      logNode.setIntValue("bounches",0);
      logNode.setIntValue("subscribe",0);
      logNode.setIntValue("unsubscribe",0);
      logNode.setIntValue("removed",0);
      logNode.setIntValue("userid",userId);
      logNode.setDateValue("logdate",new Date());
      if(handle.equals(HANDLE.ACTIVE)) {
         logNode.setIntValue("subscribe",1);
      }
      else if(handle.equals(HANDLE.INACTIVE)) {
         logNode.setIntValue("unsubscribe",1);
      }
      else if(handle.equals(HANDLE.REMOVE)) {
         logNode.setIntValue("removed",1);
      }
      else if(handle.equals(HANDLE.BOUNCE)) {
         logNode.setIntValue("bounches",1);
      }
      logNode.commit();
      RelationUtil.createRelation(newsletter, logNode, "related");
    }

	 private boolean mayLog(int userId,int newsletterId) {
	    boolean isLog = false;
	    NodeManager logNodeManager = cloud.getNodeManager("newsletterdailylog");
	    NodeQuery query = cloud.createNodeQuery();
	    Step parameterStep = query.addStep(logNodeManager);
	    query.setNodeStep(parameterStep);
	    Queries.addSortOrders(query, "logdate", "DOWN");
	    query.setMaxNumber(2);
	    SearchUtil.addEqualConstraint(query, logNodeManager.getField("newsletter"), new Integer(newsletterId));

	    SearchUtil.addEqualConstraint(query, logNodeManager.getField("userid"),  new Integer(userId));
	    NodeList logs = query.getList();
	    if(logs.size() < 2) {
	       isLog = true;
	    }
	    else if(logs.size() ==2){
	       isLog = !(DateUtils.isSameDay(new Date(),logs.getNode(0).getDateValue("logdate")) && DateUtils.isSameDay(new Date(),logs.getNode(1).getDateValue("logdate")));
	    }
	    return isLog;
	 }

}
