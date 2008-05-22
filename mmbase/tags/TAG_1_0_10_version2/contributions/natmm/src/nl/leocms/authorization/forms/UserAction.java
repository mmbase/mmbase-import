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

import java.util.Map;

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
 * LoginInitAction
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.8 $, $Date: 2006-05-17 21:18:54 $
 *
 * @struts:action name="UserForm"
 *                path="/editors/usermanagement/UserAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/usermanagement/user.jsp"
 *
 * @struts:action-forward name="success" path="/editors/usermanagement/userlist.jsp"
 */
public class UserAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(UserAction.class);

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
      log.info("UserAction - doPerform()");
      if (!isCancelled(request)) {
         UserForm userForm = (UserForm) form;
         Cloud cloud = CloudFactory.getCloud();
         Node userNode;
         int id = userForm.getNodeNumber();
         String action = userForm.getAction();
         if(action.equals(UserForm.ACTIVATE_ACTION)) {
            if (id != -1) {
               userNode = cloud.getNode(id);
               userNode.setIntValue("gracelogins", 3);
               userNode.setLongValue("expiredate", System.currentTimeMillis() / 1000 + ChangePasswordAction.PASSWORD_LIFETIME );
               userNode.setStringValue("rank",userNode.getStringValue("originalrank"));
               userNode.setStringValue("originalrank","");
               userNode.commit();
            }
         }
         if(action.equals(UserForm.SAVE_ACTION)) {
            if (id == -1) {
               userNode = cloud.getNodeManager("users").createNode();
               userNode.setStringValue("account", userForm.getUsername());
               userNode.setIntValue("gracelogins", 3);
               userNode.setLongValue("expiredate", System.currentTimeMillis() / 1000 + ChangePasswordAction.PASSWORD_LIFETIME );
            } else {
               userNode = cloud.getNode(id);
            }
            userNode.setStringValue("voornaam", userForm.getVoornaam());
            userNode.setStringValue("tussenvoegsel", userForm.getTussen());
            userNode.setStringValue("achternaam", userForm.getAchternaam());
            userNode.setStringValue("afdeling", userForm.getAfdeling());
            userNode.setBooleanValue("emailsignalering", userForm.isEmailSignalering());
            userNode.setStringValue("emailadres", userForm.getEmail() );
            if (!"".equals(userForm.getPassword())) {
               userNode.setStringValue("password", userForm.getPassword());
               userNode.setIntValue("gracelogins", 3);
               userNode.setLongValue("expiredate", System.currentTimeMillis() / 1000 + ChangePasswordAction.PASSWORD_LIFETIME );
               // use admin password for cloud.default and cloud.remote
               if ("admin".equals(userNode.getStringValue("account"))) {
                  Util.updateAdminPassword(userForm.getPassword());
               }
            }
            userNode.setStringValue("rank",userForm.getRank());
            userNode.setStringValue("notitie", userForm.getNotitie());
            userNode.commit();

            Map rollen = Util.buildRolesFromRequest(request);
            new AuthorizationHelper(cloud).setUserRights(userNode, rollen);
            new CheckBoxTree().setRelations(cloud,userNode,request);
         }
      }
      return mapping.findForward("success");
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/04/03 19:23:11  henk
 * New version A6-A9
 *
 * Revision 1.6  2006/03/31 12:04:38  henk
 * New version of A6-A9 site
 *
 * Revision 1.5  2006/03/28 07:55:54  henk
 * Changes for a6-a9 site
 *
 * Revision 1.4  2006/03/21 22:05:30  henk
 * expire date for passwords + several changes and  bugfixes
 *
 * Revision 1.3  2006/03/16 22:17:17  henk
 * Added expiredate on passwords and switched on UrlConversion again
 *
 * Revision 1.2  2006/03/08 22:23:51  henk
 * Changed log4j into MMBase logging
 *
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.4  2003/11/28 10:20:19  edwin
 * wijzigen van admin password nu correct (rank blijft bewaard)
 *
 * Revision 1.3  2003/10/16 11:34:23  edwin
 * *** empty log message ***
 *
 * Revision 1.2  2003/10/15 15:30:24  edwin
 * *** empty log message ***
 *
 * Revision 1.1  2003/10/14 15:45:47  edwin
 * *** empty log message ***
 *
 * Revision 1.1  2003/10/13 15:25:39  edwin
 * *** empty log message ***
 *
 *
 */
