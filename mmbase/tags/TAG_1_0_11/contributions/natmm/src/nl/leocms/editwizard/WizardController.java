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
package nl.leocms.editwizard;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.Roles;
import nl.leocms.content.ContentUtil;
import nl.leocms.util.ContentTypeHelper;
import nl.leocms.util.PublishUtil;
import nl.leocms.util.ApplicationHelper;
import nl.leocms.workflow.WorkflowController;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.applications.editwizard.*;
// hh import org.mmbase.applications.editwizard.SecurityException;
import org.mmbase.applications.editwizard.Config;

/** 
 * @author Edwin van der Elst
 * Date :Sep 5, 2003
 *
 * This class contains the code that is normally in wizard.jsp
 *  
 */
public class WizardController {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(WizardController.class.getName());

   public void perform(HttpServletResponse response, HttpServletRequest request,
         Config ewconfig, Config.Configurator configurator,
         boolean popup, String popupId, Object closedObject, Cloud cloud)
      throws IOException, SecurityException, WizardException, TransformerException {
      
      PrintWriter out = response.getWriter();
   
      Config.WizardConfig wizardConfig = null;
      Config.SubConfig top = null;
      if (!ewconfig.subObjects.empty()) {
         top = (Config.SubConfig) ewconfig.subObjects.peek();
         if (!popup) {
            log.debug("This is not a popup");
            if (top instanceof Config.WizardConfig) {
               log.debug("checking configuration");
               wizardConfig = (Config.WizardConfig) top;
            } else {
               log.debug("not a wizard on the stack?");
            }

         } else {
            log.debug("this is a popup");
            Stack stack = (Stack) top.popups.get(popupId);
            if (stack == null) {
               log.debug("no configuration found for popup wizard");
               stack = new Stack();
               top.popups.put(popupId, stack);
               wizardConfig = null;
            } else {
               if (stack.empty()) {
                  log.error("Empty stack?");
               } else {
                  wizardConfig = (Config.WizardConfig) stack.peek();
               }
            }
         }
      } else {
         log.debug("nothing found on stack");
         if (popup) {
            throw new WizardException("Popup without parent");
         }
      }

      if (wizardConfig != null) {
         log.debug("checking configuration");
         Config.WizardConfig checkConfig = new Config.WizardConfig();
         if (log.isDebugEnabled())
            log.trace("checkConfig" + configurator);
         configurator.config(checkConfig);
         if (checkConfig.objectNumber != null
            && (checkConfig.objectNumber.equals("new") || !checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
            log.debug("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
            wizardConfig = null;
         } else {
            if ((closedObject instanceof Config.WizardConfig) && ((Config.WizardConfig) closedObject).wiz.committed()) {
               // we move from a inline sub-wizard to a parent wizard...
               Config.WizardConfig inlineWiz = (Config.WizardConfig) closedObject;
               // with an inline popupwizard we should like to pass the newly created or updated
               // item to the 'lower' wizard.
               String objnr = inlineWiz.objectNumber;
               if ("new".equals(objnr)) {
                  // obtain new object number
                  objnr = inlineWiz.wiz.getObjectNumber();
                  String parentFid = inlineWiz.parentFid;
                  if ((parentFid != null) && (!parentFid.equals(""))) {
                     String parentDid = inlineWiz.parentDid;
                     WizardCommand wc = new WizardCommand("cmd/add-item/" + parentFid + "/" + parentDid + "//", objnr);
                     wizardConfig.wiz.processCommand(wc);
                  }
               } else {
                  WizardCommand wc = new WizardCommand("cmd/update-item////", objnr);
                  wizardConfig.wiz.processCommand(wc);
               }
            }
            log.debug("processing request");
            wizardConfig.wiz.processRequest(request);
         }
      }

      if (wizardConfig == null) {
         log.trace("creating new wizard");
         wizardConfig = configurator.createWizard(cloud);
         wizardConfig.parentFid = request.getParameter("fid");
         wizardConfig.parentDid = request.getParameter("did");
         wizardConfig.popupId = popupId;

         
         if (!popup) {
            if (log.isDebugEnabled())
               log.trace("putting new wizard on the stack for object " + wizardConfig.objectNumber);
            ewconfig.subObjects.push(wizardConfig);
         } else {
            if (log.isDebugEnabled())
               log.trace("putting new wizard in popup map  for object " + wizardConfig.objectNumber);
            Stack stack = (Stack) top.popups.get(popupId);
            stack.push(wizardConfig);
         }
      }

      if (wizardConfig.wiz.startWizard()) {
         log.debug("Starting a wizard ");
         WizardCommand cmd = wizardConfig.wiz.getStartWizardCommand();
         String parentFid = cmd.getFid();
         if (parentFid == null)
            parentFid = "";
         String parentDid = cmd.getDid();
         if (parentDid == null)
            parentDid = "";
         String objectnumber = cmd.getParameter(2);
         String origin = cmd.getParameter(3);
         String wizardname = cmd.getValue();
         String redirectTo =
            response.encodeURL(
               "wizard.jsp?fid="
                  + parentFid
                  + "&did="
                  + parentDid
                  + "&proceed=true&wizard="
                  + wizardname
                  + "&sessionkey="
                  + ewconfig.sessionKey
                  + "&objectnumber="
                  + objectnumber
                  + "&origin="
                  + origin
                  + "&popupid="
                  + popupId
                  + "&language="
                  + ewconfig.language);
         log.debug("Redirecting to " + redirectTo);
         response.sendRedirect(redirectTo);
      } else if (wizardConfig.wiz.mayBeClosed()) {
         log.trace("Closing this wizard");
         
         closeWizard(request, response, ewconfig, wizardConfig, cloud);
         
         response.sendRedirect(
            response.encodeURL("wizard.jsp?sessionkey=" + ewconfig.sessionKey + "&proceed=true" + "&remove=true" + "&popupid=" + popupId));
      } else {
         Map stylesheetParams = openWizard(request, response, ewconfig, wizardConfig, cloud);
         
         log.trace("Send html back");
         wizardConfig.wiz.writeHtmlForm(out, wizardConfig.wizard, stylesheetParams);
      }
      
   }
   
   public Map openWizard(HttpServletRequest request, 
                         HttpServletResponse response,
                         Config ewconfig,
                         Config.WizardConfig config,
                         Cloud cloud) {
                            
      HttpSession session = request.getSession();
      String readonly = (String) session.getAttribute("readonly");
      if (readonly == null || "".equals(readonly.trim())) {
         readonly = "false";
      }

      Map params =  new HashMap();
      params.put("READONLY", readonly);
      params.put("READONLY-REASON", "NONE");
      log.debug("readonly " + readonly);
      
      String objectnr = config.objectNumber;

      Node rubriek = null;
      String creatierubriek = (String) session.getAttribute("creatierubriek");
      if (creatierubriek != null && !"".equals(creatierubriek)) {
         rubriek = cloud.getNode(creatierubriek);
      }
      if (rubriek == null && objectnr != null && !"new".equals(objectnr)) {
         Node node = cloud.getNode(objectnr);
         ContentUtil cu = new ContentUtil(cloud);
         if (cu.hasCreatieRubriek(node)) {
            rubriek = cu.getCreatieRubriek(node);
            session.setAttribute("creatierubriek", "" + rubriek.getNumber());
         }
      }

      String contenttype = null;
      if (objectnr != null && "new".equals(objectnr)) {
         contenttype = (String) session.getAttribute("contenttype");
      }
      else {
         Node node = cloud.getNode(objectnr);
         contenttype = node.getNodeManager().getName();
      }
      log.debug("contenttype " + contenttype);

      int role = Roles.NONE;
      if (contenttype != null && !"".equals(contenttype) && ContentTypeHelper.isContentType(contenttype)) {
         String userrole = (String) session.getAttribute("userrole");
         if (userrole != null && !"".equals(userrole.trim())) {
            role = Integer.valueOf(userrole).intValue();
         }
         else {
            if (rubriek != null) {
               AuthorizationHelper helper = new AuthorizationHelper(cloud);
               Node userNode = helper.getUserNode(cloud.getUser().getIdentifier());
               role = helper.getRoleForUser(userNode, rubriek).getRol();
            }
         }
         log.debug("role = " + role);
         switch(role) {
            case Roles.WEBMASTER:
               params.put("WEBMASTER", "true");
            case Roles.EINDREDACTEUR:
               params.put("EINDREDACTEUR", "true");
            case Roles.REDACTEUR:
               params.put("REDACTEUR", "true");
            case Roles.SCHRIJVER:
               params.put("SCHRIJVER", "true");
               break;
            default:
               params.put("READONLY", "true");
               params.put("READONLY-REASON", "RIGHTS");
         }
      }
      else {
         if (Rank.ADMIN.toString().equals(cloud.getUser().getRank())) {
            params.put("WEBMASTER", "true");
         }
      }
      
      if (isMainWizard(ewconfig, config) &&
          ContentTypeHelper.isWorkflowType(contenttype)) {

         params.put("WORKFLOW", "true");
      
         String activity = "IN_BEWERKING";
         
         if (!"new".equals(objectnr)) {
            Node node = cloud.getNode(objectnr);
            WorkflowController wc = new WorkflowController(cloud);
            // The closeWizard() will create one if it is not present
            if (wc.hasWorkflow(node)) {
               int status = wc.getStatus(node);
               switch(status) {
                  case WorkflowController.STATUS_IN_BEWERKING :
                     activity = "IN_BEWERKING";
                     break;
                  case WorkflowController.STATUS_TE_KEUREN :
                     activity = "TE_KEUREN";
                     break;
                  case WorkflowController.STATUS_GOEDGEKEURD :
                     activity = "GOEDGEKEURD";
                     break;
               }
            }
         }
         log.debug("activity " + activity);
         params.put("ACTIVITY", activity);
      
         if ("GOEDGEKEURD".equals(activity)
            && (role == Roles.REDACTEUR || role == Roles.SCHRIJVER)) {
            params.put("READONLY", "true");
            params.put("READONLY-REASON", "WORKFLOW");
         }
      }
      else {
         if (contenttype != null && !"".equals(contenttype) && ContentTypeHelper.isContentType(contenttype)) {
            params.put("WORKFLOW", "false");
         }
         else {
            params.put("WORKFLOW", "off");
         }
      }

      log.debug("params = " + params);
      return params;
   }
   
   public void closeWizard(HttpServletRequest request, 
                           HttpServletResponse response,
                           Config ewconfig,
                           Config.WizardConfig wizardConfig,
                           Cloud cloud) {
      /* finish
       * accept
       * reject
       * publish
       */

       if (ewconfig != null && wizardConfig != null) {
          HttpSession session = request.getSession();

          Node editNode = null;
          String contenttype = null;

          String objectnr = wizardConfig.objectNumber;
          log.debug("objectnr " + objectnr);
          if (objectnr != null && !"".equals(objectnr)) {
             if (!"new".equals(objectnr) || wizardConfig.wiz.committed()) {
                if ("new".equals(objectnr)) {
                   // We are closing a wizard which was called with objectnumber=new.
                   // let's find out the objectnumber in mmbase
                   log.debug("wiz.objectnr " + wizardConfig.wiz.getObjectNumber());
                     
                   editNode = cloud.getNode(wizardConfig.wiz.getObjectNumber());
                }
                else {
                   editNode = cloud.getNode(objectnr);
                }
                session.setAttribute("ewnode-lastedited", ""+editNode.getNumber());
             }
          }
          
          if (editNode != null && ContentTypeHelper.isContentElement(editNode)) {
             ContentUtil cu = new ContentUtil(cloud);
             String rubrieknr = (String) session.getAttribute("creatierubriek");
             log.debug("Creatierubriek " + rubrieknr);
             if ("new".equals(objectnr)) {
                if (rubrieknr != null && !"".equals(rubrieknr)) {
                   cu.addCreatieRubriek(editNode, rubrieknr);
                   cu.addHoofdRubriek(editNode, rubrieknr);
                   cu.addSubsite(editNode, rubrieknr);                     
                   cu.addSchrijver(editNode);
                }
                else {
                   log.warn("ContentElement: Creatierubriek was not found in session");
                }
             }
             else {
                if (!cu.hasSchrijver(editNode)) {
                   cu.addSchrijver(editNode);
                }

                if (!cu.hasCreatieRubriek(editNode)) {
                   if (rubrieknr != null && !"".equals(rubrieknr)) {
                      cu.addCreatieRubriek(editNode, rubrieknr);
                      cu.addHoofdRubriek(editNode, rubrieknr);
                      cu.addSubsite(editNode, rubrieknr);
                   }
                }
             }
				 if (rubrieknr != null && !"".equals(rubrieknr)) {
					 ApplicationHelper ap = new ApplicationHelper(cloud);
					 if(ap.isInstalled("NatMM")||ap.isInstalled("NMIntra")) {
						 cu.updateTopics(editNode, rubrieknr);
					 }
				 }
             
             contenttype = editNode.getNodeManager().getName();
          }
          log.debug("contenttype " + contenttype);


          if (isMainWizard(ewconfig, wizardConfig) &&
              editNode != null &&
              ContentTypeHelper.isWorkflowType(contenttype)) {

            String workflowCommand = request.getParameter("workflowcommand");
            String workflowcomment = request.getParameter("workflowcomment");
         
            WorkflowController wc = new WorkflowController(cloud);

            if ("new".equals(objectnr)) {
               if (wizardConfig.wiz.committed()) { 
                  wc.createFor(editNode, workflowcomment);
               }
            }
            else {
               if (!wc.hasWorkflow(editNode)) {
                  log.debug("object " + objectnr + " missing workflow. creating one. ");
                  wc.createFor(editNode, "");
               }
            }
            
            // wizard command is commit
            if ("finish".equals(workflowCommand)) {
               log.debug("finishing object " + objectnr);
               wc.finishWriting(editNode);
            }
            if ("accept".equals(workflowCommand)) {
               log.debug("accepting object " + objectnr);
               wc.acceptContent(editNode);
            }
            if ("publish".equals(workflowCommand)) {
               log.debug("publishing object " + objectnr);
               wc.publishContent(editNode);
            }

            // wizard command is cancel. This command cannot be called on a new node
            // so there is always a workflow
            if ("reject".equals(workflowCommand)) {
               log.debug("rejecting object " + objectnr);
               wc.rejectContent(editNode, workflowcomment);
            }

         }
         else {
            if (editNode != null && !ContentTypeHelper.isWorkflowType(contenttype)) {
               String workflowCommand = request.getParameter("workflowcommand");
               if ("publish".equals(workflowCommand)) {
                  PublishUtil.PublishOrUpdateNode(editNode);
               }
            }
         }
      }
   }
   
   public boolean isMainWizard(Config ewconfig, Config.WizardConfig wconfig) {
      Stack configs = ewconfig.subObjects;
      Iterator iter = configs.iterator();
      while (iter.hasNext()) {
         Object element = iter.next();
         if (element instanceof Config.WizardConfig) {
            return element == wconfig;
         }
      }
      return false;
   }
}