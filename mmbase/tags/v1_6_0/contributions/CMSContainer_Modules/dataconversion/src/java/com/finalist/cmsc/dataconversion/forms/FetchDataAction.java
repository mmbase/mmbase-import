package com.finalist.cmsc.dataconversion.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.struts.MMBaseAction;

public class FetchDataAction  extends MMBaseAction{

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, Cloud cloud) throws Exception {
      // TODO Auto-generated method stub
      //return mapping.findForward(SUCCESS);
     // request.getresponse
      String uuid = getParameter(request, "id");
      Node node = null;
      String responseString  = "";
      String strFormat = "<signal>%s</signal>";
      NodeManager manager = cloud.getNodeManager("dataconversion");
      NodeList nodes = manager.getList("id='"+uuid+"'",null,null);
      if(nodes != null && nodes.size() >0) {
         node = nodes.getNode(0);
      }
      if(node != null && node.getStringValue("signal") != null) {
         responseString = String.format(strFormat, node.getIntValue("signal"));
      }
      else {
         responseString = String.format(strFormat," ");
      }
      response.setContentType("text/xml");
      response.getWriter().print(responseString);
      return null;
   }

}
