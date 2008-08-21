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
package nl.leocms.rubrieken.forms;

import nl.leocms.util.RubriekHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginInitAction
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.6 $, $Date: 2006-09-04 18:51:00 $
 *
 * @struts:action name="RubriekForm"
 *                path="/editors/paginamanagement/RubriekAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/paginamanagement/rubriek.jsp"
 *
 * @struts:action-forward name="success" path="/editors/paginamanagement/rubrieken.jsp"
 */
public class RubriekAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(RubriekAction.class);

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
      log.debug("RubriekAction - doPerform()");
      RubriekForm rubriekForm = (RubriekForm) form;
      if (!isCancelled(request)) {
         Cloud cloud = CloudFactory.getCloud();
         RubriekHelper rubriekHelper = new RubriekHelper(cloud);
         String number = rubriekForm.getNode();
         Node node;
         if (!number.equals("")) {
            node= cloud.getNode(number);
         }
         else {
            node = rubriekHelper.createSubrubriek( cloud.getNode(rubriekForm.getParent()));
         }

         node.setStringValue("naam",rubriekForm.getNaam());
         node.setStringValue("naam_eng",rubriekForm.getNaam_eng());
         node.setStringValue("naam_fra",rubriekForm.getNaam_fra());
         node.setStringValue("naam_de",rubriekForm.getNaam_de());

         int fra_active = !rubriekForm.isfra_active() ? (rubriekForm.isWholesubsite() ? 4 : 0) : (rubriekForm.isWholesubsite() ? 2 : 1);
         int eng_active = !rubriekForm.isEng_active() ? (rubriekForm.isWholesubsite() ? 4 : 0) : (rubriekForm.isWholesubsite() ? 2 : 1);
         int de_active = !rubriekForm.isDe_active() ? (rubriekForm.isWholesubsite() ? 4 : 0) : (rubriekForm.isWholesubsite() ? 2 : 1);
         node.setIntValue("fra_active",fra_active);
         node.setIntValue("eng_active",eng_active);
         node.setIntValue("de_active",de_active);
         node.setStringValue("url",rubriekForm.getUrl());
         node.setStringValue("url_live",rubriekForm.getUrl_live());
         node.setStringValue("isvisible",rubriekForm.getIs_visible());
         node.setStringValue("issearchable",rubriekForm.getIssearchable());

         String style = rubriekForm.getStyle();
         if (style.equals("parentstyle")) {
            style = rubriekHelper.getParentStyle(""+node.getNumber());
         }
         node.setStringValue("style", style);
         node.commit();

      }
      return mapping.findForward("success");
   }
}

