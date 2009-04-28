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
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.NodeGUITypeComparator;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.resources.forms.QueryStringComposer;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.util.KeywordUtil;

public class ContentSearchAction extends PagerAction {

   public static final String GETURL = "geturl";

   public static final String PERSONAL = "personal";
   public static final String MODE = "mode";
   public static final String AUTHOR = "author";
   public static final String OBJECTID = "objectid";
   public static final String PARENTCHANNEL = "parentchannel";
   public static final String CONTENTTYPES = "contenttypes";

   public static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(ContentSearchAction.class.getName());

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      log.debug("Starting the search:");

      // Initialize
      SearchForm searchForm = (SearchForm) form;

      String deleteContentRequest = request.getParameter("deleteContentRequest");
      String index = searchForm.getIndex();
      if (StringUtils.isEmpty(index)) {
         index = "no";
      }
      request.setAttribute("index", index);
      request.getSession().setAttribute("title", searchForm.getTitle());
      if (StringUtils.isNotEmpty(deleteContentRequest)) {
         if (deleteContentRequest.startsWith("massDelete:")) {
            massDeleteContent(deleteContentRequest.substring(11));
         } else {
            deleteContent(deleteContentRequest);
         }

         // add a flag to let search result page refresh the channels frame,
         // so that the number of item in recyclebin can update
         request.setAttribute("refreshChannels", "refreshChannels");
      }

