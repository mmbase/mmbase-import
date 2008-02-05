/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

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
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueDateConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.repository.forms.SearchAction;
import com.finalist.cmsc.repository.forms.SearchForm;
import com.finalist.cmsc.resources.forms.QueryStringComposer;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSiteAction extends PagerAction {

    /**
     * MMbase logging system
     */
    private static Logger log = Logging.getLoggerInstance(SearchAction.class.getName());
	
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {
	   
      /*System.out.println(request.getParameterNames().toString());
      System.out.println(request.getParameterMap().toString());*/
      
	   // Initialize
	   SearchForm searchForm = (SearchForm) form;
	   
      String subsite = request.getParameter("subsite");
      
      /* Purpose of this file
       * - retrieve List of all subsites
       * - retrieve List of pages if 
       *      channel parameter (subsite) is given (or only 1 subsite exists) 
       *      -> and put it in a List 
       */
      
      /*
      String orderby = request.getParameter("orderby");
      String direction = request.getParameter("direction");
      if (StringUtil.isEmpty(orderby)) {
         orderby = null;
      }
      if (StringUtil.isEmpty(direction)) {
         direction = null;
      }
      */ 

/*      //Retrieve list of subsites
      Node subsiteNode = cloud.getNode(subsite);
      if (subsiteNode != null) { 
         Node parentNode = NavigationUtil.getParent(subsiteNode);
         NodeList subsiteElements = NavigationUtil.getChildren(parentNode);
         addToRequest(request, "subsiteElements", subsiteElements);
      }*/
      
      Node subsiteNode = null;
      //Retrieve list of pages of subsite
      if (!StringUtil.isEmpty(subsite)) { 
         subsiteNode = cloud.getNode(subsite);
         /*if (subsiteNode != null) {
            NodeList elements = NavigationUtil.getChildren(subsiteNode);
        	// NodeList elements = RepositoryUtil.getLinkedElements(subsiteNode, null, null, null, false, -1, -1, -1, -1, -1);
            addToRequest(request, "elements", elements);
            //addToRequest(request, "subsite", subsite);
//            request.setAttribute("pageNodes", pageNodes);
         }*/
      } else {
    	  
    	  return mapping.findForward(CANCEL);
      }

      NodeManager nodeManager = subsiteNode.getNodeManager();
      // Order the result by:
      String order = searchForm.getOrder();

      // set default order field
      if (StringUtil.isEmpty(order)) {
          if (nodeManager.hasField("title")) {
              order = "title";
          }
          if (nodeManager.hasField("name")) {
              order = "name";
          }
      }
      
   
      //QueryStringComposer queryStringComposer = new QueryStringComposer();
      //NodeQuery query = cloud.createNodeQuery();
      //createLinkedElementsQuery(channel, orderby, direction, offset, maxNumber, year, month, day)
      NodeQuery query = createLinkedElementsQuery(subsiteNode, order, null, 0, 25, -1, -1, -1);

      
      // Add the title constraint:
      if (!StringUtil.isEmpty(searchForm.getTitle())) {
          Field field = nodeManager.getField(PagesUtil.TITLE_FIELD);
          Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle());
          SearchUtil.addConstraint(query, titleConstraint);
      }
      
      /*
      NodeManager nodeManager = subsiteNode.getNodeManager();
      
   // First add the proper step to the query.
      Step theStep = null;
      theStep = query.addStep(nodeManager);
      query.setNodeStep(theStep);
      
      
            
      query.setDistinct(true);
      
   // Add the title constraint:
      if (!StringUtil.isEmpty(searchForm.getTitle())) {
    	  
          queryStringComposer.addParameter(PagesUtil.TITLE_FIELD, searchForm.getTitle());
          Field field = nodeManager.getField(PagesUtil.TITLE_FIELD);
          Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle());
          SearchUtil.addConstraint(query, titleConstraint);
      }

      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(SearchAction.REPOSITORY_SEARCH_RESULTS_PER_PAGE);
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
      }*/

      log.debug("QUERY: " + query);

      int resultCount = Queries.count(query);
      NodeList results = cloud.getList(query);
      System.out.println("Count = " + resultCount);
      //System.out.println("Query = " + query);
      // Set everything on the request.
      searchForm.setResultCount(resultCount);
      searchForm.setResults(results);
      //request.setAttribute("geturl", queryStringComposer.getQueryString());

      
/*      if (!StringUtil.isEmpty(subsite)) {
         Node channel = cloud.getNode(subsite);
         NodeList elements = RepositoryUtil.getLinkedElements(channel, null,
               null, null, false, -1, -1, -1, -1, -1);
         addToRequest(request, "elements", elements);
       
         
         NodeList created = RepositoryUtil.getCreatedElements(channel);
         Map<String, Node> createdNumbers = new HashMap<String, Node>();
         for (Iterator<Node> iter = created.iterator(); iter.hasNext();) {
            Node createdElement = iter.next();
            createdNumbers.put(String.valueOf(createdElement.getNumber()),
                  createdElement);
         }
         addToRequest(request, "createdNumbers", createdNumbers);
      }*/

//      return mapping.findForward(SUCCESS);
      return super.execute(mapping, form, request, response, cloud);
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
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, new Integer(year), FieldValueDateConstraint.YEAR));
          }
          if(month != -1) {
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, new Integer(month), FieldValueDateConstraint.MONTH));
          }
          if(day != -1) {
             SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, new Integer(day), FieldValueDateConstraint.DAY_OF_MONTH));
          }
       }

       SearchUtil.addLimitConstraint(query, offset, maxNumber);
       return query;
   }
   
}
