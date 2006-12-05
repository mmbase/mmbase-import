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
package nl.leocms.workflow;

import java.util.Iterator;
import java.util.List;

import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.Roles;
import nl.leocms.content.ContentUtil;
import nl.leocms.signalering.SignaleringUtil;
import nl.leocms.util.ContentTypeHelper;
import nl.leocms.util.PublishUtil;
import nl.leocms.versioning.PublishManager;
import nl.leocms.versioning.VersioningController;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Edwin van der Elst
 * Date :Oct 7, 2003
 * 
 * TODO: status in Content-node bijwerken, is nodig voor selectie bij toekennen aan pagina
 * Nico: Vraag me af waar dit nu echt nodig is. Content die niet goedgekeurd is wordt
 *       niet gepubliceerd dus kun je content altijd toevoegen aan de pagina. 
 */
public class WorkflowController {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(WorkflowController.class.getName());
   
   private Cloud cloud;

   public WorkflowController(Cloud cloud) {
      this.cloud = cloud;
   }
   
   public static final int STATUS_IN_BEWERKING = 0;
   public static final int STATUS_TE_KEUREN = 1;
   public static final int STATUS_GOEDGEKEURD = 2;

   private NodeManager getManager() {
      return cloud.getNodeManager("workflowitem");
   }

   /**
    * Retrieve the workflowitem related to a contentelement.
    * If there is no workflowitem, this method fails (assertion or index-out-of-bounds).
    * 
    * @param content
    * @return
    */
   public Node getWorkflowNode(Node content) {
      NodeList list = content.getRelatedNodes("workflowitem");
      /* nb: assert list.size() == 1; // er MOET 1 workflowitem node zijn, niet meer, niet minder!!! */
      return list.getNode(0);
   }

   public int getStatus(Node content) {
      NodeList list = content.getRelatedNodes("workflowitem");
      if (!list.isEmpty()) {
         return list.getNode(0).getIntValue("status");
      }
      else {
         return STATUS_IN_BEWERKING;
      }
   }

   /**
    * Check if a contentnode has a workflow
    *  
    * @param content - Content Node 
    * @return true if the node has a related workflowitem
    */
   public boolean hasWorkflow(Node content) {
      NodeList list = content.getRelatedNodes("workflowitem");
      log.debug("Content element " + content.getNumber() + " has workflow " + !list.isEmpty());
      return !list.isEmpty();
   }

   /**
    * Maak de workflow-node, relateer het aan de contentnode
    * Status wordt 'STATUS_IN_BEWERKING'
    * 
    * @param content
    * @param opmerkingen
    * @param status
    * @return Node - de workflowitem node
    */
   public Node createFor(Node content, String opmerkingen) {

      NodeManager workflow = getManager();
      Node node = workflow.createNode();
      node.setIntValue("status", STATUS_IN_BEWERKING);
      node.setStringValue("opmerkingen", opmerkingen);
      node.commit();
         
      Relation relation = node.createRelation(content, cloud.getRelationManager("workflowitem", "contentelement", "betreft"));
      relation.commit();

      NodeList uList = content.getRelatedNodes("users");
      if(uList.size()>0) {
         
         RelationManager manager = cloud.getRelationManager("workflowitem", "users", "aan");
         node.createRelation(uList.getNode(0), manager).commit();
         log.debug("Workflow " + node.getNumber() + " created for content " + content.getNumber() + 
            " and user " + uList.getNode(0).getStringValue("account"));
      } else {
         log.warn("No user related to content " + content.getNumber() 
            + " therefore workflow item  " + node.getNumber() + " could not be assigned.");
      }
      return node;
   }
   
