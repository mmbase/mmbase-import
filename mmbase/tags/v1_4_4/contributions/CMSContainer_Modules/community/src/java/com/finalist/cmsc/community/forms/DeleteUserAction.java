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
		String userId = request.getParameter(USERID);
		if (userId != null) {
			getPersonService().deletePersonByUserId(userId);
			getAuthenticationService().deleteAuthentication(userId);
		}
		return mapping.findForward(SUCCESS);
	}
}
