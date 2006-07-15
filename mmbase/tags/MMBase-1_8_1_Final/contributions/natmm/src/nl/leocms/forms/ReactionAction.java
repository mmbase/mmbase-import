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
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.mmbase.util.CloudFactory;

/**
 * ReactionAction
 *
 * @author Jeoffrey Bakker
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action name="ReactionForm"
 *                path="/ReactionAction"
 *                scope="request"
 *                validate="true"
 *                input="/templates/content/artikel/artikel.jsp"
 */
public class ReactionAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(ReactionAction.class);


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
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
      log.debug("ReactionAction - doPerform()");

      ReactionForm reactionForm = (ReactionForm) form;

      Transaction transaction = CloudFactory.getCloud().createTransaction();

      try {

         Node reactionNode = transaction.getNodeManager("reactie").createNode();
         reactionNode.setIntValue("datum", (int) (System.currentTimeMillis()/1000));
         reactionNode.setStringValue("email", reactionForm.getEmail());
         reactionNode.setStringValue("tekst", reactionForm.getReaction());
         reactionNode.setStringValue("van", reactionForm.getName());

         Node objectNode = transaction.getNode(reactionForm.getObjectnumber());
         transaction.getRelationManager("reactieop").createRelation(objectNode, reactionNode);
         transaction.commit();
      }
      catch (Exception e) {
         log.error("Error while adding reaction to node : " + reactionForm.getObjectnumber() + e);
         transaction.cancel();
      }

      return new ActionForward(reactionForm.getReferer(), true);
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.3  2003/12/12 08:54:47  nico
 * unused imports and other small issues
 *
 * Revision 1.2  2003/10/31 14:19:43  jeoffrey
 * use redirect forward
 * and open reaction form on errors
 *
 * Revision 1.1  2003/10/30 11:14:00  jeoffrey
 * added reaction to an artikel
 *
 */