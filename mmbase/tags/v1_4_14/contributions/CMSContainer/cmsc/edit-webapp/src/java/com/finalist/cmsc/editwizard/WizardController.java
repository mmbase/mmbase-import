/*
 * Created on Sep 5, 2003 by edwin
 *
 */
package com.finalist.cmsc.editwizard;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.applications.editwizard.Config;
import org.mmbase.applications.editwizard.Config.WizardConfig;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author Nico Klasens This class contains code which extends wizard.jsp
 */
public class WizardController {

   /**
    * MMbase logging system
    */
   private static Logger log = Logging.getLoggerInstance(WizardController.class.getName());


   /**
    * Additional actions to open the wizard
    * 
    * @param request -
    *           http request
    * @param ewconfig -
    *           editwizard config
    * @param config -
    *           wizard config
    * @param cloud -
    *           cloud
    * @return Paramters to pass to the wizard transformation
    */
   public Map<String, String> openWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig config,
         Cloud cloud) {

      HttpSession session = request.getSession();
      String objectnr = config.objectNumber;
      String contenttype = null;
      if (objectnr != null && "new".equals(objectnr)) {
         contenttype = (String) session.getAttribute("contenttype");
      }
      else {
         Node node = cloud.getNode(objectnr);
         contenttype = node.getNodeManager().getName();
      }
      log.debug("contenttype " + contenttype);

      String readonly = (String) session.getAttribute("readonly");
      if (StringUtil.isEmptyOrWhitespace(readonly)) {
         readonly = "false";
      }

      Map<String, String> params = new HashMap<String, String>();
      params.put("READONLY", readonly);
      params.put("READONLY-REASON", "NONE");
      log.debug("readonly " + readonly);

      Node creationNode = null;
      if (!StringUtil.isEmpty(contenttype)) {
         String creation = (String) session.getAttribute("creation");
         if (!StringUtil.isEmpty(creation)) {
            creationNode = cloud.getNode(creation);
         }
         if (creationNode == null && objectnr != null && !"new".equals(objectnr)) {
            Node node = cloud.getNode(objectnr);

            if (PagesUtil.isPageType(node)) {
               creationNode = node;
               session.setAttribute("creation", "" + creationNode.getNumber());
            }
            if (ContentElementUtil.isContentType(contenttype)) {
               if (RepositoryUtil.hasCreationChannel(node)) {
                  creationNode = RepositoryUtil.getCreationChannel(node);
                  session.setAttribute("creation", "" + creationNode.getNumber());
               }
            }
         }
      }

      UserRole userrole = null;
      if (creationNode != null) {
         if (RepositoryUtil.isContentChannel(creationNode)) {
            userrole = RepositoryUtil.getRole(creationNode.getCloud(), creationNode, false);
         }
         if (PagesUtil.isPageType(creationNode)) {
            userrole = NavigationUtil.getRole(creationNode.getCloud(), creationNode, false);
         }
      }

      if (userrole != null) {
         log.debug("role = " + userrole.getRole());
         int roleId = userrole.getRole().getId();
         if (roleId >= Role.WEBMASTER.getId()) {
            params.put("WEBMASTER", "true");
         }
         if (roleId >= Role.CHIEFEDITOR.getId()) {
            params.put("CHIEFEDITOR", "true");
         }
         if (roleId >= Role.EDITOR.getId()) {
            params.put("EDITOR", "true");
         }
         if (roleId >= Role.WRITER.getId()) {
            params.put("WRITER", "true");
         }
         else {
            params.put("READONLY", "true");
            params.put("READONLY-REASON", "RIGHTS");
         }
      }
      else {
         if (Rank.ADMIN_INT <= cloud.getUser().getRank().getInt()) {
            params.put("WEBMASTER", "true");
         }
      }

      openWizard(request, ewconfig, config, cloud, params, userrole, contenttype);

      log.debug("params = " + params);
      return params;
   }


   @SuppressWarnings("unused")
   public void openWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig config, Cloud cloud,
         Map<String, String> params, UserRole userrole, String contenttype) {
      // nothing to do
   }


   /**
    * Additional actions to close the wizard
    * 
    * @param request -
    *           http request
    * @param ewconfig -
    *           editwizard config
    * @param wizardConfig -
    *           wizard config
    * @param cloud -
    *           cloud
    */
   public void closeWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig wizardConfig, Cloud cloud) {

      if (ewconfig != null && wizardConfig != null) {
         HttpSession session = request.getSession();

         if (isMainWizard(ewconfig, wizardConfig)) {
            if (wizardConfig.wiz.committed()) {
               session.setAttribute("wizardaction", "save");
            }
            else {
               session.setAttribute("wizardaction", "cancel");
            }
         }

         Node editNode = null;
         String contenttype = null;

         String objectnr = wizardConfig.objectNumber;
         log.debug("objectnr " + objectnr);
         if (!StringUtil.isEmpty(objectnr)) {
            if (!"new".equals(objectnr) || wizardConfig.wiz.committed()) {
               if ("new".equals(objectnr)) {
                  // We are closing a wizard which was called with
                  // objectnumber=new.
                  // let's find out the objectnumber in mmbase
                  log.debug("wiz.objectnr " + wizardConfig.wiz.getObjectNumber());

                  editNode = cloud.getNode(wizardConfig.wiz.getObjectNumber());
               }
               else {
                  editNode = cloud.getNode(objectnr);
               }
               session.setAttribute("ewnode-lastedited", "" + editNode.getNumber());
            }
         }

         if (editNode != null) {
            if (ContentElementUtil.isContentElement(editNode)) {
               if ("new".equals(objectnr)) {
                  String channelnr = (String) session.getAttribute("creation");
                  log.debug("Creation " + channelnr);

                  // this has creation channel check is needed, because with it
                  // will create double creationchannels when first "save" and
                  // then "save and close"
                  if (!RepositoryUtil.hasCreationChannel(editNode)) {
                     if (!StringUtil.isEmpty(channelnr)) {
                        RepositoryUtil.addCreationChannel(editNode, channelnr);
                        ContentElementUtil.addOwner(editNode);
                        if (isMainWizard(ewconfig, wizardConfig)) {
                           RepositoryUtil.addContentToChannel(editNode, channelnr);
                        }
                     }
                     else {
                        log.warn("ContentElement: Creationchannel was not found in session");
                     }
                  }
               }
               else {
                  if (!ContentElementUtil.hasOwner(editNode)) {
                     ContentElementUtil.addOwner(editNode);
                  }

                  if (!RepositoryUtil.hasCreationChannel(editNode)) {
                     String channelnr = (String) session.getAttribute("creation");
                     log.debug("Creation " + channelnr);

                     if (!StringUtil.isEmpty(channelnr)) {
                        RepositoryUtil.addCreationChannel(editNode, channelnr);
                     }
                  }
               }

               try {
                  if (wizardConfig.wiz.committed()) {
                     Versioning.addVersion(editNode);
                  }
               }
               catch (VersioningException e) {
                  log.error("Problem while adding version for node : " + objectnr, e);
               }
            }
            contenttype = editNode.getNodeManager().getName();
         }
         log.debug("contenttype " + contenttype);

         closeWizard(request, ewconfig, wizardConfig, cloud, editNode, contenttype);
      }
   }


   @SuppressWarnings("unused")
   public void closeWizard(HttpServletRequest request, Config ewconfig, WizardConfig wizardConfig, Cloud cloud,
         Node editNode, String contenttype) {
      // nothing to do
   }


   /**
    * Is this wizard the main wizard on the editwizard stack
    * 
    * @param ewconfig -
    *           editwizard config
    * @param wconfig -
    *           current wizard config
    * @return <code>true</code> when main wizard
    */
   public static boolean isMainWizard(Config ewconfig, Config.WizardConfig wconfig) {
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