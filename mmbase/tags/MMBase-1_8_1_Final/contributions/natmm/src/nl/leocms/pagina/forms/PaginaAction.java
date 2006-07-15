/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.pagina.forms;

import nl.leocms.pagina.PaginaUtil;
import nl.leocms.workflow.WorkflowController;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PaginaAction
 *
 * @author Gerard van de Weerd
 * @version $Revision: 1.4 $, $Date: 2006-07-11 20:41:00 $
 *
 * @struts:action name="PaginaForm"
 *                path="/editors/paginamanagement/PaginaAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/paginamanagement/pagina.jsp"
 *    
 * @struts:action-forward name="success" path="/editors/paginamanagement/frames.jsp"
 */
public class PaginaAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(PaginaAction.class);
   
   private static final int NO_ACTION = 0;
   private static final int CANCEL = 1;
   private static final int SAVE = 2;
   private static final int TO_ACCEPT = 3;
   private static final int ACCEPT = 4;
   private static final int REJECT = 5;
   private static final int PUBLISH = 6;
   
   /**
    * The actual perform function: MUST be implemented by subclasses.
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      log.debug("PaginaAction - doPerform()");
      PaginaForm paginaForm = (PaginaForm) form;
      Cloud cloud = CloudFactory2.getCloud(request);

      WorkflowController workflowController = new WorkflowController(cloud);
      Node paginaNode = null;
      switch (getSubmitAction(request)) {
         case SAVE : 
            paginaNode = savePaginaContent(cloud, paginaForm);
            if (!workflowController.hasWorkflow(paginaNode)) {
               workflowController.createFor(paginaNode, "nieuwe pagina of update");
            }
            break;
         case TO_ACCEPT :
            paginaNode = savePaginaContent(cloud, paginaForm);
            if (!workflowController.hasWorkflow(paginaNode)) {
               workflowController.createFor(paginaNode, "nieuwe pagina");
            }
            workflowController.finishWriting(paginaNode);
            break;
         case ACCEPT :
            paginaNode = savePaginaContent(cloud, paginaForm);
            if (!workflowController.hasWorkflow(paginaNode)) {
               workflowController.createFor(paginaNode, "nieuwe pagina");
            }
            workflowController.acceptContent(paginaNode);
            break;
         case REJECT :
            paginaNode = savePaginaContent(cloud, paginaForm);
            if (!workflowController.hasWorkflow(paginaNode)) {
               workflowController.createFor(paginaNode, "nieuwe pagina");
            }
            workflowController.rejectContent(paginaNode, null);
            break;
         case PUBLISH :
            paginaNode = savePaginaContent(cloud, paginaForm);
            if (!workflowController.hasWorkflow(paginaNode)) {
               workflowController.createFor(paginaNode, "nieuwe pagina");
            }
            workflowController.publishContent(paginaNode);
            break;
         case NO_ACTION : break;
         case CANCEL : break;
         default : break;
      }
      ActionForward af = mapping.findForward("success");
      af = new ActionForward(af.getPath() + "?page=" + paginaNode.getNumber());
      return af;
   }

   /**
    * @param paginaNode
    * @param paginaForm
    */
   private Node savePaginaContent(Cloud cloud, PaginaForm paginaForm) {
     
      String number = paginaForm.getNode();
      Node paginaNode;
      if (!number.equals("")) {
         paginaNode= cloud.getNode(number);
      }
      else {
         paginaNode = new PaginaUtil(cloud).createPagina(cloud.getNode(paginaForm.getParent()), paginaForm.getUsername());
         paginaNode.setBooleanValue("verwijderbaar", true);
      }
      
      paginaNode.setStringValue("titel", paginaForm.getTitel());
      paginaNode.setStringValue("titel_fra", paginaForm.getTitel_fra());
      paginaNode.setStringValue("titel_eng", paginaForm.getTitel_eng());
      paginaNode.setStringValue("titel_de", paginaForm.getTitel_de());
      paginaNode.setStringValue("kortetitel", paginaForm.getKortetitel());
      paginaNode.setStringValue("kortetitel_fra", paginaForm.getKortetitel_fra());
      paginaNode.setStringValue("kortetitel_eng", paginaForm.getKortetitel_eng());
      paginaNode.setStringValue("kortetitel_de", paginaForm.getKortetitel_de());
      // hh will overwrite omschrijving set in ew:  paginaNode.setStringValue("omschrijving", paginaForm.getOmschrijving());
      paginaNode.setStringValue("omschrijving_fra", paginaForm.getOmschrijving_fra());
      paginaNode.setStringValue("omschrijving_eng", paginaForm.getOmschrijving_eng());
      paginaNode.setStringValue("omschrijving_de", paginaForm.getOmschrijving_de());
      paginaNode.setStringValue("urlfragment", paginaForm.getUrlfragment());
      // for now always true
      paginaNode.setBooleanValue("menu_element", true);
      paginaNode.setStringValue("metatags", paginaForm.getMetatags());
      paginaNode.setLongValue("verloopdatum", paginaForm.getVerloopdatum());
      paginaNode.setLongValue("embargo", paginaForm.getEmbargo());      
      
      paginaNode.commit();
      
      String paginaTemplateNodeNumber = paginaForm.getPaginatemplate();
      
      NodeList paginaTemplateList = cloud.getList(""+paginaNode.getNumber(),
            "pagina,gebruikt,paginatemplate",
            "gebruikt.number,paginatemplate.number",
            null, null, null, "destination", true);
      
      if (paginaTemplateList.size() > 0) {
         
         String tempPaginaTemplateNodeNumber = paginaTemplateList.getNode(0).getStringValue("paginatemplate.number");
         if (!paginaTemplateNodeNumber.equals(tempPaginaTemplateNodeNumber)) {
            //remove relation
            Node relationNode = cloud.getNode(paginaTemplateList.getNode(0).getStringValue("gebruikt.number"));
            relationNode.delete(true);
            // create new relation
            RelationManager gebruiktRelMan = cloud.getRelationManager("gebruikt");
            Relation gebruiktRel = gebruiktRelMan.createRelation(paginaNode, cloud.getNode(paginaTemplateNodeNumber));
            gebruiktRel.commit();
         }
         
      } else if(paginaTemplateNodeNumber!=null) {
         // create new relation            
         RelationManager gebruiktRelMan = cloud.getRelationManager("gebruikt");
         Relation gebruiktRel = gebruiktRelMan.createRelation(paginaNode, cloud.getNode(paginaTemplateNodeNumber));
         gebruiktRel.commit();
      }
      return paginaNode;
   }
   
   private int getSubmitAction(HttpServletRequest request) {
      String cancel = request.getParameter("cancel.x");
      String save = request.getParameter("save.x");
      String publish = request.getParameter("publish.x");
      String reject = request.getParameter("reject.x");
      String accept = request.getParameter("accept.x");
      String toAccept = request.getParameter("toaccept.x");
      
      if (cancel != null) {
         return CANCEL;
      }
      if (save != null) {
         return SAVE;
      }
      if (accept != null) {
         return ACCEPT;
      }
      if (toAccept != null) {
         return TO_ACCEPT;
      }
      if (publish != null) {
         return PUBLISH;
      }
      if (reject != null) {
         return REJECT;
      }
      return NO_ACTION;
   }
}