   /**
    * Status gaat naar 'TE_KEUREN'. Komt bij alle redacteuren die dit item mogen keuren op de workflowlijst
    * 
    * @param content
    */
   public void setInitialStatus(Node content, String opmerking) {
      /* Acties:
       * - verzet de status van de bijbehorende workflowitem node naar ststus in bewerking
       * - zoek alle redacteuren met de juiste rechten via de creatie rubriek en parent rubrieken
       * - relateer de workflow node aan deze redacteuren  
       */
      Node wfItem = getWorkflowNode(content);
      wfItem.setIntValue("status", STATUS_IN_BEWERKING);
      wfItem.setStringValue("opmerkingen", opmerking);
      wfItem.commit();
      
      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      ContentUtil cu = new ContentUtil(cloud);
      Node creatieRubriek = cu.getCreatieRubriek(content);
      List schrijvers = auth.getUsersWithRights(creatieRubriek, Roles.SCHRIJVER);
      removeRelationsToUsers(wfItem);
      relateToUsers(wfItem, schrijvers);
   }
   
   /**
    * Status gaat naar 'TE_KEUREN'. Komt bij alle redacteuren die dit item mogen keuren op de workflowlijst
    * 
    * @param content
    */
   public void finishWriting(Node content) {
      finishWriting(content, "wacht op goedkeuring");
   }
   
   /**
    * Status gaat naar 'TE_KEUREN'. Komt bij alle redacteuren die dit item mogen keuren op de workflowlijst
    * 
    * @param content
    */
   public void finishWriting(Node content, String opmerking) {
      /* Acties:
       * - verzet de status van de bijbehorende workflowitem node naar TE_KEUREN
       * - zoek alle redacteuren met de juiste rechten via de creatie rubriek en parent rubrieken
       * - relateer de workflow node aan deze redacteuren  
       */
      Node wfItem = getWorkflowNode(content);
      wfItem.setIntValue("status", STATUS_TE_KEUREN);
      wfItem.setStringValue("opmerkingen", opmerking);
      wfItem.commit();

      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      ContentUtil cu = new ContentUtil(cloud);
      Node creatieRubriek = cu.getCreatieRubriek(content);
      List redacteuren = auth.getUsersWithRights(creatieRubriek, Roles.REDACTEUR);
      removeRelationsToUsers(wfItem);
      relateToUsers(wfItem, redacteuren);
   }

   public void acceptContent(Node content) {
      /* Acties:
       *  - verzet de workflowstatus naar 'goedgekeurd'
       *  - relateer de workflowitemnode aan alle users die mogen publiceren => rights >= EINDREDACTEUR
       */

      Node wf = getWorkflowNode(content);
      wf.setIntValue("status", STATUS_GOEDGEKEURD);
      wf.setStringValue("opmerkingen", "goedgekeurd");
      wf.commit();

      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      ContentUtil cu = new ContentUtil(cloud);
      Node creatieRubriek = cu.getCreatieRubriek(content);

      removeRelationsToUsers(wf);
      relateToUsers(wf, auth.getUsersWithRights(creatieRubriek, Roles.EINDREDACTEUR));
      
      if (ContentTypeHelper.isPagina(content)) {
         acceptPagina(content);
      }
   }

   private void acceptPagina(Node pagina) {
      NodeList contentelements = pagina.getRelatedNodes("contentelement", "contentrel", "DESTINATION");
      NodeIterator contentelementsiter = contentelements.nodeIterator();
      while (contentelementsiter.hasNext()) {
         Node element = contentelementsiter.nextNode();
         if (hasWorkflow(element) 
            && isAllowedToAccept(element)) {
            
            acceptContent(element);
         }
      }
		/* hh lijstcontentrel is not used in NatMM version of LeoCMS
		
      NodeList linklijst = pagina.getRelatedNodes("linklijst", "posrel", "DESTINATION");
      NodeIterator linklijstiter = linklijst.nodeIterator();
      while (linklijstiter.hasNext()) {
         Node linkl = linklijstiter.nextNode();

         NodeList contentlinklijst = linkl.getRelatedNodes("contentelement", "lijstcontentrel", "DESTINATION");
         NodeIterator contentlinklijstiter = contentlinklijst.nodeIterator();
         while (contentlinklijstiter.hasNext()) {
            Node contentelem = contentlinklijstiter.nextNode();
            if (hasWorkflow(contentelem) 
               && isAllowedToAccept(contentelem)) {

               acceptContent(contentelem);
            }
         }
      }
		*/
   }
   
