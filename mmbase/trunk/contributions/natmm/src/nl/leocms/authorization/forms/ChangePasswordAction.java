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
package nl.leocms.authorization.forms;

import nl.leocms.authorization.AuthorizationHelper;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ChangePasswordAction
 *
 * @author Ronald Kramp
 * @version $Revision: 1.1 $, $Date: 2006-03-05 21:43:58 $
 *
 * @struts:action name="ChangePasswordForm"
 *                path="/editors/usermanagement/ChangePasswordAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/usermanagement/changepassword.jsp"
 *    
 * @struts:action-forward name="success" path="/editors/usermanagement/changepassword.jsp"
 */
public class ChangePasswordAction extends Action {
   transient Category log = Category.getInstance(this.getClass());

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
      log.debug("ChangePasswordAction - doPerform()");
      if (!isCancelled(request)) {
         ChangePasswordForm changePasswordForm = (ChangePasswordForm) form;
         Cloud cloud = CloudFactory.getCloud();
         Node userNode = cloud.getNode(changePasswordForm.getNodenumber());
         userNode.setStringValue("password", changePasswordForm.getNewpassword());
         userNode.commit();
         /* hh 
         if (userNode.getStringValue("account").equals("admin")) {
            Util.updateAdminPassword(changePasswordForm.getNewpassword());
         }
         */ 
      }
      ActionForward af = mapping.findForward("success");
      af = new ActionForward(af.getPath() + "?succeeded=true");
      return af;
   }
}