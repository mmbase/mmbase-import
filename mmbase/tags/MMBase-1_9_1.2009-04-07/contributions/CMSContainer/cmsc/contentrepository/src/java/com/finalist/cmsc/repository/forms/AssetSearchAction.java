package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.NodeGUITypeComparator;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.resources.forms.QueryStringComposer;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.PagerAction;

public class AssetSearchAction extends PagerAction {

   public static final String GETURL = "geturl";

   public static final String PERSONAL = "personal";
   public static final String MODE = "mode";
   public static final String STRICT = "strict";
   public static final String AUTHOR = "author";
   public static final String OBJECTID = "objectid";
   public static final String PARENTCHANNEL = "parentchannel";
   public static final String ASSETTYPES = "assettypes";

   public static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(AssetSearchAction.class.getName());

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      log.debug("Starting the search:");

      // Initialize
      AssetSearchForm searchForm = (AssetSearchForm) form;

      String deleteAssetRequest = request.getParameter("deleteAssetRequest");
      String searchShow = request.getParameter("searchShow");
      String strict = request.getParameter(STRICT);
      // it is just for keep strict if search commit is false
      if (StringUtils.isNotEmpty(strict)) {
         request.setAttribute(STRICT, strict);
      }
      request.getSession().setAttribute("title", searchForm.getTitle());
      if (StringUtils.isEmpty(searchShow)) {
         searchShow = (String) request.getSession().getAttribute("searchShow");
         if (StringUtils.isEmpty(searchShow)) {
            searchShow = "list";
         }
      }
      if (StringUtils.isNotEmpty(deleteAssetRequest)) {
         if (deleteAssetRequest.startsWith("massDelete:")) {
            massDeleteAsset(deleteAssetRequest.substring(11));
         } else {
            deleteAsset(deleteAssetRequest);
         }

         // add a flag to let search result page refresh the channels frame,
         // so that the number of item in recyclebin can update
         request.setAttribute("refreshChannels", "refreshChannels");
      }

