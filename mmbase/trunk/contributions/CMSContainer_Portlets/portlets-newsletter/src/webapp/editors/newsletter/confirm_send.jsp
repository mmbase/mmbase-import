<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="confirm_send.title">
   <style type="text/css">
   input { width: 100px;}
   </style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:import externid="forward" />
<mm:import externid="newsletterId"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="confirm_send.title" titleClass="side_block_green">
   <p>
      <fmt:message key="confirm_send.subtitle" /> <mm:node referid="number"><b><mm:field name="title"/></b></mm:node>
   </p>
   <c:if test="${not empty restriction}">
      <p>
         <fmt:message key="confirm_send.confirm" />
      </p>
   </c:if>
   <c:if test="${empty restriction}">
      <p>
         <fmt:message key="confirm_send.confirm" />
      </p>
      <form action="?">
         <input type="hidden" name="forward" value="${forward}"/>      
         <input type="hidden" name="newsletterId" value="${newsletterId}"/>
         <html:hidden property="number" value="${number}" />
         <html:submit property="remove"><fmt:message key="confirm_send.yes"/></html:submit>&nbsp;
         <html:submit property="cancel"><fmt:message key="confirm_send.no"/></html:submit>
      </form>
   </c:if>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>