/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldValueDateConstraint;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueDateConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.forms.SearchAction;
import com.finalist.cmsc.repository.forms.SearchForm;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSiteAction extends PagerAction {

    /**
     * MMBase logging system
     */
    private static final Logger log = Logging.getLoggerInstance(SearchAction.class.getName());
	
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {
	   
       // Initialize
	   SearchForm searchForm = (SearchForm) form;
	   
      String subsite = request.getParameter("subsite");
      
      /* Purpose of this file
       * - retrieve list of subsites
       * - retrieve list of pages if 
       *      channel parameter (subsite) is given (or only 1 subsite exists) 
       *      -> and put it in a List 
       */
      
      //Retrieve list of pages of subsite
      Node subsiteNode = null;
      if (StringUtils.isBlank(subsite) || !cloud.hasNode(subsite)) {
          //If the subsiteNode does not exist or is not valid, get the first found subsite
          NodeManager nm = cloud.getNodeManager(SubSiteUtil.SUBSITE);
          NodeQuery subsitesQuery = nm.createQuery();
          SearchUtil.addLimitConstraint(subsitesQuery, 0, 1);
          SearchUtil.addSortOrder(subsitesQuery, nm, PagesUtil.TITLE_FIELD, "DOWN");
          NodeList results = subsitesQuery.getList();
          if (results.size() > 0) {
              subsiteNode = (Node) results.get(0);
          }
      }
      else {
		  subsiteNode = cloud.getNode(subsite);
	  }
      
      if (subsiteNode != null){
    	  request.setAttribute("subsite", String.valueOf(subsiteNode.getNumber()));
      }
	  
	  if (subsiteNode == null){ //If there are no subsites at all, return empty list
		  searchForm.setResultCount(0);
		  NodeList results = cloud.createNodeList();
		  searchForm.setResults(results);
		  return mapping.findForward(SUCCESS);
	  } 

      NodeManager nodeManager = subsiteNode.getNodeManager();
      // Order the result by:
      String order = searchForm.getOrder();

      // set default order field
      if (order != null && StringUtils.isEmpty(order)) {
          if (nodeManager.hasField("title")) {
              order = "title";
          }
          if (nodeManager.hasField("name")) {
              order = "name";
          }
      }
      
   // Set the offset (used for paging).
      int offset = 0;
      if (searchForm.getOffset() != null && searchForm.getOffset().matches("\\d+")) {
    	  offset = Integer.parseInt(searchForm.getOffset());
          searchForm.setOffset(Integer.toString(offset));
      }
      
      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(SearchAction.REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      int maxNumber = 25;
      if (resultsPerPage != null && resultsPerPage.matches("\\d+")) {
    	  maxNumber = Integer.parseInt(resultsPerPage);
      }
      
      searchForm.setKeywords(resultsPerPage);
      
      String direction = null;
      NodeQuery query = createLinkedElementsQuery(subsiteNode, order, direction, offset*maxNumber, maxNumber, -1, -1, -1);

      // Add the title constraint:
      if (StringUtils.isNotEmpty(searchForm.getTitle())) {
          Field field = nodeManager.getField(PagesUtil.TITLE_FIELD);
          Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle());
          SearchUtil.addConstraint(query, titleConstraint);
      }

      log.debug("QUERY: " + query);

      int resultCount = Queries.count(query);
      NodeList results = cloud.getList(query);

      // Set everything on the request.
      searchForm.setResultCount(resultCount);
      searchForm.setResults(results);
      return super.execute(mapping, searchForm, request, response, cloud);
   }

   public static NodeQuery createLinkedElementsQuery(Node channel, String orderby, String direction, int offset, int maxNumber, int year, int month, int day) {
       String destinationManager = SubSiteUtil.PERSONALPAGE;

       NodeQuery query;
       if (orderby == null) {
           orderby = NavigationUtil.NAVREL + ".pos";
        }
       
       query = SearchUtil.createRelatedNodeListQuery(channel, destinationManager, NavigationUtil.NAVREL);
       SearchUtil.addFeatures(query, channel, destinationManager, NavigationUtil.NAVREL, null, null, orderby, direction);

       // Precision of now is based on minutes.
       Calendar cal = Calendar.getInstance();
       cal.set(Calendar.SECOND, 0);
       cal.set(Calendar.MILLISECOND, 0);

       if(year != -1 || month != -1 || day != -1) {
         Field field = query.getCloud().getNodeManager(destinationManager).getField("publishdate"); //Does this work?
         StepField basicStepField = query.getStepField(field);
          if(year != -1) {
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(year), FieldValueDateConstraint.YEAR));
          }
          if(month != -1) {
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(month), FieldValueDateConstraint.MONTH));
          }
          if(day != -1) {
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(day), FieldValueDateConstraint.DAY_OF_MONTH));
          }
       }

       SearchUtil.addLimitConstraint(query, offset, maxNumber);
       return query;
   }
   
}