   public void rejectContent(Node content, String opmerkingen) {
      /* Pre:
       * status = te_keuren of goedgekeurd (!)
       * 
       * Acties:
       * - verzet de status naar IN_BEWERKING
       * - zet opmerkingen in het workflowitem
       * - relateer de workflow aan de schrijver van het contentelement
       */
      Node wf = getWorkflowNode(content);
      wf.setIntValue("status", STATUS_IN_BEWERKING);
      wf.setStringValue("opmerkingen", opmerkingen);
      wf.commit();

      removeRelationsToUsers(wf);
      NodeList list = content.getRelatedNodes("users", "schrijver", "DESTINATION");
      /* nb: assert list.size() == 1; */
      relateToUsers( wf, list);
   }

   public void publishContent(Node content) {
      /* 
       * Acties:
       * - plaats in publish queue 
       * - relateer workflow aan NIEMAND 
       * - verwijder workflowitem
       */
      Node wf = getWorkflowNode(content);
      removeRelationsToUsers(wf);
      wf.delete(true);
      PublishUtil.PublishOrUpdateNode(content);
      
      if (ContentTypeHelper.isPagina(content)) {
         publishPagina(content);
      }
      else {
         if (PublishManager.isPublished(content)) {
            SignaleringUtil.createLinkWijziging(content, cloud);
         }
         else {
            // if it's an article and it has thema's/aandachtsgebieden.
            // and it has a contentrel with a page, than add to maillijst
            if ((ContentTypeHelper.isArtikel(content)) && (isContentElementOnPage(content))) {
               NodeList themas = cloud.getList(""+content.getNumber(),"artikel,related,thema", "thema.number", "artikel.status <= 0", null, null, "DESTINATION", true);
               if (themas.size() > 0) {
                  RelationManager maillijst = cloud.getRelationManager("artikel", "thema", "maillijst");
                  for (int i = 0; i < themas.size(); i++) {
                     String themaNodeNumber = themas.getNode(i).getStringValue("thema.number");
                     Node themaNode = cloud.getNode(themaNodeNumber);
                     content.createRelation(themaNode, maillijst).commit();
                     
                     content.setIntValue("status", 1);
                     content.commit();
                  }
               }
            }
         }
         RelationList rubrieken = content.getRelations("creatierubriek");
         if (rubrieken.size()>0) { 
            // Publish relation to creatierubriek. Rubriek is already published to live cloud
            PublishUtil.PublishOrUpdateNode(rubrieken.getRelation(0));
         }
         
         publishRelated(content.getRelations("related","attachments"));
         publishRelated(content.getRelations("posrel","images"));
         publishRelated(content.getRelations("posrel","link"));
         publishRelated(content.getRelations("related","persoon"));
         
         if (ContentTypeHelper.isArtikel(content)) {
            // only artikel is related to Flash
            publishRelated(content.getRelations("related", "flash"));
         }
         if (ContentTypeHelper.isVacature(content)) {
            // only vacature is related to organisatie
            publishRelated(content.getRelations("related","organisatie"));
         }
         
         VersioningController versioningController = new VersioningController(this.cloud);
         versioningController.addVersion(content);
      }
   }
  
