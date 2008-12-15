<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="selector.newsletterpublication">
   <style type="text/css">
   input { width: 100px;}
   </style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:import externid="newsletterId"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="selector.newsletterpublications" titleClass="side_block_green">
   <p>
      <fmt:message key="confirm_send.subtitle" /> <mm:node referid="number"><b><mm:field name="title"/></b></mm:node>
   </p>
   <p style="margin-left:10px"> 
      <c:choose>
         <c:when test="${action =='approve'}">
            <c:set var="action"><fmt:message key="newsletter.edition.confirmation.approve" /></c:set>
         </c:when>
         <c:when test="${action =='revoke approving'}">
            <c:set var="action"><fmt:message key="newsletter.edition.confirmation.revoke" /></c:set>
         </c:when>
         <c:when test="${action =='freeze'}">
             <c:set var="action"><fmt:message key="newsletter.edition.confirmation.freeze" /></c:set>
         </c:when>
         <c:when test="${action =='defrost'}">
            <c:set var="action"><fmt:message key="newsletter.edition.confirmation.defrost" /></c:set>
         </c:when>
      </c:choose>     
      <fmt:message key="newsletter.edition.confirmation" >
         <fmt:param value="${action}"/>
      </fmt:message>
   </p>
   <form action="?">
      <input type="hidden" name="forward" value="${param.forward}"/>      
      <input type="hidden" name="newsletterId" value="${newsletterId}"/>
      <html:hidden property="number" value="${number}" />
      <html:submit property="save"><fmt:message key="confirm_send.yes"/></html:submit>&nbsp;
      <html:submit property="cancel"><fmt:message key="confirm_send.no"/></html:submit>
   </form>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>