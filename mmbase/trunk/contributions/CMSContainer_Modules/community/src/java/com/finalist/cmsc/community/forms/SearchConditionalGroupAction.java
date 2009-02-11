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
 * @author nikko
 *
 */
public class SearchConditionalGroupAction extends AbstractCommunityAction {

   /**
    * @param mapping goto jsp
    * @param form get parameter
    * @param request setSharedAttributes
    * @param response do nothing
    * @return ActionForward chose jsp
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response)  {
      SearchGroupForm searchform = (SearchGroupForm) form;
      PagingStatusHolder holder = setPagingInformation(request);
      HashMap map = getParameterMap(searchform);
      List <Authority> authorities = getAuthorityService().getAssociatedAuthorities(map, holder);
      int totalCount = getAuthorityService().getAssociatedAuthoritiesNum(map, holder);
      setSharedAttributes(request, authorities, totalCount);
      return mapping.findForward("success");
   }

   private void setSharedAttributes(HttpServletRequest request, List <Authority> authorities, int totalCount) {
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("results", convertAuthorityTOVO(authorities));
   }

   private PagingStatusHolder setPagingInformation(HttpServletRequest request) {
      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      holder.setDefaultSort("asn.id", "desc");
      return holder;
   }

   private HashMap getParameterMap(SearchGroupForm searchform) {
      HashMap map = new HashMap();

      if (StringUtils.isNotBlank(searchform.getMember())) {
         map.put("username", searchform.processNames(searchform.getMember()));
      }

      if (StringUtils.isNotBlank(searchform.getGroupname())) {
         map.put("group", searchform.getGroupname());
      }
      return map;
   }

   private List <GroupForShowVO> convertAuthorityTOVO(List <Authority> authorities) {
      List <GroupForShowVO> groupForShow = new ArrayList <GroupForShowVO> ();
      for (Authority authority : authorities) {
         if (null != authority) {
            GroupForShowVO group = new GroupForShowVO();
            group.setGroupName(authority.getName());
            group.setGroupId(authority.getId().toString());
            StringBuffer userNames = new StringBuffer();
            Set <Authentication> authentications = authority.getAuthentications();
            if (!authentications.isEmpty()) {
               group.setUserAmount(authentications.size());
               
               Iterator<Authentication> iterator = authentications.iterator();
               int loopTimes = (authentications.size() > 10)? 10 : authentications.size();
               for (int i = 0 ; i < loopTimes; i++) {
                  Authentication au = iterator.next();
                  Person person = getPersonService().getPersonByAuthenticationId(au.getId());
                  if (person != null) {
                     userNames.append(person.getFullName() + ", ");
                  }
               }
               group.setUsers(userNames.substring(0, userNames.length() - 2));
            } else {
               group.setUserAmount(0);
               group.setUsers("");
            }
            groupForShow.add(group);
         }
      }
      return groupForShow;
   }

}