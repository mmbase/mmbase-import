<%@include file="globals.jsp" 
%>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<cmscedit:head title="reactions.title">
<script type="text/javascript">
      function addToGroup(){
      var checkboxs = document.forms[1].getElementsByTagName("input");
      var objectnumbers = '';
      for(i = 0; i < checkboxs.length; i++) {
         if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_group') == 0 && checkboxs[i].checked) {
            objectnumbers += checkboxs[i].value;
         }
      }
      if(objectnumbers == ''){
         alert("<fmt:message key="community.search.promptgroup"/>");
         return false;
      }
      return true;
   }
</script> 
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#">
               <fmt:message key="community.search.users"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
            <a href="${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do">
               <fmt:message key="community.search.groups"/>
            </a>
         </div>
      </div>
   </div>
   <div class="editor">
      <div style="padding-left:10px;">
      <p></p>
         <html:form action="editors/community/AddUserToGroupInit.do" method="post">
            <table border="0">
               <tbody>
                  <tr>&nbsp;</tr>
                  <tr>
                     <td style="width:150px"><fmt:message key="community.search.groupname"/></td>
                     <td><html:text style="width: 250px" property="group"/></td>
                  </tr>
                  <tr>&nbsp;</tr>
                  <tr>
                     <td style="width:150px">&nbsp;</td>
                     <td><input type="submit" name="submit" value="Search"> </td>
                  </tr>
               </tbody>
            </table>
         </html:form>
      </div>
   </div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp; <fmt:message key="community.search.selectGroup"/> &nbsp;</div></div>
   <div class="body">
    <form action="${pageContext.request.contextPath }/editors/community/AddUserToGroup.do?users=${users}" method="post">
         <p><input type="submit" value="add User To Group" name="submitButton" onclick="return addToGroup()"/></p>
         <edit:ui-table items="${groupForShow}" var="group" size="${totalCount}" requestURI="/editors/community/AddUserToGroupInit.do">
            <edit:ui-tcolumn title="">
               <input type="checkbox" name="chk_group" value="${group.groupName}"/>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.groupname" sort="groupName">
               <a href="#">${group.groupName}</a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.users">
               ${group.users}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </form>
   </div>
</div>