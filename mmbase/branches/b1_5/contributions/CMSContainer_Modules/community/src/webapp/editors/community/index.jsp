<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/community" prefix="community" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<c:set var="maxPageItems" value="20" />
<c:set var="maxIndexPages" value="5" />
<cmscedit:head title="view.title">
   <script src="userlist.js" type="text/javascript"></script>
</cmscedit:head>   
<body>
<mm:cloud loginpage="../login.jsp" rank="basic user" jspvar="cloud">
<mm:hasrank minvalue="siteadmin">
<div style="float: left">
<cmscedit:sideblock title="view.groups" titleClass="side_block_gray">
      <ul class="shortcuts">
            <li class="usergroupnew">
            <a href="groupInitAction.do"><fmt:message key="view.newgroup" /></a>
         </li>
      </ul>
      <div style="clear:both; height:10px;"></div>

      <community:listGroups var="groupList" />
      <pg:pager id="communitygroup" maxPageItems="${maxPageItems}" maxIndexPages="${maxIndexPages}" items="${fn:length(groupList)}" export="offset,currentPage=pageNumber">
         <pg:param name="communityuser.offset"/>

      <table style="position:relative;left:40px;">
         <tr>
            <td><b><fmt:message key="view.group.name" /></b></td>
         </tr>
         <c:forEach var="group" items="${groupList}">
            <pg:item>
            <tr>
               <td style="padding-right: 10px"><a href="groupInitAction.do?groupid=${group}">${group}</a></td>
               <td>
                  <a href="deleteGroupAction.do?groupid=${group}">
                     <img src="../gfx/icons/delete.png" border='0' title="<fmt:message key="view.removegroup" />"
                        onclick="return confirm('<fmt:message key="view.removegroupquestion" />')" />
                  </a>
               </td>
            </tr>
            </pg:item>
         </c:forEach>
         <c:if test="${fn:length(groupList) gt maxPageItems}">
         <tr><td>
            <pg:prev>
               <a href="${pageUrl}"> <strong>&#171;</strong></a>
            </pg:prev> <pg:pages export="pageNumber,pageUrl">
               <c:choose>
                  <c:when test="${currentPage == pageNumber}">
                     <b>${pageNumber}</b>
                  </c:when>
                  <c:otherwise>
                     <a href="${pageUrl}">[${pageNumber}]</a>
                  </c:otherwise>
               </c:choose>
            </pg:pages> <pg:next>
               <a href="${pageUrl}"><strong>&#187;</strong></a>
            </pg:next>
         </td></tr>
         </c:if>
      </table>
      </pg:pager>
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

      <community:listUsers var="userList" />
      <pg:pager id="communityuser" maxPageItems="${maxPageItems}" maxIndexPages="${maxIndexPages}" items="${fn:length(userList)}" export="offset,currentPage=pageNumber">
         <pg:param name="communitygroup.offset"/>

      <table style="position:relative;left:40px;">
         <tr>
            <td><b><fmt:message key="view.user" /></b></td>
         </tr>
         <c:forEach var="auth" items="${userList}">
            <pg:item>
            <tr>
               <td style="padding-right: 10px"><a href="userAddInitAction.do?authid=${auth.id}">${auth.userId}</a></td>
               <td>
                  <a href="deleteUserAction.do?authid=${auth.id}">
                     <img src="../gfx/icons/delete.png" border='0' title="<fmt:message key="view.removeuser" />"
                        onclick="return confirm('<fmt:message key="view.removeuserquestion" />')" />
                  </a>
               </td>
            </tr>
            </pg:item>
         </c:forEach>
         <c:if test="${fn:length(userList) gt maxPageItems}">
         <tr><td>
            <pg:prev>
               <a href="${pageUrl}"> <strong>&#171;</strong></a>
            </pg:prev> <pg:pages export="pageNumber,pageUrl">
               <c:choose>
                  <c:when test="${currentPage == pageNumber}">
                     <b>${pageNumber}</b>
                  </c:when>
                  <c:otherwise>
                     <a href="${pageUrl}">[${pageNumber}]</a>
                  </c:otherwise>
               </c:choose>
            </pg:pages> <pg:next>
               <a href="${pageUrl}"><strong>&#187;</strong></a>
            </pg:next>
         </td></tr>
         </c:if>
      </table>
   </pg:pager>
</cmscedit:sideblock>
</div>
</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>