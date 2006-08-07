/*
 * Created on Sep 5, 2003 by edwin
 *
 */
package com.finalist.cmsc.editwizard;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.applications.editwizard.Config;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author Nico Klasens
 * 
 * This class contains code which extends wizard.jsp
 */
public class WizardController {

   /**
    * MMbase logging system
    */
   private static Logger log = Logging.getLoggerInstance(WizardController.class.getName());

   /**
    * Additional actions to open the wizard
    *
    * @param request  - http request
    * @param ewconfig - editwizard config
    * @param config   - wizard config
    * @param cloud    - cloud
    * @return Paramters to pass to the wizard transformation
    */
   public static Map openWizard(HttpServletRequest request,
                                Config ewconfig,
                                Config.WizardConfig config,
                                Cloud cloud) {

      HttpSession session = request.getSession();

      String readonly = (String) session.getAttribute("readonly");
      if (StringUtil.isEmptyOrWhitespace(readonly)) {
         readonly = "false";
      }

      Map<String,String> params = new HashMap<String,String>();
      params.put("READONLY", readonly);
      params.put("READONLY-REASON", "NONE");
      log.debug("readonly " + readonly);

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

      Node creationChannel = null;
      if (!StringUtil.isEmpty(contenttype)) {
            String creation = (String) session.getAttribute("creation");
            if (!StringUtil.isEmpty(creation)) {
                creationChannel = cloud.getNode(creation);
            }
            if (creationChannel == null && objectnr != null && !"new".equals(objectnr)) {
                Node node = cloud.getNode(objectnr);

                if (ContentElementUtil.isContentType(contenttype)) {
                    if (RepositoryUtil.hasCreationChannel(node)) {
                        creationChannel = RepositoryUtil.getCreationChannel(node);
                        session.setAttribute("creation", "" + creationChannel.getNumber());
                    }
                }
            }
        }

      if (Rank.ADMIN_INT <= cloud.getUser().getRank().getInt()) {
         params.put("WEBMASTER", "true");
      }

      log.debug("params = " + params);
      return params;
   }

   /**
    * Additional actions to close the wizard
    *
    * @param request      - http request
    * @param ewconfig     - editwizard config
    * @param wizardConfig - wizard config
    * @param cloud        - cloud
    */
   public static void closeWizard(HttpServletRequest request,
                                  Config ewconfig,
                                  Config.WizardConfig wizardConfig,
                                  Cloud cloud) {


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
                  // We are closing a wizard which was called with objectnumber=new.
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
            }
            contenttype = editNode.getNodeManager().getName();
            
        }
         log.debug("contenttype " + contenttype);
      }
   }

   /**
    * Is this wizard the main wizard on the editwizard stack
    *
    * @param ewconfig - editwizard config
    * @param wconfig  - current wizard config
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