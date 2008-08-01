<%@include file="globals.jsp"%>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp"/>

<mm:content type="text/html" encoding="UTF-8" expires="0">
   <cmscedit:head title="reactions.title">
      <script type="text/javascript" src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
      <script type="text/javascript" src="js/formcheck.js"></script>
      <script type="text/javascript">
         window.onload = function ()
         {
            Event.observe("selectform", "submit", function(e) {
               addToGroup("chk_", "<fmt:message key="community.search.promptuser"/>", e)
            })
         }
      </script>
   </cmscedit:head>

   <body>
   <edit:ui-tabs>
      <edit:ui-tab key="community.search.users" active="true"/>
      <edit:ui-tab key="community.search.groups">
         ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
      </edit:ui-tab>
   </edit:ui-tabs>

   <div class="editor">
      <div style="padding-left:10px;">
         <p>
            <a href="userAddInitAction.do"
               style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left center no-repeat"><fmt:message
                  key="view.new.user"/>
            </a>
         </p>
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
         <input type="button" id="foo" value="test"/>

         <form action="${addGroup}" method="post" name="selectform" id="selectform">
            <input type="submit" value="<fmt:message key="community.search.addtogroup"/>" name="submitButton" onclick="return addToGroup()"/>
            <%@ include file="userlist_table.jspf" %>
         </form>
      </div>
   </div>
   </body>
</mm:content>
