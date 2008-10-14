package com.finalist.newsletter.forms;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * using for add newsletter term
 *
 * @author Lisa
 */
public class NewsletterTermsAddAction extends MMBaseFormlessAction {

   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward refreshing the newsletter term list
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String newsletterId = request.getParameter("newsletterId");
      if (StringUtils.isNotBlank(newsletterId)) {

         Node newsletterNode = cloud.getNode(Integer.parseInt(newsletterId));
         List<Node> relTerms = newsletterNode.getRelatedNodes(cloud.getNodeManager("term"), "posrel", "destination");
         Enumeration<String> parameterNames = request.getParameterNames();
         while (parameterNames.hasMoreElements()) {

            String parameter = parameterNames.nextElement().trim();

            if (parameter.startsWith("chk_")) {

               int chkNumber = Integer.parseInt(request.getParameter(parameter));
               Node termNode = cloud.getNode(chkNumber);
               boolean hasTerm = false;

               if (relTerms.size() > 0) {
                  for (Node relTerm : relTerms) {
                     if (termNode.getIntValue("number") == relTerm.getIntValue("number")) {
                        hasTerm = true;
                        break;
                     }
                  }
                  if (!hasTerm) {
                     RelationUtil.createRelation(newsletterNode, termNode, "posrel");
                  }
               } else {
                  RelationUtil.createRelation(newsletterNode, termNode, "posrel");
               }
            }
         }
      }
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }
}