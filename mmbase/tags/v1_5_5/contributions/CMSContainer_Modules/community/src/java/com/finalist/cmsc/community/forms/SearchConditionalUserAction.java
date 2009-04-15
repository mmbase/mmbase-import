package com.finalist.cmsc.community.forms;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.PersonVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.Authority;
import com.finalist.cmsc.services.community.security.AuthorityService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Search users according to some conditions.
 * 
 * @author Eva
 * @author Lisa
 * 
 */
public class SearchConditionalUserAction extends DispatchAction {

   private PersonService personService;
   private AuthenticationService authenticationService;
   private AuthorityService authorityService;

   /**
    * @param personService need personService
    */
   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   /**
    * @param authenticationService need it
    */
   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   /**
    * @param authorityService need it
    */
   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   protected ActionForward unspecified(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {

      setPagingInformation(request);
      Map < String , String > map = getParameterMap(actionForm);
      String active=request.getParameter("state"); 
      if(null!=active){
         String authId=request.getParameter("authid");
         Long authenticationId=Long.parseLong(authId);
         personService.changeStateByAuthenticationId(authenticationId,active);
      }
      List < Person > persons = personService.getAssociatedPersons(map);
      int totalCount = personService.getAssociatedPersonsNum(map);

      setSharedAttributes(request, persons, totalCount);
      String forwardPath = (String) actionMapping.findForward("newUserLinkBack").getPath();
      request.setAttribute("forwardPath", forwardPath);

      return actionMapping.findForward("success");
   }

   /**
    * Search groups according to some search conditions.
    * @param actionMapping goto jsp
    * @param actionForm get paramate
    * @param request setSharedAttributes
    * @param httpServletResponse do nothing
    * @return ActionForward chose jsp
    * @throws Exception throw exception
    */
   public ActionForward listGroupMembers(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {
      SearchForm searchform = (SearchForm) actionForm;
      String option = searchform.getOption();
      Map < String , String > map = getParameterMap(actionForm);
      String groupName = request.getParameter("groupName");
      if (null == groupName || "".equals(groupName)) {
         groupName = (String) request.getAttribute("groupName");
      }
      if (null == option || "".equals(option)) {
         option = (String) request.getAttribute("option");
      }
      String[] authIds = searchform.getChk_();
      chooseOption(request, option, map, groupName, authIds);
      List < Person > persons = personService.getAssociatedPersons(map);
      int totalCount = personService.getAssociatedPersonsNum(map);
      setSharedAttributes(request, persons, totalCount);
      request.setAttribute("groupName", groupName);
      return actionMapping.findForward("group");
   }

   private void chooseOption(HttpServletRequest request, String option, Map < String , String > map, String groupName,
         String[] authIds) {
      // String option = searchform.getOption();
      if (null != authIds && "remove".equals(option)) {
         removeAuthorityFromUser(groupName, authIds);
      }
      setPagingInformation(request);
      map.put("group", groupName);
      map.put("strict", "strict");
      if ("select".equals(option)) {
         request.setAttribute("option", option);
         Authority authority = authorityService.findAuthorityByName(groupName);
         Set < Authentication > authentications = authority.getAuthentications();
         Set < String > userNames = new HashSet < String > ();
         for (Iterator iter = authentications.iterator(); iter.hasNext();) {
            Authentication authentication = (Authentication) iter.next();
            userNames.add(authentication.getUserId());
         }
         String transFormNames = StringUtils.join(userNames, "','");
         map.put("strict", transFormNames);
      }
   }

   /**
    * searching for newsletter subscriber candidate list
    * @param actionMapping goto jsp
    * @param actionForm get paramate
    * @param request setSharedAttributes
    * @param httpServletResponse do nothing
    * @return ActionForward chose jsp
    * @throws Exception throw exception
    */
   public ActionForward searchCandidateSubscriber(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {

      setPagingInformation(request);
      Map < String , String > map = getParameterMap(actionForm);
      map.put("strict", "strict");

      List < Person > persons = personService.getAssociatedPersons(map);
      int totalCount = personService.getAssociatedPersonsNum(map);

      setSharedAttributes(request, persons, totalCount);

      request.setAttribute("groupName", map.get("group"));
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      return actionMapping.findForward("listcandidate");
   }

   private List < PersonVO > convertToVO(List < Person > persons) {
      List < PersonVO > perShow;
      perShow = new ArrayList < PersonVO > ();
      for (Person p : persons) {
         String username = authenticationService.getAuthenticationById(p.getAuthenticationId()).getUserId();
         Set < String > authorityNames = authorityService.getAuthorityNamesForUser(username);

         String groupsName = StringUtils.join(authorityNames, ", ");

         PersonVO per = new PersonVO();
         per.setFullname(p.getFullName());
         per.setEmail(p.getEmail());
         per.setUsername(username);
         per.setGroups(groupsName);
         per.setAuthId(p.getAuthenticationId());
         per.setActive(p.getActive());
         perShow.add(per);
      }
      return perShow;
   }

   private void setSharedAttributes(HttpServletRequest request, List < Person > persons, int totalCount) {
      request.setAttribute("personForShow", convertToVO(persons));
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      request.setAttribute("method", request.getParameter("method"));
   }

   private Map < String , String > getParameterMap(ActionForm actionForm) {
      Map < String , String > map = ParameterMapper.wrap(actionForm).map("fullname", "fullName").map("username",
            "userName").map("email", "emailAddr").map("group", "groups").map("group", "groupName").getMap();
      return map;
   }

   private void setPagingInformation(HttpServletRequest request) {
      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      holder.setDefaultSort("person.id", "desc");
   }

   private void removeAuthorityFromUser(String groupName, String[] authIds) {
      for (String authId : authIds) {
         if (null != authId) {
            authenticationService.removeAuthenticationFromAuthority(authId, groupName);
         }
      }
   }
}
