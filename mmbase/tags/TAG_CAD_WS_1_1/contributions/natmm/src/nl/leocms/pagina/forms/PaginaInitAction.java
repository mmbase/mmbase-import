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

import java.util.Calendar;

/**
 *
 * @author Gerard van de Weerd
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action name="PaginaForm"
 *                path="/editors/paginamanagement/PaginaInitAction"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="success" path="/editors/paginamanagement/pagina.jsp"
 */
public class PaginaInitAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(PaginaInitAction.class);

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
      PaginaForm paginaForm = (PaginaForm) form;
      Cloud cloud = CloudFactory.getCloud();
      Calendar cal = Calendar.getInstance();
      if (number!=null) { // wijzig modus
         Node node = cloud.getNode(number);
         paginaForm.setNode(number);
         paginaForm.setOmschrijving(node.getStringValue("omschrijving"));
         paginaForm.setOmschrijving_fra(node.getStringValue("omschrijving_fra"));
         paginaForm.setOmschrijving_eng(node.getStringValue("omschrijving_eng"));
         paginaForm.setOmschrijving_de(node.getStringValue("omschrijving_de"));
         paginaForm.setTitel(node.getStringValue("titel"));
         paginaForm.setTitel_fra(node.getStringValue("titel_fra"));
         paginaForm.setTitel_eng(node.getStringValue("titel_eng"));
         paginaForm.setTitel_de(node.getStringValue("titel_de"));
         paginaForm.setKortetitel(node.getStringValue("kortetitel"));
         paginaForm.setKortetitel_fra(node.getStringValue("kortetitel_fra"));
         paginaForm.setKortetitel_eng(node.getStringValue("kortetitel_eng"));
         paginaForm.setKortetitel_de(node.getStringValue("kortetitel_de"));
         paginaForm.setUrlfragment(node.getStringValue("urlfragment"));
         paginaForm.setNotitie(node.getStringValue("notitie"));
         paginaForm.setMetatags(node.getStringValue("metatags"));
         long verloopDatum = node.getLongValue("verloopdatum");
         long embargo = node.getLongValue("embargo");
         
         if (verloopDatum > 0) {
            cal.setTimeInMillis(verloopDatum * 1000);
         }
         paginaForm.setVerloopdatumdag(cal.get(Calendar.DATE));
         paginaForm.setVerloopdatummaand(cal.get(Calendar.MONTH) + 1);
         paginaForm.setVerloopdatumjaar(cal.get(Calendar.YEAR));
         if (embargo > 0) {
            cal.setTimeInMillis(embargo * 1000);
         }
         paginaForm.setEmbargodag(cal.get(Calendar.DATE));
         paginaForm.setEmbargomaand(cal.get(Calendar.MONTH) + 1);
         paginaForm.setEmbargojaar(cal.get(Calendar.YEAR));
      } 
      else {
         String parent = request.getParameter("parent");
         paginaForm.setParent(parent);

         paginaForm.setVerloopdatumdag(1);
         paginaForm.setVerloopdatummaand(1);
         paginaForm.setVerloopdatumjaar(2038); 
         paginaForm.setEmbargodag(cal.get(Calendar.DATE));
         paginaForm.setEmbargomaand(cal.get(Calendar.MONTH) + 1);
         paginaForm.setEmbargojaar(cal.get(Calendar.YEAR));
         
         Node node = cloud.getNode(parent);
         paginaForm.setKortetitel(node.getStringValue("naam"));
         paginaForm.setKortetitel_fra(node.getStringValue("naam_fra"));
         paginaForm.setKortetitel_eng(node.getStringValue("naam_eng"));
         paginaForm.setKortetitel_de(node.getStringValue("naam_de"));
      }
      return mapping.findForward("success");
   }
}

