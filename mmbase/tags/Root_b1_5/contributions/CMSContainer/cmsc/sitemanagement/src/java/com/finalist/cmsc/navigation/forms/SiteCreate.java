/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SiteCreate extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

		String action = getParameter(request, "action");

		if (StringUtils.isBlank(action)) {
			ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=create" + "&contenttype=" + SiteUtil.SITE
					+ "&returnurl=" + mapping.findForward("returnurl").getPath());
			ret.setRedirect(true);
			return ret;
		} else {
			if ("save".equals(action)) {
				String ewnodelastedited = getParameter(request, "ewnodelastedited");
				Node administrators = SecurityUtil.getAdministratorsGroup(cloud);
				if (administrators != null) {
					NavigationUtil.addRole(cloud, ewnodelastedited, administrators, Role.WEBMASTER);
				}

				Node newSite = cloud.getNode(ewnodelastedited);
				Node layoutNode = PagesUtil.getLayout(newSite);
				PagesUtil.linkPortlets(newSite, layoutNode);

				NavigationUtil.getNavigationInfo(cloud).expand(Integer.valueOf(ewnodelastedited));

				addToRequest(request, "showpage", ewnodelastedited);

				ActionForward ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId=" + ewnodelastedited + "&fresh=fresh");
				return ret;
			}
			ActionForward ret = mapping.findForward(CANCEL);
			return ret;
		}
	}

	@Override
	public String getRequiredRankStr() {
		return ADMINISTRATOR;
	}

}
