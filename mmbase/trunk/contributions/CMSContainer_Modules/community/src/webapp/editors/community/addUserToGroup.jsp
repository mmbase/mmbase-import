<%@include file="globals.jsp" 
%>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
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

<body>
<edit:ui-tabs>
      <edit:ui-tab key="community.search.users" active="true">
         #
      </edit:ui-tab>
      <edit:ui-tab key="community.search.groups">
         ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
      </edit:ui-tab>
   </edit:ui-tabs>

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
                     <td><input type="submit" name="submitButton" value="<fmt:message key="community.search.submit"/>"> </td>
                  </tr>
               </tbody>
            </table>
         </html:form>
      </div>
   </div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp; <fmt:message key="community.search.selectGroup"/> &nbsp;</div></div>
   <div class="body">
     <c:url var="addtoGroup" value="/editors/community/AddUserToGroup.do?users=${users}"/>
    <form action="${addtoGroup}" method="post">
         <p><input type="submit" value="<fmt:message key="community.search.addUser"/>" name="submitButton" onclick="return addToGroup()"/></p>
        <%@ include file="grouplist_addusertogroup.jspf" %>
      </form>
   </div>
</div>
</body>
</mm:content>