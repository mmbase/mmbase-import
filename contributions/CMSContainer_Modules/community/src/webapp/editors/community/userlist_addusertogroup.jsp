<%@include file="globals.jsp"
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<mm:content type="text/html" encoding="UTF-8" expires="0">
   <cmscedit:head title="reactions.title">
      <script type="text/javascript" src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
      <script type="text/javascript">
         window.onload = function ()
         {
            // handle onclick event for element of ID="foo"
            Event.observe('selectform', 'submit', doStuff[useCapture = false]);
         }

         function doStuff(useCapture) {
            alert(useCapture);
            return false;
         }
         
         function addToGroup() {
            var checkboxs = document.forms[1].getElementsByTagName("input");
            var objectnumbers = '';
            for (i = 0; i < checkboxs.length; i++) {
               if (checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
                  objectnumbers += checkboxs[i].value;
               }
            }
            if (objectnumbers == '') {
               alert("<fmt:message key="community.search.promptuser"/>");
               return false;
            }
            return true;
         }
      </script>

   </cmscedit:head>

   <body>
   <mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
      <edit:ui-tabs>
         <edit:ui-tab key="community.search.users"/>
         <edit:ui-tab key="community.search.groups">
            ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
         </edit:ui-tab>
         <fmt:message key="community.search.prompt" var="title">
            <fmt:param value="${requestScope.groupName}"/>
         </fmt:message>
         <edit:ui-tab title="${title}" active="true">
            ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
         </edit:ui-tab>
      </edit:ui-tabs>

      <div class="editor">
         <div style="padding-left:10px;">
            <p>
               <a href="userAddInitAction.do"
                  style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left center no-repeat">
                  <fmt:message key="view.new.user"/>
               </a>

            <p>
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
               <input type="submit" value="add To Group" name="submitButton"/>
               <input type="submit" name="submitButton2" value="remove from group" onclick="return removeFromGroup()"/>
               <%@ include file="userlist_table.jspf" %>
            </form>
         </div>
      </div>
   </mm:cloud>
   </body>
</mm:content>