      // First prepare the typeList, we'll need this one anyway:
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
      List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, "typesList", typesList);

      // Switching tab, no searching.
      if ("false".equalsIgnoreCase(searchForm.getSearch())) {
         return mapping.getInputForward();
      }

      NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
      QueryStringComposer queryStringComposer = new QueryStringComposer();
      if (StringUtils.isNotEmpty(request.getParameter(MODE))) {
         queryStringComposer.addParameter(MODE, request.getParameter(MODE));
      }
      NodeQuery query = cloud.createNodeQuery();

      // First we add the contenttype parameter
      queryStringComposer.addParameter(CONTENTTYPES, searchForm.getContenttypes());

      // First add the proper step to the query.
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step contentStep = query.addRelationStep(nodeManager, RepositoryUtil.CONTENTREL, "DESTINATION").getNext();
      if (StringUtils.isNotEmpty(searchForm.getParentchannel())) {
         query.addNode(channelStep, cloud.getNode(searchForm.getParentchannel()));
         query.setNodeStep(contentStep);
         queryStringComposer.addParameter(PARENTCHANNEL, searchForm.getParentchannel());
      } else {
         // CMSC-1260 Content search also finds elements in Recycle bin
         Integer trashNumber = Integer.parseInt(RepositoryUtil.getTrash(cloud));
         StepField stepField = query.createStepField(channelStep, channelNodeManager.getField("number"));
         FieldValueConstraint channelConstraint = query.createConstraint(stepField, FieldCompareConstraint.NOT_EQUAL,
               trashNumber);
         SearchUtil.addConstraint(query, channelConstraint);
         query.setNodeStep(contentStep);
      }
      // Order the result by:
      String order = searchForm.getOrder();

      // set default order field
      if (StringUtils.isEmpty(order)) {
         if (nodeManager.hasField("title")) {
            order = "title";
         }
         if (nodeManager.hasField("name")) {
            order = "name";
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
      queryStringComposer.addParameter(ContentElementUtil.CREATIONDATE_FIELD, "" + searchForm.getCreationdate());
      SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.CREATIONDATE_FIELD, searchForm
            .getCreationdate());
      queryStringComposer.addParameter(ContentElementUtil.PUBLISHDATE_FIELD, "" + searchForm.getPublishdate());
      SearchUtil
            .addDayConstraint(query, nodeManager, ContentElementUtil.PUBLISHDATE_FIELD, searchForm.getPublishdate());
      queryStringComposer.addParameter(ContentElementUtil.EXPIREDATE_FIELD, "" + searchForm.getExpiredate());
      SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.EXPIREDATE_FIELD, searchForm.getExpiredate());
      queryStringComposer
            .addParameter(ContentElementUtil.LASTMODIFIEDDATE_FIELD, "" + searchForm.getLastmodifieddate());
      SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIEDDATE_FIELD, searchForm
            .getLastmodifieddate());

      // Perhaps we have some more constraints if the nodetype was specified (=>
      // not
      // contentelement).
      if (!ContentElementUtil.CONTENTELEMENT.equalsIgnoreCase(nodeManager.getName())) {
         FieldList fields = nodeManager.getFields();
         FieldIterator fieldIterator = fields.fieldIterator();

         while (fieldIterator.hasNext()) {
            Field field = fieldIterator.nextField();
            String paramName = nodeManager.getName() + "." + field.getName();
            String paramValue = request.getParameter(paramName);
            if (StringUtils.isNotEmpty(paramValue)) {
               //CMSC-1116 advanced search for dynamic form save answer gives 500 error
               //The following if to deal with INTEGER field
               if(field.getType() == Field.TYPE_INTEGER){
                  FieldValueConstraint fvc = SearchUtil.createEqualConstraint(query, nodeManager, field.getName(), Integer.parseInt(paramValue));
                  SearchUtil.addConstraint(query, fvc);
               }
               else{
                  SearchUtil.addLikeConstraint(query, field, paramValue.trim());
                  queryStringComposer.addParameter(paramName, paramValue);
               }
            }
         }
      }

      // Add the title constraint:
      if (StringUtils.isNotEmpty(searchForm.getTitle())) {

         queryStringComposer.addParameter(ContentElementUtil.TITLE_FIELD, searchForm.getTitle().trim());
         Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
         Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle().trim());
         SearchUtil.addConstraint(query, titleConstraint);
      }

      searchKey(request, searchForm, nodeManager, queryStringComposer, query);
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
         SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.NUMBER_FIELD, objectId);
         queryStringComposer.addParameter(OBJECTID, stringObjectId);
      }

      // Add the user personal:
      if (StringUtils.isNotEmpty(searchForm.getPersonal())) {

         String useraccount = cloud.getUser().getIdentifier();
         if (ContentElementUtil.LASTMODIFIER_FIELD.equals(searchForm.getPersonal())) {
            SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, useraccount);
         }
         if (AUTHOR.equals(searchForm.getPersonal())) {
            SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.CREATOR_FIELD, useraccount);
         }
         queryStringComposer.addParameter(PERSONAL, searchForm.getPersonal());
      }

      // Add the user
      if (StringUtils.isNotEmpty(searchForm.getUseraccount())) {
         String useraccount = searchForm.getUseraccount();
         SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, useraccount);
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
      request.setAttribute(GETURL, queryStringComposer.getQueryString());
      return super.execute(mapping, form, request, response, cloud);
   }

   private void searchKey(HttpServletRequest request, SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query) {
      List<String> keywords = null;
      String mode = request.getParameter(MODE);
      if (StringUtils.isNotEmpty(mode) && ("basic").equals(mode) && StringUtils.isNotEmpty(searchForm.getTitle())) {
         keywords = KeywordUtil.getKeywords(searchForm.getTitle());
      }
      // And some keyword searching
      if (StringUtils.isNotEmpty(searchForm.getKeywords())) {
         keywords = KeywordUtil.getKeywords(searchForm.getKeywords());
      }
      if (null != keywords) {
         addKeyConstraint(searchForm, nodeManager, queryStringComposer, query, keywords);
      }
   }

   private void addKeyConstraint(SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query, List<String> keywords) {
      queryStringComposer.addParameter(ContentElementUtil.KEYWORD_FIELD, searchForm.getKeywords());
      Field keywordField = nodeManager.getField(ContentElementUtil.KEYWORD_FIELD);
      for (String keyword : keywords) {
         Constraint keywordConstraint = SearchUtil.createLikeConstraint(query, keywordField, keyword);
         SearchUtil.addORConstraint(query, keywordConstraint);
      }
   }

   private void massDeleteContent(String deleteContent) {
      if (StringUtils.isNotBlank(deleteContent)) {
         String[] deleteContents = deleteContent.split(",");
         for (String content : deleteContents) {
            deleteContent(content);
         }
      }
   }

   private void deleteContent(String deleteContentRequest) {
      StringTokenizer commandAndNumber = new StringTokenizer(deleteContentRequest, ":");
      String command = commandAndNumber.nextToken();
      String nunmber = commandAndNumber.nextToken();

      if ("moveToRecyclebin".equals(command)) {
         moveContentToRecyclebin(nunmber);
      }

      if ("permanentDelete".equals(command)) {
         deleteContentPermanent(nunmber);
      }

   }

   private void deleteContentPermanent(String objectnumber) {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = provider.getCloud();

      Node objectNode = cloud.getNode(objectnumber);
      if (Workflow.hasWorkflow(objectNode)) {
         // at this time complete is the same as remove
         Workflow.complete(objectNode);
      }
      objectNode.delete(true);

   }

   private void moveContentToRecyclebin(String nunmber) {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = provider.getCloud();

      Node objectNode = cloud.getNode(nunmber);
      RepositoryUtil.removeCreationRelForContent(objectNode);
      RepositoryUtil.removeContentFromAllChannels(objectNode);
      RepositoryUtil.addContentToChannel(objectNode, RepositoryUtil.getTrash(cloud));

      // unpublish and remove from workflow
      Publish.remove(objectNode);
      Workflow.remove(objectNode);
      Publish.unpublish(objectNode);
   }

}
