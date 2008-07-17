package com.finalist.cmsc.pagewizard.forms;

import java.util.ArrayList;

import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.*;

@SuppressWarnings("serial")
public class PageWizardForm extends ActionForm {

   private int wizard;
   private int parentPage;
   private String pageName;

   private PageWizardDefinition definition;


   public int getWizard() {
      return wizard;
   }


   public void setWizard(int wizard) {
      this.wizard = wizard;
   }


   public int getParentPage() {
      return parentPage;
   }


   public void setParentPage(int parentPage) {
      this.parentPage = parentPage;
   }


   public String getPageName() {
      return pageName;
   }


   public void setPageName(String pageName) {
      this.pageName = pageName;
   }


   public PageWizardDefinition getDefinition() {
      return definition;
   }


   protected void loadDefinition(Cloud cloud) {
      Node wizardNode = cloud.getNode(wizard);
      definition = new PageWizardDefinition(wizardNode.getNumber(), wizardNode.getStringValue("name"), wizardNode
            .getStringValue("description"));

      NodeList portletList = wizardNode.getRelatedNodes("pagewizardportlet");
      ArrayList<PageWizardPortlet> portlets = new ArrayList<PageWizardPortlet>();
      for (NodeIterator pni = portletList.nodeIterator(); pni.hasNext();) {
         Node portletNode = pni.nextNode();
         PageWizardPortlet portlet = new PageWizardPortlet(portletNode.getStringValue("position"));

         NodeList viewList = portletNode.getRelatedNodes("view");
         if (viewList.size() == 1) {
            NodeList contentList = ((Node) viewList.get(0)).getRelatedNodes("typedef");
            if (contentList.size() == 1) {
               NodeManager nodeManager = cloud.getNodeManager(((Node) contentList.get(0)).getStringValue("name"));
               portlet.setContentTypeName(nodeManager.getGUIName());
               portlet.setContentType(nodeManager.getName());
            }
         }
         if (portlet.getContentType() == null) {
            portlet.setContentTypeName("contentelement");
            portlet.setContentType("contentelement");
         }

         portlets.add(portlet);

         NodeList choiceList = portletNode.getRelatedNodes("pagewizardchoice");
         ArrayList<PageWizardChoice> choices = new ArrayList<PageWizardChoice>();
         for (NodeIterator cni = choiceList.nodeIterator(); cni.hasNext();) {
            Node choiceNode = cni.nextNode();
            PageWizardChoice choice = new PageWizardChoice(choiceNode.getStringValue("type"), choiceNode
                  .getStringValue("parameter"));
            choices.add(choice);
         }
         portlet.setChoices(choices);
      }
      definition.setPortlets(portlets);
   }
}
