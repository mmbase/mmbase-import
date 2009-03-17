package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.AggregatedField;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicAggregatedField;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.util.NodeComparator;

/**
 * Select the often used images in all channels or in the current channel.
 * 
 * @author Eva Guo
 * @author Marco Fang
 * @author Billy Xie
 */
public class HighFrequencyAssetAction extends PagerAction {

   private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";
   private static final String CHANNELID = "channelid";
   private static final String RESULTCOUNT = "resultCount";
   private static final String RESULTS = "results";
   private static final String IMAGESEARCH = "imagesearch";
   private static final String ATTACHMENTSEARCH = "attachmentsearch";
   private static final String URLSEARCH = "urlsearch";
   private static final String CONTENTCHANNEL = "contentchannel";
   private static final String CREATIONREL = "creationrel";
   private static final String CURRENTCHANNEL = "current";
   private static final String ATTACHMENTS = "attachments";
   private static final String IMAGES = "images";
   private static final String URLS = "urls";
   private static final String ALL = "all";
   private static final String SITEIMAGES = "siteimages";
   private static final String CREATION = "creation";
   private static final String CONTENTELEMENT = "contentelement";
   private static final String SOURCE = "source";
   private static final String DESTINATION = "destination";
   private static final String POSREL = "posrel";
   private static final String IMAGEREL = "imagerel";
   private static final String NUMBER = "number";
   private static final String TITLE = "title";
   private static final String COUNTALIAS = "countalias";
   private static final String ASSETSHOW = "assetShow";
   private static final String STRICT = "strict";

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      HighFrequencyAssetForm highFrequencyAssetForm = (HighFrequencyAssetForm) form;

      String channelid = highFrequencyAssetForm.getChannelid();
      boolean existChannelConstraint = StringUtils.isNotEmpty(channelid) && !ALL.equals(channelid) && !SITEIMAGES.equals(channelid);
      String imageShow = highFrequencyAssetForm.getAssetShow();
      if (StringUtils.isEmpty(imageShow)) {
         imageShow = "thumbnail";
      }

      Query query = cloud.createAggregatedQuery();
      List<Node> result = new ArrayList<Node>();
      String assettypes = highFrequencyAssetForm.getAssettypes();
      NodeManager assetManager = null;
      if (assettypes.equals(ATTACHMENTS)) {
         assetManager = cloud.getNodeManager(ATTACHMENTS);
      } else if (assettypes.equals(IMAGES)) {
         assetManager = cloud.getNodeManager(IMAGES);
      } else if (assettypes.equals(URLS)) {
         assetManager = cloud.getNodeManager(URLS);
      }
      NodeManager channelManager = cloud.getNodeManager(CONTENTCHANNEL);
      Step assetStep;
      Integer trashNumber = Integer.parseInt(RepositoryUtil.getTrash(cloud));
      // search in one contentchannel
      if (existChannelConstraint) {
         // search in the current channel
         if (CURRENTCHANNEL.equals(channelid)) {
            channelid = (String) request.getSession().getAttribute(CREATION);
         }
         query.addStep(channelManager);
         RelationStep creationrelStep = query.addRelationStep(assetManager, CREATIONREL, SOURCE);
         assetStep = creationrelStep.getNext();
         Queries.addConstraints(query, channelManager.getName() + ".number=" + channelid);
      } else {// search all contentchannels
         // CMSC-1260 Content search also finds elements in Recycle bin
         Step channelStep = query.addStep(channelManager);
         assetStep = query.addRelationStep(assetManager, CREATIONREL, SOURCE).getNext();
         StepField stepField = query.createStepField(channelStep, channelManager.getField("number"));
         FieldValueConstraint constraint = query.createConstraint(stepField, FieldCompareConstraint.NOT_EQUAL,
               trashNumber);
         Queries.addConstraint(query, constraint);
      }

      NodeManager contentManager = cloud.getNodeManager(CONTENTELEMENT);
      RelationStep assetrelStep = null;
      if (assettypes.equals(ATTACHMENTS) || assettypes.equals(URLS)) {
         assetrelStep = query.addRelationStep(contentManager, POSREL, SOURCE);
      } else if (assettypes.equals(IMAGES)) {
         assetrelStep = query.addRelationStep(contentManager, IMAGEREL, SOURCE);
      }

