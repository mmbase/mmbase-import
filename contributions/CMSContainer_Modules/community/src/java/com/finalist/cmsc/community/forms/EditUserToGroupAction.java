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

public class EditUserToGroupAction extends AbstractCommunityAction {
   /** ��Ӧadd user to group����ύ�� */

   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      SearchForm searchform = (SearchForm) actionForm;

      if (null == searchform.getOption() || null == searchform.getGroupName()) {
         return actionMapping.findForward(CANCEL);
      }

      String groupName = searchform.getGroupName();
      String[] authIds = searchform.getChk_();
      String forword = searchform.getOption();

      if ("remove".equals(forword)) {
         removeAuthorityFromUser(groupName, authIds);
         request.setAttribute("searchform", searchform);
      }
      if ("add".equals(forword)) {

         PagingStatusHolder holder = PagingUtils.getStatusHolder(request);
         int totalCount = 0;
         List < Authority > authorities = new ArrayList < Authority >();

         if (StringUtils.isNotBlank(searchform.getGroup())) {
            // have conditons searching

            HashMap map = new HashMap();
            map.put("group", searchform.getGroup());

            authorities = getAuthorityService().getAssociatedAuthorities(map, holder);
            totalCount = getAuthorityService().getAssociatedAuthoritiesNum(map, holder);

         } else {
            // no conditions search
            // need authId from the last jsp
            StringBuffer userAllId = new StringBuffer();
            for (String authId : searchform.getChk_()) {
               String userId = getAuthenticationService().getAuthenticationById(Long.parseLong(authId)).getUserId();
               userAllId.append(userId + ";");
            }
            request.getSession().setAttribute("users", userAllId.substring(0, userAllId.length() - 1)); // contact
            // string[]

            authorities = getAuthorityService().getAllAuthorities(holder);
            totalCount = getAuthorityService().countAllAuthorities();
         }
         if (authorities != null) {
            request.setAttribute("groupForShow", convertAuthrityTOVO(authorities));
         }
         request.setAttribute("totalCount", totalCount);
         return actionMapping.findForward("add");

      }
      return actionMapping.findForward(forword);

   }

   private void removeAuthorityFromUser(String groupName, String[] authIds) {
      for (String authId : authIds) {
         if (null != authId) {
            getAuthenticationService().removeAuthenticationFromAuthority(authId, groupName);
         }
      }
   }

   private List < GroupForShowVO > convertAuthrityTOVO(List < Authority > authorities) {
      List < GroupForShowVO > groupForShow = new ArrayList < GroupForShowVO >();
      for (Authority authority : authorities) {
         GroupForShowVO group = new GroupForShowVO();
         group.setGroupName(authority.getName());
         group.setGroupId(authority.getId().toString());
         StringBuffer userNames = new StringBuffer();
         Set < Authentication > authentications = authority.getAuthentications();
         if (!authentications.isEmpty()) {
            for (Authentication au : authentications) {
               Person person = getPersonService().getPersonByAuthenticationId(au.getId());
               if (person != null) {
                  userNames.append(person.getFirstName() + " " + person.getLastName() + ", ");
               }
            }
            group.setUsers(userNames.substring(0, userNames.length() - 2));
         } else {
            group.setUsers("");
         }
         groupForShow.add(group);
      }
      return groupForShow;
   }

}
