package com.finalist.cmsc.community.forms;

import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.PersonVO;
import com.finalist.cmsc.services.community.person.Person;
import net.sf.mmapps.commons.util.StringUtil;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class SearchConditionalUserAction extends AbstractCommunityAction {

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)
         throws Exception {

      PagingUtils.initStatusHolder(request);

      Map<String, String> map = ParameterMapper.wrap(actionForm).
            map("fullname", "fullName").
            map("username", "userName").
            map("email", "emailAddr").
            map("group", "groups").
            getMap();

      String groupName = request.getParameter("groupName");
      if (!StringUtil.isEmptyOrWhitespace(groupName)) {
         map.put("group", groupName);
         map.put("strict", "strict");
      }


      List<Person> persons = getPersonService().getAssociatedPersons(map);
      int totalCount = getPersonService().getAssociatedPersonsNum(map);

      request.setAttribute("personForShow", convertToVO(persons));
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      request.setAttribute("method", request.getParameter("method"));

      if (!StringUtil.isEmptyOrWhitespace(groupName)) {
         request.setAttribute("groupName", groupName);
         return actionMapping.findForward("group");
      }
      else {
         return actionMapping.findForward("success");
      }
   }

   private List<PersonVO> convertToVO(List<Person> persons) {
      List<PersonVO> perShow;
      perShow = new ArrayList<PersonVO>();
      for (Person p : persons) {
         String username = getAuthenticationService().getAuthenticationById(p.getAuthenticationId()).getUserId();

         PersonVO per = new PersonVO();
         per.setFullname(p.getFullName());
         per.setEmail(p.getEmail());
         per.setUsername(username);

         String groupsName = "";
         Set<String> authorityNames = getAuthorityService().getAuthorityNamesForUser(username);
         if (authorityNames.size() >= 1) {
            Iterator<String> iter = authorityNames.iterator();
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
