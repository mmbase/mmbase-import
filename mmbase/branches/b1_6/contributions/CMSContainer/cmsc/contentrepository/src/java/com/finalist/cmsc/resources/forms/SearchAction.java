/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.struts.PagerAction;

public abstract class SearchAction extends PagerAction {

   public static final String NUMBER_FIELD = "number";

   private static final String GETURL = "geturl";
   private static final String STRICT = "strict";

   private static final String OBJECTID = "objectid";
   private static final String CONTENTTYPES = "contenttypes";

   private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(SearchAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      // Initialize
      SearchForm searchForm = (SearchForm) form;
      NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
      QueryStringComposer queryStringComposer = new QueryStringComposer();
      NodeQuery query = cloud.createNodeQuery();

      // First we add the contenttype parameter
      queryStringComposer.addParameter(CONTENTTYPES, searchForm.getContenttypes());

      // First add the proper step to the query.
      Step theStep = query.addStep(nodeManager);
      query.setNodeStep(theStep);

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
         queryStringComposer.addParameter(ORDER, order);
         queryStringComposer.addParameter(DIRECTION, "" + searchForm.getDirection());
         query.addSortOrder(query.getStepField(nodeManager.getField(order)), searchForm.getDirection());
      }

      query.setDistinct(true);

      addConstraints(searchForm, nodeManager, queryStringComposer, query);

      // Set the objectid constraint
      if (StringUtils.isNotEmpty(searchForm.getObjectid())) {
         Integer objectId = null;
         if (searchForm.getObjectid().matches("^\\d+$")) {
            objectId = Integer.valueOf(searchForm.getObjectid());
         }
         else {
            if (cloud.hasNode(searchForm.getObjectid())) {
               objectId = Integer.valueOf(cloud.getNode(searchForm.getObjectid()).getNumber());
            }
            else {
               objectId = Integer.valueOf(-1);
            }
         }
         SearchUtil.addEqualConstraint(query, nodeManager, NUMBER_FIELD, objectId);
         queryStringComposer.addParameter(OBJECTID, searchForm.getObjectid());
      }

      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         query.setMaxNumber(25);
      }
      else {
         query.setMaxNumber(Integer.parseInt(resultsPerPage));
      }

      // Set the offset (used for paging).
      if (searchForm.getOffset() != null && searchForm.getOffset().matches("\\d+")) {
         query.setOffset(query.getMaxNumber() * Integer.parseInt(searchForm.getOffset()));
         queryStringComposer.addParameter(OFFSET, searchForm.getOffset());
      }

      log.debug("QUERY: " + query);

      int resultCount = Queries.count(query);
      NodeList results = nodeManager.getList(query);

      // Set everyting on the request.
      searchForm.setResultCount(resultCount);
      searchForm.setResults(results);
      String show = searchForm.getShow();
      if(StringUtils.isEmpty(show)){
         show="list";
      }
      request.setAttribute(GETURL, queryStringComposer.getQueryString()+"&show="+show);
      request.setAttribute(STRICT, searchForm.getStrict());

      return super.execute(mapping, form, request, response, cloud);
   }


   protected abstract void addConstraints(SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query);

   protected void addField(NodeManager nodeManager, QueryStringComposer queryStringComposer, NodeQuery query,
         String fieldname, String value) {
      if (StringUtils.isNotEmpty(value)) {
         Field field = nodeManager.getField(fieldname);
         if (field.getType() == Field.TYPE_BOOLEAN) {
            Queries.addConstraints(query, fieldname + "=" + value);
         } else {
            SearchUtil.addLikeConstraint(query, field, value);
         }
         queryStringComposer.addParameter(fieldname, value);
      }
   }

}
