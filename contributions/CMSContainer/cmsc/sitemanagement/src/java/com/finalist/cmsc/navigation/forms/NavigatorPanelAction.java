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
import org.mmbase.bridge.NodeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NavigatorPanelAction extends MMBaseAction {
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        String pathofpage;

        String nodeId = request.getParameter("nodeId");
        Node parentNode = cloud.getNode(nodeId);

        boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);

        if (ServerUtil.useServerName()) {
            String[] pathElements = NavigationUtil.getPathElementsToRoot(parentNode, true);

            pathofpage = HttpUtil.getWebappUri(request, pathElements[0], secure);
            for (int i = 1; i < pathElements.length; i++) {
                pathofpage += pathElements[i] + "/";
            }
            if (!request.getServerName().equals(pathElements[0])) {
                pathofpage = HttpUtil.addSessionId(request, pathofpage);
            } else {
                pathofpage = response.encodeURL(pathofpage);
            }
        } else {
            String path = NavigationUtil.getPathToRootString(parentNode, true);
            String webappuri = HttpUtil.getWebappUri(request, secure);
            pathofpage = response.encodeURL(webappuri + path);
        }

        String page = request.getParameter("page");
        request.setAttribute("toolbar", "toolbar.jsp");
        request.setAttribute("nodeId",nodeId);
        request.setAttribute("pathofpage", pathofpage);
        return mapping.findForward("success");
    }
}
