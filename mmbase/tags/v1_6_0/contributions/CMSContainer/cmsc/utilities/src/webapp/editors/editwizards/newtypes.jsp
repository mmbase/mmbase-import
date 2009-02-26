<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="newtypes.title" /></title>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">

  <form action="WizardInitAction.do" method="post">
	 <input type="hidden" name="action" value="create" />
	 <input type="hidden" name="creation" value="${creation}" />
     <c:if test="${not empty param.sessionkey}">
		<input type="hidden" name="sessionkey" value="${param.sessionkey}" />
     </c:if>
	 <select name="contenttype">
		<c:forEach var="type" items="${contenttypes}">
		 <option value="${type}"><mm:nodeinfo nodetype="${type}" type="guitype"/></option>
		</c:forEach>
	 </select>
	 <input type="submit" name="submitButton" value="<fmt:message key="newtypes.create" />" />
  </form>

</mm:cloud>
	</body>
</html:html>
</mm:content>