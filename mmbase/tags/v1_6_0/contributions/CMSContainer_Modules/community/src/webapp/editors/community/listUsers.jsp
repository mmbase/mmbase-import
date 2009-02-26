<%@include file="globals.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<mm:content type="text/html" encoding="UTF-8" expires="0">
   <cmscedit:head title="community.title">
      <script type="text/javascript" src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
      <script type="text/javascript" src="js/formcheck.js"></script>
      <script type="text/javascript">
        function addToGroup(){
         var checkboxs = document.forms[1].getElementsByTagName("input");
         var selected = false;
         for(i = 0; i < checkboxs.length; i++) {
            if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
               selected = true;
               break;
            }
         }
         if(!selected){
            alert("<fmt:message key="community.search.promptuser"/>");
            return false;
         }
         return true;
      }

      </script>
   </cmscedit:head>

   <body>
   <edit:ui-tabs>
      <edit:ui-tab key="community.search.users" active="true">
         ${pageContext.request.contextPath }/editors/community/SearchConditionalUser.do
      </edit:ui-tab>
      <edit:ui-tab key="community.search.groups">
         ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
      </edit:ui-tab>
   </edit:ui-tabs>

   <div class="editor"> 
      <div class="body">
         <c:url var="addUserUrl" value="userAddInitAction.do">
            <c:param name="forward" value="addCommunityUser"/>
            <c:param name="path" value="${forwardPath}"/>
         </c:url>
         <ul class="shortcuts">
            <li class="new" style="text-decoration: none;">
               <a href="${addUserUrl}"><fmt:message key="view.new.user"/></a>
            </li>
         </ul>
         <html:form action="/editors/community/SearchConditionalUser.do" method="post">
            <%@include file="search_user_form_table.jspf" %>
         </html:form>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green">
         <div>&nbsp;<fmt:message key="community.search.result"/>&nbsp;</div>
      </div>
      <div class="body">
         <c:url var="addGroup" value="/editors/community/AddUserToGroupInit.do"/>

         <form action="${addGroup}" method="post" name="selectform" id="selectform">
            <input type="submit" value="<fmt:message key="community.search.addUser"/>" name="submitButton" onclick="return addToGroup()"/>
            <%@ include file="userlist_table.jspf" %>
         </form>
      </div>
   </div>
   </body>
</mm:content>
</html>