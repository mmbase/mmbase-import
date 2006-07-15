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
package nl.leocms.evenementen.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;


/**
 *
 * @author Henk Hangyi
 * @version $Revision: 1.1 $, $Date: 2006-03-05 21:43:58 $
 *
 * @struts:action name="EvenementForm"
 *                path="/editors/evenementen/EvenementForm"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="success" path="/editors/evenementen/evenement.jsp"
 */
public class EvenementInitAction extends Action {
   private static final Logger log = Logging.getLoggerInstance(EvenementInitAction.class);
   
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
      log.info("EvenementInitAction.execute()"); 
       
      EvenementForm evenementForm = (EvenementForm) form;  
      
      evenementForm.setShowPastDates("");
      evenementForm.initDates(request.getParameter("number"),request.getParameter("select"));
      evenementForm.setSelectedEvent(request.getParameter("select"));
      
      TreeMap daysOfWeek = new TreeMap();
      daysOfWeek.put("0","zo");
      daysOfWeek.put("1","ma");
      daysOfWeek.put("2","di");
      daysOfWeek.put("3","wo");
      daysOfWeek.put("4","do");
      daysOfWeek.put("5","vr");
      daysOfWeek.put("6","za");    
      evenementForm.setDaysOfWeek(daysOfWeek);
            
      return mapping.findForward("success");
   }
}
