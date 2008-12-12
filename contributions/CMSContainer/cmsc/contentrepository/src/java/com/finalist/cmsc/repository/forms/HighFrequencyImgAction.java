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
import org.mmbase.storage.search.AggregatedField;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.implementation.BasicAggregatedField;
import org.mmbase.util.NodeComparator;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.struts.PagerAction;

/**
 * Select the often used images in all channels or in the current channel.
 * 
 * @author Eva Guo
 * @author Marco Fang
 */
public class HighFrequencyImgAction extends PagerAction {

   private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";
   private static final String CHANNELID = "channelid";
   private static final String RESULTCOUNT = "resultCount";
   private static final String RESULTS = "results";
   private static final String SUCCESS = "success";
   private static final String CONTENTCHANNEL = "contentchannel";
   private static final String CREATIONREL = "creationrel";
   private static final String CURRENTCHANNEL = "current";
   private static final String IMAGES = "images";
   private static final String ALL = "all";
   private static final String CREATION = "creation";
   private static final String CONTENTELEMENT = "contentelement";
   private static final String SOURCE = "source";
   private static final String DESTINATION = "destination";
   private static final String IMAGEREL = "imagerel";
   private static final String NUMBER = "number";
   private static final String IMAGENUMBER = "imageNumber";
   private static final String COUNT = "count";
   private static final String TITLE = "title";
   private static final String COUNTALIAS = "countalias";

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      HighFrequencyImgForm highFrequencyImgForm = (HighFrequencyImgForm) form;
      
      String channelid = highFrequencyImgForm.getChannelid();
      boolean existChannelConstraint = StringUtils.isNotEmpty(channelid) && !ALL.equals(channelid);
      
      Query query = cloud.createAggregatedQuery();
      List<Node> result = new ArrayList<Node>();
      Step imageStep;
      NodeManager imgManager = cloud.getNodeManager(IMAGES);
      NodeManager channelManager = cloud.getNodeManager(CONTENTCHANNEL);
      
      // search in one contentchannel
      if (existChannelConstraint) {
         // search in the current channel
         if (CURRENTCHANNEL.equals(channelid)) {
            channelid = (String) request.getSession().getAttribute(CREATION);
         }
         query.addStep(channelManager);
         RelationStep creationrelStep = query.addRelationStep(imgManager, CREATIONREL, SOURCE);
         imageStep = creationrelStep.getNext();
         Queries.addConstraints(query, channelManager.getName() + ".number=" + channelid);
      } else {// search all contentchannels
         imageStep = query.addStep(imgManager);
      }
      
      NodeManager contentManager = cloud.getNodeManager(CONTENTELEMENT);
      RelationStep imagerelStep = query.addRelationStep(contentManager, IMAGEREL, SOURCE);
      Step contentStep = imagerelStep.getNext();
      query.addAggregatedField(imageStep, imgManager.getField(NUMBER), AggregatedField.AGGREGATION_TYPE_GROUP_BY);
      BasicAggregatedField countField = (BasicAggregatedField) query.addAggregatedField(contentStep, contentManager
            .getField(NUMBER), AggregatedField.AGGREGATION_TYPE_COUNT);
      countField.setAlias(COUNTALIAS);

      NodeList usedImgCountResult = query.getList();

      if(usedImgCountResult!=null&&!usedImgCountResult.isEmpty()){
         Vector<String> fields = new Vector();
         fields.addAll(Arrays.asList(COUNTALIAS, TITLE));
         Vector<String> sortDirs = new Vector();
         sortDirs.addAll(Arrays.asList(NodeComparator.DOWN, NodeComparator.UP));
         NodeComparator comparator = new NodeComparator(fields, sortDirs);
         Collections.sort(usedImgCountResult, comparator);
      }
      
      NodeQuery nodeQuery = cloud.createNodeQuery();
      imageStep = nodeQuery.addStep(imgManager);
      //search in one contentchannel
      if(existChannelConstraint){
         nodeQuery.addRelationStep(channelManager,CREATIONREL,DESTINATION);
         Queries.addConstraints(nodeQuery, channelManager.getName() + ".number=" + channelid);
      }
      nodeQuery.setNodeStep(imageStep);
      
      NodeList unusedImgResult = nodeQuery.getList();
      
      for (int i = 0; i < usedImgCountResult.size(); i++) {
         int imgNumber = usedImgCountResult.getNode(i).getIntValue(NUMBER);
         for (int j = 0; j < unusedImgResult.size(); j++) {
            Node imgNode = unusedImgResult.getNode(j);
            if (imgNumber == imgNode.getIntValue(NUMBER)) {
               result.add(imgNode);
               unusedImgResult.remove(imgNode);
               break;
            }
         }
      }
      
      if(unusedImgResult!=null&&!unusedImgResult.isEmpty()){
         Vector<String> titleField = new Vector();
         titleField.add(TITLE);
         com.finalist.cmsc.util.NodeComparator titleComparator = new com.finalist.cmsc.util.NodeComparator(titleField);
         Collections.sort(unusedImgResult, titleComparator);
      }
      
      if(unusedImgResult!=null&&!unusedImgResult.isEmpty()){
      result.addAll(unusedImgResult);
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
      if (highFrequencyImgForm.getOffset() != null && highFrequencyImgForm.getOffset().matches("\\d+")) {
         offset = Integer.parseInt(highFrequencyImgForm.getOffset());
      }
      if (offset * maxnum + maxnum < result.size()) {
         resultAfterPaging = result.subList(offset * maxnum, offset * maxnum + maxnum);
      } else {
         resultAfterPaging = result.subList(offset * maxnum, result.size());
      }
      request.setAttribute(CHANNELID, channelid);
      request.setAttribute(RESULTCOUNT, resultCount);
      request.setAttribute(RESULTS, resultAfterPaging);
      return mapping.findForward(SUCCESS);
   }

}
