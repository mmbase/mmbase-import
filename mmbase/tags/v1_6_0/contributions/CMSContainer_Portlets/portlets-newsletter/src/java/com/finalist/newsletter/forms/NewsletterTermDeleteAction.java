package com.finalist.newsletter.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * using for deleting relationship between newsletter and term
 *
 * @author Lisa
 */
public class NewsletterTermDeleteAction extends MMBaseFormlessAction {

   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String newsletterId = getParameter(request, "newsletterId");
      String termId = getParameter(request, "termId");

      Node newsletterNode = cloud.getNode(Integer.parseInt(newsletterId));
      RelationList termRelList = newsletterNode.getRelations("posrel", cloud.getNodeManager("term"));
      Iterator relItor = termRelList.iterator();

      while (relItor.hasNext()) {
         Relation termRelation = (Relation) relItor.next();
         int dnumber = termRelation.getIntValue("dnumber");
         if (dnumber == Integer.parseInt(termId)) {
            termRelation.delete();
         }
      }

      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }

}