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

public class SubscribeInitAction extends Action {
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
      log.info("SubscribeInitAction.execute(" + request.getParameter("number") + ")");
      // *** special states (not caused by action button):
      // startsubscription: after init
      // promptforconfirmation: after book from website
      // canceled: after cancel action

      SubscribeForm subscribeForm = (SubscribeForm) form;
      subscribeForm.setNode(request.getParameter("number"));
      subscribeForm.setParent(subscribeForm.findParent());
      subscribeForm.setPageNumber(request.getParameter("p"));
      subscribeForm.setPaymentType(subscribeForm.getDefaultPaymentType());
      subscribeForm.resetNumbers();

      return mapping.findForward("success");
   }
}
