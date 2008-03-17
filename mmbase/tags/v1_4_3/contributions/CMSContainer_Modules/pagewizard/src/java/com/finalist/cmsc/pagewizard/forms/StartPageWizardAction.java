package com.finalist.cmsc.pagewizard.forms;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class StartPageWizardAction extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      NodeManager nodeManager = cloud.getNodeManager("pagewizarddefinition");
      NodeList allDefinitions = nodeManager.getList(nodeManager.createQuery());
      ArrayList<PageWizardDefinition> definitionList = new ArrayList<PageWizardDefinition>();
      for (NodeIterator ni = allDefinitions.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         definitionList.add(new PageWizardDefinition(node.getNumber(), node.getStringValue("name"), node
               .getStringValue("description")));
      }
      request.setAttribute("definitions", definitionList);
      request.setAttribute("parentPage", request.getParameter("number"));

      return mapping.findForward(SUCCESS);
   }

}
