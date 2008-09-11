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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Struts action to clean up attributes in the session used for the editwizards
 *
 * @author Nico Klasens (Finalist IT Group)
 * @created 29-okt-2003
 * @version $Revision: 1.1 $
 *
 * @struts:action path="/editors/WizardCloseAction"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="success" path="/editors/"
 */
public class WizardCloseAction extends Action {
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
   public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {

      HttpSession session = request.getSession();

      session.removeAttribute("contenttype");
      session.removeAttribute("creatierubriek");

      String ewnodeLastedited = (String) session.getAttribute("ewnode-lastedited");
      session.removeAttribute("ewnode-lastedited");

      String returnurl = (String) session.getAttribute("returnurl");
      session.removeAttribute("returnurl");
      
      String closewindow = (String) session.getAttribute("closewindow");
      session.removeAttribute("closewindow");
      
      String popup = (String) session.getAttribute("popup");
      session.removeAttribute("popup");
      
      if (returnurl == null || "".equals(returnurl.trim())) {
         returnurl = "/editors/empty.html";
      }
      else {
         if (ewnodeLastedited != null && !"".equals(ewnodeLastedited.trim())) {
            if (returnurl.indexOf("?") > -1) {
               returnurl += "&";
            }
            else {
               returnurl += "?";
            }
            returnurl += "objectnumber=" + ewnodeLastedited;
            returnurl += "&closewindow=" + closewindow;
            returnurl += "&popup=" + popup;
         }
      }

      // Editwizard starten:
      ActionForward ret = new ActionForward(returnurl);
      ret.setRedirect(true);
      return ret;
   }
}
