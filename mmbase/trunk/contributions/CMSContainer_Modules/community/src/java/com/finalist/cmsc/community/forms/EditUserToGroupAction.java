package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.GroupForShowVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.Authority;

public class EditUserToGroupAction extends AbstractCommunityAction{
	/**响应add user to group这个提交键*/
	
	public ActionForward execute(ActionMapping actionMapping,ActionForm actionForm, HttpServletRequest request,HttpServletResponse response) throws Exception {
		SearchForm searchform = (SearchForm) actionForm;
		if (null != searchform.getOption()&&null!=searchform.getGroupName()) {
			String groupName = searchform.getGroupName();
			String[] authIds = searchform.getChk_();
			//String forword=request.getParameter("group");
			String forword=searchform.getOption();
			if ("remove".equals(forword)) {
				removeAuthorityFromUser(groupName, authIds);
				request.setAttribute("searchform", searchform);
			}
			if ("add".equals(forword)) {
				PagingStatusHolder holder = PagingUtils.getStatusHolder(request);
				int totalCount = 0;
				List<Authority> authorities = new ArrayList();
				if(!StringUtil.isEmptyOrWhitespace(searchform.getGroup())){
					authorities.clear();
					HashMap map = new HashMap();
					map.put("group", searchform.getGroup());
					if (map.size() > 0) {
						authorities = getAuthorityService().getAssociatedAuthorities(map,
								holder);
						totalCount = getAuthorityService().getAssociatedAuthoritiesNum(map,
								holder);
					}
				}else{
					StringBuffer userAllId = new StringBuffer();
					for(String userId:searchform.getChk_()){
						userAllId.append(userId+";");
					}
					request.getSession().setAttribute("users",userAllId.substring(0, userAllId.length()-1)); //把从上一个页面上传过来的数组链接成字符串，添加到attribute中
					authorities = getAuthorityService().getAllAuthorities(holder);
					totalCount = getAuthorityService().countAllAuthorities();
				}
				if(authorities!=null)
				request.setAttribute("groupName", groupName);
				request.setAttribute("groupForShow", convertAuthrityTOVO(authorities));
				request.setAttribute("totalCount", totalCount);
			}			
			return actionMapping.findForward(forword);
		}
		return actionMapping.findForward(CANCEL);
	}
	private void removeAuthorityFromUser(String groupName, String[] authIds) {
		for (String authId : authIds) {
			if (null!=authId) {
				getAuthenticationService().removeAuthenticationFromAuthority(authId, groupName);
			}			
		}
	}
	private List<GroupForShowVO> convertAuthrityTOVO(List<Authority> authorities){
		List<GroupForShowVO> groupForShow = new ArrayList<GroupForShowVO>();
		for(Authority authority : authorities){
			GroupForShowVO group = new GroupForShowVO();
			group.setGroupName(authority.getName());
			group.setGroupId(authority.getId().toString());
			StringBuffer userNames = new StringBuffer();
			Set<Authentication> authentications = authority.getAuthentications();
			if(!authentications.isEmpty()){
				for(Authentication au:authentications){
					Person person = getPersonService().getPersonByAuthenticationId(au.getId());
					if(person!=null){
						userNames.append(person.getFirstName()+" "+person.getLastName()+", ");
					}
				}
				group.setUsers(userNames.substring(0, userNames.length()-2));
			}else{
				group.setUsers("");
			}
			groupForShow.add(group);
		}
		return groupForShow;
	}
	
}
	/*private void addAuthorityToUser(String[] groupNames,String[] userIds){
		for(String groupName:groupNames){
			for(String userId:userIds){
				getAuthenticationService().addAuthorityToUser(userId, groupName);
			}
		}
	}*/
	/*if(request.getAttribute("users")!=null){
	String[] userIds = (String[])request.getAttribute("users");
	SearchForm searchform = (SearchForm)actionForm;
	//String operation=(String) request.getSession().getAttribute("operation");
	if (null!=(searchform.getChk_group())){
		String[] groupNames = searchform.getChk_group();
		addAuthorityToUser( groupNames, userIds);
		return actionMapping.findForward("user");
	}else if(null!=request.getSession().getAttribute("groupName")){
		String groupName=(String) request.getSession().getAttribute("groupName");
		removeAuthorityFromUser(groupName,userIds);
		return actionMapping.findForward("group");
	}
}*/
	

