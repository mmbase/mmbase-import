package com.finalist.cmsc.repository.status;

import java.util.Calendar;
import java.util.Date;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.cache.CachePolicy;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicLegacyConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;

public class StatusCronJob implements CronJob {

	private static final String STATUS_EXPIRED = "expired";

	private static final String TYPE_CONTENTELEMENT = "contentelement";

	private static final String STATUS_ARCHIVED = "archived";

	private static final String STATUS_NEW = "new";

	/** an embargoed object is not published yet */
	private static final String STATUS_EMBARGOED = "embargoed";

	private static final String FIELD_STATUS = "status";

	private static final String FIELD_ARCHIVEDATE = "archivedate";

	private static final String FIELD_PUBLISHDATE = "publishdate";

	private static final String FIELD_EXPIREDATE = "expiredate";

	private static final String FIELD_USEEXPIRYDATE = "use_expirydate";

	private static final String OPERATOR_LESS_EQUAL = "<=";

	private static final String OPERATOR_GREATER_EQUAL = ">=";

	private static final String PROPERTY_QUERYSIZE = "cronjob.status.querysize";

	private long maximumEndDate;

	private static Logger log = Logging.getLoggerInstance(StatusCronJob.class.getName());

	public void init(CronEntry cronEntry) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(9999, 11, 31, 12, 59, 59);
		maximumEndDate = calendar.getTimeInMillis();
	}

	public void stop() {
	}

	public void run() {

		long startTime = System.currentTimeMillis();

		int newlyExpired = updateNewlyExpired();
		int newlyArchived = updateNewlyArchived();
		int newlyPublished = updateNewlyPublished();

		int degeneratedNew = updateDegeneratedNew();
		int degeneratedPublished = updateDegeneratedPublished();
		int degeneratedArchived = updateDegeneratedArchived();

		if (newlyExpired > 0 || newlyArchived > 0 || newlyPublished > 0 || degeneratedNew > 0 || degeneratedPublished > 0
				|| degeneratedArchived > 0) {
			log.info("newly: embar:" + newlyPublished + " arch:" + newlyArchived + " exp:" + newlyExpired
					+ "   degenerated: new:" + degeneratedNew + " embar:" + degeneratedPublished + " arch:" + degeneratedArchived
					+ "   in " + (System.currentTimeMillis() - startTime) + "ms");
		}
	}

	private int updateDegeneratedNew() {
		NodeList archivedNodeList = getContentWithRestraints(FIELD_PUBLISHDATE, OPERATOR_LESS_EQUAL, new String[] {
				STATUS_EMBARGOED, STATUS_ARCHIVED, STATUS_EXPIRED }, STATUS_NEW);
		return updateListStatus(archivedNodeList, STATUS_NEW);
	}

	private int updateNewlyPublished() {
		NodeList publishedNodeList = getContentWithRestraints(FIELD_PUBLISHDATE, OPERATOR_GREATER_EQUAL,
				new String[] { STATUS_NEW }, STATUS_EMBARGOED);
		return updateListStatus(publishedNodeList, STATUS_EMBARGOED);
	}

	private int updateDegeneratedPublished() {
		NodeList publishedNodeList = getContentWithRestraints(FIELD_ARCHIVEDATE, OPERATOR_LESS_EQUAL, new String[] {
				STATUS_ARCHIVED, STATUS_EXPIRED }, STATUS_EMBARGOED);
		return updateListStatus(publishedNodeList, STATUS_EMBARGOED);
	}

	private int updateNewlyArchived() {
		NodeList archivedNodeList = getContentWithRestraints(FIELD_ARCHIVEDATE, OPERATOR_GREATER_EQUAL, new String[] {
				STATUS_NEW, STATUS_EMBARGOED }, STATUS_ARCHIVED);
		return updateListStatus(archivedNodeList, STATUS_ARCHIVED);
	}

	private int updateDegeneratedArchived() {
		NodeList archivedNodeList = getContentWithRestraints(FIELD_EXPIREDATE, OPERATOR_LESS_EQUAL,
				new String[] { STATUS_EXPIRED }, STATUS_ARCHIVED);
		return updateListStatus(archivedNodeList, STATUS_ARCHIVED);
	}

	private int updateNewlyExpired() {
		NodeList expiredNodeList = getContentWithRestraints(FIELD_EXPIREDATE, OPERATOR_GREATER_EQUAL, new String[] { STATUS_NEW,
				STATUS_EMBARGOED, STATUS_ARCHIVED }, STATUS_EXPIRED);
		return updateListStatus(expiredNodeList, STATUS_EXPIRED);
	}

	private int updateListStatus(NodeList nodeList, String newStatus) {
		
		int resultOk = 0;
		for (NodeIterator i = nodeList.nodeIterator(); i.hasNext();) {
			try {
				Node node = i.nextNode();
				if (ContentElementUtil.isContentElement(node)) {
					node.setStringValue(FIELD_STATUS, newStatus);
					
					cleckDates(node);
					
					node.commit();
					resultOk++;
				} else {
					log.debug("Node " + node.getNumber() + " is not a contentelement!");
				}
			} catch (Exception e) {
				log.error("Unable to update status", e);
			}
		}
		return resultOk;
	}

	private void cleckDates(Node node) {
		Date publishDate = node.getDateValue(FIELD_PUBLISHDATE);
		Date archiveDate = node.getDateValue(FIELD_ARCHIVEDATE);
		Date expireDate = node.getDateValue(FIELD_EXPIREDATE);
		
		if(archiveDate.getTime() < publishDate.getTime()) {
			archiveDate = publishDate;
			node.setDateValue(FIELD_ARCHIVEDATE, archiveDate);
			log.debug("fixed archive date for: "+node.getNumber());
		}
		
		if(expireDate.getTime() < archiveDate.getTime()) {
			expireDate = archiveDate;
			node.setDateValue(FIELD_EXPIREDATE, expireDate);
			log.debug("fixed expire date for: "+node.getNumber());
		}
	}

	private NodeList getContentWithRestraints(String fieldName, String operator, String[] statusValues, String currentStatus) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeManager nodeManager = cloud.getNodeManager(TYPE_CONTENTELEMENT);
		NodeQuery nodeQuery = nodeManager.createQuery();
		nodeQuery.setCachePolicy(CachePolicy.NEVER);

		String maxQuerySize = PropertiesUtil.getProperty(PROPERTY_QUERYSIZE, cloud);
		if (!StringUtil.isEmpty(maxQuerySize)) {
			nodeQuery.setMaxNumber(Integer.parseInt(maxQuerySize));
		}

		BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

		if (operator.equals(OPERATOR_GREATER_EQUAL)) {
			constraint.addChild(SearchUtil.createDatetimeConstraint(nodeQuery, nodeManager.getField(fieldName), 0, System
					.currentTimeMillis()));
		} else if (operator.equals(OPERATOR_LESS_EQUAL)) {
			constraint.addChild(SearchUtil.createDatetimeConstraint(nodeQuery, nodeManager.getField(fieldName), System
					.currentTimeMillis(), maximumEndDate));
		}

		// also put use_expirydate field to query constraints
		if (fieldName.equalsIgnoreCase(FIELD_EXPIREDATE)) {
			Field useExpireField = nodeManager.getField(FIELD_USEEXPIRYDATE);
			constraint.addChild(nodeQuery.createConstraint(nodeQuery.getStepField(useExpireField), FieldCompareConstraint.EQUAL,
					Boolean.TRUE));
		}

		Field useStatusField = nodeManager.getField(FIELD_STATUS);
		constraint.addChild(nodeQuery.createConstraint(nodeQuery.getStepField(useStatusField), FieldCompareConstraint.NOT_EQUAL,
				currentStatus));

		BasicCompositeConstraint statusConstraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_OR);
		for (int count = 0; count < statusValues.length; count++) {
			String value = statusValues[count];
			if (value == null) {
				statusConstraint.addChild(new BasicLegacyConstraint("m_" + FIELD_STATUS + " IS NULL"));
			} else {
				statusConstraint.addChild(SearchUtil.createEqualConstraint(nodeQuery, nodeManager.getField(FIELD_STATUS), value));
			}
		}
		constraint.addChild(statusConstraint);

		nodeQuery.setConstraint(constraint);

		// log.info(nodeQuery.toSql());

		return nodeManager.getList(nodeQuery);
	}
}
