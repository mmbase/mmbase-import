package com.finalist.cmsc.community.forms;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.PersonVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchConditionalUserAction extends DispatchAction {

   private PersonService personService;
   private AuthenticationService authenticationService;
   private AuthorityService authorityService;

   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   protected ActionForward unspecified(ActionMapping actionMapping, ActionForm actionForm,
                                       HttpServletRequest request, HttpServletResponse httpServletResponse)
         throws Exception {

      setPagingInformation(request);
      Map<String, String> map = getParameterMap(actionForm);

      List<Person> persons = personService.getAssociatedPersons(map);
      int totalCount = personService.getAssociatedPersonsNum(map);

      setSharedAttributes(request, persons, totalCount);

      return actionMapping.findForward("success");
   }


   public ActionForward listGroupMembers(ActionMapping actionMapping, ActionForm actionForm,
                                          HttpServletRequest request, HttpServletResponse httpServletResponse)
            throws Exception {

      setPagingInformation(request);
      Map<String, String> map = getParameterMap(actionForm);

      String groupName = request.getParameter("groupName");

      map.put("group", groupName);
      map.put("strict", "strict");

      List<Person> persons = personService.getAssociatedPersons(map);
      int totalCount = personService.getAssociatedPersonsNum(map);

      setSharedAttributes(request, persons, totalCount);

      request.setAttribute("groupName", groupName);
      return actionMapping.findForward("group");
   }
   
   private List<PersonVO> convertToVO(List<Person> persons) {
      List<PersonVO> perShow;
      perShow = new ArrayList<PersonVO>();
      for (Person p : persons) {
         String username = authenticationService.getAuthenticationById(p.getAuthenticationId()).getUserId();
         Set<String> authorityNames = authorityService.getAuthorityNamesForUser(username);

         String groupsName = StringUtils.join(authorityNames,", ");

         PersonVO per = new PersonVO();
         per.setFullname(p.getFullName());
         per.setEmail(p.getEmail());
         per.setUsername(username);
         per.setGroups(groupsName);
         per.setAuthId(p.getAuthenticationId());
         perShow.add(per);
      }
      return perShow;
   }

   private void setSharedAttributes(HttpServletRequest request, List<Person> persons, int totalCount) {
      request.setAttribute("personForShow", convertToVO(persons));
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      request.setAttribute("method", request.getParameter("method"));
   }

   private Map<String, String> getParameterMap(ActionForm actionForm) {
      Map<String, String> map = ParameterMapper.wrap(actionForm).
            map("fullname", "fullName").
            map("username", "userName").
            map("email", "emailAddr").
            map("group", "groups").
            getMap();
      return map;
   }

   private void setPagingInformation(HttpServletRequest request) {
      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      holder.setDefaultSort("person.id","desc");
   }
}
