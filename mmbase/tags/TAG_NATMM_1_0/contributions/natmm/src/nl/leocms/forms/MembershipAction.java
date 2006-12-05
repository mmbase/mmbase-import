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
package nl.leocms.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import javax.servlet.*;
import nl.leocms.forms.MembershipForm;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class MembershipAction extends Action {
   private static final Logger log = Logging.getLoggerInstance(MembershipAction.class);

   private void removeObsoleteFormBean(ActionMapping mapping, HttpServletRequest request) {
      // *** Remove the obsolete form bean ***
      if (mapping.getAttribute() != null) {
         if ("request".equals(mapping.getScope()))
            request.removeAttribute(mapping.getAttribute());
         else {
            HttpSession session = request.getSession();
            session.removeAttribute(mapping.getAttribute());
         }
      }
   }

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
        log.info("MembershipFormAction.execute()");

        Cloud cloud = CloudFactory.getCloud();

        MembershipForm membershipForm = (MembershipForm) form;
        ActionForward forwardAction = null;
        String action = membershipForm.getAction();

        if (action.equals(membershipForm.submitAction) || action.equals(membershipForm.correctAction) || action.equals(membershipForm.skipValidationAction)) {

           forwardAction = mapping.findForward("continue");

        } else if (action.equals(membershipForm.backAction)){

           membershipForm.setAction(membershipForm.initAction); // show form again
           forwardAction = mapping.findForward("continue");

        } else if (action.equals(membershipForm.confirmAction)) {

            Node newMember = membershipForm.createMember(cloud);
            membershipForm.sendConfirmEmail(cloud, newMember);
            forwardAction = mapping.findForward("continue");

        } else if (action.equals(membershipForm.readyAction)) {

           removeObsoleteFormBean(mapping, request);
           forwardAction = mapping.findForward("success");

        }

        return forwardAction;
   }



}
