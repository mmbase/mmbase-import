package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class GroupAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(DeleteGroupAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {

		if (!isCancelled(request)) {
			GroupForm groupForm = (GroupForm) actionForm;

			String id = groupForm.getName();

			AuthorityService aus = getAuthorityService();

			if (groupForm.getAction().equalsIgnoreCase(ACTION_ADD)) {
				if (!aus.authorityExists(id)) {
					aus.createAuthority(null, id);

					
					
				} else {
					log.info("authority already exists");
				}

			} else if (groupForm.getAction().equalsIgnoreCase(ACTION_EDIT)) {

			} else {
				log.info("action failed");
			}
		} else {
			log.info("cancelled");
		}

		// removeFromSession(httpServletRequest, actionForm);

		return actionMapping.findForward(SUCCESS);

	}

}
