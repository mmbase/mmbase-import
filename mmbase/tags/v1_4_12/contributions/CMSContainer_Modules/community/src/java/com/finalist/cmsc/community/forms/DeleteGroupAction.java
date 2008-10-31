/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Delete a Community group
 * 
 * @author Wouter Heijke
 */
public class DeleteGroupAction extends AbstractCommunityAction {

    protected static final String GROUPID = "groupid";

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {
		String groupId = request.getParameter(GROUPID);
		if (groupId != null) {
			getAuthorityService().deleteAuthority(groupId);
		}
		return actionMapping.findForward(SUCCESS);
	}

}
