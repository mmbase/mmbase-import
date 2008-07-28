<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<cmscedit:head title="reactions.title">
<script type="text/javascript">
   function addToGroup(){
      var checkboxs = document.forms[1].getElementsByTagName("input");
      var objectnumbers = '';
      for(i = 0; i < checkboxs.length; i++) {
         if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
            objectnumbers += checkboxs[i].value;
         }
      }
      if(objectnumbers == ''){
         alert("<fmt:message key="community.search.promptuser"/>");
         return false;
      }
      document.getElementById("option").value ="add";
      return true;
   }
   function removeFromGroup(){
      var checkboxs = document.forms[1].getElementsByTagName("input");
      var objectnumbers = '';
      var j=0;
      for(i = 0; i < checkboxs.length; i++) {
         if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
            objectnumbers += checkboxs[i].value;
            j++;
         }
      }
      if(objectnumbers == ''){
         alert("<fmt:message key="community.search.promptuser"/>");
         return false;
      }
      alert("<fmt:message key="community.search.option"><fmt:param>j</fmt:param></fmt:message>");
      document.getElementById("option").value ="remove";
      return true;
   }

</script>
</cmscedit:head>
   
<div class="tabs">
   <div class="tab">
      <div class="body">
         <div>
            <a href="${pageContext.request.contextPath }/editors/community/SearchConditionalUser.do">
               Users
            </a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
            <a href="${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do">
               Groups
            </a>
         </div>
      </div>
   </div>
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do">
               Memeber List of Group ${groupName} 
            </a>
         </div>
      </div>
   </div>
</div>

<div class="editor">
   <div style="padding-left:10px;">
      <p></p>
      <html:form action="/editors/community/SearchConditionalUser.do?groupName=${groupName}" method="post">
    
      <table border="0">
         <tbody >
            <tr> 
               <td style="width:150px"><fmt:message key="community.search.fullname"/></td>
               <td><html:text style="width: 250px" property="fullName"/></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.username"/></td>
               <td><html:text style="width: 250px" property="userName" /></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.emailAddress"/></td>
               <td><html:text style="width: 250px" property="emailAddr" /></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.groups"/></td>
               <td><html:text style="width: 250px" property="groups" /></td>
            </tr>
            <tr>
               <td style="width:150px"></td>
               <td><input type="submit" value="Search" name="submitButton"/></td>
            </tr>
         </tbody>
      </table>
      </html:form>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="community.search.result"/>&nbsp;</div></div>
   <div class="body">
      <form action="${pageContext.request.contextPath }/editors/community/editUserToGroupAction.do" method="post">
            <input type="hidden" name="groupName" value="${groupName}"/>
            <input type="hidden" id="option" name="option" />
         <p><input type="submit" value="add To Group" name="submitButton" onclick="return addToGroup()"/>
            <input type="submit" name="submitButton2" value="remove from group" onclick="return removeFromGroup()"/>
         </p>
         <edit:ui-table items="${personForShow}" var="person" size="${totalCount}" requestURI="/editors/community/SearchConditionalUser.do">
            <edit:ui-tcolumn title="">
               <input type="checkbox" name="chk_" value="${person.authId}"/>&nbsp;
               <a href="${pageContext.request.contextPath }/editors/community/userAddInitAction?authid=${person.authId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" width="16" height="16" title="edit"></a>
               <a href="${pageContext.request.contextPath }/editors/community/deleteUserAction?authid=${person.authId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="delete"></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.fullname" sort="fullname">
               <c:url var="userActionUrl" value="/editors/community/userAddInitAction.do"/>
               <a href="${userActionUrl}?authid=${person.authId}">${person.fullname}</a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.username" sort="username">
               ${person.username }
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.emailAddress" sort="email">
               ${person.email}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.memberOf">
               ${person.groups}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </form>
   </div>
</div>