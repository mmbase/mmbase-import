/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SiteEdit extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

		String action = getParameter(request, "action");

		if (StringUtils.isBlank(action)) {
			String objectnumber = getParameter(request, "number", true);

			ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber=" + objectnumber + "&returnurl="
					+ mapping.findForward("returnurl").getPath() + URLEncoder.encode("?objectnumber") + "=" + objectnumber);
			ret.setRedirect(true);
			return ret;
		} else {
			int nodeId = Integer.parseInt(request.getParameter("objectnumber"));
			SecurityUtil.clearUserRoles(cloud);
			ActionForward ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?nodeId=" + nodeId);
			return ret;
		}
	}

	@Override
	public String getRequiredRankStr() {
		return ADMINISTRATOR;
	}

}
