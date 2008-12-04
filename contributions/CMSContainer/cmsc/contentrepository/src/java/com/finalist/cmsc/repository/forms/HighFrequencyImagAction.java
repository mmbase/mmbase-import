package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

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

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.util.ComparisonUtil;

/**
 * Select the often used images in all channels or in the current channel.
 * 
 * @author Eva 
 * @author Marco
 */
public class HighFrequencyImagAction extends PagerAction {

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
   private static final String IMAGEREL = "imagerel";
   private static final String NUMBER = "number";
   private static final String IMAGENUMBER = "imageNumber";
   private static final String COUNT = "count";
   private static final String CON = "con";

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      HighFrequencyForm highFrequencyForm = (HighFrequencyForm) form;
      Query query = cloud.createAggregatedQuery();
      Step step;
      NodeManager imgManager = cloud.getNodeManager(IMAGES);
      //search in one contentchannel
      String channelid = highFrequencyForm.getChannelid();
      if (!StringUtil.isEmpty(channelid) && !ALL.equals(channelid)) {
         //search in the current channel
         if (CURRENTCHANNEL.equals(channelid)) {
            channelid = (String) request.getSession().getAttribute(CREATION);
         }
         NodeManager channelManager = cloud.getNodeManager(CONTENTCHANNEL);
         query.addStep(channelManager);
         RelationStep creationrelStep = query.addRelationStep(imgManager, CREATIONREL, SOURCE);
         step = creationrelStep.getNext();
         Queries.addConstraints(query, channelManager.getName() + ".number=" + channelid);
      } else {
         step = query.addStep(imgManager);
      }
      NodeManager relManager = cloud.getNodeManager(CONTENTELEMENT);
      RelationStep relStep = query.addRelationStep(relManager, IMAGEREL, SOURCE);
      Step contentStep = relStep.getNext();
      query.addAggregatedField(step, imgManager.getField(NUMBER), AggregatedField.AGGREGATION_TYPE_GROUP_BY);

      BasicAggregatedField field = (BasicAggregatedField) query.addAggregatedField(contentStep, relManager
            .getField(NUMBER), AggregatedField.AGGREGATION_TYPE_COUNT);
      field.setAlias(CON);

      NodeList middleResults = query.getList();
      List<Map<Object, Object>> results = new ArrayList<Map<Object, Object>>();
      for (int i = 0; i < middleResults.size(); i++) {
         Map<Object, Object> result = new HashMap<Object, Object>();
         Node n = middleResults.getNode(i);
         result.put(IMAGENUMBER, n.getValue(NUMBER));
         result.put(COUNT, n.getIntValue(CON));
         results.add(result);

      }

      ComparisonUtil comparator = new ComparisonUtil();
      comparator.setFields_user(new String[] { COUNT });
      Collections.sort(results, comparator);
      Collections.reverse(results);

      //TODO:this imgResult contains all images,it can be changed to the conditional image list 
      NodeQuery query1 = imgManager.createQuery();
      query1.getNodeStep();
      NodeList imgResult = query1.getList();

      List<Node> newresult = new ArrayList<Node>();
      int x = 0;
      for (int i = 0; i < results.size(); i++) {
         int mid = (Integer) results.get(i).get(IMAGENUMBER);
         for (int j = 0; j < imgResult.size(); j++) {
            Node img = imgResult.getNode(j);
            if (mid == img.getIntValue(NUMBER)) {
               newresult.add(x++, img);
               imgResult.remove(img);
               break;
            }
         }
      }
      int resultCount = newresult.size();
      newresult.addAll(resultCount,imgResult);
      resultCount = newresult.size(); 
      
      // used for paging about maxnum
      int maxnum = 0;
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         maxnum = 25;
      } else {
         maxnum = Integer.parseInt(resultsPerPage);
      }

      // Set the offset (used for paging).
      List<Node> resultAfterPaging = newresult;
      int offset = 0;
      if (highFrequencyForm.getOffset() != null && highFrequencyForm.getOffset().matches("\\d+")) {
         offset = Integer.parseInt(highFrequencyForm.getOffset());
      }
      if (offset * maxnum + maxnum < newresult.size()) {
         resultAfterPaging = newresult.subList(offset * maxnum, offset * maxnum + maxnum);
      } else {
         resultAfterPaging = newresult.subList(offset * maxnum, newresult.size());
      }
      request.setAttribute(CHANNELID, channelid);
      request.setAttribute(RESULTCOUNT, resultCount);
      request.setAttribute(RESULTS, resultAfterPaging);
      return mapping.findForward(SUCCESS);
   }

}
