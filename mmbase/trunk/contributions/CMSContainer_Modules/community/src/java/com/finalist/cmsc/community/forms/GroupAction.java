package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.LabelValueBean;

import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class GroupAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(GroupAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {

		if (!isCancelled(request)) {
			GroupForm groupForm = (GroupForm) actionForm;
			List<LabelValueBean> membersList = new ArrayList<LabelValueBean>();
			List<LabelValueBean> usersList = new ArrayList<LabelValueBean>();

			String id = groupForm.getName();

			// get all users
			// NodeList users = SecurityUtil.getUsers(cloud);
			AuthenticationService as = getAuthenticationService();
			List<Authentication> users = as.findAuthentications();

			AuthorityService aus = getAuthorityService();

			for (Iterator<Authentication> iter = users.iterator(); iter.hasNext();) {
				Authentication user = iter.next();
				String label = user.getUserId();
				LabelValueBean bean = new LabelValueBean(label, label);
				usersList.add(bean);

			}
			// get members and remove them from users
			for (String memberName : groupForm.getMembers()) {
				// Node member = cloud.getNode(memberNumber);
				String label = memberName;
				LabelValueBean beanMember = new LabelValueBean(label, label);
				membersList.add(beanMember);
				usersList.remove(beanMember);
			}

			request.setAttribute("membersList", membersList);
			request.setAttribute("usersList", usersList);

			// validate
			ActionMessages errors = new ActionMessages();

			// Node groupNode = getOrCreateNode(groupForm, cloud,
			// SecurityUtil.GROUP);
			if (groupForm.getAction().equalsIgnoreCase(ACTION_ADD)) {
				if (id == null || id.length() < 3) {
					errors.add("groupname", new ActionMessage("error.groupname.invalid"));
					saveErrors(request, errors);
					return actionMapping.getInputForward();
				} else {
					String name = id;
					// NodeList list =
					// MMBaseAction.getCloudFromSession(request).getNodeManager("mmbasegroups").getList(
					// "name='" + name + "'", null, null);
					boolean exist = aus.authorityExists(name);
					if (exist) {
						errors.add("groupname", new ActionMessage("error.groupname.alreadyexists"));
						saveErrors(request, errors);
						return actionMapping.getInputForward();
					}
				}

				// groupNode.setStringValue("name", groupForm.getName());
				aus.createAuthority(null, id);
			}

			// groupNode.commit();
			// SecurityUtil.setGroupMembers(cloud, groupNode,
			// groupForm.getMembers());
			if (id != null) {
				for (String memberName : groupForm.getMembers()) {
					as.addAuthorityToUser(memberName, id);
				}
			}
		}
		removeFromSession(request, actionForm);

		return actionMapping.findForward(SUCCESS);

	}
}
