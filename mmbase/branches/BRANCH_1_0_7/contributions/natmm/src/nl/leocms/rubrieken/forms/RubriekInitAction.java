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
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.6 $, $Date: 2006-09-13 13:18:24 $
 *
 * @struts:action name="RubriekForm"
 *                path="/editors/paginamangement/RubriekInitAction"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="success" path="/editors/paginamanagement/rubriek.jsp"
 */
public class RubriekInitAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(RubriekInitAction.class);

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
      String number = request.getParameter("number");
      RubriekForm rubriekForm = (RubriekForm) form;
      Cloud cloud = CloudFactory.getCloud();

      if (number!=null) { // wijzig modus
         Node node = cloud.getNode(number);
         rubriekForm.setNode(number);
         rubriekForm.setNaam(node.getStringValue("naam"));
         rubriekForm.setNaam_eng(node.getStringValue("naam_eng"));
         rubriekForm.setNaam_fra(node.getStringValue("naam_fra"));
         rubriekForm.setNaam_de(node.getStringValue("naam_de"));
         rubriekForm.setfra_active(node.getIntValue("fra_active") == 1 || node.getIntValue("fra_active") == 2);
         rubriekForm.setEng_active(node.getIntValue("eng_active") == 1 || node.getIntValue("eng_active") == 2);
         rubriekForm.setDe_active(node.getIntValue("de_active") == 1 || node.getIntValue("de_active") == 2);
         boolean wholeSubsite = node.getIntValue("fra_active") == 2 || node.getIntValue("eng_active") == 2 || node.getIntValue("de_active") == 2;
         wholeSubsite |= node.getIntValue("fra_active") == 4 || node.getIntValue("eng_active") == 4 || node.getIntValue("de_active") == 4;
         rubriekForm.setWholesubsite(wholeSubsite);
         rubriekForm.setUrl(node.getStringValue("url"));
         rubriekForm.setStyle(node.getStringValue("style"));
         rubriekForm.setLevel(node.getIntValue("level"));
         rubriekForm.setUrl_live(node.getStringValue("url_live"));
         rubriekForm.setIs_visible(node.getStringValue("isvisible"));
         rubriekForm.setIssearchable(node.getStringValue("issearchable"));
      } else {
         String parent = request.getParameter("parent");
         rubriekForm.setParent( parent );
      }
      return mapping.findForward("success");
   }
}

