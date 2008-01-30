package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Delete a Community user
 * 
 * @author Wouter Heijke
 */
public class DeleteUserAction extends AbstractCommunityAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {
		String userid = request.getParameter("userid");
		if (userid != null) {
			getAuthenticationService().deleteAuthentication(userid);
		}
		return mapping.findForward("success");
	}
}
