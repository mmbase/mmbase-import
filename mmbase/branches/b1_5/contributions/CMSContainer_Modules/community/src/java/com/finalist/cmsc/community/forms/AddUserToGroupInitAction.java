package com.finalist.cmsc.community.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.*;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.GroupForShowVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.Authority;

/**
 * Get users from checkbox.
 * Show all groups when there is no search conditions.
 * Otherwise show the groups according to the search conditions.
 * 
 * @author Eva
 */
public class AddUserToGroupInitAction extends AbstractCommunityAction {
   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      SearchForm searchform = (SearchForm) actionForm;
      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      int totalCount = 0;
      List <Authority> authorities = new ArrayList <Authority> ();

      if (StringUtils.isNotBlank(searchform.getGroup())) {
         // have conditions searching
         HashMap map = new HashMap();
         map.put("group", searchform.getGroup());
         authorities = getAuthorityService().getAssociatedAuthorities(map, holder);
         totalCount = getAuthorityService().getAssociatedAuthoritiesNum(map, holder);
      } else {
         // no conditions search
         // need authId from the last jsp
         if (searchform.getChk_() != null) {
            StringBuffer userAllId = new StringBuffer();
            for (String authId : searchform.getChk_()) {
               String userId = getAuthenticationService().getAuthenticationById(Long.parseLong(authId)).getUserId();
               userAllId.append(userId + ";");
            }
            request.getSession().setAttribute("users", userAllId.substring(0, userAllId.length() - 1)); // contact
                                                                                                         // string[]
         }

         authorities = getAuthorityService().getAllAuthorities(holder);
         totalCount = getAuthorityService().countAllAuthorities();
      }
      if (authorities != null) {
         request.setAttribute("groupForShow", convertAuthorityTOVO(authorities));
      }
      request.setAttribute("totalCount", totalCount);
      removeFromSession(request, searchform);
      return actionMapping.findForward("success");

   }

   private List <GroupForShowVO> convertAuthorityTOVO(List <Authority> authorities) {
      List <GroupForShowVO> groupForShow = new ArrayList <GroupForShowVO> ();
      for (Authority authority : authorities) {
         if (authority != null) {
            GroupForShowVO group = new GroupForShowVO();
            group.setGroupName(authority.getName());
            group.setGroupId(authority.getId().toString());
            StringBuilder userNames = new StringBuilder();
            Set <Authentication> authentications = authority.getAuthentications();
            if (!authentications.isEmpty()) {
               for (Authentication au : authentications) {
                  Person person = getPersonService().getPersonByAuthenticationId(au.getId());
                  if (person != null) {
                     userNames.append(person.getFullName() + ", ");
                  }
               }
               group.setUsers(userNames.substring(0, userNames.length() - 2));
            } else {
               group.setUsers("");
            }
            groupForShow.add(group);
         }
      }
      return groupForShow;
   }
}
