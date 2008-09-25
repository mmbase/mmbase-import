/*
 * Created on Sep 5, 2003 by edwin
 *
 */
package com.finalist.cmsc.editwizard;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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

/**
 * @author Nico Klasens This class contains code which extends wizard.jsp
 */
public class WizardController {

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(WizardController.class.getName());

   protected static final String TRUE = "true";
   protected static final String FALSE = "false";

   protected static final String NEW_OBJECT = "new";

   protected static final String SESSION_CONTENTTYPE = "contenttype";
   protected static final String SESSION_CREATION = "creation";
   protected static final String SESSION_READONLY = "readonly";

   protected static final String ACTION_SAVE = "save";
   protected static final String ACTION_CANCEL = "cancel";


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
      if (objectnr != null && NEW_OBJECT.equals(objectnr)) {
         contenttype = (String) session.getAttribute(SESSION_CONTENTTYPE);
      }
      else {
         Node node = cloud.getNode(objectnr);
         contenttype = node.getNodeManager().getName();
      }
      log.debug("contenttype " + contenttype);

      String readonly = (String) session.getAttribute(SESSION_READONLY);
      if (StringUtils.isBlank(readonly)) {
         readonly = FALSE;
      }

      Map<String, String> params = new HashMap<String, String>();
      params.put("READONLY", readonly);
      params.put("READONLY-REASON", "NONE");
      log.debug("readonly " + readonly);

      Node creationNode = null;
      if (StringUtils.isNotEmpty(contenttype)) {
         String creation = (String) session.getAttribute(SESSION_CREATION);
         if (StringUtils.isNotEmpty(creation)) {
            creationNode = cloud.getNode(creation);
         }
         if (creationNode == null && objectnr != null && !NEW_OBJECT.equals(objectnr)) {
            Node node = cloud.getNode(objectnr);

            if (PagesUtil.isPageType(node)) {
               creationNode = node;
               session.setAttribute(SESSION_CREATION, "" + creationNode.getNumber());
            }
            if (ContentElementUtil.isContentType(contenttype)) {
               if (RepositoryUtil.hasCreationChannel(node)) {
                  creationNode = RepositoryUtil.getCreationChannel(node);
                  session.setAttribute(SESSION_CREATION, "" + creationNode.getNumber());
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
            params.put("WEBMASTER", TRUE);
         }
         if (roleId >= Role.CHIEFEDITOR.getId()) {
            params.put("CHIEFEDITOR", TRUE);
         }
         if (roleId >= Role.EDITOR.getId()) {
            params.put("EDITOR", TRUE);
         }
         if (roleId >= Role.WRITER.getId()) {
            params.put("WRITER", TRUE);
         }
         else {
            params.put("READONLY", TRUE);
            params.put("READONLY-REASON", "RIGHTS");
         }
      }
      else {
         if (Rank.ADMIN_INT <= cloud.getUser().getRank().getInt()) {
            params.put("WEBMASTER", TRUE);
         }
      }

      openWizard(request, ewconfig, config, cloud, params, userrole, contenttype);

      log.debug("params = " + params);
      return params;
   }


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
               session.setAttribute("wizardaction", ACTION_SAVE);
            }
            else {
               session.setAttribute("wizardaction", ACTION_CANCEL);
            }
         }

         Node editNode = null;
         String contenttype = null;

         String objectnr = wizardConfig.objectNumber;
         log.debug("objectnr " + objectnr);
         if (StringUtils.isNotEmpty(objectnr)) {
            if (!NEW_OBJECT.equals(objectnr) || wizardConfig.wiz.committed()) {
               if (NEW_OBJECT.equals(objectnr)) {
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
               if (NEW_OBJECT.equals(objectnr)) {
                  String channelnr = (String) session.getAttribute(SESSION_CREATION);
                  log.debug("Creation " + channelnr);

                  // this has creation channel check is needed, because with it
                  // will create double creationchannels when first "save" and
                  // then "save and close"
                  if (!RepositoryUtil.hasCreationChannel(editNode)) {
                     if (StringUtils.isNotEmpty(channelnr)) {
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
                     String channelnr = (String) session.getAttribute(SESSION_CREATION);
                     log.debug("Creation " + channelnr);

                     if (StringUtils.isNotEmpty(channelnr)) {
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
      for(Object element : configs) {
         if (element instanceof Config.WizardConfig) {
            return element == wconfig;
         }
      }
      return false;
   }
}