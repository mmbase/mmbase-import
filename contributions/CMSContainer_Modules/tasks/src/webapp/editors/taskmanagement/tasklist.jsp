<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="tasks.title" />
<body>
   <c:choose>
      <c:when test="${requestScope.taskShowType eq 'task.showtype.assignedtome'}">
         <c:set var="tmpRole">assignedrel</c:set>
      </c:when>
      <c:when test="${requestScope.taskShowType eq 'task.showtype.createdbyme'}">
         <c:set var="tmpRole">creatorrel</c:set>
      </c:when>
      <c:otherwise>
         <c:set var="tmpRole"></c:set>
      </c:otherwise>
   </c:choose>

   <mm:cloud jspvar="cloud" loginpage="../login.jsp">
   <mm:cloudinfo type="user" id="cloudusername" write="false" />
       <c:set var="dashboardTaskTitle"><fmt:message key="tasks.title" /></c:set>
       <%@ include file="tasklist_table.jspf"%> 
   </mm:cloud>
</body>
</html:html>
</mm:content>
