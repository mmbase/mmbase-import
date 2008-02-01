/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class SubSiteAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {

      /*System.out.println(request.getParameterNames().toString());
      System.out.println(request.getParameterMap().toString());*/
      
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

      //Retrieve list of pages of subsite
      if (! subsite.equalsIgnoreCase("")) {  
         Node subsiteNode = cloud.getNode(subsite);
         if (subsiteNode != null) {
            NodeList pageNodes = NavigationUtil.getChildren(subsiteNode);
            addToRequest(request, "pageNodes", pageNodes);
//            request.setAttribute("pageNodes", pageNodes);
         }
      }
      
      
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

      return mapping.findForward(SUCCESS);
//      return super.execute(mapping, form, request, response, cloud);
   }

}
