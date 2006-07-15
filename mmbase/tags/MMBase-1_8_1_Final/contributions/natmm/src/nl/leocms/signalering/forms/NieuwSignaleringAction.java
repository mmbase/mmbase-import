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
package nl.leocms.signalering.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import java.util.Date;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.leocms.signalering.SignaleringUtil;

/**
 * NieuwSignaleringAction
 *
 * @author Ronald Kramp
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action name="NieuwSignaleringForm"
 *                path="/editors/signalering/NieuwSignaleringAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/signalering/nieuw.jsp"
 *    
 * @struts:action-forward name="success" path="/editors/signalering/overzicht.jsp"
 * @struts:action-forward name="failure" path="/editors/signalering/nieuw.jsp"
 */
public class NieuwSignaleringAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(NieuwSignaleringAction.class);

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
      log.debug("NieuwSignaleringAction - doPerform()");
      if (!isCancelled(request)) {
         NieuwSignaleringForm nieuwSignaleringForm = (NieuwSignaleringForm) form;
         
         long verloopDatumInSeconds = 0;
         String date = nieuwSignaleringForm.getDatumVanDag() + "-" + nieuwSignaleringForm.getDatumVanMaand() + "-" + nieuwSignaleringForm.getDatumVanJaar();
         try {
            nieuwSignaleringForm.DATE_FORMAT.setLenient(false);
            Date verloopDatum = nieuwSignaleringForm.DATE_FORMAT.parse(date);
            verloopDatumInSeconds = verloopDatum.getTime() / 1000;
            // if we are here the date is valid
         }
         catch (ParseException e) {
             log.error("could not parse date: " + e);
             throw new Exception(e.getMessage());
         }
         
         if (nieuwSignaleringForm.isEmailnotificatie()) {
            //herhaling in dagen
            int herhaling = 5;
            try {
               herhaling = Integer.parseInt(nieuwSignaleringForm.getHerhaling());
               if (herhaling < 0) {
                  herhaling = 5;
               }
            }
            catch(NumberFormatException nfe) {
               // leave herhaling to initial value
            }
            long herhalingDatumInSeconds = verloopDatumInSeconds - (herhaling * 24 * 60 * 60);
            SignaleringUtil.addSignalering(nieuwSignaleringForm.getPagenumber(),
                  nieuwSignaleringForm.getUsernumber(),
                  nieuwSignaleringForm.getAfzender(),
                  nieuwSignaleringForm.getContentelement(),
                  nieuwSignaleringForm.getNotitie(),
                  verloopDatumInSeconds,
                  herhalingDatumInSeconds,
                  nieuwSignaleringForm.isEmailnotificatie(),
                  SignaleringUtil.TAAK);
         }
         else {
            // zonder verloop datum
            SignaleringUtil.addSignalering(nieuwSignaleringForm.getPagenumber(),
                  nieuwSignaleringForm.getUsernumber(),
                  nieuwSignaleringForm.getAfzender(),
                  nieuwSignaleringForm.getContentelement(),
                  nieuwSignaleringForm.getNotitie(),
                  verloopDatumInSeconds,
                  SignaleringUtil.TAAK);
         }
      }
      return mapping.findForward("success");
   }
}