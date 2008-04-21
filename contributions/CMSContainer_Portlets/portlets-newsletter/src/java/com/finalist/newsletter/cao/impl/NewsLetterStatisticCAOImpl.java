package com.finalist.newsletter.cao.impl;

import java.util.Date;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueBetweenConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueDateConstraint;
import org.mmbase.storage.search.implementation.BasicStep;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.util.StatisticUtil;
import com.finalist.newsletter.domain.StatisticResult;

public class NewsLetterStatisticCAOImpl implements NewsLetterStatisticCAO {

	private Cloud cloud;
	
	 public NewsLetterStatisticCAOImpl() {
   }
   
   public NewsLetterStatisticCAOImpl(Cloud cloud) {
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
	
	 public void logPubliction(int newsletterId, int post) {
      NodeManager logManager = cloud.getNodeManager("newsletterdailylog");
      Node logNode = logManager.createNode();
      logNode.setIntValue("newsletter",newsletterId);
      logNode.setIntValue("post",post);
      logNode.commit();
   }
}
