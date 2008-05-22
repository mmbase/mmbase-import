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
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action path="/editors/usermanagement/DeleteUserAction"
 *                scope="request"
 *                validate="false"
 *    
 * @struts:action-forward name="success" path="/editors/usermanagement/userlist.jsp"
 */
public class DeleteUserAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(DeleteUserAction.class);

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
      String id = request.getParameter("id");
      if (id!=null) {
         Cloud cloud = CloudFactory.getCloud();
         Node user = cloud.getNode(id);
         user.delete(true);
      }
      return mapping.findForward("success");
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.1  2003/10/16 11:34:23  edwin
 * *** empty log message ***
 *
 *
 */