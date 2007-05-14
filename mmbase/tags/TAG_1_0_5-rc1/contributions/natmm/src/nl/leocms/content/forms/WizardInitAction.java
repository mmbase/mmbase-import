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
package nl.leocms.content.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action path="/editors/WizardInitAction"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="success" path="/editors/"
 */
public class WizardInitAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(WizardInitAction.class);


   /** Used to find the wizard definitions */
   private static String REFERRER_URL = "/editors/WizardCloseAction.eb";

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
      String objectNumber = null;
      
      
      String action = request.getParameter("action");
      String contenttype = request.getParameter("contenttype");
      if ("create".equals(action)) {
         objectNumber = "new";
      } else {
         objectNumber = request.getParameter("objectnumber");
      }

      HttpSession session = request.getSession();
      Cloud cloud = CloudFactory.getCloud();
      NodeManager manager = cloud.getNodeManager("editwizards");
      if (contenttype == null) {
         if (objectNumber != null) {
            Node node = cloud.getNode(objectNumber);
            contenttype = node.getNodeManager().getName();
         }
         else {
            throw new RuntimeException(
               "No criteria available to find a wizard." +
               " Provide a contenttype or objectnumber");
         }
      }
      log.debug("contenttype found " + contenttype);
      session.setAttribute("contenttype", contenttype);

      NodeList list = null;
      list = manager.getList("nodepath = '" + contenttype + "'", null, null);
      if (list.isEmpty()) {
         throw new RuntimeException(
            "Unable to find a wizard for contenttype" + contenttype +
            " or objectnumber " + objectNumber);
      }

      Node wizard = list.getNode(0);
      String config = wizard.getStringValue("wizard");

      String creatierubriek = request.getParameter("rubriek");
      if (creatierubriek != null && !"".equals(creatierubriek)) {
         Node rubriek = cloud.getNode(creatierubriek);
         session.setAttribute("creatierubriek",creatierubriek);
      }
      
      String returnurl = request.getParameter("returnurl");
      session.setAttribute("returnurl", returnurl);
      
      String closewindow = request.getParameter("closewindow");
      session.setAttribute("closewindow", closewindow);
      
      String popup = request.getParameter("popup");
      session.setAttribute("popup", popup);
      
      // Editwizard starten:
      ActionForward ret = new ActionForward("/mmbase/edit/wizard/jsp/wizard.jsp?language=nl&wizard="+config+"&objectnumber="+objectNumber+"&referrer="+REFERRER_URL);
      ret.setRedirect(true);
      return ret;
   }
}