   private void publishPagina(Node pagina) {
      if (PublishManager.isPublished(pagina)) {
         SignaleringUtil.createWijziging(pagina, cloud);
      }
      
      NodeList contentelements = pagina.getRelatedNodes("contentelement", "contentrel", "DESTINATION");
      NodeIterator contentelementsiter = contentelements.nodeIterator();
      while (contentelementsiter.hasNext()) {
         Node element = contentelementsiter.nextNode();
         if (hasWorkflow(element) 
               && getStatus(element) >= STATUS_GOEDGEKEURD 
               && isAllowedToPublish(element)) {
            publishContent(element);
         }
         else {
            Node contentNode = cloud.getNode(element.getNumber());
            String nodeManagerName = contentNode.getNodeManager().getName();
            log.debug("nodeManagerName = " + nodeManagerName);
            if ((!hasWorkflow(contentNode)) 
                  && (!ContentTypeHelper.isWorkflowType(nodeManagerName))
                  && (isAllowedToPublish(contentNode))) {
               PublishUtil.PublishOrUpdateNode(contentNode);
            }               
         }
      }
      
		/* hh lijstcontentrel is not used in NatMM version of LeoCMS
		
      NodeList linklijst = pagina.getRelatedNodes("linklijst", "posrel", "DESTINATION");
      NodeIterator linklijstiter = linklijst.nodeIterator();
      while (linklijstiter.hasNext()) {
         Node linkl = linklijstiter.nextNode();
         PublishUtil.PublishOrUpdateNode(linkl);

         NodeList contentlinklijst = linkl.getRelatedNodes("contentelement", "lijstcontentrel", "DESTINATION");
         NodeIterator contentlinklijstiter = contentlinklijst.nodeIterator();
         while (contentlinklijstiter.hasNext()) {
            Node contentelem = contentlinklijstiter.nextNode();
            if (hasWorkflow(contentelem) 
            && getStatus(contentelem) >= STATUS_GOEDGEKEURD 
            && isAllowedToPublish(contentelem)) {
               publishContent(contentelem);
            }
         }
         contentlinklijst = linkl.getRelatedNodes("link", "lijstcontentrel", "DESTINATION");
         contentlinklijstiter = contentlinklijst.nodeIterator();
         while (contentlinklijstiter.hasNext()) {
            Node contentelem = contentlinklijstiter.nextNode();
            if (isAllowedToPublish(contentelem)) {
               PublishUtil.PublishOrUpdateNode(contentelem);
            }   
         }
      }
		*/
   }

   public boolean isAllowedToPublish(Node content) {
      ContentUtil cu = new ContentUtil(cloud);
      Node creatieRubriek = cu.getCreatieRubriek(content);
      
      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      Node user = auth.getUserNode(cloud.getUser().getIdentifier());

      return auth.getRoleForUser(user, creatieRubriek).getRol() >= Roles.EINDREDACTEUR;
   }

   public boolean isAllowedToAccept(Node content) {
      ContentUtil cu = new ContentUtil(cloud);
      Node creatieRubriek = cu.getCreatieRubriek(content);
      
      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      Node user = auth.getUserNode(cloud.getUser().getIdentifier());

      return auth.getRoleForUser(user, creatieRubriek).getRol() >= Roles.REDACTEUR;
   }
   
   /**
    * Publish related nodes and the relations to that nodes
    * 
    * @param list
    */
   private void publishRelated(RelationList list) {
      RelationIterator i = list.relationIterator();
      while (i.hasNext()) {
         Relation rel = i.nextRelation();
         PublishUtil.PublishOrUpdateNode(rel.getDestination());
      }
   }

   private void removeRelationsToUsers(Node workflowItem) {
      workflowItem.deleteRelations("aan");
   }

   private void relateToUsers(Node workflowItem, List users) {
      RelationManager manager = cloud.getRelationManager("workflowitem", "users", "aan");

      for (Iterator iter = users.iterator(); iter.hasNext();) {
         workflowItem.createRelation((Node) iter.next(), manager).commit();
      }
   }
   
   /**
    * Checks if the given object contains on a page
    *
    * @param contentElementNode
    * @return
    */
   public boolean isContentElementOnPage(Node contentElementNode) {
		// Note: this function should be implemented by PaginaHelper.pathsFromPageToElements
      if (contentElementNode != null) {
         NodeList paginas = contentElementNode.getRelatedNodes("pagina", "contentrel", "SOURCE");
         if (paginas.size() > 0) {
            return true;
         }
			/* hh lijstcontentrel is not used in NatMM version of LeoCMS
			
         NodeList linkLijsten = cloud.getList(""+contentElementNode.getNumber(),"contentelement,lijstcontentrel,linklijst,posrel,pagina", "pagina.number", null, null, null, "SOURCE", true);
         if (linkLijsten.size() > 0) {
            return true;
         }
			*/ 
         NodeList dossiers = cloud.getList(""+contentElementNode.getNumber(),"artikel,posrel,dossier,related,pagina", "pagina.number", null, null, null, "SOURCE", true);
         if (dossiers.size() > 0) {
            return true;
         }
      }
      return false;
   }
}
