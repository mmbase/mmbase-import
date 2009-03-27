<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<fmt:setBundle basename="com.finalist.cmsc.portlets.LoginPortlet" scope="request"/>
<c:if test="${empty active}">
   <h3><fmt:message key="register.success" /></h3>
   <p><fmt:message key="register.success.information" /></p>
</c:if>
<c:if test="${!empty active}">
   <p>
   <c:if test="${active == 'success'}">
      <fmt:message key="register.active.success" />
   </c:if>
   <c:if test="${active == 'failure'}">
      <fmt:message key="register.active.failure" />
   </c:if>
   <c:if test="${active == 'actived'}">
      <fmt:message key="register.active.alreadyactivated" />
   </c:if>
   </p>
</c:if>