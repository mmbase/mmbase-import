<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>

<cmscedit:head title="reactions.title">
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
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#">
               Groups
            </a>
         </div>
      </div>
   </div>

<div class="editor">
   <div style="padding-left:10px;">
     <p><a href="${pageContext.request.contextPath }/editors/community/groupInitAction.do" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left center no-repeat">New Group</a><p>
      <html:form action="/editors/community/searchConditionalGroupAction.do" method="post">
            <table border="0">
               <tbody>
                  <tr>&nbsp;</tr>
                  <tr>
                     <td style="width:150px">Group name</td>
                     <td><html:text style="width: 250px" property="groupname"/></td>
                  </tr>
                   <tr>
            <td width="150px">Member(s)</td>
            <td><input type="text" style="width:250px" value="" name="member"></td>
             <tr>
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
   <div class="ruler_green"><div>&nbsp; result &nbsp;</div></div>
   <div class="body">
      <form action="">
         <table border="0" style="padding-left:10px;">
         <edit:ui-table items="${results}" var="result" size="${totalCount}" requestURI="/editors/community/searchConditionalGroupAction.do">
         <edit:ui-tcolumn title="">&nbsp;
               <a href="./SearchConditionalUser.do?groupName=${result.groupName}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" width="16" height="16"></a>
               <a href="./deleteGroupAction.do?groupid=${result.groupName}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16"></a>
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="Group" sort="groupName">
               <c:url var="userActionUrl" value="/editors/community/SearchConditionalUser.do"/>
               <a href="${userActionUrl}?groupName=${result.groupName}">${result.groupName}</a>
          </edit:ui-tcolumn>
          <edit:ui-tcolumn titlekey="Users" >
               ${result.users }
          </edit:ui-tcolumn>
          </edit:ui-table></form>      
   </div>
</div>