<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/community" prefix="community" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="userlist.title">
   <script src="userlist.js" type="text/javascript"></script>
</cmscedit:head>   
<body>
<mm:cloud loginpage="../login.jsp" rank="administrator" jspvar="cloud">
<div style="float: left">
<cmscedit:sideblock title="userlist.groups" titleClass="side_block_gray">
      <ul class="shortcuts">
            <li class="usergroupnew">
            <a href="GroupInitAction.do"><fmt:message key="userlist.newgroup" /></a>
         </li>
      </ul>
      <div style="clear:both; height:10px;"></div>

      <table style="position:relative;left:40px;">
         <tr>
            <td><b><fmt:message key="group.name" /></b></td>
         </tr>

      </table>
   </cmscedit:sideblock>
</div>

<div style="float: left;padding-left: 5px">
<cmscedit:sideblock title="view.adduser" 
   titleClass="side_block_green" titleStyle="float: left">
      <ul class="shortcuts">
            <li class="usernew">
            <a href="userAddInitAction.do"><fmt:message key="view.add" /></a>
         </li>
      </ul>
      <div style="clear:both; height:10px;"></div>

      <table style="position:relative;left:40px;">
         <tr>
            <td><b><fmt:message key="view.user" /></b></td>
         </tr>
         <community:listUsers var="userList" />
         <c:forEach var="auth" items="${userList}">
            <tr>
               <td style="padding-right: 10px"><a href="userAddInitAction.do?userid=${auth.userId}">${auth.userId}</a></td>
               <td>
                  <a href="javascript:info('${auth.id}')">
                     <img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="content.info" />"/>
                  </a>
               </td>
               <td><maydelete>
                  <a href="deleteUserAction.do?userid=${auth.userId}">
                     <img src="../gfx/icons/delete.png" border='0' title="<fmt:message key="view.removeuser" />"
                        onclick="return confirm('<fmt:message key="view.removeuserquestion" />')" />
                  </a>
               </maydelete></td>
            </tr>
         </c:forEach>
      </table>
</cmscedit:sideblock>
</div>
</mm:cloud>
</body>
</html:html>
</mm:content>