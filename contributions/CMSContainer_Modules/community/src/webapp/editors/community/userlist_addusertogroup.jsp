<%@include file="globals.jsp"%>

<mm:content type="text/html" encoding="UTF-8" expires="0">
	<cmscedit:head title="reactions.title">
		<script type="text/javascript"
			src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
		<script type="text/javascript" src="js/formcheck.js"></script>
		<script type="text/javascript">
         window.onload = function ()
         {
            Event.observe("selectform", "submit", function(e) {
               addToGroup("chk_", "<fmt:message key="community.search.promptuser"/>", e)
            })
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
      //document.getElementById("option").value ="remove";
      return true;
	 }
	 return false;
   }
      </script>
	</cmscedit:head>

	<body>
		<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
			<edit:ui-tabs>
				<edit:ui-tab key="community.search.users" />
				<edit:ui-tab key="community.search.groups">
            ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
         </edit:ui-tab>
				<fmt:message key="community.search.prompt" var="title">
					<fmt:param value="${requestScope.groupName}" />
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
							<fmt:message key="view.new.user" /> </a>
					<p>
						<html:form action="/editors/community/SearchConditionalUser.do"
							method="post">
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
					<form action="${editGroup}" method="post" id="selectform">
						<input type="submit"
							value="<fmt:message key="community.search.addUser" />"
							name="submitButton" onclick="return addToGroup()" />
						<input type="submit" name="submitButton2"
							value="<fmt:message key="community.search.removeUser" />"
							onclick="return removeFromGroup()" />
						<%@ include file="userlist_table.jspf"%>
					</form>
				</div>
			</div>
		</mm:cloud>
	</body>
</mm:content>
