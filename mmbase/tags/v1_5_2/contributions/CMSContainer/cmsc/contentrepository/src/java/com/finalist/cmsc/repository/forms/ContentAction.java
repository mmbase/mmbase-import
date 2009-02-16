/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class ContentAction extends MMBaseAction {
   
   private final static String MOVECONTENTTOCHANNEL = "moveContentToChannel";
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      if(StringUtils.isNotEmpty(action) && action.equals(MOVECONTENTTOCHANNEL)) {
         return mapping.findForward(MOVECONTENTTOCHANNEL);
      }
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

      String parentchannel = request.getParameter("parentchannel");
      String orderby = request.getParameter("orderby");
      String direction = request.getParameter("direction");
      if (StringUtils.isEmpty(orderby)) {
         orderby = null;
      }
      if (StringUtils.isEmpty(direction)) {
    	 direction = null;
      }

      // Set the offset (used for paging).
      String offsetString = request.getParameter("offset");
      int offset = 0;
      if (offsetString != null && offsetString.matches("\\d+")) {
    	  offset = Integer.parseInt(offsetString);
      }
      
      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(SearchAction.REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      int maxNumber = 25;
      if (resultsPerPage != null && resultsPerPage.matches("\\d+")) {
    	  maxNumber = Integer.parseInt(resultsPerPage);
      }
      addToRequest(request, "resultsPerPage", Integer.toString(maxNumber));
      
      if (StringUtils.isNotEmpty(parentchannel)) {
         Node channel = cloud.getNode(parentchannel);
         NodeList elements = RepositoryUtil.getLinkedElements(channel, null, orderby, direction, false, offset*maxNumber, maxNumber, -1, -1, -1);
         int elementCount = RepositoryUtil.countLinkedContent(channel);
         addToRequest(request, "direction", direction);
         addToRequest(request, "orderby", orderby);
         addToRequest(request, "elements", elements);
         addToRequest(request, "elementCount", Integer.toString(elementCount));

         NodeList created = RepositoryUtil.getCreatedElements(channel);
         Map<String, Node> createdNumbers = new HashMap<String, Node>();
         for (Iterator<Node> iter = created.iterator(); iter.hasNext();) {
            Node createdElement = iter.next();
            createdNumbers.put(String.valueOf(createdElement.getNumber()), createdElement);
         }
         addToRequest(request, "createdNumbers", createdNumbers);
      }
      return mapping.findForward(SUCCESS);
   }

}
