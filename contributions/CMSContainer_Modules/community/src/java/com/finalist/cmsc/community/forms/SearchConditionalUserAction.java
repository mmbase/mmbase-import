package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authority;

public class SearchConditionalUserAction extends AbstractCommunityAction {

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();

      String groupName = request.getParameter("groupName");
      SearchForm searchform = (SearchForm) actionForm;
      HashMap map = new HashMap();
      if (!StringUtil.isEmptyOrWhitespace(searchform.getFullName())) {
         map.put("fullname", searchform.getFullName());
      }
      if (!StringUtil.isEmptyOrWhitespace(searchform.getUserName())) {
         map.put("username", searchform.getUserName());
      }
      if (!StringUtil.isEmptyOrWhitespace(searchform.getemailAddr())) {
         map.put("email", searchform.getemailAddr());
      }
      if (!StringUtil.isEmptyOrWhitespace(searchform.getGroups())) {
         map.put("group", searchform.getGroups());
      }
      if (!StringUtil.isEmptyOrWhitespace(groupName)) {
         map.put("group", groupName);
      }
      if (!StringUtil.isEmptyOrWhitespace(groupName)) {
         map.put("group", groupName);
         map.put("strict", "strict");
      }



      List<Person> persons;
      int totalCount = 0;
      if (map.size() > 0) {
         persons = getPersonService().getAssociatedPersons(map);
         totalCount = getPersonService().getAssociatedPersonsNum(map, holder);
      }
      else {
         persons = getPersonService().getAllPeople(holder);
         totalCount = getPersonService().countAllPersons();
      }


      request.setAttribute("personForShow", convertToVO(persons));
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      request.setAttribute("method", request.getParameter("method"));
      if (!StringUtil.isEmptyOrWhitespace(groupName)) {
         request.setAttribute("groupName", groupName);
         return actionMapping.findForward("group");
      }
      removeFromSession(request, searchform);
      ActionForward ret = actionMapping.findForward("success");
      return ret;
	}

	private List<PersonForShow> convertToVO(List<Person> persons) {
		List<PersonForShow> perShow;
		perShow = new ArrayList<PersonForShow>();
		for (Person p : persons) {
         String username = getAuthenticationService().getAuthenticationById(p.getAuthenticationId()).getUserId();

         PersonForShow per = new PersonForShow();
         per.setFullname(p.getFullName());
         per.setEmail(p.getEmail());
         per.setUsername(username);

         String groupsName = "";
         Set<String> authorityNames = getAuthorityService().getAuthorityNamesForUser(username);
         if (authorityNames.size() >= 1) {
            Iterator iter = authorityNames.iterator();
            while (iter.hasNext()) {
               groupsName += iter.next() + ", ";
            }
            groupsName = groupsName.substring(0, groupsName.length() - 2);
            per.setGroups(groupsName);
         }
         else {
            per.setGroups("");
         }
         per.setAuthId(p.getAuthenticationId());
         perShow.add(per);
      }
      return perShow;
   }
}
