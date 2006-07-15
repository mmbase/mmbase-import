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
package nl.leocms.oneclickedit.tag;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.Roles;
import nl.leocms.authorization.UserRole;
import nl.leocms.util.PaginaHelper;
import nl.leocms.workflow.WorkflowController;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

/**
 * @author Gerard van de Weerd
 *
 * This class implements the tag support to provide custom behaviour for
 * the leocms website:
 * <ul>
 * <li>check if the edit mode is set
 * <li>
 */
public class MayEditTag extends TagSupport {

   private static final Logger log = Logging.getLoggerInstance(MayEditTag.class);

   private boolean inverse = false;
   private String paginaNumber;
   private String rubriekNumber;


   /** Check the rights of the user account for the given pagina number.
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException {
      int returnValue = SKIP_BODY;

      HttpSession session = pageContext.getSession();
      ServletRequest request = pageContext.getRequest();
      if (request instanceof HttpServletRequest) {
         Cloud cloud = (Cloud) session.getAttribute("cloud_mmbase");
         if (cloud != null) {
            log.debug("Cloud loaded: " + cloud.getName());
         }
         else {
            //throw new JspException("MMBase Cloud is null using cloud_mmbase atttibute in the session.");
            log.warn("MMBase cloud is not found: granting action");
            return EVAL_BODY_INCLUDE;
         }
         AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
         PaginaHelper pHelper = new PaginaHelper(cloud);
         
         Node rubriekNode = pHelper.getRubriek(getPaginaNumber());
         if (rubriekNode == null) {
            rubriekNode = cloud.getNode(getRubriekNumber());
         }

         String userAccount = cloud.getUser().getIdentifier();
         UserRole role = authHelper.getRoleForUser(authHelper.getUserNode(userAccount), rubriekNode);
         if (role.getRol() >= Roles.SCHRIJVER ^ isInverse()) {
            returnValue = EVAL_BODY_INCLUDE;
           
            Node paginaNode = cloud.getNode(getPaginaNumber());
            if (paginaNode != null) {
               WorkflowController workflowController = new WorkflowController(cloud);
               if ((workflowController.getStatus(paginaNode) >= WorkflowController.STATUS_TE_KEUREN) 
                     && (role.getRol() < Roles.REDACTEUR)) {
                  returnValue = SKIP_BODY;
               }
               else if ((workflowController.getStatus(paginaNode) >= WorkflowController.STATUS_GOEDGEKEURD) 
                     && (role.getRol() < Roles.EINDREDACTEUR)) {
                  returnValue = SKIP_BODY;
               }
            }
         }
      }
      return returnValue;
   }


   /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doEndTag()
    */
   public int doEndTag() throws JspException {
      // TODO Auto-generated method stub
      return super.doEndTag();
   }


   /**
    * @return
    */
   public boolean isInverse() {
      return inverse;
   }


   /**
    * @param b
    */
   public void setInverse(boolean b) {
      inverse = b;
   }

   /**
    * @return Returns the paginaNumber.
    */
   public String getPaginaNumber() {
      return paginaNumber;
   }

   /**
    * @param paginaNumber The paginaNumber to set.
    */
   public void setPaginaNumber(String paginaNumber) {
      this.paginaNumber = paginaNumber;
   }

   /**
    * @return Returns the rubriekNumber.
    */
   public String getRubriekNumber() {
      return rubriekNumber;
   }

   /**
    * @param paginaNumber The paginaNumber to set.
    */
   public void setRubriekNumber(String rubriekNumber) {
      this.rubriekNumber = rubriekNumber;
   }
}
