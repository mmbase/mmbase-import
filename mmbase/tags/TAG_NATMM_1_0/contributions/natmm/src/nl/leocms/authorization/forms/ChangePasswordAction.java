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
 * ChangePasswordAction
 *
 * @author Ronald Kramp
 * @version $Revision: 1.4 $, $Date: 2006-05-17 21:18:54 $
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

   private static final Logger log = Logging.getLoggerInstance(ChangePasswordAction.class);

   public static int PASSWORD_LIFETIME = 180*24*60*60; // half year
   public static int WARNING_INTERVAL = 10*24*60*60; // 10 days

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
         userNode.setIntValue("gracelogins", 3);
         userNode.setLongValue("expiredate", System.currentTimeMillis() / 1000 + PASSWORD_LIFETIME );
         userNode.commit();
         // use admin password for cloud.default and cloud.remote
         if (userNode.getStringValue("account").equals("admin")) {
            Util.updateAdminPassword(changePasswordForm.getNewpassword());
         }
      }
      ActionForward af = mapping.findForward("success");
      af = new ActionForward(af.getPath() + "?succeeded=true");
      return af;
   }
}