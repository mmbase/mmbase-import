<%@include file="globals.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <mm:content type="text/html" encoding="UTF-8" expires="0">
      <cmscedit:head title="community.title">
         <script type="text/javascript"
            src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
         <script type="text/javascript" src="js/formcheck.js"></script>
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
               return true;
            }
            
            function selectOtherUsers(){
               document.getElementById("option").value ="select";
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
              if(confirm("<fmt:message key="community.search.option"><fmt:param>"+j+"</fmt:param></fmt:message>")){
               document.getElementById("option").value ="remove";
               return true;
             }
             return false;
            }
        </script>
      </cmscedit:head>
   
      <body>
         <mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
            <edit:ui-tabs>
               <edit:ui-tab key="community.search.users" >
               ${pageContext.request.contextPath }/editors/community/SearchConditionalUser.do
                   </edit:ui-tab>
               <edit:ui-tab key="community.search.groups">
                       ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
                   </edit:ui-tab>
               <fmt:message key="community.search.prompt" var="title">
                  <fmt:param value="${requestScope.groupName}" />
               </fmt:message>
               <edit:ui-tab title="${title}" active="true">#</edit:ui-tab>
                       
            </edit:ui-tabs>
   
            <div class="editor">
               <div class="body">
                     <html:form
                        action="/editors/community/SearchConditionalUser.do?method=listGroupMembers&&groupName=${groupName}" method="post">
                     <c:if test="${not empty option}">
                         <input type="hidden" id="option" name="option" value="select"/>
                     </c:if>
                        <%@include file="search_user_form_table.jspf"%>
                     </html:form>   
               </div>
            </div>
   
            <div class="editor">
               <div class="ruler_green">
                  <div>
                     &nbsp;
                     <fmt:message key="community.search.result" />
                     &nbsp;
                  </div>
               </div>
               <div class="body">
                  <c:url var="userActionUrl"
                     value="/editors/community/SearchConditionalUser.do">
                     <c:param name="groupName" value="${groupName}" />
                     <c:param name="method" value="listGroupMembers" />
                  </c:url>
                  <c:choose>
                     <c:when test="${empty option}">
                        <form action="${userActionUrl}" method="post" id="selectform">
                           <input type="hidden" id="option" name="option" />
                           <input type="submit"
                              value="<fmt:message key="community.search.selectUser" ><fmt:param>${groupName}</fmt:param></fmt:message>"
                              name="submitButton" onclick="return selectOtherUsers()" />
                           <input type="submit" name="submitButton2"
                              value="<fmt:message key="community.search.removeUser" />"
                              onclick="return removeFromGroup()" />
                     </c:when>
                     <c:otherwise>
                        <form
                           action="${pageContext.request.contextPath}/editors/community/AddUserToGroup.do?groupName=${groupName}"
                           method="post" id="selectform">
                           <input type="submit"
                              value="<fmt:message key="community.search.addUserToGroup"><fmt:param>${groupName}</fmt:param></fmt:message>"
                              name="submitButton" onclick="return addToGroup()" />
                     </c:otherwise>
                  </c:choose>
                  <%@ include file="userlist_table.jspf"%>
                  </form>
               </div>
            </div>
         </mm:cloud>
      </body>
   </mm:content>
</html>