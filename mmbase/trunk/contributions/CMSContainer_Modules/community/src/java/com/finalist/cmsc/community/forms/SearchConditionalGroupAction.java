package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.domain.GroupForShowVO;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.Authority;
import com.finalist.cmsc.services.community.security.AuthorityService;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.paging.PagingStatusHolder;

public class SearchConditionalGroupAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(SearchConditionalGroupAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SearchGroupForm searchform = (SearchGroupForm) form;
		List<GroupForShowVO> relationsVO = new ArrayList<GroupForShowVO>();
		List<Authority> authorities = new ArrayList<Authority>();
		PagingStatusHolder holder = PagingUtils.getStatusHolder(request);
		int totalCount = 0;
		HashMap map = new HashMap();

		if (!StringUtil.isEmptyOrWhitespace(searchform.getMember())) {
			map.put("username", searchform.processNames(searchform.getMember()));
		}

		if (!StringUtil.isEmptyOrWhitespace(searchform.getGroupname())) {
			map.put("group", searchform.getGroupname());
		}
		if (map.size() > 0) {
			authorities = getAuthorityService().getAssociatedAuthorities(map,holder);
			totalCount = getAuthorityService().getAssociatedAuthoritiesNum(map,holder);
		} else {
			authorities = getAuthorityService().getAllAuthorities(holder);
			totalCount = getAuthorityService().countAllAuthorities();
		}
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("results", convertAuthrityTOVO(authorities));
		return mapping.findForward("success");
	}

	private List<GroupForShowVO> convertAuthrityTOVO(List<Authority> authorities){
		List<GroupForShowVO> groupForShow = new ArrayList<GroupForShowVO>();
		for(Authority authority : authorities){
			if (null!=authority) {
				GroupForShowVO group = new GroupForShowVO();
				group.setGroupName(authority.getName());
				group.setGroupId(authority.getId().toString());
				StringBuffer userNames = new StringBuffer();
				Set<Authentication> authentications = authority.getAuthentications();
				if (!authentications.isEmpty()) {
					for (Authentication au : authentications) {
						Person person = getPersonService()
								.getPersonByAuthenticationId(au.getId());
						if (person != null) {
							userNames.append(person.getFirstName() + " "+ person.getLastName() + ", ");
						}
					}
					group.setUsers(userNames.substring(0,
							userNames.length() - 2));
				} else {
					group.setUsers("");
				}
				groupForShow.add(group);
			}			
		}
		return groupForShow;
   }

}