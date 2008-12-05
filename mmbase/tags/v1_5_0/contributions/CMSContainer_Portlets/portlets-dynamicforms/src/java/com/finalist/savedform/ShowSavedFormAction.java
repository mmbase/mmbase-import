package com.finalist.savedform;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowSavedFormAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String nodeNumber = request.getParameter("nodenumber");
      if (StringUtils.isNotBlank(nodeNumber) && cloud.hasNode(nodeNumber)) {

         Set<String> headersNumbers = new TreeSet<String>();
         Node responseForm = cloud.getNode(nodeNumber);

         NodeList savedFormNodeList = responseForm.getRelatedNodes("savedform");
         NodeIterator savedFormIterator = savedFormNodeList.nodeIterator();
         while (savedFormIterator.hasNext()) {
            Node savedForm = savedFormIterator.nextNode();
            NodeList savedFieldsList = savedForm.getRelatedNodes("savedfieldvalue");
            NodeIterator savedFieldIterator = savedFieldsList.nodeIterator();
            while (savedFieldIterator.hasNext()) {
               Node savedField = savedFieldIterator.nextNode();
               headersNumbers.add(savedField.getStringValue("field"));
            }
         }
         request.setAttribute("headerNumbers", headersNumbers);
         request.setAttribute("savedFormNodeList", savedFormNodeList);

         UserRole role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(responseForm), false);
         request.setAttribute("isAllowed", SecurityUtil.isWriter(role));
      }
      else {
         String message = getResources(request, "SAVEDFORM").getMessage("incorrect.nodenumber", nodeNumber);
         request.setAttribute("error", message);
      }

      return mapping.findForward("success");
   }
}
