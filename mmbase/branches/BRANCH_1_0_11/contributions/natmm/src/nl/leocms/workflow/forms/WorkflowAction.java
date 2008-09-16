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
package nl.leocms.workflow.forms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import nl.leocms.workflow.WorkflowController;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Edwin van der Elst
 * @version $Revision: 1.3 $, $Date: 2006-05-18 13:03:35 $
 * 
 * @struts:action path="/workflow/WorkflowAction" scope="request"
 * validate="false"
 * 
 * @struts:action-forward name="success" path="/workflow/workflow.jsp"
 * redirect="true"
 */
public class WorkflowAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(WorkflowAction.class);

   /**
	 * The actual perform function: MUST be implemented by subclasses.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return @throws
	 *         Exception
	 */
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      Cloud c = CloudFactory2.getCloud(request);
      
      List nodes = new ArrayList();
      Enumeration pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
         String name = (String) pNames.nextElement();
         if (name.startsWith("check_")) {
            int lastUScore = name.lastIndexOf("_");
            Node n = c.getNode(Integer.parseInt(name.substring(lastUScore + 1)));
            nodes.add(n);
         }
      }
      int actionvalue = Integer.parseInt(request.getParameter("actionvalue"));
      performWorkflowAction(actionvalue, nodes, request, c);
      ActionForward forward = mapping.findForward("success");
      ActionForward ret = new ActionForward(forward.getPath()+"?status="+request.getParameter("status"));
      ret.setRedirect(true);
      return ret; 
   }

   private void performWorkflowAction(int action, List nodes, HttpServletRequest request, Cloud c) {
      WorkflowController controller = new WorkflowController(c);
      switch (action) {
         case 1 : // Goedkeuren
            for (Iterator i = nodes.iterator(); i.hasNext();) {
               controller.acceptContent((Node) i.next());
            }
            break;

         case 2 : // Afkeuren
            String opmerkingen = request.getParameter("opmerkingen");
            for (Iterator i = nodes.iterator(); i.hasNext();) {
               controller.rejectContent((Node) i.next(), opmerkingen);
            }
            break;

         case 3 : // Publiceren
            for (Iterator i = nodes.iterator(); i.hasNext();) {
               controller.publishContent((Node) i.next());
            }
            break;

         case 4 : // Voltooi
            for (Iterator i = nodes.iterator(); i.hasNext();) {
               controller.finishWriting((Node) i.next());
            }
            break;
      }
   }

}


/**
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/08 22:23:51  henk
 * Changed log4j into MMBase logging
 *
 * Revision 1.1  2006/03/05 21:43:59  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.3  2003/11/24 13:34:56  nico
 * The WorkflowController requires a user cloud, not an admin cloud.

 * Cloud is used to check rights
 *
 * Revision 1.2  2003/11/05 17:27:58  nico
 * Publishing pagina done
 *
 * Revision 1.1  2003/10/21 12:49:33  edwin
 * *** empty log message ***
 *
 */