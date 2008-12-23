/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class WorkflowTreeStatusAction extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String treeItem = request.getParameter("treeItem");
      if (treeItem != null) {
         Map<String, Integer> treeStatus = (Map<String, Integer>)request.getSession().getAttribute("workflowTreeStatus");
         Integer value = treeStatus.get(treeItem);
         if(value==1){
            value=0;
         }else if(value==0){
            value=1;
         }
         treeStatus.put(treeItem, value);
      }
      return null;
   }
}
