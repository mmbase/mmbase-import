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
package nl.leocms.versioning.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.leocms.versioning.VersioningController;
import nl.leocms.workflow.WorkflowController;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

/**
 * @author Edwin van der Elst
 * @version $Revision: 1.3 $, $Date: 2006-04-14 16:07:32 $
 * 
 * @struts:action path="/editors/beheerbibliotheek/RestoreAction" scope="request" validate="false"
 * 
 * @struts:action-forward name="success" path="index.jsp"
 * redirect="true"
 */
public class RestoreAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(RestoreAction.class);

   /**
	 * Restore a version of a contentelement from the archive to the actual node.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward - back to the beheerbibliotheek/index.jsp
    * @throws Exception
	 */
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      Cloud c = CloudFactory.getCloud();
      Node archiveNode = c.getNode( Integer.parseInt(request.getParameter("node")));
      VersioningController versioningController = new VersioningController(c);
      versioningController.restoreVersion( archiveNode );
      if(!nl.leocms.builders.ContentElementBuilder.ADDVERSION_ON_COMMIT) {
         WorkflowController workflowController = new WorkflowController(c);
         Node contentNode = c.getNode( archiveNode.getIntValue("original_node"));
         if (workflowController.hasWorkflow(contentNode)) {
            Node workflow = workflowController.getWorkflowNode(contentNode);
            workflow.delete(true);
         }
         Node wf = workflowController.createFor(contentNode,"uit archief terug gezet");
      }
      return mapping.findForward("success"); 
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
 * Revision 1.2  2003/11/10 14:05:58  edwin
 * documentation
 *
 * Revision 1.1  2003/11/07 14:50:15  edwin
 * Restore version logic
 *
 */