      // First prepare the typeList, we'll need this one anyway:
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types = AssetElementUtil.getAssetTypes(cloud);
      List<String> hiddenTypes = AssetElementUtil.getHiddenAssetTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, "typesList", typesList);
      request.getSession().setAttribute("searchShow", searchShow);

      // Switching tab, no searching.
      if ("false".equalsIgnoreCase(searchForm.getSearch())) {
         return mapping.getInputForward();
      }

      NodeManager nodeManager = cloud.getNodeManager(searchForm.getAssettypes());
      QueryStringComposer queryStringComposer = new QueryStringComposer();
      if (StringUtils.isNotEmpty(strict)) {
         queryStringComposer.addParameter(STRICT, strict);
      }
      if (StringUtils.isNotEmpty(request.getParameter(MODE))) {
         queryStringComposer.addParameter(MODE, request.getParameter(MODE));
      }
      NodeQuery query = cloud.createNodeQuery();

      // First we add the assettype parameter
      queryStringComposer.addParameter(ASSETTYPES, searchForm.getAssettypes());

      // First add the proper step to the query.
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step assetStep = query.addRelationStep(nodeManager, RepositoryUtil.CREATIONREL, "SOURCE").getNext();
      if (StringUtils.isNotEmpty(searchForm.getParentchannel())) {
         query.addNode(channelStep, cloud.getNode(searchForm.getParentchannel()));
         query.setNodeStep(assetStep);
         queryStringComposer.addParameter(PARENTCHANNEL, searchForm.getParentchannel());
      } else {
         // CMSC-1260 Content search also finds elements in Recycle bin
         Integer trashNumber = Integer.parseInt(RepositoryUtil.getTrash(cloud));
         StepField stepField = query.createStepField(channelStep, channelNodeManager.getField("number"));
         FieldValueConstraint channelConstraint = query.createConstraint(stepField, FieldCompareConstraint.NOT_EQUAL,
               trashNumber);
         SearchUtil.addConstraint(query, channelConstraint);
         query.setNodeStep(assetStep);
      }

      // Order the result by:
      String order = searchForm.getOrder();

      // set default order field
      if (StringUtils.isEmpty(order)) {
         if (nodeManager.hasField("number")) {
            order = "number";
         }
         if (nodeManager.hasField("title")) {
            order = "title";
         }
      }
      if (StringUtils.isNotEmpty(order)) {
         queryStringComposer.addParameter(ORDER, searchForm.getOrder());
         queryStringComposer.addParameter(DIRECTION, "" + searchForm.getDirection());
         // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
         if (!"otype".equals(order)) {
            query.addSortOrder(query.getStepField(nodeManager.getField(order)), searchForm.getDirection());
         }
      }

      query.setDistinct(true);

      // Set some date constraints.
      queryStringComposer.addParameter(AssetElementUtil.CREATIONDATE_FIELD, "" + searchForm.getCreationdate());
      SearchUtil
            .addDayConstraint(query, nodeManager, AssetElementUtil.CREATIONDATE_FIELD, searchForm.getCreationdate());
      queryStringComposer.addParameter(AssetElementUtil.PUBLISHDATE_FIELD, "" + searchForm.getPublishdate());
      SearchUtil.addDayConstraint(query, nodeManager, AssetElementUtil.PUBLISHDATE_FIELD, searchForm.getPublishdate());
      queryStringComposer.addParameter(AssetElementUtil.EXPIREDATE_FIELD, "" + searchForm.getExpiredate());
      SearchUtil.addDayConstraint(query, nodeManager, AssetElementUtil.EXPIREDATE_FIELD, searchForm.getExpiredate());
      queryStringComposer.addParameter(AssetElementUtil.LASTMODIFIEDDATE_FIELD, "" + searchForm.getLastmodifieddate());
      SearchUtil.addDayConstraint(query, nodeManager, AssetElementUtil.LASTMODIFIEDDATE_FIELD, searchForm
            .getLastmodifieddate());

      // Perhaps we have some more constraints if the nodetype was specified (=>
      // not
      // assetelement).
      if (!AssetElementUtil.ASSETELEMENT.equalsIgnoreCase(nodeManager.getName())) {
         FieldList fields = nodeManager.getFields();
         FieldIterator fieldIterator = fields.fieldIterator();

         while (fieldIterator.hasNext()) {
            Field field = fieldIterator.nextField();
            String paramName = nodeManager.getName() + "." + field.getName();
            String paramValue = request.getParameter(paramName);
            if (StringUtils.isNotEmpty(paramValue)) {
               SearchUtil.addLikeConstraint(query, field, paramValue.trim());
            }
            queryStringComposer.addParameter(paramName, paramValue);
         }
      }

      // Add the title constraint:
      if (StringUtils.isNotEmpty(searchForm.getTitle())) {
         queryStringComposer.addParameter(AssetElementUtil.TITLE_FIELD, searchForm.getTitle().trim());
         Field field = nodeManager.getField(AssetElementUtil.TITLE_FIELD);
         Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle().trim());
         SearchUtil.addConstraint(query, titleConstraint);
      }
      if (StringUtils.isNotEmpty(searchForm.getTitle())) {
         queryStringComposer.addParameter(AssetElementUtil.CREATOR_FIELD, searchForm.getTitle().trim());
         Field field = nodeManager.getField(AssetElementUtil.CREATOR_FIELD);
         Constraint creatorConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle().trim());
         SearchUtil.addORConstraint(query, creatorConstraint);
      }

      // Set the objectid constraint
      if (StringUtils.isNotEmpty(searchForm.getObjectid())) {
         String stringObjectId = searchForm.getObjectid().trim();
         Integer objectId = null;
         if (stringObjectId.matches("^\\d+$")) {
            objectId = Integer.valueOf(stringObjectId);
         } else {
            if (cloud.hasNode(stringObjectId)) {
               objectId = Integer.valueOf(cloud.getNode(stringObjectId).getNumber());
            } else {
               objectId = Integer.valueOf(-1);
            }
         }
         SearchUtil.addEqualConstraint(query, nodeManager, AssetElementUtil.NUMBER_FIELD, objectId);
         queryStringComposer.addParameter(OBJECTID, stringObjectId);
      }

      // Add the user personal:
      if (StringUtils.isNotEmpty(searchForm.getPersonal())) {

         String useraccount = cloud.getUser().getIdentifier();
         if (AssetElementUtil.LASTMODIFIER_FIELD.equals(searchForm.getPersonal())) {
            SearchUtil.addEqualConstraint(query, nodeManager, AssetElementUtil.LASTMODIFIER_FIELD, useraccount);
         }
         if (AUTHOR.equals(searchForm.getPersonal())) {
            SearchUtil.addEqualConstraint(query, nodeManager, AssetElementUtil.CREATOR_FIELD, useraccount);
         }
         queryStringComposer.addParameter(PERSONAL, searchForm.getPersonal());
      }

      // Add the user
      if (StringUtils.isNotEmpty(searchForm.getUseraccount())) {
         String useraccount = searchForm.getUseraccount();
         SearchUtil.addEqualConstraint(query, nodeManager, AssetElementUtil.LASTMODIFIER_FIELD, useraccount);
      }

      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         resultsPerPage = "25";
      }
      // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
      if (StringUtils.isEmpty(order) || !"otype".equals(order)) {
         query.setMaxNumber(Integer.parseInt(resultsPerPage));
      }

      // Set the offset (used for paging).
      String offset = "0";
      if (searchForm.getOffset() != null && searchForm.getOffset().matches("\\d+")) {
         offset = searchForm.getOffset();
         // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
         if (StringUtils.isEmpty(order) || !"otype".equals(order)) {
            query.setOffset(query.getMaxNumber() * Integer.parseInt(offset));
         }
         queryStringComposer.addParameter(OFFSET, searchForm.getOffset());
      }

      log.debug("QUERY: " + query);

      int resultCount = Queries.count(query);
      NodeList results = query.getNodeManager().getList(query);
      // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
      if (StringUtils.isNotEmpty(order) && "otype".equals(order)) {
         boolean reverse = false;
         if (searchForm.getDirection() == SortOrder.ORDER_DESCENDING) {
            reverse = true;
         }
         Collections.sort(results, new NodeGUITypeComparator(cloud.getLocale(), reverse));
         int fromIndex = (Integer.parseInt(resultsPerPage)) * Integer.parseInt(offset);
         int toIndex = resultCount < (fromIndex + Integer.parseInt(resultsPerPage)) ? resultCount
               : (fromIndex + Integer.parseInt(resultsPerPage));
         results = results.subNodeList(fromIndex, toIndex);
      }

      // Set everything on the request.
      searchForm.setResultCount(resultCount);
      searchForm.setResults(results);
      request.setAttribute(GETURL, queryStringComposer.getQueryString()
            + ((searchShow == null) ? "" : "&searchShow=" + searchShow));
      return super.execute(mapping, form, request, response, cloud);
   }

   private void massDeleteAsset(String deleteAsset) {
      if (StringUtils.isNotBlank(deleteAsset)) {
         String[] deleteAssets = deleteAsset.split(",");
         for (String asset : deleteAssets) {
            deleteAsset(asset);
         }
      }
   }

   private void deleteAsset(String deleteAssetRequest) {
      StringTokenizer commandAndNumber = new StringTokenizer(deleteAssetRequest, ":");
      String command = commandAndNumber.nextToken();
      String nunmber = commandAndNumber.nextToken();

      if ("moveToRecyclebin".equals(command)) {
         moveAssetToRecyclebin(nunmber);
      }

      if ("permanentDelete".equals(command)) {
         deleteAssetPermanent(nunmber);
      }

   }

   private void deleteAssetPermanent(String objectnumber) {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = provider.getCloud();

      Node objectNode = cloud.getNode(objectnumber);
      if (Workflow.hasWorkflow(objectNode)) {
         // at this time complete is the same as remove
         Workflow.complete(objectNode);
      }
      objectNode.delete(true);

   }

   private void moveAssetToRecyclebin(String nunmber) {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = provider.getCloud();

      Node objectNode = cloud.getNode(nunmber);

      // NodeList channels = RepositoryUtil.getDeletionChannels(objectNode);
      Node channelNode = RepositoryUtil.getCreationChannel(objectNode);
      if (channelNode != null) {
         RepositoryUtil.addAssetDeletionRelation(objectNode, channelNode);
         RepositoryUtil.removeCreationRelForAsset(objectNode);
      }

      RepositoryUtil.addAssetToChannel(objectNode, RepositoryUtil.getTrash(cloud));

      // unpublish and remove from workflow
      Publish.remove(objectNode);
      Workflow.remove(objectNode);
      Publish.unpublish(objectNode);
   }

}
