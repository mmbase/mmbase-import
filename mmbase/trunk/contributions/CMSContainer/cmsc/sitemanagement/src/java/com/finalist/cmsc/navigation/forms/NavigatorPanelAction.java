package com.finalist.cmsc.navigation.forms;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NavigatorPanelAction extends MMBaseAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			Cloud cloud) throws Exception {

		String nodeId = request.getParameter("nodeId");

		Node parentNode = cloud.getNode(nodeId);

		String pathofpage = NavigationUtil.getNavigationItemUrl(request,
				response, parentNode);

		String pageCurMode = "edit";

		if (StringUtils.isNotEmpty(request.getParameter("pageMode"))) {
			pageCurMode = request.getParameter("pageMode");
		}
		else if (StringUtils.isNotEmpty((String)request.getSession().getAttribute("pageMode"))) {
			pageCurMode = (String)request.getSession().getAttribute("pageMode");
		}
		request.getSession().removeAttribute("pageMode");
		request.getSession().setAttribute("pageMode", pageCurMode);

		String fresh = request.getParameter("fresh");

		if (StringUtils.isNotEmpty(fresh)) {
			request.setAttribute("fresh", fresh);
		}
		request.setAttribute("nodeId", nodeId);
		request.setAttribute("pathofpage", pathofpage);

		return mapping.findForward("success");
	}

}
