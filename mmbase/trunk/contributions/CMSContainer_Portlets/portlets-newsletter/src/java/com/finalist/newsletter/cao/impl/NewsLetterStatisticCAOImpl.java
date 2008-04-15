package com.finalist.newsletter.cao.impl;

import java.util.Date;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Query;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueBetweenConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueDateConstraint;
import org.mmbase.storage.search.implementation.BasicStep;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;


public class NewsLetterStatisticCAOImpl implements NewsLetterStatisticCAO {

	private Cloud cloud;


   public NewsLetterStatisticCAOImpl() {
   }
   
   public NewsLetterStatisticCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }



   public NodeList getAllRecords() {
		Query query = cloud.createQuery();
		query.addStep(cloud.getNodeManager("newsletterdailylog"));
		NodeList list = query.getList();

		return list;
	}

	public NodeList getRecordsByNewsletter(int newsletter) {
		Query query = cloud.createQuery();
		NodeManager manager = cloud.getNodeManager("newsletterdailylog");

		Step step1 = query.addStep(manager);
		StepField field1 = query.addField(step1, manager.getField("newsletter"));
		query.addField(step1, manager.getField("subscribe"));
		query.addField(step1, manager.getField("bounches"));
		query.addField(step1, manager.getField("unsubscribe"));
		query.addField(step1, manager.getField("removed"));
		query.addField(step1, manager.getField("logdate"));
		BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(
				field1, newsletter);
		query.setConstraint(constraint);
		NodeList list = query.getList();
		return list;
	}

	public NodeList getAllRecordsByPeriod(Date start, Date end) {
		Query query = cloud.createQuery();
		NodeManager manager = cloud.getNodeManager("newsletterdailylog");

		/*
		 * ¶Ônewsletterdailylog±íµÄÈÕÆÚ×Ö¶Î½øÐÐÔ¼Êø£¬²¢È¡³öËùÓÐÐÅÏ¢ ÏëÒª´ïµ½µÄÐ§¹û£º select * from
		 * newsletterdailylog where logdate between date1 and date2;
		 */
		Step step1 = query.addStep(manager);
		query.addField(step1, manager.getField("subscribe"));
		query.addField(step1, manager.getField("bounches"));
		query.addField(step1, manager.getField("unsubscribe"));
		query.addField(step1, manager.getField("removed"));
		query.addField(step1, manager.getField("newsletter"));
		StepField field = query.addField(step1, manager.getField("logdate"));
		BasicFieldValueBetweenConstraint constraint = new BasicFieldValueBetweenConstraint(
				field, start, end);

		/*
		 * BasicFieldValueDateConstraint constraint = new
		 * BasicFieldValueDateConstraint(field,start.getTime(),BasicFieldValueDateConstraint.MILLISECOND);
		 */
		query.setConstraint(constraint);
		NodeList list = query.getList();
		return list;
	}

	public NodeList getRecordsByNewsletterAndPeriod(Date start, Date end,
			int newsletter) {
		Query query = cloud.createQuery();
		NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      
      Step step1 = query.addStep(manager);
		query.addField(step1, manager.getField("subscribe"));
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

		NodeList list = query.getList();

		return list;
	}

   public void logPubliction(int newsletterId, int post) {
      NodeManager logManager = cloud.getNodeManager("newsletterdailylog");
      Node logNode = logManager.createNode();
      logNode.setIntValue("newsletter",newsletterId);
      logNode.setIntValue("post",post);
      logNode.commit();
   }

}
