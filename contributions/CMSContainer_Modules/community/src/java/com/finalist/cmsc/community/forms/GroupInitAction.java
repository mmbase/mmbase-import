/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Wouter Heijke
 */
public class GroupInitAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(GroupInitAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {

		String id = request.getParameter(GROUPID);
		GroupForm groupForm = (GroupForm) actionForm;

		if (id != null) {
			groupForm.setAction(ACTION_EDIT);
		} else {
			// new
			groupForm.setAction(ACTION_ADD);
		}

		return actionMapping.findForward(SUCCESS);

	}
}
