package com.finalist.cmsc.navigation.forms;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import net.sf.mmapps.commons.util.HttpUtil;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import net.sf.mmapps.commons.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NavigatorPanelAction extends MMBaseAction {
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

	    String nodeId = request.getParameter("nodeId");
	    Node parentNode = cloud.getNode(nodeId);

      String pathofpage = NavigationUtil.getNavigationItemUrl(request, response, parentNode);
      
      String fresh = request.getParameter("fresh");
      if (!StringUtil.isEmpty(fresh)) {
         request.setAttribute("fresh", fresh); 
      }
      request.setAttribute("toolbar", "toolbar.jsp");
      request.setAttribute("nodeId", nodeId);
      request.setAttribute("pathofpage", pathofpage);
      return mapping.findForward("success");
   }

}
