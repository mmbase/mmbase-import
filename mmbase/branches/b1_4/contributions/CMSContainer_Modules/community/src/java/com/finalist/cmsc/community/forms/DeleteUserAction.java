package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Delete a Community user
 * 
 * @author Wouter Heijke
 */
public class DeleteUserAction extends AbstractCommunityAction {

    protected static final String AUTHENTICATION_ID = "authid";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {
		String authenticationId = request.getParameter(AUTHENTICATION_ID);
		if (!StringUtils.isBlank(authenticationId)) {
			Long authId = Long.valueOf(authenticationId);
            getPersonService().deletePersonByAuthenticationId(authId);
			getAuthenticationService().deleteAuthentication(authId);
		}
		return mapping.findForward(SUCCESS);
	}
}