      Step contentStep = assetrelStep.getNext();
      query.addAggregatedField(assetStep, assetManager.getField(NUMBER), AggregatedField.AGGREGATION_TYPE_GROUP_BY);
      query.addAggregatedField(assetStep, assetManager.getField(TITLE), AggregatedField.AGGREGATION_TYPE_GROUP_BY);
      BasicAggregatedField countField = (BasicAggregatedField) query.addAggregatedField(contentStep, contentManager
            .getField(NUMBER), AggregatedField.AGGREGATION_TYPE_COUNT);
      countField.setAlias(COUNTALIAS);

      NodeList usedAssetCountResult = query.getList();

      if (usedAssetCountResult != null && !usedAssetCountResult.isEmpty()) {
         Vector<String> fields = new Vector();
         fields.addAll(Arrays.asList(COUNTALIAS, TITLE));
         Vector<String> sortDirs = new Vector();
         sortDirs.addAll(Arrays.asList(NodeComparator.DOWN, NodeComparator.UP));
         NodeComparator comparator = new NodeComparator(fields, sortDirs);
         Collections.sort(usedAssetCountResult, comparator);
      }

      NodeQuery nodeQuery = cloud.createNodeQuery();
      // search in one contentchannel
      if (existChannelConstraint) {
         assetStep = nodeQuery.addStep(assetManager);
         nodeQuery.addRelationStep(channelManager, CREATIONREL, DESTINATION);
         Queries.addConstraints(nodeQuery, channelManager.getName() + ".number=" + channelid);
      } else {
         // CMSC-1260 Content search also finds elements in Recycle bin
         Step channelStep = nodeQuery.addStep(channelManager);
         assetStep = nodeQuery.addRelationStep(assetManager, RepositoryUtil.CREATIONREL, "SOURCE").getNext();
         StepField stepField = nodeQuery.createStepField(channelStep, channelManager.getField("number"));
         FieldValueConstraint channelConstraint = nodeQuery.createConstraint(stepField,
               FieldCompareConstraint.NOT_EQUAL, trashNumber);
         SearchUtil.addConstraint(nodeQuery, channelConstraint);
      }
      nodeQuery.setNodeStep(assetStep);

      NodeList unusedAssetResult = nodeQuery.getList();

      for (int i = 0; i < usedAssetCountResult.size(); i++) {
         int imgNumber = usedAssetCountResult.getNode(i).getIntValue(NUMBER);
         for (int j = 0; j < unusedAssetResult.size(); j++) {
            Node assetNode = unusedAssetResult.getNode(j);
            if (imgNumber == assetNode.getIntValue(NUMBER)) {
               result.add(assetNode);
               unusedAssetResult.remove(assetNode);
               break;
            }
         }
      }

      if (unusedAssetResult != null && !unusedAssetResult.isEmpty()) {
         Vector<String> titleField = new Vector();
         titleField.add(TITLE);
         NodeComparator titleComparator = new NodeComparator(titleField);
         Collections.sort(unusedAssetResult, titleComparator);
      }

      if (unusedAssetResult != null && !unusedAssetResult.isEmpty()) {
         result.addAll(unusedAssetResult);
      }

      int resultCount = result.size();

      // used for paging about maxnum
      int maxnum = 0;
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         maxnum = 25;
      } else {
         maxnum = Integer.parseInt(resultsPerPage);
      }

      // Set the offset (used for paging).
      List<Node> resultAfterPaging = result;
      int offset = 0;
      if (highFrequencyAssetForm.getOffset() != null && highFrequencyAssetForm.getOffset().matches("\\d+")) {
         offset = Integer.parseInt(highFrequencyAssetForm.getOffset());
      }
      if (offset * maxnum + maxnum < result.size()) {
         resultAfterPaging = result.subList(offset * maxnum, offset * maxnum + maxnum);
      } else {
         resultAfterPaging = result.subList(offset * maxnum, result.size());
      }
      request.setAttribute(CHANNELID, channelid);
      request.setAttribute(ASSETSHOW, imageShow);
      request.setAttribute(OFFSET, offset);
      request.setAttribute(RESULTCOUNT, resultCount);
      request.setAttribute(RESULTS, resultAfterPaging);
      request.setAttribute(STRICT, highFrequencyAssetForm.getStrict());

      String targetForward = null;
      if (assettypes.equals(ATTACHMENTS)) {
         targetForward = ATTACHMENTSEARCH;
      } else if (assettypes.equals(IMAGES)) {
         targetForward = IMAGESEARCH;
      } else if (assettypes.equals(URLS)) {
         targetForward = URLSEARCH;
      }
      return mapping.findForward(targetForward);
   }

}
