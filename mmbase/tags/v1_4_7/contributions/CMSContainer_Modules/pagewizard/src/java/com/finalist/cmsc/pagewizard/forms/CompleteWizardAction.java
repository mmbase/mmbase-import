package com.finalist.cmsc.pagewizard.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.services.workflow.Workflow;

public class CompleteWizardAction extends MMBaseAction {

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (isCancelled(request)) {
         return mapping.findForward(CANCEL);
      }
      else {
         PageWizardForm wizardForm = (PageWizardForm) form;
         Node wizardNode = cloud.getNode(wizardForm.getWizard());

         Node newPageNode = createPage(cloud, wizardForm, wizardNode);

         NodeList portletList = wizardNode.getRelatedNodes("pagewizardportlet");
         for (NodeIterator pni = portletList.nodeIterator(); pni.hasNext();) {
            Node wizardPortletNode = pni.nextNode();
            Node newPortletNode = createPortlet(cloud, wizardForm, newPageNode, wizardPortletNode);

            NodeList choiceList = wizardPortletNode.getRelatedNodes("pagewizardchoice");

            for (NodeIterator cni = choiceList.nodeIterator(); cni.hasNext();) {
               Node choiceNode = cni.nextNode();
               String portletPosition = wizardPortletNode.getStringValue("position");
               String parameter = choiceNode.getStringValue("parameter");
               String value = request.getParameter("selected_" + portletPosition + "_" + parameter);

               createChoiceParameter(cloud, newPortletNode, parameter, value);
            }

            NodeList parameterList = wizardPortletNode.getRelatedNodes("pagewizardparameter");
            for (NodeIterator pani = parameterList.nodeIterator(); pani.hasNext();) {
               createPortletParameter(cloud, newPortletNode, pani.nextNode());
            }
         }

         request.setAttribute("showpage", "" + newPageNode.getNumber());
         return mapping.findForward(SUCCESS);
      }
   }


   private void createPortletParameter(Cloud cloud, Node newPortletNode, Node parameterNode) {
      String parameter = parameterNode.getStringValue("parameter");
      String value = parameterNode.getStringValue("value");
      Node newPortletParameter = PortletUtil.createPortletParameter(cloud, parameter, value);
      PortletUtil.addPortletParameter(newPortletNode, newPortletParameter);
   }


   private void createChoiceParameter(Cloud cloud, Node newPortletNode, String parameter, String value) {
      Node targetNode = cloud.getNode(value);
      Node newPortletNodeParameter = PortletUtil.createNodeParameter(cloud, parameter, targetNode);
      PortletUtil.addPortletParameter(newPortletNode, newPortletNodeParameter);
   }


   private Node createPortlet(Cloud cloud, PageWizardForm wizardForm, Node newPageNode, Node wizardPortletNode) {
      String portletPosition = wizardPortletNode.getStringValue("position");
      Node portletViewNode = null;
      try {
         portletViewNode = wizardPortletNode.getRelatedNodes("view").getNode(0);
      }
      catch (Exception e) {
         throw new IllegalArgumentException("No view set for portlet " + portletPosition + " in wizard with number: "
               + wizardForm.getWizard());
      }

      Node portletDefinitionNode = null;
      try {
         portletDefinitionNode = wizardPortletNode.getRelatedNodes("portletdefinition").getNode(0);
      }
      catch (Exception e) {
         throw new IllegalArgumentException("No portletdefinition set for portlet " + portletPosition
               + " in wizard with number: " + wizardForm.getWizard());
      }

      Node newPortletNode = PortletUtil.createPortlet(cloud, portletPosition, portletDefinitionNode, portletViewNode);
      PortletUtil.addPortlet(newPageNode, newPortletNode, portletPosition);
      return newPortletNode;
   }


   private Node createPage(Cloud cloud, PageWizardForm wizardForm, Node wizardNode) {
      Node parentPageNode = cloud.getNode(wizardForm.getParentPage());

      Node layoutNode = null;
      try {
         layoutNode = wizardNode.getRelatedNodes("layout").getNode(0);
      }
      catch (Exception e) {
         throw new IllegalArgumentException("No layout set for wizard with number: " + wizardForm.getWizard());
      }

      String pageName = wizardForm.getPageName();
      Node newPageNode = PagesUtil.createPage(cloud, pageName, layoutNode);
      NavigationUtil.appendChild(parentPageNode, newPageNode);
      if (!Workflow.hasWorkflow(newPageNode)) {
         Workflow.create(newPageNode, "");
      }
      return newPageNode;
   }

}
